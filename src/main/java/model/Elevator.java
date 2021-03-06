package model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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

	public Command getNextCommand(List<Call> calls) {
		Command command = new Command(id);

		if (passengers.size() == 0) {    // 빈 엘리베이터 인경우
			//가장 가까운 층의 콜을 가져온다
			Call targetCall = calls.stream()
								   .min(Comparator.comparingInt(value -> Math.abs(value.getStart() - floor)))
								   .orElse(null);

			//콜이 없을 경우 STOP 상태로 만들거나 유지한다
			if (targetCall == null) {
				switch (status) {
					case UPWARD:
					case DOWNWARD:
						command.setCommand("STOP");
						break;
					case OPENED:
						command.setCommand("CLOSE");
						break;
					case STOPPED:
						command.setCommand("STOP");
						break;
				}
			} else {
				int start = targetCall.getStart();
				switch (status) {
					case UPWARD:
						if (start > floor) {
							command.setCommand("UP");
						} else {
							command.setCommand("STOP");
						}
						break;
					case DOWNWARD:
						if (start < floor) {
							command.setCommand("DOWN");
						} else {
							command.setCommand("STOP");
						}
						break;
					case OPENED:
						if (start == floor) {
							command.setCommand("ENTER");

							//콜을 태울때 기준이 되는 콜과 같은 층에 있고 같은 방향으로 가는 콜을 함께 태운다.
							List<Integer> ids = calls.stream()
													 .filter(call -> call.getStart() == floor)    //같은 층
													 .filter(call -> !((targetCall.getStart() - targetCall.getEnd() > 0) ^ (call.getStart() - call.getEnd() > 0)))    //같은 방향
													 .limit(MAX_PASSENGER)    //엘리베티어 용량
													 .map(Call::getId)
													 .collect(Collectors.toList());

							Iterator iterator = calls.iterator();
							int num = 0;
							while (iterator.hasNext()) {
								Call call = (Call)iterator.next();
								if (call.getStart() == floor && !((targetCall.getStart() - targetCall.getEnd() > 0) ^ (call.getStart() - call.getEnd() > 0)) && num < MAX_PASSENGER) {
									iterator.remove();
									num++;
								}
							}

							command.setCallIds(ids);
						} else {
							command.setCommand("CLOSE");
						}
						break;
					case STOPPED:
						if (start > floor) {
							command.setCommand("UP");
						} else if (start == floor) {
							command.setCommand("OPEN");
						} else {
							command.setCommand("DOWN");
						}
						break;
				}
			}
			return command;
		} else {    //승객이 타고 있을때
			//가장 먼저 내리는 승객을 찾기 위해 진행방향에 따라 정렬한다
			boolean up = passengers.get(0).getEnd() - passengers.get(0).getStart() > 0;
			if (up) {
				passengers.sort(Comparator.comparingInt(Call::getEnd));
			} else {
				passengers.sort((o1, o2) -> Integer.compare(o2.getEnd(), o1.getEnd()));
			}

			if (passengers.get(0).getEnd() == floor) {
				switch (status) {
					case UPWARD:
					case DOWNWARD:
						command.setCommand("STOP");
						break;
					case OPENED:
						command.setCommand("EXIT");
						//해당 층에서 내리는 모든 승객을 하차
						List<Integer> ids = passengers.stream()
													  .filter(call -> call.getEnd() == floor)
													  .map(Call::getId)
													  .collect(Collectors.toList());
						command.setCallIds(ids);
						break;
					case STOPPED:
						command.setCommand("OPEN");
						break;
				}

			} else if (passengers.size() < MAX_PASSENGER && calls.stream()
																 .anyMatch(call -> call.getStart() == floor &&
																	 ((call.getEnd() - call.getStart() > 0 && up) || (call.getEnd() - call.getStart() < 0 && !up)))) {
				switch (status) {
					case UPWARD:
					case DOWNWARD:
						command.setCommand("STOP");
						break;
					case OPENED:
						command.setCommand("ENTER");
						List<Integer> ids = calls.stream()
												 .filter(call -> call.getStart() == floor)
												 .filter(call -> (call.getEnd() - call.getStart() > 0 && up) || (call.getEnd() - call.getStart() < 0 && !up))
												 .limit(MAX_PASSENGER - passengers.size())
												 .map(Call::getId)
												 .collect(Collectors.toList());

						Iterator iterator = calls.iterator();
						int num = passengers.size();
						while (iterator.hasNext()) {
							Call call = (Call)iterator.next();
							if (call.getStart() == floor && (call.getEnd() - call.getStart() > 0 && up) || (call.getEnd() - call.getStart() < 0 && !up) && num < MAX_PASSENGER) {
								iterator.remove();
								num++;
							}
						}

						command.setCallIds(ids);
						break;
					case STOPPED:
						command.setCommand("OPEN");
						break;
				}
			} else if (passengers.get(0).getEnd() < floor) {
				switch (status) {
					case UPWARD:
						command.setCommand("STOP");
						break;
					case DOWNWARD:
						command.setCommand("DOWN");
						break;
					case OPENED:
						command.setCommand("CLOSE");
						break;
					case STOPPED:
						command.setCommand("DOWN");
						break;
				}
			} else {
				switch (status) {
					case UPWARD:
						command.setCommand("UP");
						break;
					case DOWNWARD:
						command.setCommand("STOP");
						break;
					case OPENED:
						command.setCommand("CLOSE");
						break;
					case STOPPED:
						command.setCommand("UP");
						break;
				}
			}
		}

		return command;
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

	@Override
	public String toString() {
		return "Elevator{" +
			"id=" + id +
			", floor=" + floor +
			", passengers=" + passengers +
			", status=" + status +
			'}';
	}
}
