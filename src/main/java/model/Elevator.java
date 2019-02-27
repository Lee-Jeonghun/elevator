package model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class Elevator {
	@SerializedName("id")
	public int id;
	@SerializedName("floor")
	public int floor;
	@SerializedName("passengers")
	public List<Call> passengers;
	@SerializedName("status")
	public String status;
}
