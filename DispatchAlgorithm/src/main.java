
import java.io.File;
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
        InputData d=new InputData(new File("Solomon\\c101.txt"),30/*CostumersCounter*/,6/*RidersCounter*/,5/*RestaurantsCounter*/);
        Vector<HeuristicSolution> v=HeuristicSolution.GeneticAlgorithm(d,100);
        for(HeuristicSolution s:v){
            s.ShowSolution(d);
            s.GraphDefinition(d);
            s.GanttDiagramm(d);
        }
    }
}