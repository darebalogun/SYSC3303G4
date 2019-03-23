import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * <blockquote> RUN this to start Elevator SubSystem to ensure thread running of
 * multiple elevator
 * 
 * @author Muhammad Tarequzzaman |100954008| responsible for <b> Entire Elevator
 *         System</b>
 * 
 */
public class ElevatorSubSystem {

	private static final int RECEIVE_PORT1 = 5248;
	private static final int RECEIVE_PORT2 = 5249;
	private static final int RECEIVE_PORT3 = 5250;
	private static final int RECEIVE_PORT4 = 5251;
	private static final int Floors = 22;
	private static final int MAINTENANCE_PORT = 6009;

	public static void main(String[] args) {

		
		Elevator E1 = new Elevator(1, Floors, RECEIVE_PORT1, 1);
		Elevator E2 = new Elevator(2, Floors, RECEIVE_PORT2, 1);
		Elevator E3 = new Elevator(3, Floors, RECEIVE_PORT3, 1);
		Elevator E4 = new Elevator(4, Floors, RECEIVE_PORT4, 5);
		Elevator[] elevators = { E1, E2, E3, E4 };

		for (Elevator E : elevators) { // start all elevator
			try {
				E.start();
			} catch (Exception e5) {
				// TODO: handle exception
				e5.printStackTrace();
			}

		}
		
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			System.out.println("Error in receiveSocketPort creation \n");
			se.printStackTrace();
			System.exit(1);
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

	// ------------------------------------------------------------------------------------------------------//

	private static final String INPUT_PATH = "src/ElevatorInputEvents.txt";
	private static int currentLine = 0;
	private static boolean moreToRead;
	
	private static DatagramPacket sendPacket; /* Packet */
	private static DatagramSocket sendReceiveSocket; 

	public synchronized static void ElevatorInputRead() {

		// get text file path
		Path path = Paths.get(INPUT_PATH);

		ArrayList<String> inputArrayList = new ArrayList<String>();
		moreToRead = true;

		// iterate through the file and read each line
		while (moreToRead) {
			try (Stream<String> lines = Files.lines(path)) {
				try {
					inputArrayList.add(lines.skip(currentLine).findFirst().get());
				} catch (NoSuchElementException e) {
					moreToRead = false;
					lines.close();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentLine++;
		}
		Pair pair = null;
				
		for (int i = 1; i < inputArrayList.size(); i++) {
			String inputElevatorEvent = inputArrayList.get(i);
			String[] inputEvents = inputElevatorEvent.split(" ");
			
			pair = new Pair(inputEvents[0] ,Integer.parseInt(inputEvents[1]), Integer.parseInt(inputEvents[2]) );	
			
		}

		///notifyAll();
		
		sendPacketToScheduler(pair);
		return;

	}

	private static final int BYTE_SIZE = 6400;

	private static byte[] PairToByteArray(Pair pair) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(BYTE_SIZE);

		ObjectOutputStream oos = null;

		try {
			oos = new ObjectOutputStream(baos);
		} catch (IOException e1) {
			// Unable to create object output stream
			e1.printStackTrace();
		}

		try {
			oos.writeObject(pair);
		} catch (IOException e) {
			// Unable to write eventList in bytes
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	
	
	public static void sendPacketToScheduler(Pair pair) {
		
		
		
		sendPacket = packetCreator(pair);
		packetSend(sendPacket);
		

	}
	
	/**
	 * @param packet
	 */
	public static void packetSend(DatagramPacket packet) {
		try {
			sendReceiveSocket.send(packet);
			
		} catch (IOException e) {
			System.out.print("Packet Sending Error, Retrying \n");
			e.printStackTrace();
			packetSend(packet);
			//System.exit(1);
		}

		
	}
	/**
	 * @author Muhammad Tarequzzaman
	 * @param pair
	 * @return 
	 * @Description: Create Datagram packet containing byte array of event list
	 *               information
	 */
	public static DatagramPacket packetCreator(Pair pair) {
		// Create Datagram packet containing byte array of event list information
		byte[] byteArr = PairToByteArray(pair);

		try { 
			sendPacket = new DatagramPacket(byteArr, byteArr.length, InetAddress.getLocalHost(),MAINTENANCE_PORT);
		} catch (UnknownHostException e) {
			System.out.print("sendPacket creation Error, Retrying creation \n");
			e.printStackTrace();
			packetCreator(pair);
			//System.exit(1);
		}
		return sendPacket;
	}
}