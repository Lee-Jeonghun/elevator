package model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import model.Call;
import model.Elevator;

/**
 * @author JeongHun, Lee
 */
public class OnCallsResponse {
	@SerializedName("token")
	public String token;
	@SerializedName("timestamp")
	public int timestamp;
	@SerializedName("elevators")
	public List<Elevator> elevators;
	@SerializedName("calls")
	public List<Call> calls;
	@SerializedName("is_end")
	public boolean isEnd;
}
