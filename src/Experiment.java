
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

    public Experiment(String language) {
        this.rootFile = new File(Values.DEFAULT_DIREC);
        this.profiles = new Profile[9];
        this.languages = rootFile.listFiles();
        this.language_being_tested = language;
        testingProfile = new Profile(language);
    }

    void startExperiment() {
        ExecutorService es = Executors.newFixedThreadPool(languages.length / 2);
        int i = 0;

        for (File f : languages) {
            profiles[i] = new Profile(f.getName());
            if (!f.getName().equals(language_being_tested)) {
                es.submit(new NgramCreator(f, profiles[i]));
            } else {
                es.submit(new NgramCreator(f, profiles[i], testingProfile));
            }
            i++;
        }
        es.shutdown();
        try {
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Experiment e = new Experiment("Ndebele");
        e.startExperiment();
        for (Profile p : e.profiles) {
            System.out.println(p.language_represented + " " + p.compareProfiles(e.testingProfile));
        }

    }
}
