Project Group 4

Member: 

	1. Muhammad Tarequzzaman | 100954008 
	2. Sama Adil Sheikh | 101060020
	3. Anannya Bhatia | 100989250
	4.
	5.


Objective of Iteration:
 
	Work Products for Iteration #0:
		* None your first submission is Iteration #1 (below) which includes Iteration #0.
	
	Iteration 1 :“ Adding the Scheduler and Elevator Subsystems.
		The goal of this iteration is to add the state machines for the scheduler and elevator subsystems assuming that
		there is only one elevator. However, you should bear in mind that for the next iteration, your system is
		expected to coordinate between the elevators in order to maximize the number of passengers carried over time
		(i.e., the throughput). Note that the floor subsystem is used to notify the scheduler that an elevator has reached
		a floor, so that once an elevator has been told to move, the floor subsystem also has to be informed so that it
		can send out messages back to the scheduler to denote the arrival by an elevator. You can either maintain a
		single event list or have separate tasks for each elevator. Perhaps you can think of another way of doing it too.
	
	Work Products for Iteration #1:
		* "README.txt" file explaining the names of your files, set up instructions, etc.
		* Breakdown of responsibilities of each team member for this iteration
		* UML class diagram
		* State machine diagram for the scheduler and elevator subsystems.
		* Detailed set up and test instructions, including test files used
		* Code (.java files, all required Eclipse files, etc.)

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
	
	
