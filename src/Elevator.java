import java.util.*;

public class Elevator {
	
	public ArrayList<Boolean> buttonList; 
	public ArrayList<Boolean> elevatorLamp; 
	public int timeBtwFloors;
	public int doorDelay;
	public Boolean dooropen;
	public int currentFloor; 
	public int nextFloor;
	
	
	
	




	public Elevator(int timeBtwFloors, int doorDelay, Boolean dooropen, int currentFloor, int nextFloor) {
		
		// create ButtonList for 13 floor and Initialize as FALSE 
		this.buttonList = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(buttonList, Boolean.FALSE);
		
		// create Elevatorlamp for 13 floor and Initialize as FALSE 
		this.elevatorLamp = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(buttonList, Boolean.FALSE);
		
		this.timeBtwFloors = timeBtwFloors;
		
		this.doorDelay = doorDelay;
		
		this.dooropen = false;
		
		
	}
	
}

