
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Othmane
 */
public class main {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.lang.CloneNotSupportedException
     */
    public static void main(String[] args) throws InterruptedException, CloneNotSupportedException {
        // TODO code application logic here
        InputData data=new InputData(new File("Solomon\\c101.txt")
                                                                ,30/*CostumersCount*/
                                                                ,6/*RidersCount*/
                                                                ,5/*RestaurantsCount*/);
        new GeneticAlgorithm(data).Run(5/*Seconds*/)
                                .stream()
                                .forEach(s->{
                                    s.ShowSolution(data);
                                    s.GraphDefinition(data);
                                    s.GanttDiagramm(data);
                                });
    }
}