
public class InputEvent {
	// Event time log
	private final String time;
	
	// Floor where button was pressed
	private final Integer currentFloor;
	
	// Up or down
	private final Boolean up;
	
	// Car button
	private final Integer destinationFloor;

	public InputEvent(String time, Integer currentFloor, Boolean up, Integer destinationFloor) {
		
		this.time = time;
		
		this.currentFloor = currentFloor;
		
		this.up = up;
		
		this.destinationFloor = destinationFloor;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
