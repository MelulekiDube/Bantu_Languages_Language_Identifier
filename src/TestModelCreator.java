
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
public class TestModelCreator implements Runnable {

    private File currentFile;
    private File ngramFile;
    private Profile model_profile;
    Profile testing_profile;
    int char_counter = 0;

    public TestModelCreator(File f, Profile model_profile, Profile testing_profile) {
        this.currentFile = f.listFiles()[0];
        this.model_profile = model_profile;
        this.testing_profile = testing_profile;
    }

    @Override
    public void run() {
        testing_profile.frequencyTable.clear();
        model_profile.frequencyTable.clear();
        produceTrainingAndTesting();
        model_profile.sortHashMap();
    }

    private void produceTrainingAndTesting() {
        int tempStartTesting = testing_profile.start_of_testing;
        int tempEndTesting = testing_profile.singleDivision + tempStartTesting;
        int charecters_written_to_testing_model = 0;
        try {
            BufferedReader fileBufferedReader = new BufferedReader(new FileReader(currentFile));
            String token = fileBufferedReader.readLine();
            int count = 0;
            String testing_chunk = "";
            while (token != null) {
//                System.out.println("Reading line: " + temp);
                boolean write_to_test = ((count >= tempStartTesting && count < tempEndTesting));
                char[] tempArr = token.toCharArray();
//                System.out.println(token);
                if (write_to_test) {
                    for (char c : tempArr) {
                        if (charecters_written_to_testing_model <= testing_profile.testing_chunk_size) {
                            testing_chunk += c;
//                            System.out.print(c);
                            charecters_written_to_testing_model++;
                        }
                        count++;
                        write_to_test = ((count >= tempStartTesting && count < tempEndTesting));
                        if (!write_to_test) {
                            write_to_profile(testing_chunk, "T");
                            write_to_profile(token, "M");
                            testing_chunk = "";
//                            System.out.println(testing_chunk);
                            break;
                        }
                    }
                } else {
                    String model_chunk_data = "";
                    for (char c : tempArr) {
                        model_chunk_data += c;
//                        token = token.replaceFirst(c + "", "");
                        count++;
                        write_to_test = ((count >= tempStartTesting && count < tempEndTesting));
                        if (write_to_test) {
                            if (charecters_written_to_testing_model <= testing_profile.testing_chunk_size) {
                                testing_chunk += c;
                                charecters_written_to_testing_model++;
                            }
                            token = token.replace(c + "", "");
                        }
                    }
                    write_to_profile(testing_chunk, "T");
                    write_to_profile(model_chunk_data, "M");
//                    System.out.println(testing_chunk);
                }
                token = fileBufferedReader.readLine();
            }
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace().length);
            for (StackTraceElement t : ex.getStackTrace()) {
                System.out.println("================");
                System.out.println("Error on File: " + t.getFileName());
                System.out.println("Error on File: " + t.getClassName());
                System.out.println("Error on File: " + t.getMethodName());
                System.out.println("Error on File: " + t.getLineNumber());
                System.out.println("================");
            }
        }
        testing_profile.sortHashMap();
    }

    void write_to_profile(String token, String p) {
        List<String> ngrams;
        if (p.equals("M")) {
            String modelData = "";
            for (char c : token.toCharArray()) {
//                System.out.println("Comparing: " + char_counter + " to " + model_profile.num_items_in_model);
                if (char_counter <= model_profile.num_items_in_model) {
                    char_counter++;
                    modelData += c;
                } else {
                    break;
                }
            }
            ngrams = ngramFromLine(modelData);
        } else {
            ngrams = ngramFromLine(token);
        }
        ngrams.forEach((tempNgram) -> {
            if (p.equals("M")) {
                model_profile.insert(tempNgram);
            } else {
                testing_profile.insert(tempNgram);
            }
        });
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
