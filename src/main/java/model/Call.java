package model;

import java.util.Comparator;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class Call{
	@SerializedName("id")
	private int id;
	@SerializedName("timestamp")
	private int timestamp;
	@SerializedName("start")
	private int start;
	@SerializedName("end")
	private int end;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "Call{" +
			"id=" + id +
			", timestamp=" + timestamp +
			", start=" + start +
			", end=" + end +
			'}';
	}
}
