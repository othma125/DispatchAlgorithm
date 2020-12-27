


import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Othmane
 */
public class CrossoverOperation implements Runnable{
    private final HeuristicSolution Father,Mother;
    private HeuristicSolution Child1,Child2;
    private final InputData Data;
    private final boolean Mutation,CrossoverType,LocalSearch;
    private boolean Waite;
    private int CutPoint1,CutPoint2;
    public CrossoverOperation(InputData d,HeuristicSolution father,HeuristicSolution mother) throws InterruptedException, CloneNotSupportedException{
        this.Data=d;
        this.Father=father;
        this.Mother=mother;
        this.Mutation=Math.random()<0.1d;
        this.CrossoverType=Math.random()<0.3d;
        this.LocalSearch=Math.random()<0.3d;
        if(this.CrossoverType)
            this.CutPoint1=(int)(this.Father.GiantRoute.length*Math.random());
        else{
            this.CutPoint1=(int)(this.Father.GiantRoute.length*Math.random());
            this.CutPoint2=this.CutPoint1+(int)((this.Father.GiantRoute.length-this.CutPoint1)*Math.random());
        }
        this.run();
        if(this.CrossoverType)
            this.Child1=this.Mother.Crossover(this.Father,this.Data,this.CutPoint1,this.Mutation);
        else
            this.Child1=this.Mother.Crossover(this.Father,this.Data,this.CutPoint1,this.CutPoint2,this.Mutation);
        if(this.LocalSearch)
            this.Child1.LocalSearch(this.Data);
    }
    @Override
    public void run(){
        this.Waite=true;
        if(this.CrossoverType)
            try {
                this.Child2=this.Father.Crossover(this.Mother,this.Data,this.CutPoint1,this.Mutation);
        } catch (InterruptedException ex) {
            Logger.getLogger(CrossoverOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(CrossoverOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        else
            try {
                this.Child2=this.Father.Crossover(this.Mother,this.Data,this.CutPoint1,this.CutPoint2,this.Mutation);
        } catch (InterruptedException ex) {
            Logger.getLogger(CrossoverOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(CrossoverOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(this.LocalSearch){
            try {
                this.Child2.LocalSearch(this.Data);
            } catch (InterruptedException ex) {
                Logger.getLogger(CrossoverOperation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(CrossoverOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.Waite=false;
    }
    public HeuristicSolution getBestChild() throws InterruptedException{
        while(this.Waite)
            Thread.sleep(0,1);
        if(this.Child1.Improves(this.Child2))
            return this.Child1;
        else
            return this.Child2;
    }
}