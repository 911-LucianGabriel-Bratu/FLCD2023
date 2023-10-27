package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public List<String> readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        List<String> res = new ArrayList<>();
        while ((line = br.readLine()) != null) {
              res.add(line);
        }
        br.close();
        return res;
    }

    public void writeToFile(String fileName, String str) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();
    }
}
