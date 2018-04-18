
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
public class Experiment {

    File rootFile;
    File[] languages;
    String language_being_tested;
    Profile[] profiles;
    Profile testingProfile;
    FileDivider fd;
    private static int a = 0;

    public Experiment(String language) {
        this.rootFile = new File(Values.DEFAULT_DIREC);
        this.profiles = new Profile[9];
        this.languages = rootFile.listFiles();
        this.language_being_tested = language;
        testingProfile = new Profile(language);
        fd = new FileDivider(language);
        fd.calculateDivisions();
        int i = 0;
        testingProfile.singleDivision = fd.singleDivision;
        testingProfile.start_of_testing = fd.current_start_of_test;
        for (File f : languages) {
            profiles[i] = new Profile(f.getName());
            i++;
        }
    }

    void startExperiment() {
        ExecutorService es = Executors.newFixedThreadPool(languages.length / 2);
        int i = 0;
        testingProfile.start_of_testing += ((a * testingProfile.singleDivision));
        for (File f : languages) {
            profiles[i].frequencyTable.clear();
            if (!f.getName().equals(language_being_tested)) {
                es.submit(new NgramCreator(f, profiles[i]));
            } else {
                es.submit(new NgramCreator(f, profiles[i], testingProfile));
            }
            i++;
        }
        es.shutdown();
        try {
            System.out.println("Waiting for es to shutdown");
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
        }
        a = 1;
    }

    void performTesting() {
        Pair<Profile, Integer> result = new Pair(testingProfile, Integer.MAX_VALUE);
        int accuracy = 0;
        int i = 0;
        for (; i < 9; i++) {
            startExperiment();
            System.out.println("======================================================");
            testingProfile.printFirstTen();
            System.out.println("======================================================");
            for (Profile p : profiles) {
                if (p.compareProfiles(testingProfile) < result.getValue()) {
                    result = new Pair<>(p, p.compareProfiles(testingProfile));
                }
            }
            System.out.println("Predicted language is: " + result.getKey().language_represented);
            if (result.getKey().language_represented.equals(language_being_tested)) {
                accuracy++;
            }
        }
        System.out.println("Accuracy is: " + ((accuracy / 1) * 100));
    }

    public static void main(String[] args) {
        Experiment e = new Experiment("Xhosa");
        System.out.println(e.testingProfile.singleDivision);
        System.out.println("Exp 1");
        e.performTesting();
    }
}
