import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * <blockquote>  RUN this to start Elevator SubSystem to ensure thread running of multiple elevator 
 * @author Muhammad Tarequzzaman |100954008|
 *  responsible for <b> Entire Elevator System</b> 
 *  
 */
public class ElevatorSubSystem {

	private static final int RECEIVE_PORT1 = 5248;
	private static final int RECEIVE_PORT2 = 5249;
	private static final int RECEIVE_PORT3 = 5250;
	private static final int RECEIVE_PORT4 = 5251;
	private static final int Floors = 5;
	//private static final int MAINTENANCE_PORT = 5252;
	
	
	public static void main(String[] args) {
		

		Elevator E1 = new Elevator(1, Floors, RECEIVE_PORT1, 1);
		Elevator E2 = new Elevator(2, Floors, RECEIVE_PORT2, 1);
		Elevator E3 = new Elevator(3, Floors, RECEIVE_PORT3, 1);
		Elevator E4 = new Elevator(4, Floors, RECEIVE_PORT4, 5);
		Elevator[] elevators = {E1, E2,E3,E4};
			
		for (Elevator E : elevators) { // start all elevator
			try {
				E.start();
			} catch (Exception e5) {
				// TODO: handle exception
				e5.printStackTrace();
			}
			
		}
			
		
	}
	
	
	public static int getReceivePort1() {
		return RECEIVE_PORT1;
	}
	public static int getReceivePort2() {
		return RECEIVE_PORT2;
	}
	public static int getReceivePort3() {
		return RECEIVE_PORT3;
	}
	public static int getReceivePort4() {
		return RECEIVE_PORT4;
	}
	public static int getFloors() {
		return Floors;
	}
	
	//------------------------------------------------------------------------------------------------------//
	
		private static final String INPUT_PATH = "src/InputEvents.txt";
		private int currentLine = 0;
		private boolean moreToRead;
		
		public synchronized void ElevatorInputRead () {
			
			//get text file path
			Path path = Paths.get(INPUT_PATH);
			
			ArrayList<String> 	inputArrayList = new ArrayList<String>();
			moreToRead = true;
			
			
			//iterate through the file and read each line
			while (moreToRead) {
				try (Stream<String> lines = Files.lines(path)) {
					try {
						inputArrayList.add(lines.skip(currentLine).findFirst().get());				
					}catch (NoSuchElementException e) {
						moreToRead = false;
						lines.close();
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				currentLine++;
			}
			
			for (int i = 0; i < inputArrayList.size(); i++) {
				String inputElevatorEvent = inputArrayList.get(i);
				String[] inputEvents = inputElevatorEvent.split(" ");
				
				if ("e" + String.valueOf(elevatorNumber) == inputEvents [1]) {
						ArrayList <Integer> userDest = new ArrayList<Integer>(1);
						userDest.add(Integer.parseInt(inputEvents[2]));
						setNextFloorList(userDest);
					}
				}
			
			notifyAll();
			return;
			
		}
	
		private static final int BYTE_SIZE = 6400;

		private byte[] ELEToByteArray(ELE ele) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(Elevator.BYTE_SIZE);

			ObjectOutputStream oos = null;

			try {
				oos = new ObjectOutputStream(baos);
			} catch (IOException e1) {
				// Unable to create object output stream
				e1.printStackTrace();
			}

			try {
				oos.writeObject(ele);
			} catch (IOException e) {
				// Unable to write eventList in bytes
				e.printStackTrace();
			}

			return baos.toByteArray();
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
}