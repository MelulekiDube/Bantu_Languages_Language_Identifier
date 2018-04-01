
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki
 */
public class Language_Model {

    private final HashMap<String, Integer> unsortedNgrams;

    public Language_Model() {
        unsortedNgrams = new HashMap<>();
    }

    /**
     * Constructor that generates the model. Takes in a String with the path of
     * the file with the n-grams for that model
     *
     * @param filePath
     */
    public Language_Model(String filePath) {
        unsortedNgrams = new HashMap<>();
        generateModel(filePath);
    }

    /**
     * Method to generate a language model from a file {filePatj} that consists
     * of n-grams
     *
     * @param filePath which is the file where the n-grams are located for the
     * language
     */
    public final void generateModel(String filePath) {
        //creating a file object for reading
        File file = new File(filePath);
        try {
//            Opening a file reader and buffered reader to read the file
            Scanner fileRead = new Scanner(file);
            while (fileRead.hasNextLine()) {
                insert(fileRead.nextLine());
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void insert(String ngram) {
        int currentValue = (unsortedNgrams.containsKey(ngram)) ? unsortedNgrams.get(ngram) : 0;
        unsortedNgrams.put(ngram, currentValue++);
    }

    public String[] getNgrams() {
        sortHashMap();
        return (String[]) unsortedNgrams.keySet().toArray();
    }

    /**
     * Method to sort the current hash Map
     */
    private void sortHashMap() {
        List<Map.Entry<String, Integer>> unsortedNGramsList = new LinkedList<>(unsortedNgrams.entrySet());
        //sorting the list
        Collections.sort(unsortedNGramsList, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> o1.getValue().compareTo(o2.getValue()));
        //converting the sorted list to the hashMap
        unsortedNgrams.clear();

        unsortedNGramsList.forEach((tempItem) -> {
            unsortedNgrams.put(tempItem.getKey(), tempItem.getValue());
        });
    }
}
