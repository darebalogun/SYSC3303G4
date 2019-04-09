Project Group 4
L5G4_milestone_Final_Project


Members:-

	1. Muhammad Tarequzzaman | 100954008
	Responsiblities: ElevatorSubSystem.java, Elevator.java, ElevatorTest.java and ElevatorSubSystemTest.java., InputEvent.txt, 
						ElevatorInputEvent.txt
	2. Sama Adil Sheikh | 101060020
	Responsiblities: Timing Diagrams, Readme.txt, co-author of FloorSubSystem.java and Java doc comments.
	3. Anannya Bhatia | 100989250
	Responsibilites: UDP communication for FloorSubSystem.java, Scheduler.java and ElevatorSubSystem, UML Diagrams, timing analysis.
	4. Dare Balogun | 101062340
	Responsiblilities: FloorSubSystem.java, InputEvents.java, Pair.java, ElevatorState.java, ElevatorInputEvent.txt, Scheduler.java and co-author of methods 
						(PairToByteArray, byteArrayToList and sendArrivalInfo)  in Elevator.
	5. Mrunal Patel | 101001748
	Responsibilities: Worked on on UML Diagrams and Scheduler.java, ElevaotorSubSystem.java, Elevator.java, ElevatorInputEvent.txt, Readme.txt.


SetUp Instructions:-

	-> Open eclipse-java
	-> Click File->Open Projects from File System->Archive then select the zip file "L5G4_milestone_3.zip"
	-> Click Finish
	-> Run ElevatorSubSystem as Java Application
	-> Wait for the class to execute fully
	-> Open new console
	-> Run Scheduler as Java Application
	-> Wait for the class to execute fully
	-> Open new Console
	-> Run FloorSubsystem as Java Application
	-> wait for the elevator to reach there desired floor
	-> press the buttons in elevators to direct it to the desired floor
	-> Please see the Video on "SYSC3303G4/Demo Video/" for the demo.

FloorSubsystem.java:-

	-> This file contains the FloorSubsystem class
	-> The purpose of this class is to send and receive packets from the Scheduler
	-> Communication between FloorSubsystem and Scheduler is done through UDP communication using Datagram Sockets and Datagram Packets
	-> FloorSubsystem converts an ArrayList of input events into an array byte which is then sent to the Scheduler as a DatagramPacket. 
	-> FloorSubsystem also checks to see if the elevator is already present on the floor in operation
	-> The main method inside FloorSubsystem class when run, reads the requests from the InputEvents file
	-> By default the FloorSubsystem creates 5 floors
	-> FloorSubSystem adds 5 random events with random timing between 0 to 10 seconds

Scheduler.java:-

	-> This file contains the Scheduler class
	-> The purpose of this class is to receive the information/requests from the FloorSubsystem and send them to ElevatorSubSystem
	   and send response back to the FloorSubsystem
	-> The scheduler accepts inputs from the InputEvent class and send the requests to ElevatorSubSystem. 
	-> The Scheduler is also updated when an Elevator reaches it's desired floor

ElevatorSubSystem.java:-

	-> This file contains the ElevatorSubSystem class
	-> The purpose of this class is run multiple Elevator's as a multiple thread unit
	
Elevator.java:-

	-> It receives and send Datagram packets to the Scheduler using UDP communication
	-> Runs as an individual thread for each instance and receive information from schedulers. 	
	-> It's functional features include opening/closing the door, buttons and lamps for floors,
		time delay between the opening/closing of the door and time delay in between floors.
	-> TearDown after test 

ElevatorSubSystemTest.java:-
	
	-> This file contains the ElevatorSubSystemTest class 
	-> Contains the JUnit tests for the system
	-> It tests if the ElevatorSubsystem constructor creates the appropriate object

ElevatorState.java:-

	-> This file contains the states of the elevator
	-> Used as a data structure

ElevatorTest.java:-

	-> A JUnit test for Elevator.java 
	-> It tests precondition for elevator's multiple successful instance 
	-> Tests for all precondition to initiate thread 
	-> TearDown after test  
	
ElevatorButtons.java:-
	
	-> This class contains GUI imlementation for the buttons
	-> The buttons are located inside the elevator
	
InputEvent.java:-

	-> This file contains the InputEvent class which is used as a data structure to pass information from the FloorSubsystem to the Scheduler
	-> Reads inputs from the InputEvents test file
	-> Contains information about the current floor, destination floor, time between floors and direction of the elevator

Pair.java:-
	
	-> This file contains the Pair class which is used as a data structure to send arrival information from the ElevatorSubsystem to the Scheduler
	-> and back to the FloorSubsystem. The Pair class functions similarly to a tuple however it only takes one string argument and one integer


InputEvents.txt:-

	-> Contains test cases/requests from the FloorSubsystem for the ElevatorSubSystem 
	-> Change this file to change test cases in the system

SYSC3303_FinalProjectReport:-

	-> Contains final report for the ElevatorSubsystem design project



































	
