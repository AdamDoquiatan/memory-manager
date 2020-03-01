package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class MemoryManager {

    private ArrayList<ArrayList<String>> instructions;
    private int mode;
    private int totalMem;
    private ArrayList<ArrayList<Integer>> allocMemList = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> freeMemList = new ArrayList<>();

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

        for (int i = 0; i < instructions.size(); i++) {

            String task = instructions.get(i).get(0);
            switch (task) {
                case "A":
                    int pid = Integer.parseInt(instructions.get(i).get(1));
                    int entryBase = 0;
                    int entryLimit = Integer.parseInt(instructions.get(i).get(2));

                    // Mem empty -- stick first thing at 0
                    if (allocMemList.isEmpty()) {
                        System.out.println("Allocating mem to empty list");
                        freeMemList.get(0).set(1, entryLimit);

                        ArrayList<Integer> entry = new ArrayList<>(Arrays.asList(pid, entryBase, entryBase + entryLimit));
                        allocMemList.add(entry);

                    // Find where to stick the next entry
                    } else {
                        for (int j = 0; j < freeMemList.size(); j++) {
                            System.out.println("here 1");

                            entryBase = freeMemList.get(j).get(1) + 1;
                            System.out.println("Entry Base: " + entryBase);

                            if (entryBase + entryLimit <= totalMem) {
                                System.out.println("here 2");

                                System.out.println(entryBase >= freeMemList.get(j).get(1));
                                System.out.println(entryBase < freeMemList.get(j).get(2));

                                if (entryBase >= freeMemList.get(j).get(1) && entryLimit < freeMemList.get(j).get(2)) {
                                    System.out.println("here 3");
                                    freeMemList.get(j).set(1, entryBase + entryLimit);

                                    ArrayList<Integer> entry = new ArrayList<>(Arrays.asList(pid, entryBase, entryBase + entryLimit));
                                    allocMemList.add(entry);
                                    break;
                                } else {
                                    System.out.println("here 5");
                                }
                            } else {
                                System.out.println("No memory remaining (maybe run compaction)");
                            }
                        }
                    }

                    System.out.println("Free: " + Arrays.deepToString(freeMemList.toArray()));
                    System.out.println("Alloc: " + Arrays.deepToString(allocMemList.toArray()));

                    break;
                case "D":
                    break;
                case "P":
                    break;


            }
        }
    }


    private void modeBestFit() {
        System.out.println("Running ModeBestFit");
    }

    private void modeWorstFit() {
        System.out.println("Running ModeWorstFit");
    }

    public void manageMemory() {
        System.out.println("Now managing memory");

        ArrayList<Integer> freeMemInit = new ArrayList<>(Arrays.asList(null, 0, totalMem));
        freeMemList.add(freeMemInit);

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

            while ((line = bufferedReader.readLine()) != null) {
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

