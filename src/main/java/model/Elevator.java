package model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author JeongHun, Lee
 */
public class Elevator {
	private static final int MAX_PASSENGER = 8;
	@SerializedName("id")
	private int id;
	@SerializedName("floor")
	private int floor;
	@SerializedName("passengers")
	private List<Call> passengers;
	@SerializedName("status")
	private Status status;
	private List<Call> awaiter = new ArrayList<>();
	private int expectCompleteTimestamp = 0;

	public int getExpectTimestamp(Call call) {
		int calculatedTimestamp = expectCompleteTimestamp;
		if (passengers.size() >= MAX_PASSENGER) {
			if (passengers.stream().allMatch(passenger -> passenger.getEnd() > call.getStart())) {
				return -1;
			}
		}

		boolean up = false;
		if (passengers.size() != 0) {
			if (status == Status.UPWARD) {
				//현재 진행방향과 반대인 경우
				if (call.getEnd() < floor || call.getStart() < floor) {
					return -1;
				}
				up = true;
			} else if (status == Status.DOWNWARD) {
				//현재 진행방향과 반대인 경우
				if (call.getStart() > floor || call.getEnd() > floor) {
					return -1;
				}
				up = false;
			} else if (status == Status.OPEND || status == Status.STOPPED) {
				if (passengers.stream().anyMatch(passenger -> passenger.getEnd() > floor)) {
					if (call.getEnd() < floor || call.getStart() < floor) {
						return -1;
					}
					up = true;
				} else if (passengers.stream().anyMatch(passenger -> passenger.getEnd() < floor)) {
					if (call.getStart() > floor || call.getEnd() > floor) {
						return -1;
					}
					up = false;
				}
			}
		} else {
			if(awaiter.size() != 0){

			}
		}

		if (passengers.stream().anyMatch(passenger -> passenger.getEnd() == call.getStart())) {
			if (passengers.stream().anyMatch(passenger -> passenger.getEnd() == call.getEnd())) {
				calculatedTimestamp += 2; // ENTER, EXIT
			} else {
				calculatedTimestamp += 5; // ENTER, STOP, OPEN, EXIT, CLOSE
			}
		} else {
			if (passengers.stream().anyMatch(passenger -> passenger.getEnd() == call.getEnd())) {
				calculatedTimestamp += 5; // STOP, OPEN, ENTER, CLOSE, EXIT
			} else {
				calculatedTimestamp += 8; // STOP, OPEN, ENTER, CLOSE, STOP, OPEN, EXIT, CLOSE
			}
		}

		int passengersSize = passengers.size();
		if (passengersSize == 0) {
			return calculatedTimestamp + Math.abs(call.getStart() - floor) + Math.abs(call.getEnd() - call.getStart());
		} else {
			int currentLastFloor = passengers.get(passengersSize - 1).getEnd();
			if (up) {
				return currentLastFloor > call.getEnd() ? calculatedTimestamp : calculatedTimestamp + call.getEnd() - currentLastFloor;
			} else {
				return currentLastFloor < call.getEnd() ? calculatedTimestamp : calculatedTimestamp + currentLastFloor - call.getEnd();
			}
		}

	}

	public Command getNextComment() {
		return null;
	}

	public void init(Elevator elevator) {
		this.id = elevator.getId();
		this.floor = elevator.getFloor();
		this.passengers = elevator.getPassengers();
		this.status = elevator.getStatus();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public List<Call> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Call> passengers) {
		this.passengers = passengers;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Call> getAwaiter() {
		return awaiter;
	}

	public void setAwaiter(List<Call> awaiter) {
		this.awaiter = awaiter;
	}

	public int getExpectCompleteTimestamp() {
		return expectCompleteTimestamp;
	}

	public void setExpectCompleteTimestamp(int expectCompleteTimestamp) {
		this.expectCompleteTimestamp = expectCompleteTimestamp;
	}
}
