package Memory;

import java.util.Arrays;

public class InstructionMemory {

    private static final int MEMORY_SIZE = 50;
    private static String[][] instructionMemory = new String[MEMORY_SIZE][];
    private static int currentInstructionIndex = 0;

    private InstructionMemory() {
        // Private constructor to prevent instantiation
    }

    public static InstructionMemory getInstance() {
        // Use a lazy initialization singleton pattern
        return InstructionMemoryHolder.INSTANCE;
    }

    private static class InstructionMemoryHolder {
        private static final InstructionMemory INSTANCE = new InstructionMemory();
    }

    public void addInstruction(String[] instruction) {
        if (currentInstructionIndex < MEMORY_SIZE) {
            instructionMemory[currentInstructionIndex++] = instruction;
        } else {
            throw new IllegalStateException("Instruction Memory overflow");
        }
    }

    public String[][] getInstructionMemory() {
        return instructionMemory;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Instruction Memory:\n");
        for (String[] instruction : instructionMemory) {
            if (instruction != null) {
                sb.append("Instruction: ").append(String.join(" ", instruction)).append("\n");
            } else {
                sb.append("null\n");
            }
        }
        return sb.toString();
    }
}
