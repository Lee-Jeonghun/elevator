import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import model.ActionResponse;
import model.Call;
import model.Command;
import model.Elevator;
import model.OnCallsResponse;
import model.StartResponse;

/**
 * @author JeongHun, Lee
 */
public class Process {
	private static final String URL = "http://10.105.186.205:8000";
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private Gson gson = new Gson();
	private int currentTimestamp = 0;
	private String token;
	private Elevator[] elevators = new Elevator[] {new Elevator(), new Elevator(), new Elevator(), new Elevator()};
	private Set<Integer> assignedCalls = new HashSet<>();

	public void doProcess(String user, int problem, int count) throws IOException {
		StartResponse startResponse = start(user, problem, count);
		token = startResponse.getToken();

		for (Elevator elevator : startResponse.getElevators()) {
			elevators[elevator.getId()].init(elevator);
		}

		for (OnCallsResponse onCallsResponse = onCalls(); !onCallsResponse.isEnd(); onCallsResponse = onCalls(), currentTimestamp++) {

			for (Elevator elevator : onCallsResponse.getElevators()) {
				elevators[elevator.getId()].init(elevator);
			}

			for (Call call : onCallsResponse.getCalls()) {
				if (assignedCalls.contains(call.getId())) {
					continue;
				}

				int id = 0;
				int minTimestamp = Integer.MAX_VALUE;
				for (Elevator elevator : elevators) {
					int expectTimestamp = elevator.getExpectTimestamp(call);
					if (expectTimestamp != -1 && expectTimestamp < minTimestamp) {
						id = elevator.getId();
						minTimestamp = expectTimestamp;
					}
				}

				if (minTimestamp < Integer.MAX_VALUE) {
					elevators[id].setExpectCompleteTimestamp(minTimestamp);
					elevators[id].getAwaiter().add(call);
					assignedCalls.add(call.getId());
				}
			}

			//TODO actionCall
		}
	}

	private StartResponse start(String user, int problem, int count) throws IOException {
		String uri = URL + "/start" + "/" + user + "/" + problem + '/' + count;
		HttpPost httpPost = new HttpPost(uri);
		CloseableHttpResponse httpResponse = httpclient.execute(httpPost);

		StartResponse startResponse;
		try {
			HttpEntity entity = httpResponse.getEntity();
			startResponse = gson.fromJson(EntityUtils.toString(entity), StartResponse.class);

			EntityUtils.consume(entity);
		} finally {
			httpResponse.close();
		}

		return startResponse;
	}

	private OnCallsResponse onCalls() throws IOException {
		String uri = URL + "/oncalls";
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("X-Auth-Token", token);
		CloseableHttpResponse httpResponse = httpclient.execute(httpGet);

		OnCallsResponse onCallsResponse;
		try {
			HttpEntity entity = httpResponse.getEntity();
			onCallsResponse = gson.fromJson(EntityUtils.toString(entity), OnCallsResponse.class);

			EntityUtils.consume(entity);
		} finally {
			httpResponse.close();
		}

		return onCallsResponse;
	}

	private ActionResponse action(List<Command> commands) throws IOException {
		String uri = URL + "/action";
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("X-Auth-Token", token);
		httpPost.setHeader(ContentType.APPLICATION_JSON.getMimeType(), token);
		CloseableHttpResponse httpResponse = httpclient.execute(httpPost);

		ActionResponse actionResponse;
		try {
			HttpEntity entity = httpResponse.getEntity();
			actionResponse = gson.fromJson(EntityUtils.toString(entity), ActionResponse.class);

			EntityUtils.consume(entity);
		} finally {
			httpResponse.close();
		}

		return actionResponse;
	}
}
