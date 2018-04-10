import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
            try (BufferedReader fileRead = new BufferedReader(new FileReader(currentFile))) {
                String token;
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(ngramFile))) {
                    token= fileRead.readLine();
                    while (token!=null) {
                        List<String> ngrams = ngramFromLine(token);
                        ngrams.forEach((tempNgram) -> {
                            try {
                                bw.write(tempNgram + "\n");
                            } catch (IOException ex) {
                                Logger.getLogger(NgramCreator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        token= fileRead.readLine();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.setOut(System.out);
            System.out.println(ex);
        } catch (IOException ex) {
            Logger.getLogger(NgramCreator.class.getName()).log(Level.SEVERE, null, ex);
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
        for (int i = 1;i < stringToConvert.length()-3; i++) {
            String temp = stringToConvert.substring(i, i + 3);
            tempList.add(temp);
        }
        tempList.add(stringToConvert.substring(stringToConvert.length() - 3));
        return tempList;
    }

}
