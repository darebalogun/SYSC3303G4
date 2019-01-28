
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class OLD_ElevatorSubsystem 

{
	
	public ArrayList<ElevatorSubSystem> ElevatorSubSystems;
	//end 
	
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;
	
	public OLD_ElevatorSubsystem()
	{
		try
		{
			receiveSocket = new DatagramSocket(69);
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
		
		
		
		
	}
	
	
	public void receiveAndSendToScheduler()
	{
		
		//we have to add here that we receiving from scheduler and what we sending to scheduler
		
		byte[] data = null;
		receivePacket = new DatagramPacket(data, data.length);
		
		  try
		  {
			  receiveSocket.receive(receivePacket);								//receiving packets from the host
	      } 
	       catch (IOException e) 
	        {
	    	   e.printStackTrace();
	           System.exit(1);
	        }
		  
		  
		  
		  try
		  {
			  sendSocket = new DatagramSocket();
		  }
		  catch(SocketException se)
		  {
			  se.printStackTrace();
			  System.exit(1);;
		  }
		  
		  try
		  {
			  sendSocket.send(sendPacket);
		  }
		  catch(IOException e)
		  {
			  e.printStackTrace();
			  System.exit(1);
		  }
		  
		  sendSocket.close();
		  
		  
	}
	
	
	
	
	public static void main(String[] args) 
	
	{
		OLD_ElevatorSubsystem s = new OLD_ElevatorSubsystem();
		s.receiveAndSendToScheduler();

	}

}
