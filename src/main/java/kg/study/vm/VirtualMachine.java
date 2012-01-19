package kg.study.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Stack;

public class VirtualMachine {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachine.class);

    private int[] var;

    public void run(Object[] instructions) {
        boolean isRunning = true;
        Stack<Integer> stack = new Stack<>();
        var = new int[26];
        Arrays.fill(var, 0);
        int pc = 0;
        while (isRunning) {
            VMInstruction instruction = (VMInstruction) instructions[pc];
            Object arg = null;
            if (pc < instructions.length - 1) {
                arg = instructions[pc + 1];
            }
            switch (instruction) {
                case IFETCH: {
                    stack.add(var[(Integer) arg]);
                    pc += 2;
                    break;
                }
                case ISTORE: {
                    var[(Integer) arg] = stack.peek();
                    pc += 2;
                    break;
                }
                case IPUSH: {
                    stack.add((Integer) arg);
                    pc += 2;
                    break;
                }
                case IPOP: {
                    stack.pop();
                    pc += 1;
                    break;
                }
                case IADD: {
                    stack.set(stack.size() - 2, stack.get(stack.size() - 2) + stack.get(stack.size() - 1));
                    stack.pop();
                    pc += 1;
                    break;
                }
                case ISUB: {
                    stack.set(stack.size() - 2, stack.get(stack.size() - 2) - stack.get(stack.size() - 1));
                    stack.pop();
                    pc += 1;
                    break;
                }
                case ILT: {
                    if (stack.get(stack.size() - 2) < stack.get(stack.size() - 1)) {
                        stack.set(stack.size() - 2, 1);
                    } else {
                        stack.set(stack.size() - 2, 0);
                    }
                    stack.pop();
                    pc += 1;
                    break;
                }
                case JZ: {
                    if (stack.pop() == 0) {
                        pc = (Integer) arg;
                    } else {
                        pc += 2;
                    }
                    break;
                }
                case JNZ: {
                    if (stack.pop() != 0) {
                        pc = (Integer) arg;
                    } else {
                        pc += 2;
                    }
                    break;
                }
                case JMP: {
                    pc = (Integer) arg;
                    break;
                }
                case PRINT: {
                    System.out.println(stack.peek());
                    pc += 1;
                    break;
                }
                case HALT:
                    isRunning = false;
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown instruction : " + instruction);
            }
            LOGGER.debug("{} : {}", instruction, stack);
        }
        LOGGER.info("Execution finished.");
        for (int i = 0; i < 26; i++) {
            if (var[i] != 0) {
                LOGGER.debug("{} = {}", indexToName(i), var[i]);
            }
        }
    }

    public int[] getVar() {
        return var;
    }

    public static char indexToName(int index) {
        return (char) (index + (int) 'a');
    }

    public static int nameToIndex(char name) {
        return (int) name - (int) 'a';
    }
}
