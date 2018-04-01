
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki
 */
public class NgramCreator implements Runnable {

    File currentFile;
    File ngramFile;

    NgramCreator(File f) {
        currentFile = f.listFiles()[0];
        ngramFile = new File(f.getAbsolutePath() + "/ngram");
        try {
            if (!ngramFile.exists()) {
                ngramFile.createNewFile();
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    @Override
    public void run() {
        try {
            Scanner fileRead = new Scanner(currentFile);
            String line;
            System.setOut(new PrintStream(ngramFile));
            while (fileRead.hasNextLine()) {
                line = fileRead.nextLine();
                List<String> ngrams = ngramFromLine(line);
                ngrams.forEach((tempNgram) -> {
                    System.out.println(tempNgram);
                });
            }
            System.setOut(System.out);

        } catch (FileNotFoundException ex) {
            System.setOut(System.out);
            System.out.println(ex);
        }
    }

    /**
     * Converts the string into a list of ngrams characters and returns the
     * string
     *
     * @param stringToConvert which is the srting which is going to be converted
     * to trigrams
     * @return list of ngram charecters produced from the string
     */
    private List<String> ngramFromLine(String stringToConvert) {
        List<String> tempList = new ArrayList<>();
        for (int i = 0; (i + 3) < stringToConvert.length(); i++) {
            String temp = stringToConvert.substring(i, i + 3);
            tempList.add(temp);
        }
        tempList.add(stringToConvert.substring(stringToConvert.length()-3));
        return tempList;
    }

}
