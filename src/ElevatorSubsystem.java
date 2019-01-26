
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ElevatorSubsystem 

{

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;
	
	public ElevatorSubsystem()
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
		
		  try
		  {
			  receiveSocket.receive(receivePacket);								//receiving packets from the host
	       } 
	       catch (IOException e) 
	        {
	    	   e.printStackTrace();
	            System.exit(1);
	         }

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static void main(String[] args) 
	
	{
		

	}

}
