package model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class Command {
	@SerializedName("elevator_id")
	private int elevatorId;
	@SerializedName("command")
	private String command;
	@SerializedName("call_ids")
	private List<Integer> callIds;

	public Command() {
	}

	public Command(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	public int getElevatorId() {
		return elevatorId;
	}

	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<Integer> getCallIds() {
		return callIds;
	}

	public void setCallIds(List<Integer> callIds) {
		this.callIds = callIds;
	}

	@Override
	public String toString() {
		return "Command{" +
			"elevatorId=" + elevatorId +
			", command='" + command + '\'' +
			", callIds=" + callIds +
			'}';
	}
}
