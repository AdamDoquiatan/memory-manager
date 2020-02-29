package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

    private static int mode;
    private static int totalMemory;

    public static ArrayList<ArrayList<String>> loadTextFile(String fileName) {
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

        // Prints an arraylist of arraylists to strings
        System.out.println(Arrays.deepToString(instructions.toArray()));

        mode = Integer.parseInt(instructions.get(0).get(0));
        totalMemory = Integer.parseInt(instructions.get(1).get(0));

        System.out.println("Mode: " + mode);
        System.out.println("TotalMemory: " + totalMemory);

    }

    }

