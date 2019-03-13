package model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        if (passengers.size() == 0) {
            Call targetCall = calls.stream()
                    .min(Comparator.comparingInt(value -> Math.abs(value.getStart() - floor)))
                    .orElse(null);

            if (targetCall == null) {
                switch (status) {
                    case UPWARD:
                    case DOWNWARD:
                        command.setCommand("STOP");
                        break;
                    case OPEND:
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
                    case OPEND:
                        if (start == floor) {
                            command.setCommand("ENTER");

                            List<Integer> ids = calls.stream()
                                    .filter(call -> call.getStart() == floor)
                                    .filter(call -> !((targetCall.getStart() - targetCall.getEnd() > 0) ^ (call.getStart() - call.getEnd() > 0)))
                                    .map(Call::getId)
                                    .collect(Collectors.toList());
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
            calls.remove(targetCall);
            return command;
        } else {
            if (passengers.get(0).getEnd() - passengers.get(0).getStart() > 0) {
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
                    case OPEND:
                        command.setCommand("EXIT");
                        List<Integer> ids = passengers.stream().filter(call -> call.getEnd() == floor).map(Call::getId).collect(Collectors.toList());
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
                    case OPEND:
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
                    case OPEND:
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

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }
}
