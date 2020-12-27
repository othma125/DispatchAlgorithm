



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Othmane
 */
public class AuxiliaryGraphArcs implements Runnable{
    public InputData Data;
    public BiObjectiveValue BestKnownValue;
    public HeuristicSolution Solution;
    public AuxiliaryGraphNode[] Nodes;
    public int Index;
    public double ObjectiveFitness;
    public AuxiliaryGraphArcs(InputData d,HeuristicSolution sol,AuxiliaryGraphNode[] nodes,int index,BiObjectiveValue F){
        this.Data=d;
        this.Solution=sol;
        this.Nodes=nodes;
        this.Index=index;
        this.BestKnownValue=F;
        this.run();
    }
    @Override
    public void run(){
        if(this.BestKnownValue!=null && !this.Nodes[this.Index].Improves(this.BestKnownValue)){
            this.Nodes[this.Index].LastNode=this.Solution.GiantRoute.length;
            return;
        }
        double td,tt,DeliveryTime,TraveledDistance=this.Data.getDistance(this.Data.getRestaurantIndexOrderedFrom(this.Solution.GiantRoute[this.Index]),this.Data.getCostumerIndex(this.Solution.GiantRoute[this.Index]));
        int costumer,n=0;
        BiObjectiveValue F;
        for(int j=this.Index;j<this.Solution.GiantRoute.length;this.Nodes[this.Index].LastNode++,j++,n++){
            if(n==this.Data.MaxDeliveriesByRoute){
                this.Nodes[this.Index].LastNode=this.Solution.GiantRoute.length;
                return;
            }
            costumer=this.Solution.GiantRoute[j];
            for(int rider=0;rider<this.Data.RidersCounter;rider++){
                td=this.Data.getDistance(this.Nodes[this.Index].RidersAvailabilityPositions[rider],this.Data.getRestaurantIndexOrderedFrom(costumer))+TraveledDistance;
                DeliveryTime=Math.max(this.Nodes[this.Index].RidersAvailabilityTime[rider]+td-TraveledDistance,this.Data.ProductPreparingTime)+TraveledDistance;
                tt=DeliveryTime-this.Data.CostumersDueDates[costumer];
                F=new BiObjectiveValue(td,Math.max(0,tt));
                F=new BiObjectiveValue(F,this.Nodes[this.Index].Label);
                if(F.Improves(this.Nodes[j+1])){
                    this.Nodes[j+1].Label=F.clone();
                    this.Nodes[j+1].Posterior=this.Index;
                    this.Nodes[j+1].Rider=rider;
                    this.Nodes[j+1].VisitMoments=this.Nodes[this.Index].VisitMoments.clone();
                    this.Nodes[j+1].RidersAvailabilityPositions=this.Nodes[this.Index].RidersAvailabilityPositions.clone();
                    this.Nodes[j+1].RidersAvailabilityTime=this.Nodes[this.Index].RidersAvailabilityTime.clone();
                    double x=DeliveryTime;
                    for(int k=j;k>=this.Index;k--){
                        this.Nodes[j+1].VisitMoments[this.Solution.GiantRoute[k]]=x;
                        if(k>this.Index){
                            x-=this.Data.getDistance(this.Data.getCostumerIndex(this.Solution.GiantRoute[k-1]),this.Data.getCostumerIndex(this.Solution.GiantRoute[k]));
                            x-=this.Data.DeliveryFixedTime;
                        }
                    }
                    this.Nodes[j+1].RidersAvailabilityPositions[rider]=this.Data.getCostumerIndex(costumer);
                    this.Nodes[j+1].RidersAvailabilityTime[rider]=this.Nodes[this.Index].RidersAvailabilityTime[rider]+td+this.Data.DeliveryFixedTime;
                }
            }
            if(j+1<this.Solution.GiantRoute.length){
                if(this.Data.HasTheSameSupplier(costumer,this.Solution.GiantRoute[j+1])){
                    TraveledDistance+=this.Data.getDistance(this.Data.getCostumerIndex(costumer),this.Data.getCostumerIndex(this.Solution.GiantRoute[j+1]));
                    TraveledDistance+=this.Data.DeliveryFixedTime;
                }
                else{
                    this.Nodes[this.Index].LastNode=this.Solution.GiantRoute.length;
                    return;
                }
            }
        }
    }
}