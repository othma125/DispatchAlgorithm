

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Othmane
 */
public class AuxiliaryGraphNode{
    public int Posterior;
    public int LoadUnloadMoment;
    public int Rider;
    public double[] VisitMoments;
    public double[] RidersAvailabilityTime;
    public int[] RidersAvailabilityPositions;
    public BiObjectiveValue Label;
    
    public AuxiliaryGraphNode(InputData d,int i){
        this.VisitMoments=new double[d.CostumersCount];
        this.Label=new BiObjectiveValue();
    }
    
    boolean Improves(BiObjectiveValue F){
        return this.Label.Improves(F);
    }
}