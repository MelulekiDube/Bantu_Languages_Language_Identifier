
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki
 */
public class Driver extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Accuracy chart"); // Title for the window opening
        /*Next thing you need to do is define the x and y exis setting them nto number axis*/
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        /*Setting the name of the two axis*/
        xAxis.setLabel("Chunk Size as number of charecters in each chunk");
        yAxis.setLabel("Accuracy as a %");

        /*defining linechar and setting the title for the graph*/
        final LineChart<Number, Number> lChart = new LineChart<>(xAxis, yAxis);
        lChart.setTitle("Accuracy vs Chunk size");
        /*Defining the series for the XYChart now*/
        XYChart.Series series = new XYChart.Series();
        /*series to be used for the different model sizes*/
 /*Defining experiment*/
        float accuracy = 0;
        File f = new File(Values.DEFAULT_DIREC);
        File result = new File("result");
        result.createNewFile();
        try {
            BufferedWriter bw= new BufferedWriter(new FileWriter(result));
            File files[] = f.listFiles();
            for (int i = 0; i < 100000; i += 1000) {
                Experiment e = null;
                accuracy = 0;
                for (File tempF : files) {
                    e = new Experiment(tempF.getName(), 1000 + i);
                    accuracy += e.performTesting();
                    bw.write(""+accuracy+" ");
                }
                bw.write(""+(accuracy / 9));
                bw.newLine();
                series.getData().add(new XYChart.Data<>(e.size, (accuracy / 9)));
            }
        }catch(IOException e){
            System.out.println(e.toString());
        }
        Scene scene = new Scene(lChart, 800, 600);
        lChart.getData().add(series);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
