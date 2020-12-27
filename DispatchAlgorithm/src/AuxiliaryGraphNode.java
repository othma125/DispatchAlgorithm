

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
    public int Posterior,LastNode,LoadUnloadMoment,Rider;
    public double[] VisitMoments,RidersAvailabilityTime;
    public int[] RidersAvailabilityPositions;
    public BiObjectiveValue Label;
    public boolean Improves(BiObjectiveValue F){
        return this.Label.Improves(F);
    }
    public AuxiliaryGraphNode(InputData d,int i){
        this.VisitMoments=new double[d.CostumersCounter];
        this.Label=new BiObjectiveValue();
        this.LastNode=i;
    }
    public static boolean Wait(AuxiliaryGraphNode[] Nodes,int index){
        for(int i=index-1;i>=0;i--)
            if(Nodes[i].LastNode<index)
                return true;
        return false;
    }
}