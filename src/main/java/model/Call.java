package model;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class Call {
	@SerializedName("id")
	public int id;
	@SerializedName("timestamp")
	public int timestamp;
	@SerializedName("start")
	public int start;
	@SerializedName("end")
	public int end;
}
