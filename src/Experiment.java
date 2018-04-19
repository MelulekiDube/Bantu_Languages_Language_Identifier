
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
    int size;

    public Experiment(String language, int s) {
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
        size = s;
    }

    void startExperiment() {
        ExecutorService es = Executors.newFixedThreadPool(languages.length);
        int i = 0;
        testingProfile.start_of_testing += ((a * testingProfile.singleDivision));
        for (File f : languages) {
            if (!f.getName().equals(language_being_tested)) {
                es.submit(new NgramCreator(f, profiles[i]));
            } else {
                testingProfile.testing_chunk_size = size;
                es.submit(new TestModelCreator(f, profiles[i], testingProfile));
            }
            i++;
        }
        es.shutdown();
        try {
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
        }
        a = 1;
    }

    float performTesting() {
        Pair<Profile, Integer> result = new Pair(testingProfile, Integer.MAX_VALUE);
        float accuracy = 0;
        int i = 0;
//        System.out.println("======================================================");
        for (; i < 10; i++) {
            result = new Pair(testingProfile, Integer.MAX_VALUE);
            startExperiment();
//            System.out.println("======================================================");
            for (Profile p : profiles) {
//                System.out.println("Comparing with: " + p.language_represented + " " + p.compareProfiles(testingProfile));
                if (p.compareProfiles(testingProfile) < result.getValue()) {
                    result = new Pair<>(p, p.compareProfiles(testingProfile));
                }
            }
//            System.out.println("Predicted language is: " + result.getKey().language_represented);
            if (result.getKey().language_represented.equals(language_being_tested)) {
                accuracy++;
            }
        }
//        System.out.println(((float)(accuracy/10)*100));
        return (float) (accuracy / i) * 100;
    }
}
