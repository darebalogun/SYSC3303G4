
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Muhammad Tarequzzaman | 100954008 
 *
 */
public class Elevator {
	
	private int elevatorNumber; 
	public ArrayList<Boolean> buttonList;
	public ArrayList<Boolean> specialButtonList;
	public ArrayList<Boolean> elevatorLamp;
	public int timeBtwFloors;
	public int doorDelay;
	public Boolean dooropen;
	public int currentFloor;
	public int nextFloor;
	private int motorDelay;

	public Elevator(int timeBtwFloors, int doorDelay, int elevatorNumber ) {

		// create buttonList for 13 floor and Initialize as FALSE
		this.buttonList = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(buttonList, Boolean.FALSE);

		// create elevatorLamp for 13 floor and Initialize as FALSE
		this.elevatorLamp = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(elevatorLamp, Boolean.FALSE);

		// create specialButtonList for 3 button and Initialize as FALSE
		this.specialButtonList = new ArrayList<Boolean>(Arrays.asList(new Boolean[3]));
		Collections.fill(specialButtonList, Boolean.FALSE);

		this.timeBtwFloors = timeBtwFloors;
		this.doorDelay = doorDelay;
		this.dooropen = false;
		this.elevatorNumber = elevatorNumber;
	}

	/**
	 * @param motorDelay comes from timeNeed
	 */
	public void runMotor(int motorDelay) {
		try {
			TimeUnit.SECONDS.sleep(1 * motorDelay +(2*doorDelay));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void timeNeed() {

		try {
			if (currentFloor > nextFloor) {
				this.motorDelay = timeBtwFloors*(currentFloor - nextFloor);
			} else {
				this.motorDelay = timeBtwFloors*( nextFloor - currentFloor);
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buttonList == null) ? 0 : buttonList.hashCode());
		result = prime * result + currentFloor;
		result = prime * result + doorDelay;
		result = prime * result + ((dooropen == null) ? 0 : dooropen.hashCode());
		result = prime * result + ((elevatorLamp == null) ? 0 : elevatorLamp.hashCode());
		result = prime * result + motorDelay;
		result = prime * result + nextFloor;
		result = prime * result + timeBtwFloors;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Elevator other = (Elevator) obj;
		if (buttonList == null) {
			if (other.buttonList != null)
				return false;
		} else if (!buttonList.equals(other.buttonList))
			return false;
		if (currentFloor != other.currentFloor)
			return false;
		if (doorDelay != other.doorDelay)
			return false;
		if (dooropen == null) {
			if (other.dooropen != null)
				return false;
		} else if (!dooropen.equals(other.dooropen))
			return false;
		if (elevatorLamp == null) {
			if (other.elevatorLamp != null)
				return false;
		} else if (!elevatorLamp.equals(other.elevatorLamp))
			return false;
		if (motorDelay != other.motorDelay)
			return false;
		if (nextFloor != other.nextFloor)
			return false;
		if (timeBtwFloors != other.timeBtwFloors)
			return false;
		return true;
	}

	public ArrayList<Boolean> getButtonList() {
		return buttonList;
	}

	public void setButtonList(ArrayList<Boolean> buttonList) {
		this.buttonList = buttonList;
	}

	public ArrayList<Boolean> getSpecialButtonList() {
		return specialButtonList;
	}

	public void setSpecialButtonList(ArrayList<Boolean> specialButtonList) {
		this.specialButtonList = specialButtonList;
	}

	public ArrayList<Boolean> getElevatorLamp() {
		return elevatorLamp;
	}

	public void setElevatorLamp(ArrayList<Boolean> elevatorLamp) {
		this.elevatorLamp = elevatorLamp;
	}

	public int getTimeBtwFloors() {
		return timeBtwFloors;
	}

	public void setTimeBtwFloors(int timeBtwFloors) {
		this.timeBtwFloors = timeBtwFloors;
	}

	public int getDoorDelay() {
		return doorDelay;
	}

	public void setDoorDelay(int doorDelay) {
		this.doorDelay = doorDelay;
	}

	public Boolean getDooropen() {
		return dooropen;
	}

	public void setDooropen(Boolean dooropen) {
		this.dooropen = dooropen;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public int getNextFloor() {
		return nextFloor;
	}

	public void setNextFloor(int nextFloor) {
		this.nextFloor = nextFloor;
	}

	public int getMotorDelay() {
		return motorDelay;
	}

	public void setMotorDelay(int motorDelay) {
		this.motorDelay = motorDelay;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public void setElevatorNumber(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
	}

}
