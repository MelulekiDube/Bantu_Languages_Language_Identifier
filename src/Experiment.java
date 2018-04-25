
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    File f;
    BufferedWriter bw;

    /**
     *
     * @param language the language to be tested by the experiment
     * @param ts which is the test size
     * @param ms the model size. This is the size of the models through out the
     * model.
     */
    public Experiment(String language, int ts, int ms) {
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
        for (File file : languages) {
            profiles[i] = new Profile(file.getName());
            profiles[i].num_items_in_model = ms;
            i++;
        }
        size = ts;
        f = new File("result/" + language + "test_result");
        FileWriter fw;
        try {
            if (!f.exists()) {
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdir();
                }
                f.createNewFile();
                fw = new FileWriter(f);
            } else {
                fw = new FileWriter(f, true);
            }
            bw = new BufferedWriter(fw);
            bw.write("=======================================================================================================\n");
            bw.write("language being tested is: " + language + "\t Test chunk size is: " + size + "\tModel size is: " + ms);
            bw.newLine();
            bw.write("Predicted languages are: ");
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    void startExperiment() {
        ExecutorService es = Executors.newFixedThreadPool(languages.length);
        int i = 0;
        testingProfile.start_of_testing += ((a * testingProfile.singleDivision));
        for (File file : languages) {
            if (!file.getName().equals(language_being_tested)) {
                es.submit(new ModelCreator(file, profiles[i]));
            } else {
                testingProfile.testing_chunk_size = size;
                es.submit(new TestModelCreator(file, profiles[i], testingProfile));
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
        Pair<Profile, Integer> result;
        float accuracy = 0;
        int i = 0;
//        System.out.println("======================================================");
        for (; i < 9; i++) {
            result = new Pair(testingProfile, Integer.MAX_VALUE);
            startExperiment();
//            System.out.println("======================================================");
            for (Profile p : profiles) {
                if (testingProfile.frequencyTable.size() == 2) {
                    testingProfile.printFirstTen();
                    System.out.println(testingProfile.singleDivision);
                    System.out.println(testingProfile.start_of_testing);
                    System.out.println(testingProfile.testing_chunk_size);
                }
//                System.out.println("Comparing with: " + p.language_represented + " " + p.compareProfiles(testingProfile));
                if (p.compareProfiles(testingProfile) < result.getValue()) {
                    result = new Pair<>(p, p.compareProfiles(testingProfile));
                }
            }
            try {
                bw.write(result.getKey().language_represented);
                bw.write(" ");
            } catch (IOException ex) {
                Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (result.getKey().language_represented.equals(language_being_tested)) {
                accuracy++;
            }
        }
        try {
            bw.newLine();
            bw.write("Precision is: " + Float.toString((float) (accuracy / i) * 100));
            bw.newLine();
            bw.write("=======================================================================================================\n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (float) (accuracy / i) * 100;
    }
}
