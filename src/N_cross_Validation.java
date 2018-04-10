import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki
 */
public class N_cross_Validation {

    private final File testing;
    private final File model;
    private static final String RFOLDER = "resources";
    public Pair<String, String> language_file_identifier;
    private static int prevBegTestLine, fileDivision;

    public N_cross_Validation(Pair<String, String> lcode_lname) {
        language_file_identifier = lcode_lname;
        prevBegTestLine = 0;
        fileDivision = 0;
        this.testing = new File(RFOLDER + "/" + lcode_lname.getKey() + "/" + lcode_lname.getValue() + "testingFile");
        this.model = new File(RFOLDER + "/" + lcode_lname.getKey() + "/" + lcode_lname.getValue() + "modelFile");
        if(this.testing.exists()||this.model.exists()) {
            this.testing.delete();
            this.model.delete();
            
            try {
                this.model.createNewFile();
                this.testing.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(N_cross_Validation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void divideMainFile() {
        getLineNumber();
        int lineCounter = 0;
        File f = new File(RFOLDER + "/" + language_file_identifier.getKey()+"/ngram");
        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                int limit =prevBegTestLine + fileDivision;
                if (lineCounter < limit) {
                    writeTOTestFile(sc.nextLine());
                } else {
                    writeTOModelFile(sc.nextLine());
                }
                lineCounter++;
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
        prevBegTestLine += fileDivision;
    }

    public void writeTOModelFile(String line_to_write) {
        FileWriter modelFile;
        try {
            modelFile = new FileWriter(model, true);
            try (PrintWriter pw = new PrintWriter(modelFile)) {
                pw.println(line_to_write);
            }
            modelFile.close();
        } catch (IOException ex) {
            Logger.getLogger(N_cross_Validation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeTOTestFile(String line_to_write) {
        FileWriter testFile;
        try {
            testFile = new FileWriter(testing, true);
            try (PrintWriter pw = new PrintWriter(testFile)) {
                pw.println(line_to_write);
            }
            testFile.close();
        } catch (IOException ex) {
            Logger.getLogger(N_cross_Validation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getLineNumber() {
        File fmain = new File(RFOLDER + "/" + language_file_identifier.getKey()+"/ngram");
        int lineCount = 0;
        Scanner sc = null;
        try {
            sc = new Scanner(fmain);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(N_cross_Validation.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (sc.hasNext()) {
            lineCount++;
            sc.nextLine();
        }
        fileDivision = (int)(lineCount / 10);
    }
    
    public static void main(String [] args){
        N_cross_Validation ncv= new N_cross_Validation(new Pair<>("nr", "Ndebele"));
        ncv.divideMainFile();
    }
}
