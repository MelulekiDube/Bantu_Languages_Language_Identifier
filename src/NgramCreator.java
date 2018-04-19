
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private File currentFile;
    private File ngramFile;
    private Profile model_profile;

    NgramCreator(File f, Profile pr) {
        currentFile = f.listFiles()[0];
        model_profile = pr;
    }

    @Override
    public void run() {
        model_profile.frequencyTable.clear();
//        if (testing_profile != null) {
//            produceTrainingAndTesting();
//        } else {
        try {
            producemodelOnly();
        } catch (Exception ex) {
            System.out.println("Error is " + ex.getMessage());
            for (StackTraceElement t : ex.getStackTrace()) {
                System.out.println("================");
                System.out.println("Error on File: " + t.getFileName());
                System.out.println("Error on File: " + t.getClassName());
                System.out.println("Error on File: " + t.getMethodName());
                System.out.println("Error on File: " + t.getLineNumber());
                System.out.println("================");
            }
        }
//        }
        model_profile.sortHashMap();
    }

    void write_to_profile(String token, String p) {
        List<String> ngrams = ngramFromLine(token);
        ngrams.forEach((tempNgram) -> {
            model_profile.insert(tempNgram);

        });
    }

    void producemodelOnly() {
        try {
            try (BufferedReader fileRead = new BufferedReader(new FileReader(currentFile))) {
                String token;
                token = fileRead.readLine();
                while (token != null) {
                    write_to_profile(token, "M");
                    token = fileRead.readLine();
                }
            }
        } catch (IOException ex) {
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
        if (stringToConvert.length() >= 3) {
            for (int i = 1; i < stringToConvert.length() - 3; i++) {
                String temp = stringToConvert.substring(i, i + 3);
                tempList.add(temp);
            }
            tempList.add(stringToConvert.substring(stringToConvert.length() - 3));
        }
        return tempList;
    }

}
