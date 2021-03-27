package io.github.claude_6969;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Config {

    private static final String[] lines = readLines();

    private static String[] readLines() {
        String[] configLines = new String[2];
        File file = new File("%s/config.txt".formatted(System.getProperty("user.dir")));
        try {
            Scanner scanner = new Scanner(file);
            int i = 0;
            while(scanner.hasNextLine()) {
                String data = scanner.nextLine();
                configLines[i] = data;
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return configLines;
    }

    public static String Token() { return lines[0]; }
    public static String Prefix() { return lines[1]; }
}
