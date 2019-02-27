package model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class Command {
	@SerializedName("elevator_id")
	public int elevatorId;
	@SerializedName("command")
	public String command;
	@SerializedName("call_ids")
	public List<Integer> callIds;
}
