
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki
 */
public class Profile {

    public final LinkedHashMap<String, Integer> frequencyTable;
    public int start_of_testing, singleDivision;
    public String language_represented;
    int testing_chunk_size = 0;
    int num_items_in_model=0;
    /**
     * Default constructor that sets up the profile parameters
     *
     * @param lang
     */
    public Profile(String lang) {
        this.language_represented = lang;
        frequencyTable = new LinkedHashMap<>();
        start_of_testing = 0;
    }

    /**
     *
     * Method that adds the new ngram into the map
     *
     * @param ngram the ngram being added to the model
     */
    public void insert(String ngram) {
        int currentValue = (frequencyTable.containsKey(ngram)) ? frequencyTable.get(ngram) : 0;
        frequencyTable.put(ngram, ++currentValue);
//        printFirstTen();
    }

    /**
     *
     * @return a string array of the resulting ngrams from the model
     */
    public String[] getNgrams() {
        sortHashMap();
        return (String[]) frequencyTable.keySet().toArray();
    }

    /**
     * Method to sort the current hash Map
     */
    public void sortHashMap() {
        List<Map.Entry<String, Integer>> unsortedNGramsList = new LinkedList<>(frequencyTable.entrySet());
        //sorting the list
        Collections.sort(unsortedNGramsList, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> o2.getValue().compareTo(o1.getValue()));
        //converting the sorted list to the hashMap
        frequencyTable.clear();
        unsortedNGramsList.forEach((tempItem) -> {
//            System.out.println(tempItem.getKey()+" "+tempItem.getValue());
            frequencyTable.put(tempItem.getKey(), tempItem.getValue());
        });
    }

    public void printFirstTen() {
        int a = 0;
        for (Map.Entry<String, Integer> temp : frequencyTable.entrySet()) {
            System.out.println(temp.getKey() + " " + temp.getValue());
            a++;
            if (a == 10) {
                return;
            }
        }
    }

    public void setStartOfTesting(int newStart) {
        this.start_of_testing = newStart;
    }

    public int getStartOfTesting() {
        return this.start_of_testing;
    }

    public int compareProfiles(Profile testing_profile) {
        List<String> profile_ngrams = new ArrayList<>(frequencyTable.keySet());
        List<String> testing_profile_ngrams = new ArrayList<>(testing_profile.frequencyTable.keySet());
        int limit= (testing_profile_ngrams.size()>=300) ?300:testing_profile_ngrams.size();
        int distance_between_profiles = 0;
        boolean found = false;
        try {
            for (int i = 0; i < limit; i++) {
//                System.out.println("==================================");
                found = false;
                for (int j = 0; j < limit; j++) {
                    if (testing_profile_ngrams.get(i).equals(profile_ngrams.get(j))) {
//                        System.out.println("Comparing " + testing_profile_ngrams.get(i) + " to " + profile_ngrams.get(j));
                        distance_between_profiles += (Math.abs(j - i));
                        found = true;
                    }
                }
                if (!found) {
                    distance_between_profiles += 300;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error is "+ex.getMessage());
            for (StackTraceElement t : ex.getStackTrace()) {
                System.out.println("================");
                System.out.println("Error on File: " + t.getFileName());
                System.out.println("Error on File: " + t.getClassName());
                System.out.println("Error on File: " + t.getMethodName());
                System.out.println("Error on File: " + t.getLineNumber());
                System.out.println("================");
            }
        }
        return distance_between_profiles;
    }
}
