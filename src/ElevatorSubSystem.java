import java.util.*;


public class ElevatorSubSystem extends Thread{

	private static final int RECEIVE_PORT1 = 0;
	private static final int RECEIVE_PORT2 = 0;
	private static final int RECEIVE_PORT3 = 0;
	private static final int RECEIVE_PORT4 = 0;
	private static final int Floors = 5;
	private static final int MAINTENANCE_PORT =0;
	public static void main(String[] args) {
		
		
	}
	
	public void run() {
		Elevator E1 = new Elevator(1, Floors, RECEIVE_PORT1, 1);
		Elevator E2 = new Elevator(2, Floors, RECEIVE_PORT2, 1);
		Elevator E3 = new Elevator(3, Floors, RECEIVE_PORT3, 5);
		Elevator E4 = new Elevator(4, Floors, RECEIVE_PORT4, 5);
		Elevator[] elevators = new Elevator [3];
		
		int[] ports = {RECEIVE_PORT1,RECEIVE_PORT1,RECEIVE_PORT1,RECEIVE_PORT1};
		// use MAINTENANCE_PORT to send a initial packet to scheduler  
			try {
				//>>> here<<<//
				sendMaintenance(ports) ;
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		for (Elevator E : elevators) { // start all elevator
			E.start();
		}
			
		
	}
	
	public void sendMaintenance(int[]ports) {
		// please implement send ports to scheduler here 
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}