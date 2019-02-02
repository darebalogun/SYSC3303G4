Project Group 4

Member: 

	1. Muhammad Tarequzzaman | 100954008 
	2. Sama Adil Sheikh | 101060020
	3. Anannya Bhatia | 100989250
	4.
	5.


Included files and setup instructions are as under:-

FloorSubsystem.java:-

-> This file contains the FloorSubsystem class
-> The purpose of this class is to send and receive packets from the Scheduler
-> Communication between FloorSubsystem and Scheduler is done through UDP communication using Datagram Sockets and Datagram Packets
-> FloorSubsystem converts an ArrayList of input events into an array byte which is then sent to the Scheduler as a DatagramPacket. 
-> FloorSubsystem also checks to see if the elevator is already present on the floor in operation
-> The main method inside FloorSubsystem class when run, reads the requests from the InputEvents file

Scheduler.java:-

-> This file contains the Scheduler class
-> The purpose of this class is to receive the information/requests from the FloorSubsystem and send them to ElevatorSubSystem
   and send response back to the FloorSubsystem
-> The scheduler accepts inputs from the InputEvent class and send the requests to ElevatorSubSystem. 
-> The Scheduler is also updated when an Elevator reaches it's desired floor

ElevatorSubSystem.java:-

-> This file contains the ElevatorSubSystem class
-> The purpose of this class is to represent the Elevator Car as a unit
-> It receives and send Datagram packets to the Scheduler using UDP communication
-> It's functional features include opening/closing the door, buttons and lamps for floors, 
   time delay between the opening/closing of the door and time delay in between floors.
-> It also gets updates for next floor from the Schedular

InputEvent.java:-

-> This file contains the InputEvent class
-> Reads inputs from the InputEvents test file
-> Contains information about the current floor, destination floor, time between floors and direction of the elevator
InputEvents.txt:-

-> Contains test cases/requests from the FloorSubsystem for the ElevatorSubSystem 

UML_DIAGRAM:-

-> Contains the UML class diagrams for the system

STATE_MACHINE_DIAGRAM:-

-> Contains the State diagram for the system

SetUp Instructions:-

-> Open eclipse-java
-> Make new project and load all the files described above in the created project
-> Run ElevatorSubSysetm::main
-> Open new console
-> Run Scheduler:: main
-> Open new Console
-> Run FloorSubsystem
-> Open different consoles to check the output


































	
