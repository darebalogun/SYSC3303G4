import java.util.concurrent.ConcurrentSkipListSet;

public class ElevatorState implements Comparable<ElevatorState>{
	
	private int number;
	
	private Scheduler.Direction direction;
	
	private int currentFloor;
	
	private int destinationFloor;
	
	private ConcurrentSkipListSet<Integer> taskList;
	
	public ElevatorState(int number) {
		this.number = number;
		
		this.direction = Scheduler.Direction.IDLE;
		
		this.currentFloor = 1;
		
		taskList = new ConcurrentSkipListSet<Integer>();
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Scheduler.Direction getDirection() {
		return direction;
	}

	public void setDirection(Scheduler.Direction direction) {
		this.direction = direction;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
	}

	public ConcurrentSkipListSet<Integer> getTaskList() {
		return taskList;
	}

	public void setTaskList(ConcurrentSkipListSet<Integer> destList) {
		this.taskList = destList;
	}
	
	public void addTask(Integer task) {
		
		if (this.direction == Scheduler.Direction.UP) {
			if (task > this.taskList.last()) {
				this.destinationFloor = task;
			}
		} else if (this.direction == Scheduler.Direction.DOWN) {
			if (task < this.taskList.first()) {
				this.destinationFloor = task;
			}
		} else {
			this.destinationFloor = task;
			
			if (task > this.currentFloor) {
				this.direction = Scheduler.Direction.UP;
			} else if (task < this.currentFloor) {
				this.direction = Scheduler.Direction.DOWN;
			} 
		}
		
		this.taskList.add(task);
		
	}
	
	public void removeTask(Integer task) {
		this.taskList.remove(task);
		if (this.taskList.isEmpty()) {
			this.direction = Scheduler.Direction.IDLE;
		}
	}

	@Override
	public int compareTo(ElevatorState state) {
		return (this.getNumber() - state.getNumber());
	}
	
	
	
}
