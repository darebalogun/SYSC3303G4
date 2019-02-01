Project Group 4

Member: 

	1. Muhammad Tarequzzaman | 100954008 
	2. Sama Adil Sheikh | 101060020
	3. Anannya Bhatia | 100989250
	4.
	5.



READ.ME

1.FloorSubSystem.java
The purpose of this class is to send and receive packets from the Scheduler.java, which are done with the use
of Datagram Sockets. It reads an ArrayList of input events and converts it to an array byte which is later
sent to the Scheduler.java as a DatagramPacket. This class also checks to see if the elevator is present
on the floor in question.

2.Scheduler.java
The purpose of this class is to update the position of the elevator and to send this update to the
ElevatorSubSystem and FloorSubSystem class. The scheduler accepts input from the InputEvent.java and sends
these commands to their related classes. The Scheduler is also updated when an Elevator reaches a floor by the
FloorSubSystem class.

3.ElevatorSubSystem.java
The purpose of this class is to represent the Elevator Car as a unit. It sends and receives packets from
the Scheduler.java. It's functional features include opening/closing the door, buttons and lamps for floors, 
time delay between the opening/closing of the door and time delay inbetween floors. It also gets updates for
next floor from the Schedular.java
	
	
