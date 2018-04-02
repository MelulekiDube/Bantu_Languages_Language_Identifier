
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki
 */
public class CreateNgrams {

    private final Map<String, String> fileMatchings;
    File file;
    File[] languages;

    public CreateNgrams() {
        fileMatchings = new HashMap<>();
        fixMatchings();
        file = new File("resources");
        languages = file.listFiles();
    }

    private void fixMatchings() {
        fileMatchings.put("zu", "isiZulu");
        fileMatchings.put("xh", "isiXhosa");
        fileMatchings.put("ve", "Venda");
        fileMatchings.put("ts", "Tsonga");
        fileMatchings.put("st", "Sesotho");
        fileMatchings.put("nr", "Ndebele");
        fileMatchings.put("nso", "Pedi");
        fileMatchings.put("tn", "Setswana");
        fileMatchings.put("ss", "Siswati");
    }

    public void perfomCreation() {
        ExecutorService es = Executors.newFixedThreadPool(languages.length);
        for (File f : languages) {
            es.submit(new NgramCreator(f));
        }
        es.shutdown();
    }

    public static void main(String[] args) {
        CreateNgrams cn=  new CreateNgrams();
        cn.perfomCreation();
    }
}
