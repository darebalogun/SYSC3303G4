Project Group 4
L5G4_milestone_1


Member: 

	1. Muhammad Tarequzzaman | 100954008
	Responsiblities: ElevatorSubSystem.java, Elevator.java, ElevatorTest.java and ElevatorSubSystemTest.java.
	2. Sama Adil Sheikh | 101060020
	Responsiblities: Readme.txt, co-author of FloorSubSystem.java and Java doc comments.
	3. Anannya Bhatia | 100989250
	Responsibilites: UDP communication for FloorSubSystem.java, Scheduler.java and ElevatorSubSystem, along with UML Diagrams.
	4. Dare Balogun | 101062340
	Responsiblilities: FloorSubSystem.java, InputEvents.java, Pair.java, Scheduler.java and co-author of ElevatorSubSystem.
	5. Mrunal Patel | 101001748
	Responsibilities: State Machine Diagram


SetUp Instructions:-

	-> Open eclipse-java
	-> Click File->Open Projects from File System->Archive then select the zip file "L5G4_milestone_2.zip"
	-> Click Finish
	-> Run ElevatorSubSystem as Java Application
	-> Wait for the class to execute
	-> Open new console
	-> Run Scheduler as Java Application
	-> Wait for the class to execute fully
	-> Open new Console
	-> Run FloorSubsystem as Java Application
	-> Open different consoles to check the output.
	

FloorSubsystem.java:-

	-> This file contains the FloorSubsystem class
	-> The purpose of this class is to send and receive packets from the Scheduler
	-> Communication between FloorSubsystem and Scheduler is done through UDP communication using Datagram Sockets and Datagram Packets
	-> FloorSubsystem converts an ArrayList of input events into an array byte which is then sent to the Scheduler as a DatagramPacket. 
	-> FloorSubsystem also checks to see if the elevator is already present on the floor in operation
	-> The main method inside FloorSubsystem class when run, reads the requests from the InputEvents file
	-> By default the FloorSubsystem creates 5 floors

Scheduler.java:-

	-> This file contains the Scheduler class
	-> The purpose of this class is to receive the information/requests from the FloorSubsystem and send them to ElevatorSubSystem
	   and send response back to the FloorSubsystem
	-> The scheduler accepts inputs from the InputEvent class and send the requests to ElevatorSubSystem. 
	-> The Scheduler is also updated when an Elevator reaches it's desired floor

ElevatorSubSystem.java:

	-> This file contains the ElevatorSubSystem class
	-> The purpose of this class is to represent the Elevator Car as a unit
	-> It receives and send Datagram packets to the Scheduler using UDP communication
	-> It's functional features include opening/closing the door, buttons and lamps for floors, 
	   time delay between the opening/closing of the door and time delay in between floors.
	-> It also gets updates for next floor from the Scheduler

ElevatorSubSystemTest.java:
	
	-> This file contains the ElevatorSubSystemTest class 
	-> Contains the Junit tests for the system
	-> It tests if the ElevatorSubsystem constructor creates the appropriate object

InputEvent.java:-

	-> This file contains the InputEvent class which is used as a data structure to pass information from the FloorSubsystem to the Scheduler
	-> Reads inputs from the InputEvents test file
	-> Contains information about the current floor, destination floor, time between floors and direction of the elevator

Pair.java
	
	-> This file contains the Pair class which is used as a data structure to send arrival information from the ElevatorSubsystem to the Scheduler
	-> and back to the FloorSubsystem. The Pair class functions similarly to a tuple however it only takes one string argument and one integer


InputEvents.txt:-

	-> Contains test cases/requests from the FloorSubsystem for the ElevatorSubSystem 
	-> Change this file to change test cases in the system

UML_DIAGRAM:-

	-> Contains the UML class diagrams for the system

SEQUENCE_DIAGRAM:-

	-> Contains the sequence diagram for the system




































	
