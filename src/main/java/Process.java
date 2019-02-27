import java.io.IOException;
import java.util.List;

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
import model.Command;
import model.OnCallsResponse;
import model.StartResponse;

/**
 * @author JeongHun, Lee
 */
public class Process {
	private static final String URL = "http://10.105.186.205:8000";
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private Gson gson = new Gson();

	public void doProcess(String user, int problem, int count) throws IOException {
		StartResponse startResponse = start(user, problem, count);
		String token = startResponse.token;
		OnCallsResponse onCallsResponse = onCalls(token);
		String token1 = onCallsResponse.token;
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

	private OnCallsResponse onCalls(String token) throws IOException {
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

	private ActionResponse action(String token, List<Command> commands) throws IOException {
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
