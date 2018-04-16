
import java.io.File;
import java.io.FileNotFoundException;
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
public class FileDivider {

    public String language_name;
    private static int singleDivision, current_start_of_test;

    public FileDivider(String lcode_lname) {
        language_name = lcode_lname;
        singleDivision = 0;
        current_start_of_test = 0;
    }

    public int get_start_of_Test() {
        int result = current_start_of_test;
        current_start_of_test += singleDivision;
        return result;
    }

    public void calculateDivisions() {
        File fmain = new File(Values.DEFAULT_DIREC + "/" + language_name);
        fmain = fmain.listFiles()[0];
        int charCount = 0;

        try {
            Scanner sc = new Scanner(fmain);
            while (sc.hasNextLine()) {

                String temp = sc.nextLine();
                for (char c : temp.toCharArray()) {
                    charCount++;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileDivider.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(charCount);
        singleDivision = (charCount / 10);
    }
    public int getsingleDivisionSize(){
        return singleDivision;
    }
            
    public static void main(String[] args) {
        FileDivider nc = new FileDivider("Ndebele");
        nc.calculateDivisions();
        System.out.println();
    }
}
