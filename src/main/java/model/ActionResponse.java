package model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class ActionResponse {
	@SerializedName("token")
	public int token;
	@SerializedName("timestamp")
	public int timestamp;
	@SerializedName("elevators")
	public List<Elevator> elevators;
	@SerializedName("is_end")
	public boolean isEnd;
}
