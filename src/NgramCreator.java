
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    Profile model_profile;
    Profile testing_profile;
    boolean test_flag;

    NgramCreator(File f, Profile pr) {
        currentFile = f.listFiles()[0];
        model_profile = pr;
        testing_profile = null;
        test_flag = false;
    }

    public NgramCreator(File f, Profile model_profile, Profile testing_profile) {
        this.currentFile = f.listFiles()[0];
        this.model_profile = model_profile;
        this.testing_profile = testing_profile;
        test_flag = true;
    }

    @Override
    public void run() {
        if (test_flag) {
            produceTrainingAndTesting();
        } else {
            producemodelOnly();
        }
        model_profile.sortHashMap();
    }

    private void produceTrainingAndTesting() {
        int tempStartTesting = testing_profile.start_of_testing;
        try {
            BufferedReader fileBufferedReader = new BufferedReader(new FileReader(currentFile));

            String token = fileBufferedReader.readLine();
            int count = tempStartTesting;
            while (token != null) {
                boolean write_to_test = (((count >= tempStartTesting) && (count <(tempStartTesting + testing_profile.singleDivision))));
                while (write_to_test) {
                    System.out.println("Reading cha: "+count);
                    char[] tempArr = token.toCharArray();
                    String testing_chunk = "";
                    for (char c : tempArr) {
                        testing_chunk += c;
                        token = token.replaceFirst(c + "", "");
                        count++;
                        write_to_test = ((count == tempStartTesting) || (count >= tempStartTesting) && (count <= (tempStartTesting + testing_profile.singleDivision)));
                        if (!write_to_test) {
                            write_to_profile(testing_chunk, "T");
                            break;
                        }
                    }
                }
                count+=token.toCharArray().length;
                write_to_profile(token, "M");
                token = fileBufferedReader.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(NgramCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        testing_profile.sortHashMap();
    }

    void write_to_profile(String token, String p) {
        List<String> ngrams = ngramFromLine(token);
        ngrams.forEach((tempNgram) -> {
            if (p.equals("M")) {
                model_profile.insert(tempNgram);
            } else {
                testing_profile.insert(tempNgram);
            }
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
        for (int i = 1; i < stringToConvert.length() - 3; i++) {
            String temp = stringToConvert.substring(i, i + 3);
            tempList.add(temp);
        }
        tempList.add(stringToConvert.substring(stringToConvert.length() - 3));
        return tempList;
    }

}
