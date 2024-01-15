// Parser.java
package Parsing;

//import Instruction.Instruction;
import Memory.InstructionMemory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private static Map<String, Integer> labelOffsets = new HashMap<>();

    public Parser() {

    }

    public static void parseProgram(String fileName) throws Exception {
        BufferedReader bufferedreader = new BufferedReader(new FileReader(fileName));
        int lineCount = 0;

        while (bufferedreader.ready()) {
            String inputLine = bufferedreader.readLine().trim();
            parse(inputLine, lineCount);

            if (!inputLine.isEmpty() && !inputLine.endsWith(":")) {
                lineCount++;
            }
        }

        bufferedreader.close();
    }

    private static void parse(String inputLine, int lineCount) {
        String[] parts = inputLine.split("\\s+", 2);

        if (parts.length < 1) {
            return;
        }

        String firstPart = parts[0];
        String instructionName;
        String operands;

        if (firstPart.endsWith(":")) {
            // Handle labels
            String label = firstPart.substring(0, firstPart.length() - 1);
            labelOffsets.put(label, lineCount);
            instructionName = (parts.length > 1) ? parts[1] : "";
            operands = "";
        } else {
            instructionName = firstPart;
            operands = (parts.length > 1) ? parts[1] : "";
        }

        String[] instructionParts = instructionName.split("\\s+", 2);
        instructionName = instructionParts[0];
        if (instructionParts.length > 1) {
            operands = instructionParts[1] + " " + operands;
        }

        String[] instructionArray = { instructionName, operands, inputLine };
        InstructionMemory.getInstance().addInstruction(instructionArray);
    }
    public static void main(String[] args) {
        try {
            parseProgram("instructions.txt");

         // Print instruction memory
            System.out.println("Instruction Memory:");
            String[][] instructions = InstructionMemory.getInstance().getInstructionMemory();

            for(int i=0 ; i<instructions.length;i++) {
            	if(instructions[i]!=null) {
            		for(int j=0 ;j<instructions[i].length;j++) {
                        System.out.println("Instruction "+i+" "+instructions[i][j]);
            		}
            	}
            }
            
            // Print label offsets
            System.out.println("\nLabel Offsets:");
            for (Map.Entry<String, Integer> entry : labelOffsets.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static Map<String, Integer> getLabelOffsets() {
		return labelOffsets;
	}

	public static void setLabelOffsets(Map<String, Integer> labelOffsets) {
		Parser.labelOffsets = labelOffsets;
	}
}
