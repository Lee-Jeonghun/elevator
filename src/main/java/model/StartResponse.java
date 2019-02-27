package model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import model.Elevator;

/**
 * @author JeongHun, Lee
 */
public class StartResponse {
	@SerializedName("token")
	public String token;
	@SerializedName("timestamp")
	public int timestamp;
	@SerializedName("elevators")
	public List<Elevator> elevators;
	@SerializedName("is_end")
	public boolean isEnd;
}
