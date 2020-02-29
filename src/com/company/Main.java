package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class MemoryManager {

    private ArrayList<ArrayList<String>> instructions;
    private int mode;
    private int totalMem;
    private LinkedList<ArrayList<Integer>> allocatedMem = new LinkedList<>();
    private LinkedList<ArrayList<Integer>> freeMem = new LinkedList<>();

    public MemoryManager(ArrayList<ArrayList<String>> instructions) {
        this.instructions = instructions;
        this.mode = Integer.parseInt(instructions.get(0).get(0));
        this.totalMem = Integer.parseInt(instructions.get(1).get(0));

        System.out.println("Mode: " + this.mode);
        System.out.println("TotalMemory: " + this.totalMem);
        System.out.println(Arrays.deepToString(instructions.toArray()));
    }

    private void modeFirstFit() {
        System.out.println("Running ModeFirstFit");
    }

    private void modeBestFit() {
        System.out.println("Running ModeBestFit");
    }

    private void modeWorstFit() {
        System.out.println("Running ModeWorstFit");
    }

    public void manageMemory() {

        System.out.println("Now managing memory");

        switch (this.mode) {
            case 1:
                modeFirstFit();
                break;
            case 2:
                modeBestFit();
                break;
            case 3:
                modeWorstFit();
                break;
        }





    }

}


public class Main {

    private static ArrayList<ArrayList<String>> loadTextFile(String fileName) {
        ArrayList<ArrayList<String>> instructions = new ArrayList<>();

        String line = null;

        try {
            Path pathToFile = Paths.get(fileName);
            System.out.println(pathToFile.toAbsolutePath());

            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                line = line.replaceAll("( )+", " ");
                ArrayList<String> words = new ArrayList<>(Arrays.asList(line.split(" ")));
//                System.out.println(Arrays.toString(words));
                instructions.add(words);
            }

            bufferedReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return instructions;
    }

    public static void main(String[] args) {

        System.out.println("Running");
        System.out.println("Arg 1: " + args[0]);

        String fileName = args[0];
        ArrayList<ArrayList<String>> instructions;

        instructions = loadTextFile(fileName);

        MemoryManager memoryManager = new MemoryManager(instructions);
        memoryManager.manageMemory();


    }

}

