
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
    FileDivider fd;

    NgramCreator(File f, Profile pr) {
        currentFile = f.listFiles()[0];
        model_profile = pr;
        testing_profile = null;
        test_flag = false;
        fd = null;
    }

    public NgramCreator(File f, Profile model_profile, Profile testing_profile) {
        this.currentFile = f.listFiles()[0];
        this.model_profile = model_profile;
        this.testing_profile = testing_profile;
        test_flag = true;
        fd = new FileDivider(model_profile.language_represented);
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
        int tempStartTesting = fd.get_start_of_Test();
        try {
            BufferedReader fileBufferedReader = new BufferedReader(new FileReader(currentFile));
            String token;
            token = fileBufferedReader.readLine();
            int count = 0;
            while (token != null) {
                List<String> ngrams = ngramFromLine(token);
                for (String tempNgram : ngrams) {
                    if (count == tempStartTesting)//we are at the start of the testing data
                    {
                        testing_profile.insert(tempNgram);
                    } else if (count < tempStartTesting)// we haven't started reading the testing data
                    {
                        model_profile.insert(tempNgram);
                    } else if ((count > tempStartTesting) && (count < (tempStartTesting + fd.getsingleDivisionSize())))//then we are inbetween the testing data
                    {
                        testing_profile.insert(tempNgram);
                    } else {
                        model_profile.insert(tempNgram);
                    }
                }
                count += token.toCharArray().length;
                token = fileBufferedReader.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(NgramCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        testing_profile.sortHashMap();
    }

    void producemodelOnly() {
        try {
            try (BufferedReader fileRead = new BufferedReader(new FileReader(currentFile))) {
                String token;
                token = fileRead.readLine();
                while (token != null) {
                    List<String> ngrams = ngramFromLine(token);
                    ngrams.forEach((tempNgram) -> {
                        model_profile.insert(tempNgram);
                    });
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
