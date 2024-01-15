package MIPSSimulator;

import Memory.*;
import ReservationStation.ReservationStation;
import RegisterFile.Register;
import RegisterFile.RegisterInt;
import Buffers.LoadBuffer;
import Buffers.StoreBuffer;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import javax.sound.midi.SysexMessage;

import Parsing.Parser;

public class TomasuloCPU {

	private ReservationStation[] addReservationStations;
	private ReservationStation[] multReservationStations;
	private Register[] registerFile;
	private RegisterInt[] registerFileInt;
	private LoadBuffer[] loadBuffers;
	private StoreBuffer[] storeBuffers;
	private Cache cache;
	private InstructionMemory instructionMemory;
	private static Parser parser;

	private int addLatency;
	private int subLatency;
	private int mulLatency;
	private int divLatency;
	private int addiLatency;
	private int subiLatency;
	private int ldLatency;
	private int sdLatency;
	private int bnezLatency;
	private int clock = 1;
	private int PC = 0;

	private boolean write=true;
	private boolean lastBNEZ = false;
	private boolean flag = false;

	public TomasuloCPU() {
		// Get user input for instruction latencies
		getUserInputForLatencies();

		// Get user input for sizes of stations and buffers
		int numAddReservationStations = getUserInputForSize("Enter the number of ADD reservation stations: ");
		int numMultReservationStations = getUserInputForSize("Enter the number of MULT reservation stations: ");
		int numLoadBuffers = getUserInputForSize("Enter the number of LOAD buffers: ");
		int numStoreBuffers = getUserInputForSize("Enter the number of STORE buffers: ");
		initializeRegisterFile();
		initializeRegisterFileInt();
		initializeCache();

		// Initialize reservation stations for addition and multiplication
		addReservationStations = new ReservationStation[numAddReservationStations];
		for (int i = 0; i < numAddReservationStations; i++) {
			addReservationStations[i] = new ReservationStation("A" + i, 0, "", "", "", "", "", "", 0, 0, 0, 0, 0, 0, 0,
					false, false, false, false, false, false, false);
		}

		multReservationStations = new ReservationStation[numMultReservationStations];
		for (int i = 0; i < numMultReservationStations; i++) {
			multReservationStations[i] = new ReservationStation("M" + i, 0, "", "", "", "", "", "", 0, 0, 0, 0, 0, 0, 0,
					false, false, false, false, false, false, false);
		}

		// Initialize load buffers
		loadBuffers = new LoadBuffer[numLoadBuffers];
		for (int i = 0; i < numLoadBuffers; i++) {
			loadBuffers[i] = new LoadBuffer("", 0, 0, 0, false);
		}

		// Initialize store buffers
		storeBuffers = new StoreBuffer[numStoreBuffers];
		for (int i = 0; i < numStoreBuffers; i++) {
			storeBuffers[i] = new StoreBuffer("", 0, 0, "", 0, 0, false);
		}
		startSimulation();
	}

	private void initializeRegisterFile() {
		registerFile = new Register[32];
		Scanner scanner = new Scanner(System.in);

		System.out.print(
				"If you want to load the registers in the regi2ster file, Enter 1,if not, Enter 2 (It will be loaded with some values) : ");
		int userChoiceInput = scanner.nextInt();

		if (userChoiceInput == 1) {
			System.out.println("Now you will load the content of each register block sequentially.");
			System.out.println(
					"If you finished loading at some point, don't type the content and press enter. Now u will start loading:");
			int i = 0;
			String userInput2 = scanner.nextLine().trim();

			while (true) {
				System.out.print("Content of Register block " + i + ": ");
				userInput2 = scanner.nextLine().trim();
				if (userInput2.isEmpty())
					break;
				float content = Float.parseFloat(userInput2);
				String name = "F" + i;
				Register reg = new Register(name, "0", content);
				registerFile[i] = reg;
				i++;
				if (i == 32)
					break;
			}
			if (i < 32) {
				for (int j = i; j < 32; j++) {
					Register reg = new Register("F" + j, "0", 0);
					registerFile[j] = reg;
				}
			}
		} else if (userChoiceInput == 2) {
			System.out.println("Register File is loaded with the default values");
			for (int j = 0; j < 32; j++) {
				Register reg = new Register("F" + j, "0", 0);
				registerFile[j] = reg;
			}
		} else {
			System.out.println("Invalid input, please type 1 or 2 according to your choice ");
			initializeRegisterFile();
		}

	}

	private void initializeRegisterFileInt() {
		registerFileInt = new RegisterInt[32];
		Scanner scanner = new Scanner(System.in);

		System.out.print(
				"If you want to load the integer registers in the register file, Enter 1,if not, Enter 2 (It will be loaded with some values) : ");
		int userChoiceInput = scanner.nextInt();

		if (userChoiceInput == 1) {
			System.out.println("Now you will load the content of each register block sequentially.");
			System.out.println(
					"If you finished loading at some point, don't type the content and press enter. Now u will start loading:");
			int i = 0;
			String userInput2 = scanner.nextLine().trim();

			while (true) {
				System.out.print("Content of Register block " + i + ": ");
				userInput2 = scanner.nextLine().trim();
				if (userInput2.isEmpty())
					break;
				int content = Integer.parseInt(userInput2);
				String name = "R" + i;
				RegisterInt reg = new RegisterInt(name, "0", content);
				registerFileInt[i] = reg;
				i++;
				if (i == 32)
					break;
			}
			if (i < 32) {
				for (int j = i; j < 32; j++) {
					RegisterInt reg = new RegisterInt("R" + j, "0", 0);
					registerFileInt[j] = reg;
				}
			}
		} else if (userChoiceInput == 2) {
			System.out.println("Register File is loaded with the default values");
			for (int j = 0; j < 32; j++) {
				RegisterInt reg = new RegisterInt("R" + j, "0", 0);
				registerFileInt[j] = reg;
			}
		} else {
			System.out.println("Invalid input, please type 1 or 2 according to your choice ");
			initializeRegisterFileInt();
		}

	}

	public void initializeCache() {
		cache = new Cache();
		Scanner scanner = new Scanner(System.in);

		System.out.print(
				"If you want to load the cache, Enter 1, if not, Enter 2 (It will be loaded with some values): ");
		int userChoiceInput = scanner.nextInt();

		if (userChoiceInput == 1) {
			System.out.println("Now you will load the content of each cache block sequentially.");
			System.out.println("If you finished loading at some point, don't type the content and press enter.");
			System.out.println("Now u will start loading:");

			int i = 0;
			String userInput = scanner.nextLine().trim();
			while (true) {
				System.out.print("Content of Cache Block " + i + ": ");
				userInput = scanner.nextLine().trim();
				if (userInput.isEmpty()) {
					break;
				}

				float content = Float.parseFloat(userInput);

				cache.getCache()[i] = content;

				i++;
				if (i == cache.getCache().length) {
					break;
				}
			}

			if (i < cache.getCache().length) {
				for (int j = i; j < cache.getCache().length; j++) {
					cache.getCache()[j] = 0; // You can set a default value here.
				}
			}
		} else if (userChoiceInput == 2) {
			System.out.println("Cache is loaded with the default values" + "\n");
			for (int j = 0; j < cache.getCache().length; j++) {
				cache.getCache()[j] = 0; // You can set a default value here.
			}
		} else {
			System.out.println("Invalid input. Please type 1 or 2 according to your choice." + "\n");
			initializeCache(); // Recursive call to handle invalid input
		}
		System.out.println("Executing...");

	}

	public boolean end() {
		boolean end = true;
		for (int i = 0; i < addReservationStations.length; i++) {
			if (addReservationStations[i].getBusy() == 1)
				end = false;
		}
		for (int i = 0; i < multReservationStations.length; i++) {
			if (multReservationStations[i].getBusy() == 1)
				end = false;
		}
		for (int i = 0; i < loadBuffers.length; i++) {
			if (loadBuffers[i].getBusy() == 1)
				end = false;
		}
		for (int i = 0; i < storeBuffers.length; i++) {
			if (storeBuffers[i].getBusy() == 1)
				end = false;
		}
		return end;
	}

	public void startSimulation() {

		while (true) {
			if (clock == 1) {
				String[] instruction = fetch();
				boolean issue = issue(instruction);
				if (issue) {
					System.out.println("--------------------clock" + clock + "--------------------");
					System.out.println("stall because issue");
					clock++;
				} else {
					loadreservation(instruction);
				}
			} else {
//				int numinstruction = InstructionMemory.getInstance().getInstructionMemory().length - 1;
//				String[] last = InstructionMemory.getInstance().getInstructionMemory()[numinstruction];
//				if (PC == numinstruction && last[0].compareTo("BNEZ") != 0) {
//					break;
//				}

//				if(lastBNEZ) {
//					String[] instruction = fetch();
//					boolean issue = issue(instruction);
//					if (issue) {
//						System.out.println("--------------------clock" + clock + "--------------------");
//						System.out.println("stall because issue");
//						clock++;
//					} else {
//						loadreservation(instruction);
//					}
//				}

				simulateExecution();
				System.out.println(
						"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA :::::::" + PC);

				String[] instruction = fetch();
				if(instruction!=null)
				System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII :::::::"
						+ instruction[0]);

				if (instruction == null && end()) {
					System.out.println("--------------------clock " + clock + "--------------------");
					System.out.println("fetch instruction number" + (clock + 1));
					System.out.println("name     " + "busy   " + "op      " + "Vj    " + "Vk    " + "Qj   " + "Qk    "
							+ "A" + "\n");
					for (int i = 0; i < addReservationStations.length; i++) {
						addReservationStations[i].printReservation();
					}
					System.out.println("---------------------mul reservation---------------");
					for (int i = 0; i < multReservationStations.length; i++) {
						multReservationStations[i].printReservation();
					}
					System.out.println("---------------------load buffer--------------------");
					System.out.println("Name    " + ", Busy     " + ", Address     ");
					for (int i = 0; i < loadBuffers.length; i++) {
						loadBuffers[i].printLoadBuffer();
					}
					System.out.println("----------------------store buffer-------------------");
					System.out.println("Name    " + ", Busy    " + ", Address    " + ", Q    " + ", V    ");
					for (int i = 0; i < storeBuffers.length; i++) {

						storeBuffers[i].printStoreBuffer();
					}
					System.out.println("----------------------float register file-------------");
					System.out.println("Name     " + ", Qi     " + ", Content: ");
					for (int i = 0; i < registerFile.length; i++) {

						registerFile[i].printRegister();
					}
					System.out.println("----------------------integer register file-------------");
					System.out.println("Name     " + ", Qi     " + ", Content: ");
					for (int i = 0; i < registerFileInt.length; i++) {

						registerFileInt[i].printRegister();
					}
					System.out.println("----------------------cache---------------------------");
					for (int i = 0; i < cache.getCache().length; i++) {
						System.out.println("R" + i + " :   " + cache.getCache()[i]);
					}
					break;
				}

				if (instruction != null) {
					boolean issue = issue(instruction);
					if (issue) {
						System.out.println("--------------------clock" + clock + "--------------------");
						System.out.println("stall because issue");
						clock++;
					} else {
						loadreservation(instruction);
					}
				}
			}
			System.out.println("--------------------clock " + clock + "--------------------");
			System.out.println("fetch instruction number" + (clock + 1));
			System.out.println(
					"name     " + "busy   " + "op      " + "Vj    " + "Vk    " + "Qj   " + "Qk    " + "A" + "\n");
			for (int i = 0; i < addReservationStations.length; i++) {
				addReservationStations[i].printReservation();
			}
			System.out.println("---------------------mul reservation---------------");
			for (int i = 0; i < multReservationStations.length; i++) {
				multReservationStations[i].printReservation();
			}
			System.out.println("---------------------load buffer--------------------");
			System.out.println("Name    " + ", Busy     " + ", Address     ");
			for (int i = 0; i < loadBuffers.length; i++) {
				loadBuffers[i].printLoadBuffer();
			}
			System.out.println("----------------------store buffer-------------------");
			System.out.println("Name    " + ", Busy    " + ", Address    " + ", Q    " + ", V    ");
			for (int i = 0; i < storeBuffers.length; i++) {

				storeBuffers[i].printStoreBuffer();
			}
			System.out.println("----------------------float register file-------------");
			System.out.println("Name     " + ", Qi     " + ", Content: ");
			for (int i = 0; i < registerFile.length; i++) {
				registerFile[i].printRegister();
			}
			System.out.println("----------------------integer register file-------------");
			System.out.println("Name     " + ", Qi     " + ", Content: ");
			for (int i = 0; i < registerFileInt.length; i++) {

				registerFileInt[i].printRegister();
			}
			System.out.println("----------------------cache---------------------------");
			for (int i = 0; i < cache.getCache().length; i++) {
				System.out.println("R" + i + " :   " + cache.getCache()[i]);
			}
			clock++;
		}

	}

	public String[] fetch() {
		return InstructionMemory.getInstance().getInstructionMemory()[PC++];
	}

	public boolean issue(String[] instruction) {
		String func = instruction[0];
		boolean res = true;
		switch (func) {
		case "ADD.D": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					res = false;
				}

			}
			break;
		}
		case "SUB.D": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					res = false;
				}

			}
			break;
		}
		case "MUL.D": {
			for (int i = 0; i < multReservationStations.length; i++) {
				if (multReservationStations[i].getBusy() == 0) {
					res = false;
				}

			}
			break;
		}
		case "DIV.D": {
			for (int i = 0; i < multReservationStations.length; i++) {
				if (multReservationStations[i].getBusy() == 0) {
					res = false;
				}

			}
			break;
		}
		case "ADDI": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					res = false;
				}
			}
			break;
		}
		case "SUBI": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					res = false;
				}

			}
			break;
		}
		case "L.D": {
			for (int i = 0; i < loadBuffers.length; i++) {
				if (loadBuffers[i].getBusy() == 0) {
					res = false;
				}

			}
			break;
		}
		case "S.D": {
			for (int i = 0; i < storeBuffers.length; i++) {
				if (storeBuffers[i].getBusy() == 0) {
					res = false;
				}

			}
			break;
		}
		case "BNEZ": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					res = false;
				}
			}
			break;
		}

		default:

			break;
		}
		return res;

	}

	public void loadreservation(String[] instruction) {
		String func = instruction[0];

		boolean f;
		switch (func) {
		case "ADD.D": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					String registers = instruction[1];
					System.out.println(registers);
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					System.out.println(numbersArray[0]);
					System.out.println(numbersArray[1]);
					System.out.println(numbersArray[2]);
					int dst = Integer.parseInt(numbersArray[0]);
					int src = Integer.parseInt(numbersArray[1]);
					int tmp = Integer.parseInt(numbersArray[2]);
					addReservationStations[i].setName("A" + i);
					addReservationStations[i].setBusy(1);
					addReservationStations[i].setOp("ADD.D");
					if (f) {
						String vj = Float.toString(registerFile[src].getContent());
						String vk = Float.toString(registerFile[tmp].getContent());
						addReservationStations[i].setQj(registerFile[src].getQi());
						addReservationStations[i].setQk(registerFile[tmp].getQi());
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(vk);
						registerFile[dst].setQi("A" + i);
					} else {
						String vj = Float.toString(registerFileInt[src].getContent());
						String vk = Float.toString(registerFileInt[tmp].getContent());
						addReservationStations[i].setQj(registerFileInt[src].getQi());
						addReservationStations[i].setQk(registerFileInt[tmp].getQi());
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(vk);
						registerFileInt[dst].setQi("A" + i);
					}

					break;
				}

			}
			break;
		}
		case "SUB.D": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					String registers = instruction[1];
					System.out.println(registers);
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					System.out.println(numbersArray[0]);
					System.out.println(numbersArray[1]);
					int dst = Integer.parseInt(numbersArray[0]);
					int src = Integer.parseInt(numbersArray[1]);
					int tmp = Integer.parseInt(numbersArray[2]);
					addReservationStations[i].setName("A" + i);
					addReservationStations[i].setBusy(1);
					addReservationStations[i].setOp("SUB.D");
					if (f) {
						String vj = Float.toString(registerFile[src].getContent());
						String vk = Float.toString(registerFile[tmp].getContent());
						addReservationStations[i].setQj(registerFile[src].getQi());
						addReservationStations[i].setQk(registerFile[tmp].getQi());
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(vk);
						registerFile[dst].setQi("A" + i);
					} else {
						String vj = Float.toString(registerFileInt[src].getContent());
						String vk = Float.toString(registerFileInt[tmp].getContent());
						addReservationStations[i].setQj(registerFileInt[src].getQi());
						addReservationStations[i].setQk(registerFileInt[tmp].getQi());
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(vk);
						registerFileInt[dst].setQi("A" + i);
					}

					break;
				}

			}
			break;
		}
		case "MUL.D": {
			for (int i = 0; i < multReservationStations.length; i++) {
				if (multReservationStations[i].getBusy() == 0) {
					String registers = instruction[1];
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					int dst = Integer.parseInt(numbersArray[0]);
					int src = Integer.parseInt(numbersArray[1]);
					int tmp = Integer.parseInt(numbersArray[2]);
					multReservationStations[i].setName("M" + i);
					multReservationStations[i].setBusy(1);
					multReservationStations[i].setOp("MUL");
					if (f) {
						String vj = Float.toString(registerFile[src].getContent());
						String vk = Float.toString(registerFile[tmp].getContent());
						multReservationStations[i].setQj(registerFile[src].getQi());
						multReservationStations[i].setQk(registerFile[tmp].getQi());
						multReservationStations[i].setVj(vj);
						multReservationStations[i].setVk(vk);
						registerFile[dst].setQi("M" + i);
					} else {
						String vj = Float.toString(registerFileInt[src].getContent());
						String vk = Float.toString(registerFileInt[tmp].getContent());
						multReservationStations[i].setQj(registerFileInt[src].getQi());
						multReservationStations[i].setQk(registerFileInt[tmp].getQi());
						multReservationStations[i].setVj(vj);
						multReservationStations[i].setVk(vk);
						registerFileInt[dst].setQi("M" + i);
					}

					break;
				}

			}
			break;
		}
		case "DIV.D": {
			for (int i = 0; i < multReservationStations.length; i++) {
				if (multReservationStations[i].getBusy() == 0) {
					String registers = instruction[1];
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					int dst = Integer.parseInt(numbersArray[0]);
					int src = Integer.parseInt(numbersArray[1]);
					int tmp = Integer.parseInt(numbersArray[2]);
					multReservationStations[i].setName("M" + i);
					multReservationStations[i].setBusy(1);
					multReservationStations[i].setOp("DIV");
					if (f) {
						String vj = Float.toString(registerFile[src].getContent());
						String vk = Float.toString(registerFile[tmp].getContent());
						multReservationStations[i].setQj(registerFile[src].getQi());
						multReservationStations[i].setQk(registerFile[tmp].getQi());
						multReservationStations[i].setVj(vj);
						multReservationStations[i].setVk(vk);
						registerFile[dst].setQi("M" + i);
					} else {
						String vj = Float.toString(registerFileInt[src].getContent());
						String vk = Float.toString(registerFileInt[tmp].getContent());
						multReservationStations[i].setQj(registerFileInt[src].getQi());
						multReservationStations[i].setQk(registerFileInt[tmp].getQi());
						multReservationStations[i].setVj(vj);
						multReservationStations[i].setVk(vk);
						registerFileInt[dst].setQi("M" + i);
					}

					break;
				}

			}
			break;
		}
		case "ADDI": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					String registers = instruction[1];
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					// System.out.println("erfwcfwc"+numbersArray[0]);
					System.out.println(numbersArray[0]);
					System.out.println(numbersArray[1]);
					int dst = Integer.parseInt(numbersArray[0]);
					int src = Integer.parseInt(numbersArray[1]);
					String tmp = numbersArray[2];
					addReservationStations[i].setName("A" + i);
					addReservationStations[i].setBusy(1);
					addReservationStations[i].setOp("ADDI");
					if (f) {
						String vj = Float.toString(registerFile[src].getContent());
						addReservationStations[i].setQj(registerFile[src].getQi());
						addReservationStations[i].setQk("0");
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(tmp);
						registerFile[dst].setQi("A" + i);
					} else {
						String vj = Float.toString(registerFileInt[src].getContent());
						addReservationStations[i].setQj(registerFileInt[src].getQi());
						addReservationStations[i].setQk("0");
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(tmp);
						registerFileInt[dst].setQi("A" + i);
					}

					break;
				}

			}
			break;
		}
		case "SUBI": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					String registers = instruction[1];
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					System.out.println(registers);
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					System.out.println(numbersArray[0]);
					System.out.println(numbersArray[1]);
					int dst = Integer.parseInt(numbersArray[0]);
					int src = Integer.parseInt(numbersArray[1]);
					String tmp = numbersArray[2];
					addReservationStations[i].setName("A" + i);
					addReservationStations[i].setBusy(1);
					addReservationStations[i].setOp("SUBI");
					if (f) {
						String vj = Float.toString(registerFile[src].getContent());
						addReservationStations[i].setQj(registerFile[src].getQi());
						addReservationStations[i].setQk("0");
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(tmp);
						registerFile[dst].setQi("A" + i);
					} else {
						String vj = Float.toString(registerFileInt[src].getContent());
						addReservationStations[i].setQj(registerFileInt[src].getQi());
						addReservationStations[i].setQk("0");
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(tmp);
						registerFileInt[dst].setQi("A" + i);
					}

					break;
				}

			}
			break;
		}
		case "L.D": {
			for (int i = 0; i < loadBuffers.length; i++) {
				if (loadBuffers[i].getBusy() == 0) {
					String registers = instruction[1];
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					int dst = Integer.parseInt(numbersArray[0]);
					int address = Integer.parseInt(numbersArray[1]);
					loadBuffers[i].setBusy(1);
					loadBuffers[i].setAddress(address);
					loadBuffers[i].setName("L" + i);
					if (f) {
						registerFile[dst].setQi("L" + i);
					} else {
						registerFileInt[dst].setQi("L" + i);
					}

					break;
				}

			}
			break;
		}
		case "S.D": {
			for (int i = 0; i < storeBuffers.length; i++) {
				if (storeBuffers[i].getBusy() == 0) {
					String registers = instruction[1];
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					int dst = Integer.parseInt(numbersArray[0]);
					int address = Integer.parseInt(numbersArray[1]);
					storeBuffers[i].setBusy(1);
					storeBuffers[i].setAddress(address);
					storeBuffers[i].setName("S" + i);
					if (f) {
						storeBuffers[i].setV(registerFile[dst].getContent());
						storeBuffers[i].setQ(registerFile[dst].getQi());
					} else {
						storeBuffers[i].setV(registerFileInt[dst].getContent());
						storeBuffers[i].setQ(registerFileInt[dst].getQi());
					}

					break;
				}
			}
			break;

		}
		case "BNEZ": {
			for (int i = 0; i < addReservationStations.length; i++) {
				if (addReservationStations[i].getBusy() == 0) {
					String registers = instruction[1];
					if (registers.startsWith("F")) {
						f = true;
					} else {
						f = false;
					}
					String[] numbersArray = registers.split("\\D+");

					// Remove the empty string at the beginning
					if (numbersArray.length > 0 && numbersArray[0].isEmpty()) {
						numbersArray = Arrays.copyOfRange(numbersArray, 1, numbersArray.length);
					}
					int src = Integer.parseInt(numbersArray[0]);
					addReservationStations[i].setName("A" + i);
					addReservationStations[i].setBusy(1);
					addReservationStations[i].setOp("BNEZ");
					if (f) {

					} else {
						String vj = Float.toString(registerFileInt[src].getContent());
						String vk = "";
						addReservationStations[i].setQj(registerFileInt[src].getQi());
						addReservationStations[i].setQk("0");
						addReservationStations[i].setVj(vj);
						addReservationStations[i].setVk(vk);

					}
					break;
				}
			}
			break;
		}
		default:

			break;
		}
	}

	private void simulateExecution() {
//		System.out.println("~~ Cycle : " + clock + " ~~" + "\n");
//		// printing content
//		System.out.println("Add reservation stations :" + "\n");
//		System.out
//				.println("name     " + "busy   " + "op      " + "Vj    " + "Vk    " + "Oj   " + "Qk    " + "A" + "\n");
//		for (int i = 0; i < addReservationStations.length; i++) {
//			System.out.println(addReservationStations[i].getName() + ", " + addReservationStations[i].getBusy() + ", "
//					+ addReservationStations[i].getOp() + ", " + addReservationStations[i].getVj() + ", "
//					+ addReservationStations[i].getVk() + ", " + addReservationStations[i].getQj() + ", "
//					+ addReservationStations[i].getQk() + ", " + addReservationStations[i].getA());
//
//		}
//
//		System.out.println("\nMUL reservation stations :" + "\n");
//		System.out.println("name     " + "busy   " + "op      " + "Vj   " + "Vk    " + "Oj   " + "Qk    " + "A" + "\n");
//		for (int i = 0; i < multReservationStations.length; i++) {
//			System.out.println(multReservationStations[i].getName() + ", " + multReservationStations[i].getBusy() + ", "
//					+ multReservationStations[i].getOp() + ", " + multReservationStations[i].getVj() + ", "
//					+ multReservationStations[i].getVk() + ", " + multReservationStations[i].getQj() + ", "
//					+ multReservationStations[i].getQk() + ", " + multReservationStations[i].getA());
//		}
//
//		System.out.println("\nLoad Buffers :" + "\n");
//		System.out.println("name     " + "busy   " + "Address         " + "\n");
//		for (int i = 0; i < loadBuffers.length; i++) {
//			System.out.println(
//					loadBuffers[i].getName() + ", " + loadBuffers[i].getBusy() + ", " + loadBuffers[i].getAddress());
//		}
//
//		System.out.println("\nStore Buffers :" + "\n");
//		System.out.println("name     " + "busy   " + "Address    " + "V    " + "Q    " + "\n");
//		for (int i = 0; i < storeBuffers.length; i++) {
//
//			System.out.println(storeBuffers[i].getName() + ", " + storeBuffers[i].getBusy() + ", "
//					+ storeBuffers[i].getAddress() + ", " + storeBuffers[i].getV() + ", " + storeBuffers[i].getQ());
//		}

		// Execution part
		// add Reservation Stations execution
		for (int i = 0; i < addReservationStations.length; i++) {
			float result = 0;
			String tag = addReservationStations[i].getName();
			String[][] resultAddresses = whoIsWaiting(tag);

			if (addReservationStations[i].getBusy() == 1 && addReservationStations[i].getQj().equals("0")
					&& addReservationStations[i].getQk().equals("0")) {

				switch (addReservationStations[i].getOp()) {

				case "ADD.D":

					int c = clock + addLatency;
					if(!write) {
						c++;
						
					}
					
					if (!addReservationStations[i].isAddDWaitF()) {
						addReservationStations[i].setAddDWait(c);
						addReservationStations[i].setAddDWaitF(true);
					}

					if (addReservationStations[i].getAddDWait() == clock) {
						result = Float.parseFloat(addReservationStations[i].getVj())
								+ Float.parseFloat(addReservationStations[i].getVk());
						for (int j = 0; j < resultAddresses[0].length; j++) {
							if (addReservationStations.length <= resultAddresses[0].length) {
								if (resultAddresses[0][j] != null) {
									int numOfAdd = Integer.parseInt(resultAddresses[0][j].substring(1, 2));
									if (addReservationStations[numOfAdd].getQj()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQj("0");
										addReservationStations[numOfAdd].setVj(result + "");
									}
									if (addReservationStations[numOfAdd].getQk()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQk("0");
										addReservationStations[numOfAdd].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[1].length; j++) {

							if (multReservationStations.length <= resultAddresses[1].length) {
								if (resultAddresses[1][j] != null) {

									int numOfMult = Integer.parseInt(resultAddresses[1][j].substring(1, 2));
									if (multReservationStations[numOfMult].getQj()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQj("0");
										multReservationStations[numOfMult].setVj(result + "");
									}
									if (multReservationStations[numOfMult].getQk()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQk("0");
										multReservationStations[numOfMult].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[2].length; j++) {
							if (storeBuffers.length <= resultAddresses[2].length) {
								if (resultAddresses[2][j] != null) {
									int numOfMult = Integer.parseInt(resultAddresses[2][j].substring(1, 2));
									storeBuffers[numOfMult].setQ("0");
									storeBuffers[numOfMult].setV(result);
								}
							}
						}

						for (int j = 0; j < resultAddresses[3].length; j++) {
							if (registerFile.length <= resultAddresses[3].length) {
								if (resultAddresses[3][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[3][j].substring(1));
									registerFile[numberOfReg].setContent(result);
									registerFile[numberOfReg].setQi("0");
								}
							}
						}
						for (int j = 0; j < resultAddresses[4].length; j++) {
							if (registerFileInt.length <= resultAddresses[4].length) {
								if (resultAddresses[4][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[4][j].substring(1));
									registerFileInt[numberOfReg].setContent((int) result);
									registerFileInt[numberOfReg].setQi("0");
								}
							}
						}
						addReservationStations[i].setBusy(0);
						addReservationStations[i].setOp("");
						addReservationStations[i].setVj("");
						addReservationStations[i].setVk("");
						addReservationStations[i].setQj("");
						addReservationStations[i].setQk("");
						addReservationStations[i].setA("");
						addReservationStations[i].setAddDWaitF(false);
						addReservationStations[i].setAddDWait(0);
					}
					break;
				case "SUB.D":

					if (!addReservationStations[i].isSubDWaitF()) {
						addReservationStations[i].setSubDWait(clock + subLatency);
						addReservationStations[i].setSubDWaitF(true);
					}
					if (addReservationStations[i].getSubDWait() == clock) {
						result = Float.parseFloat(addReservationStations[i].getVj())
								- Float.parseFloat(addReservationStations[i].getVk());
						for (int j = 0; j < resultAddresses[0].length; j++) {
							if (addReservationStations.length <= resultAddresses[0].length) {
								if (resultAddresses[0][j] != null) {
									int numOfAdd = Integer.parseInt(resultAddresses[0][j].substring(1, 2));
									if (addReservationStations[numOfAdd].getQj()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQj("0");
										addReservationStations[numOfAdd].setVj(result + "");
									}
									if (addReservationStations[numOfAdd].getQk()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQk("0");
										addReservationStations[numOfAdd].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[1].length; j++) {
							if (multReservationStations.length <= resultAddresses[1].length) {
								if (resultAddresses[1][j] != null) {
									int numOfMult = Integer.parseInt(resultAddresses[1][j].substring(1, 2));
									if (multReservationStations[numOfMult].getQj()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQj("0");
										multReservationStations[numOfMult].setVj(result + "");
									}
									if (multReservationStations[numOfMult].getQk()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQk("0");
										multReservationStations[numOfMult].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[2].length; j++) {
							if (storeBuffers.length <= resultAddresses[2].length) {
								if (resultAddresses[2][j] != null) {
									int numOfMult = Integer.parseInt(resultAddresses[2][j].substring(1, 2));
									storeBuffers[numOfMult].setQ("0");
									storeBuffers[numOfMult].setV(result);
								}
							}
						}

						for (int j = 0; j < resultAddresses[3].length; j++) {
							if (registerFile.length <= resultAddresses[3].length) {
								if (resultAddresses[3][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[3][j].substring(1, 2));
									registerFile[numberOfReg].setContent(result);
									registerFile[numberOfReg].setQi("0");
								}
							}
						}
						for (int j = 0; j < resultAddresses[4].length; j++) {
							if (registerFileInt.length <= resultAddresses[4].length) {
								if (resultAddresses[4][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[4][j].substring(1, 2));
									registerFileInt[numberOfReg].setContent((int) result);
									registerFileInt[numberOfReg].setQi("0");
								}
							}
						}
						addReservationStations[i].setBusy(0);
						addReservationStations[i].setOp("");
						addReservationStations[i].setVj("");
						addReservationStations[i].setVk("");
						addReservationStations[i].setQj("");
						addReservationStations[i].setQk("");
						addReservationStations[i].setA("");
						addReservationStations[i].setSubDWaitF(false);
						addReservationStations[i].setSubDWait(0);
					}
					break;
				case "ADDI":
					if (!addReservationStations[i].isAddIWaitF()) {
						addReservationStations[i].setAddIWait(clock + addiLatency);
						addReservationStations[i].setAddIWaitF(true);
					}
					if (addReservationStations[i].getAddIWait() == clock) {
						result = Float.parseFloat(addReservationStations[i].getVj())
								+ Float.parseFloat(addReservationStations[i].getVk());
						for (int j = 0; j < resultAddresses[0].length; j++) {
							if (addReservationStations.length <= resultAddresses[0].length) {
								if (resultAddresses[0][j] != null) {
									int numOfAdd = Integer.parseInt(resultAddresses[0][j].substring(1, 2));
									if (addReservationStations[numOfAdd].getQj()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQj("0");
										addReservationStations[numOfAdd].setVj(result + "");
									}
									if (addReservationStations[numOfAdd].getQk()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQk("0");
										addReservationStations[numOfAdd].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[1].length; j++) {
							if (multReservationStations.length <= resultAddresses[1].length) {
								if (resultAddresses[1][j] != null) {
									int numOfMult = Integer.parseInt(resultAddresses[1][j].substring(1, 2));
									if (multReservationStations[numOfMult].getQj()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQj("0");
										multReservationStations[numOfMult].setVj(result + "");
									}
									if (multReservationStations[numOfMult].getQk()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQk("0");
										multReservationStations[numOfMult].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[2].length; j++) {
							if (storeBuffers.length <= resultAddresses[2].length) {
								if (resultAddresses[2][j] != null) {
									int numOfMult = Integer.parseInt(resultAddresses[2][j].substring(1, 2));
									storeBuffers[numOfMult].setQ("0");
									storeBuffers[numOfMult].setV(result);
								}
							}
						}

						for (int j = 0; j < resultAddresses[3].length; j++) {
							if (registerFile.length <= resultAddresses[3].length) {
								if (resultAddresses[3][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[3][j].substring(1));
									registerFile[numberOfReg].setContent(result);
									registerFile[numberOfReg].setQi("0");
								}
							}
						}
						for (int j = 0; j < resultAddresses[4].length; j++) {
							if (registerFileInt.length <= resultAddresses[4].length) {
								if (resultAddresses[4][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[4][j].substring(1));
									registerFileInt[numberOfReg].setContent((int) result);
									registerFileInt[numberOfReg].setQi("0");
								}
							}
						}
						addReservationStations[i].setBusy(0);
						addReservationStations[i].setOp("");
						addReservationStations[i].setVj("");
						addReservationStations[i].setVk("");
						addReservationStations[i].setQj("");
						addReservationStations[i].setQk("");
						addReservationStations[i].setA("");
						addReservationStations[i].setAddIWaitF(false);
						addReservationStations[i].setAddIWait(0);
					}
					break;
				case "SUBI":
					if (!addReservationStations[i].isSubIWaitF()) {
						addReservationStations[i].setSubIWait(clock + subiLatency);
						addReservationStations[i].setSubIWaitF(true);
					}
					if (addReservationStations[i].getSubIWait() == clock) {
						result = Float.parseFloat(addReservationStations[i].getVj())
								- Float.parseFloat(addReservationStations[i].getVk());
						for (int j = 0; j < resultAddresses[0].length; j++) {
							if (addReservationStations.length <= resultAddresses[0].length) {
								if (resultAddresses[0][j] != null) {
									int numOfAdd = Integer.parseInt(resultAddresses[0][j].substring(1, 2));
									if (addReservationStations[numOfAdd].getQj()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQj("0");
										addReservationStations[numOfAdd].setVj(result + "");
									}
									if (addReservationStations[numOfAdd].getQk()
											.equals(addReservationStations[i].getName())) {
										addReservationStations[numOfAdd].setQk("0");
										addReservationStations[numOfAdd].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[1].length; j++) {
							if (multReservationStations.length <= resultAddresses[1].length) {
								if (resultAddresses[1][j] != null) {
									int numOfMult = Integer.parseInt(resultAddresses[1][j].substring(1, 2));
									if (multReservationStations[numOfMult].getQj()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQj("0");
										multReservationStations[numOfMult].setVj(result + "");
									}
									if (multReservationStations[numOfMult].getQk()
											.equals(addReservationStations[i].getName())) {
										multReservationStations[numOfMult].setQk("0");
										multReservationStations[numOfMult].setVk(result + "");
									}
								}
							}
						}

						for (int j = 0; j < resultAddresses[2].length; j++) {
							if (storeBuffers.length <= resultAddresses[2].length) {
								if (resultAddresses[2][j] != null) {
									int numOfMult = Integer.parseInt(resultAddresses[2][j].substring(1, 2));
									storeBuffers[numOfMult].setQ("0");
									storeBuffers[numOfMult].setV(result);
								}
							}
						}

						for (int j = 0; j < resultAddresses[3].length; j++) {
							if (registerFile.length <= resultAddresses[3].length) {
								if (resultAddresses[3][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[3][j].substring(1));
									registerFile[numberOfReg].setContent(result);
									registerFile[numberOfReg].setQi("0");
								}
							}
						}
						for (int j = 0; j < resultAddresses[4].length; j++) {
							if (registerFileInt.length <= resultAddresses[4].length) {
								if (resultAddresses[4][j] != null) {
									int numberOfReg = Integer.parseInt(resultAddresses[4][j].substring(1));
									registerFileInt[numberOfReg].setContent((int) result);
									registerFileInt[numberOfReg].setQi("0");
								}
							}
						}
						addReservationStations[i].setBusy(0);
						addReservationStations[i].setOp("");
						addReservationStations[i].setVj("");
						addReservationStations[i].setVk("");
						addReservationStations[i].setQj("");
						addReservationStations[i].setQk("");
						addReservationStations[i].setA("");
						addReservationStations[i].setSubIWaitF(false);
						addReservationStations[i].setSubIWait(0);
					}
					break;
				case "BNEZ": {
					if (!addReservationStations[i].isBENZWaitF()) {
						addReservationStations[i].setBENZWait(clock + bnezLatency + 1);
						addReservationStations[i].setBENZWaitF(true);
					}
					if (addReservationStations[i].getBENZWait() == clock) {

						int value = Integer.parseInt(addReservationStations[i].getVj().substring(0,
								addReservationStations[i].getVj().length() - 2));
						if (value != 0) {
							PC = 0;
						}else {
							System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"+PC);
						}
						if (PC != 0) {
							PC-=3;

							String[] instruction = fetch();
							if(instruction!=null)
							loadreservation(instruction);
						}
						addReservationStations[i].setBusy(0);
						addReservationStations[i].setOp("");
						addReservationStations[i].setVj("");
						addReservationStations[i].setVk("");
						addReservationStations[i].setQj("");
						addReservationStations[i].setQk("");
						addReservationStations[i].setA("");
						addReservationStations[i].setBENZWaitF(false);
						addReservationStations[i].setBENZWait(0);
					}

					break;
				}

				}

			}

		}
		// mult Reservation Stations execution
		for (int i = 0; i < multReservationStations.length; i++) {
			float result = 0;
			String tag = multReservationStations[i].getName();
			String[][] resultAddresses = whoIsWaiting(tag);

			if (multReservationStations[i].getBusy() == 1 && multReservationStations[i].getQj().equals("0")
					&& multReservationStations[i].getQk().equals("0")) {

				if (isNumeric(multReservationStations[i].getVj()) && isNumeric(multReservationStations[i].getVk())) {
					switch (multReservationStations[i].getOp()) {
					case "MUL":
						if (!multReservationStations[i].isMULWaitF()) {
							multReservationStations[i].setMULWait(clock + mulLatency);
							multReservationStations[i].setMULWaitF(true);
						}
						if (multReservationStations[i].getMULWait() == clock) {
							result = Float.parseFloat(multReservationStations[i].getVj())
									* Float.parseFloat(multReservationStations[i].getVk());
							for (int j = 0; j < resultAddresses[0].length; j++) {
								if (addReservationStations.length <= resultAddresses[0].length) {
									if (resultAddresses[0][j] != null) {
										int numOfAdd = Integer.parseInt(resultAddresses[0][j].substring(1, 2));
										if (addReservationStations[numOfAdd].getQj()
												.equals(multReservationStations[i].getName())) {
											addReservationStations[numOfAdd].setQj("0");
											addReservationStations[numOfAdd].setVj(result + "");
										}
										if (addReservationStations[numOfAdd].getQk()
												.equals(multReservationStations[i].getName())) {
											addReservationStations[numOfAdd].setQk("0");
											addReservationStations[numOfAdd].setVk(result + "");
										}
									}
								}
							}

							for (int j = 0; j < resultAddresses[1].length; j++) {
								if (multReservationStations.length <= resultAddresses[1].length) {
									if (resultAddresses[1][j] != null) {
										int numOfMult = Integer.parseInt(resultAddresses[1][j].substring(1, 2));
										if (multReservationStations[numOfMult].getQj()
												.equals(multReservationStations[i].getName())) {
											multReservationStations[numOfMult].setQj("0");
											multReservationStations[numOfMult].setVj(result + "");
										}
										if (multReservationStations[numOfMult].getQk()
												.equals(multReservationStations[i].getName())) {
											multReservationStations[numOfMult].setQk("0");
											multReservationStations[numOfMult].setVk(result + "");
										}
									}
								}
							}
							for (int j = 0; j < resultAddresses[2].length; j++) {
								if (storeBuffers.length <= resultAddresses[2].length) {
									if (resultAddresses[2][j] != null) {
										int numOfMult = Integer.parseInt(resultAddresses[2][j].substring(1, 2));
										storeBuffers[numOfMult].setQ("0");
										storeBuffers[numOfMult].setV(result);
									}
								}
							}
							for (int j = 0; j < resultAddresses[3].length; j++) {
								if (registerFile.length <= resultAddresses[3].length) {
									if (resultAddresses[3][j] != null) {
										int numberOfReg = Integer.parseInt(resultAddresses[3][j].substring(1));

										registerFile[numberOfReg].setContent(result);
										registerFile[numberOfReg].setQi("0");
									}
								}
							}
							for (int j = 0; j < resultAddresses[4].length; j++) {
								if (registerFileInt.length <= resultAddresses[4].length) {
									if (resultAddresses[4][j] != null) {
										int numberOfReg = Integer.parseInt(resultAddresses[4][j].substring(1));
										registerFileInt[numberOfReg].setContent((int) result);
										registerFileInt[numberOfReg].setQi("0");
									}
								}
							}
							multReservationStations[i].setBusy(0);
							multReservationStations[i].setOp("");
							multReservationStations[i].setVj("");
							multReservationStations[i].setVk("");
							multReservationStations[i].setQj("");
							multReservationStations[i].setQk("");
							multReservationStations[i].setA("");
							multReservationStations[i].setMULWaitF(false);
							multReservationStations[i].setMULWait(0);
						}
						break;
					case "DIV":
						if (!multReservationStations[i].isDIVWaitF()) {
							multReservationStations[i].setDIVWait(clock + divLatency);
							multReservationStations[i].setDIVWaitF(true);
						}
						if (multReservationStations[i].getDIVWait() == clock) {
							result = Float.parseFloat(multReservationStations[i].getVj())
									/ Float.parseFloat(multReservationStations[i].getVk());
							for (int j = 0; j < resultAddresses[0].length; j++) {
								if (addReservationStations.length <= resultAddresses[0].length) {
									if (resultAddresses[0][j] != null) {
										int numOfAdd = Integer.parseInt(resultAddresses[0][j].substring(1, 2));
										if (addReservationStations[numOfAdd].getQj()
												.equals(multReservationStations[i].getName())) {
											addReservationStations[numOfAdd].setQj("0");
											addReservationStations[numOfAdd].setVj(result + "");
										}
										if (addReservationStations[numOfAdd].getQk()
												.equals(multReservationStations[i].getName())) {
											addReservationStations[numOfAdd].setQk("0");
											addReservationStations[numOfAdd].setVk(result + "");
										}
									}
								}
							}

							for (int j = 0; j < resultAddresses[1].length; j++) {
								if (multReservationStations.length <= resultAddresses[1].length) {
									if (resultAddresses[1][j] != null) {
										int numOfMult = Integer.parseInt(resultAddresses[1][j].substring(1, 2));
										if (multReservationStations[numOfMult].getQj()
												.equals(multReservationStations[i].getName())) {
											multReservationStations[numOfMult].setQj("0");
											multReservationStations[numOfMult].setVj(result + "");
										}
										if (multReservationStations[numOfMult].getQk()
												.equals(multReservationStations[i].getName())) {
											multReservationStations[numOfMult].setQk("0");
											multReservationStations[numOfMult].setVk(result + "");
										}
									}
								}
							}
							for (int j = 0; j < resultAddresses[2].length; j++) {
								if (storeBuffers.length <= resultAddresses[2].length) {
									if (resultAddresses[2][j] != null) {
										int numOfMult = Integer.parseInt(resultAddresses[2][j].substring(1, 2));
										storeBuffers[numOfMult].setQ("0");
										storeBuffers[numOfMult].setV(result);
									}
								}
							}
							for (int j = 0; j < resultAddresses[3].length; j++) {
								if (registerFile.length <= resultAddresses[3].length) {
									if (resultAddresses[3][j] != null) {
										int numberOfReg = Integer.parseInt(resultAddresses[3][j].substring(1));
										registerFile[numberOfReg].setContent(result);
										registerFile[numberOfReg].setQi("0");
									}
								}
							}
							for (int j = 0; j < resultAddresses[4].length; j++) {
								if (registerFileInt.length <= resultAddresses[4].length) {
									if (resultAddresses[4][j] != null) {
										int numberOfReg = Integer.parseInt(resultAddresses[4][j].substring(1));
										registerFileInt[numberOfReg].setContent((int) result);
										registerFileInt[numberOfReg].setQi("0");
									}
								}
							}
							multReservationStations[i].setBusy(0);
							multReservationStations[i].setOp("");
							multReservationStations[i].setVj("");
							multReservationStations[i].setVk("");
							multReservationStations[i].setQj("");
							multReservationStations[i].setQk("");
							multReservationStations[i].setA("");
							multReservationStations[i].setDIVWaitF(false);
							multReservationStations[i].setDIVWait(0);
						}
					}

				}
			}
		}

		// store Buffers execution
		for (int i = 0; i < storeBuffers.length; i++) {
			String tag = storeBuffers[i].getName();
			if (storeBuffers[i].getBusy() == 1 && storeBuffers[i].getQ().equals("0"))
				if (!storeBuffers[i].isStoreWaitF()) {
					storeBuffers[i].setStoreWait(clock + sdLatency);
					storeBuffers[i].setStoreWaitF(true);
				}
			if (storeBuffers[i].getStoreWait() == clock) {
				cache.getCache()[storeBuffers[i].getAddress()] = storeBuffers[i].getV();

				storeBuffers[i].setBusy(0);
				storeBuffers[i].setAddress(0);
				storeBuffers[i].setName("");
				storeBuffers[i].setQ("");
				storeBuffers[i].setV(0);
				storeBuffers[i].setStoreWait(0);
				storeBuffers[i].setStoreWaitF(false);
			}
		}

		// load Buffers execution
		for (int i = 0; i < loadBuffers.length; i++) {
			String tag = loadBuffers[i].getName();
			String[][] resultAddresses = whoIsWaiting(tag);
			if (loadBuffers[i].getBusy() == 1) {
				if (!loadBuffers[i].isLoadWaitF()) {
					loadBuffers[i].setLoadWait(clock + ldLatency);
					loadBuffers[i].setLoadWaitF(true);
				}
				if (loadBuffers[i].getLoadWait() == clock) {
					for (int j = 0; j < resultAddresses[0].length; j++) {
						if (addReservationStations.length <= resultAddresses[0].length) {
							if (resultAddresses[0][j] != null) {
								int numOfAdd = Integer.parseInt(resultAddresses[0][j].substring(1, 2));
								if (addReservationStations[numOfAdd].getQj().equals(loadBuffers[i].getName())) {
									addReservationStations[numOfAdd].setQj("0");
									addReservationStations[numOfAdd]
											.setVj(cache.getCache()[loadBuffers[i].getAddress()] + "");
								}
								if (addReservationStations[numOfAdd].getQk().equals(loadBuffers[i].getName())) {
									addReservationStations[numOfAdd].setQk("0");
									addReservationStations[numOfAdd]
											.setVk(cache.getCache()[loadBuffers[i].getAddress()] + "");
								}
							}
						}
					}

					for (int j = 0; j < resultAddresses[1].length; j++) {
						if (multReservationStations.length <= resultAddresses[1].length) {
							if (resultAddresses[1][j] != null) {
								int numOfMult = Integer.parseInt(resultAddresses[1][j].substring(1, 2));
								if (multReservationStations[numOfMult].getQj().equals(loadBuffers[i].getName())) {
									multReservationStations[numOfMult].setQj("0");
									multReservationStations[numOfMult]
											.setVj(cache.getCache()[loadBuffers[i].getAddress()] + "");
								}
								if (multReservationStations[numOfMult].getQk().equals(loadBuffers[i].getName())) {
									multReservationStations[numOfMult].setQk("0");
									multReservationStations[numOfMult]
											.setVk(cache.getCache()[loadBuffers[i].getAddress()] + "");
								}
							}
						}
					}

					for (int j = 0; j < resultAddresses[3].length; j++) {

						if (resultAddresses[3][j] != null) {

							if (registerFile.length <= resultAddresses[3].length) {

								int numberOfReg = Integer.parseInt(resultAddresses[3][j].substring(1));
								registerFile[numberOfReg].setContent(cache.getCache()[loadBuffers[i].getAddress()]);
								registerFile[numberOfReg].setQi("0");
							}
						}
					}
					for (int j = 0; j < resultAddresses[4].length; j++) {
						if (resultAddresses[4][j] != null) {
							if (registerFileInt.length <= resultAddresses[4].length) {
								int numberOfReg = Integer.parseInt(resultAddresses[4][j].substring(1));
								registerFileInt[numberOfReg]
										.setContent((int) cache.getCache()[loadBuffers[i].getAddress()]);
								registerFileInt[numberOfReg].setQi("0");
							}
						}
					}

					loadBuffers[i].setName("");
					loadBuffers[i].setAddress(0);
					loadBuffers[i].setBusy(0);
					loadBuffers[i].setLoadWait(0);
					loadBuffers[i].setLoadWaitF(false);
				}
			}
		}
	}

	private String[][] whoIsWaiting(String tag) {
		String who[][] = new String[5][Math.max(Math.max(storeBuffers.length, registerFile.length),
				Math.max(addReservationStations.length, multReservationStations.length))];

		for (int i = 0; i < who[0].length; i++) {

			if (i < addReservationStations.length)
				if (addReservationStations[i].getQj().equals(tag) || addReservationStations[i].getQk().equals(tag)) {
					who[0][i] = addReservationStations[i].getName();
				}
		}
		for (int i = 0; i < who[1].length; i++) {
			if (i < multReservationStations.length)
				if (multReservationStations[i].getQj().equals(tag) || multReservationStations[i].getQk().equals(tag)) {
					who[1][i] = multReservationStations[i].getName();
				}
		}
		for (int i = 0; i < who[2].length; i++) {
			if (i < storeBuffers.length)
				if (storeBuffers[i].getQ().equals(tag)) {
					who[2][i] = storeBuffers[i].getName();
				}
		}
		for (int i = 0; i < who[3].length; i++) {
			if (i < registerFile.length)
				if (registerFile[i].getQi().equals(tag)) {
					who[3][i] = registerFile[i].getName();
				}
		}
		for (int i = 0; i < who[4].length; i++) {
			if (i < registerFileInt.length)
				if (registerFileInt[i].getQi().equals(tag)) {
					who[4][i] = registerFileInt[i].getName();
				}
		}
		return who;
	}

	private static boolean isNumeric(String str) {
		// Use regular expression to check if the string contains only digits or a valid
		// decimal number
		return str.matches("\\d*\\.?\\d+");
	}

	// Method to get user input for instruction latencies
	private void getUserInputForLatencies() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter latency for ADD.D instructions: ");
		this.addLatency = scanner.nextInt();
		System.out.print("Enter latency for SUB.D instructions: ");
		this.subLatency = scanner.nextInt();
		System.out.print("Enter latency for MUL.D instructions: ");
		this.mulLatency = scanner.nextInt();
		System.out.print("Enter latency for DIV.D instructions: ");
		this.divLatency = scanner.nextInt();
		this.addiLatency = 1;
		System.out.print("Enter latency for SUBI instructions: ");
		this.subiLatency = scanner.nextInt();
		System.out.print("Enter latency for L.D instructions: ");
		this.ldLatency = scanner.nextInt();
		System.out.print("Enter latency for S.D instructions: ");
		this.sdLatency = scanner.nextInt();
		this.bnezLatency = 1;
	}

	// Method to get user input for the size of stations/buffers
	private int getUserInputForSize(String prompt) {
		Scanner scanner = new Scanner(System.in);
		System.out.print(prompt);
		return scanner.nextInt();
	}

	public static void main(String[] args) {
		parser = new Parser();
		try {
			parser.parseProgram("instructions.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[][] instructions = InstructionMemory.getInstance().getInstructionMemory();

		for (int i = 0; i < instructions.length; i++) {
			if (instructions[i] != null) {
				for (int j = 0; j < instructions[i].length; j++) {
					System.out.println("Instruction " + i + " " + instructions[i][j]);
				}
			}
		}

		System.out.println("\nLabel Offsets:");
		for (Map.Entry<String, Integer> entry : parser.getLabelOffsets().entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue() + "\n");
		}
		TomasuloCPU tomasuloCPU = new TomasuloCPU();

	}
}