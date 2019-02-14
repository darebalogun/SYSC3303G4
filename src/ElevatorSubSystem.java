import java.net.DatagramPacket;
import java.util.*;


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
		
//		int[] ports = {RECEIVE_PORT1,RECEIVE_PORT1,RECEIVE_PORT1,RECEIVE_PORT1};
		// use MAINTENANCE_PORT to send a initial packet to scheduler  
	//		try {
				//>>> here<<<//
		//		sendMaintenancePort(ports) ;
		//	}catch (Exception e) {
				// TODO: handle exception
		//	}
			
		for (Elevator E : elevators) { // start all elevator
			try {
				E.start();
			} catch (Exception e5) {
				// TODO: handle exception
				e5.printStackTrace();
			}
			
		}
			
		
	}
	
	//public void sendMaintenancePort(int[]ports) {
		// please implement send ports to scheduler here 
		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}