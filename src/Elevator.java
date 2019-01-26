import java.util.*;

public class Elevator {
	
	public ArrayList<Boolean> ButtonList; 
	public ArrayList<Boolean> Elevatorlamp; 
	public int timeBtwFloors;
	public int doorDelay;
	public Boolean dooropen;
	public int currentFloor; 
	public int nextFloor;
	
	
	
	//test del folder




	public Elevator(int timeBtwFloors, int doorDelay, Boolean dooropen, int currentFloor, int nextFloor) {
		
		this.ButtonList = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(ButtonList, Boolean.FALSE);
		
		this.Elevatorlamp = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(ButtonList, Boolean.FALSE);
		
		this.timeBtwFloors = timeBtwFloors;
		this.doorDelay = doorDelay;
		this.dooropen = dooropen;
		this.currentFloor = currentFloor;
		this.nextFloor = nextFloor;
	}
	
}

