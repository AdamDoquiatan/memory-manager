package com.company;

import javax.management.ConstructorParameters;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class MemoryManager implements Comparable {

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

    private void modeFirstFit(ArrayList<String> newChunk) {
        System.out.println("Running ModeFirstFit");

        int pid = Integer.parseInt(newChunk.get(1));
        int entryBase = 0;
        int entryLimit = Integer.parseInt(newChunk.get(2));;

        // Mem empty -- stick first thing at 0
        if (allocMemList.isEmpty()) {
            System.out.println("Allocating mem to empty list");
            freeMemList.get(0).set(1, entryLimit + 1);

            ArrayList<Integer> entry = new ArrayList<>(Arrays.asList(pid, entryBase, entryBase + entryLimit));
            allocMemList.add(entry);

            // Find where to stick the next entry
        } else {
            for (int j = 0; j < freeMemList.size(); j++) {
                entryBase = freeMemList.get(j).get(1);

                if (entryBase + entryLimit <= totalMem) {
                    System.out.println(entryBase >= freeMemList.get(j).get(1));
                    System.out.println(entryBase < freeMemList.get(j).get(2));

                    if (entryBase >= freeMemList.get(j).get(1) && entryLimit < freeMemList.get(j).get(2)) {
                        freeMemList.get(j).set(1, entryBase + entryLimit + 1);

                        ArrayList<Integer> entry = new ArrayList<>(Arrays.asList(pid, entryBase, entryBase + entryLimit));
                        allocMemList.add(entry);
                        break;
                    }
                } else {
                    System.out.println("No memory remaining (maybe run compaction)");
                }
            }
        }

        System.out.println("Free: " + Arrays.deepToString(freeMemList.toArray()));
        System.out.println("Alloc: " + Arrays.deepToString(allocMemList.toArray()));
    }

    private void modeBestFit(ArrayList<String> newChunk) {
        System.out.println("Running ModeBestFit");

                int pid = Integer.parseInt(newChunk.get(1));
                int entryBase = 0;
                int entryLimit = Integer.parseInt(newChunk.get(2));
                int newChunkSize = entryLimit;


                // Mem empty -- stick first thing at 0
                if (allocMemList.isEmpty()) {
                    System.out.println("Allocating mem to empty list");
                    freeMemList.get(0).set(1, entryLimit + 1);

                    ArrayList<Integer> entry = new ArrayList<>(Arrays.asList(pid, entryBase, entryBase + entryLimit));
                    allocMemList.add(entry);

                    // Find where to stick the next entry
                } else {
                    int bestFitSize = Integer.MAX_VALUE;
                    int bestFitBase = -1;
                    int bestFitIndex = -1;

                    for (int j = 0; j < freeMemList.size(); j++) {

                        entryBase = freeMemList.get(j).get(1);


                        // Checks that new chunk doesn't go beyond max memory
                        if (entryBase + entryLimit <= totalMem) {
                            System.out.println(entryBase >= freeMemList.get(j).get(1));
                            System.out.println(entryBase < freeMemList.get(j).get(2));

                            // Checks if new chunk will fit in free space -- tracks smallest free spot
                            int freeSpotSize = freeMemList.get(j).get(2) - freeMemList.get(j).get(1);
                            if (newChunkSize <= freeSpotSize && freeSpotSize < bestFitSize) {
                                  bestFitSize = freeSpotSize;
                                  bestFitBase = freeMemList.get(j).get(1);
                                  bestFitIndex = j;
                            }
                        } else {
                            System.out.println("No memory remaining (maybe run compaction)");
                            return;
                        }
                    }
                    freeMemList.get(bestFitIndex).set(1, bestFitBase + newChunkSize + 1);

                    ArrayList<Integer> entry = new ArrayList<>(Arrays.asList(pid, bestFitBase, bestFitBase + newChunkSize));
                    allocMemList.add(entry);

                }

                System.out.println("Free: " + Arrays.deepToString(freeMemList.toArray()));
                System.out.println("Alloc: " + Arrays.deepToString(allocMemList.toArray()));
        }






    private void modeWorstFit() {
        System.out.println("Running ModeWorstFit");
    }

    private void deallocateMemory(int pid) {
        System.out.println("Deallocating Memory for PID: " + pid);

        for (ArrayList<Integer> chunk : allocMemList) {
            if (chunk.get(0) == pid) {
                int allocChunkBase = chunk.get(1);
                int allocChunkLimit = chunk.get(2);
                allocMemList.remove(chunk);

                //TODO: Doesn't always merge free adjacent partitions (but does sometimes?)
                boolean merged = false;
                for (ArrayList<Integer> freeChunk : freeMemList) {
                    System.out.println("Alloc Base: " + (allocChunkBase - 1));
                    System.out.println("Free Limit: " + (freeChunk.get(2)));
                    if (allocChunkBase - 1 == freeChunk.get(2)) {
                        System.out.println("Got In here!");
                        // merge chunks -- alloc is above free
                        freeChunk.set(2, allocChunkLimit);
                        merged = true;
                        break;
                    } else if (allocChunkLimit + 1 == freeChunk.get(1)) {
                        System.out.println("Got In here2!");
                        // merge chunks -- alloc is below free
                        freeChunk.set(1, allocChunkBase);
                        merged = true;
                        break;
                    }
                }

                if (!merged) {
                    // add and sort
                    ArrayList<Integer> newFreeChunk = new ArrayList<Integer>(Arrays.asList(null, allocChunkBase, allocChunkLimit));
                    freeMemList.add(newFreeChunk);
                    Collections.sort(freeMemList, (Comparator<List<Integer>>) (o1, o2) -> o1.get(1).compareTo(o2.get(1)));

                    System.out.println("Index of newfreechunk: " + freeMemList.indexOf(newFreeChunk));
                    int freeChunkIndex = freeMemList.indexOf(newFreeChunk);

                    // TODO: Messes up the output
                    //Checks if new floor is lesser than below mem's ceiling -- makes new arraylist
//                if(freeMemList.size() > freeChunkIndex + 1 && newFreeChunk.get(1) <= freeMemList.get(freeChunkIndex + 1).get(2)) {
//                    freeMemList.add(new ArrayList<Integer>(Arrays.asList(null, freeMemList.get(freeChunkIndex + 1).get(1), newFreeChunk.get(1) - 1)));
//                    freeMemList.get(freeChunkIndex + 1).set(1, newFreeChunk.get(2) + 1);
//                    Collections.sort(freeMemList, (Comparator<List<Integer>>) (o1, o2) -> o1.get(1).compareTo(o2.get(1)));
//                }

                    // Checks if new ceiling is larger than above mem's base -- adjusts above's floor
                    if (freeMemList.size() > freeChunkIndex + 1 && newFreeChunk.get(2) >= freeMemList.get(freeChunkIndex + 1).get(1)) {
                        freeMemList.get(freeChunkIndex + 1).set(1, newFreeChunk.get(2) + 1);
                    }
                }
                break;
            }
        }
    }

    private void printMemoryAllocations() {
        System.out.println("-------------- State of Memory -------------");
        String strFreeChunks = "Free Chunks: ";
        for (ArrayList<Integer> chunk : freeMemList) {
//                    System.out.println("ff: " + Arrays.toString(chunk.toArray()));
//                    System.out.println("ff: " + chunk.get(1));
            strFreeChunks += "[ " + String.valueOf(chunk.get(1)) + "->" + String.valueOf(chunk.get(2)) + " ] ";
        }
        System.out.println(strFreeChunks);

        String strAllocChunks = "Allocated Chunks: ";
        for (ArrayList<Integer> chunk : allocMemList) {
            strAllocChunks += "PID" + String.valueOf(chunk.get(0)) + "[ " + String.valueOf(chunk.get(1)) + "->" + String.valueOf(chunk.get(2)) + " ] ";
        }

        System.out.println(strAllocChunks);
    }


    public void manageMemory() {
        System.out.println("Now managing memory");

        ArrayList<Integer> freeMemInit = new ArrayList<>(Arrays.asList(null, 0, totalMem));
        freeMemList.add(freeMemInit);

        for (int i = 2; i < instructions.size(); i++) {
            ArrayList<String> newChunk = instructions.get(i);

            String task = instructions.get(i).get(0);

            switch (task) {
                case "A":
                    switch (this.mode) {
                        case 1:
                            modeFirstFit(newChunk);
                            break;
                        case 2:
                            modeBestFit(newChunk);
                            break;
                        case 3:
                            modeWorstFit();
                            break;
                    }
                    break;
                case "D":
                    int pid = Integer.parseInt(instructions.get(i).get(1));
                    this.deallocateMemory(pid);

                    break;
                case "P":
                    this.printMemoryAllocations();
                    break;
            }
        }

    }


    @Override
    public int compareTo(Object o) {
        return 0;
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

