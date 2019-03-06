package model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class OnCallsResponse {
	@SerializedName("token")
	private String token;
	@SerializedName("timestamp")
	private int timestamp;
	@SerializedName("elevators")
	private List<Elevator> elevators;
	@SerializedName("calls")
	private List<Call> calls;
	@SerializedName("is_end")
	private boolean end;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public List<Elevator> getElevators() {
		return elevators;
	}

	public void setElevators(List<Elevator> elevators) {
		this.elevators = elevators;
	}

	public List<Call> getCalls() {
		return calls;
	}

	public void setCalls(List<Call> calls) {
		this.calls = calls;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}
}
