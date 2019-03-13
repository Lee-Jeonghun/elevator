import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

	public void doProcess(String user, int problem, int count) throws IOException {
		StartResponse startResponse = start(user, problem, count);
		System.out.println("Start: " + startResponse);
		token = startResponse.getToken();

		for (Elevator elevator : startResponse.getElevators()) {
			elevators[elevator.getId()].init(elevator);
		}

		for (OnCallsResponse onCallsResponse = onCalls(); !onCallsResponse.isEnd(); onCallsResponse = onCalls(), currentTimestamp++) {
			System.out.println("OnCall: " + onCallsResponse);
			List<Command> commands = new ArrayList<>();

			for (Elevator elevator : onCallsResponse.getElevators()) {
				Elevator targetElevator = elevators[elevator.getId()];
				targetElevator.init(elevator);
				commands.add(targetElevator.getNextCommand(onCallsResponse.getCalls()));
			}

			action(commands);
			System.out.println("Action: " + commands);
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
			String response = EntityUtils.toString(entity);
			onCallsResponse = gson.fromJson(response, OnCallsResponse.class);

			EntityUtils.consume(entity);
		} finally {
			httpResponse.close();
		}

		return onCallsResponse;
	}

	private void action(List<Command> commands) throws IOException {
		String uri = URL + "/action";
		HttpPost httpPost = new HttpPost(uri);

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("commands", gson.toJsonTree(commands));

		httpPost.setHeader("X-Auth-Token", token);
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		httpPost.setEntity(new StringEntity(jsonObject.toString()));

		CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
		httpResponse.close();
	}
}
