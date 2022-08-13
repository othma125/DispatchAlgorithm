



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Othmane
 */
public class AuxiliaryGraphArcs{
    public InputData Data;
    public BiObjectiveValue BestKnownValue;
    public Solution Solution;
    public AuxiliaryGraphNode[] Nodes;
    public int Index;
    
    public AuxiliaryGraphArcs(InputData d,Solution sol,AuxiliaryGraphNode[] nodes,int index,BiObjectiveValue F){
        this.Data=d;
        this.Solution=sol;
        this.Nodes=nodes;
        this.Index=index;
        this.BestKnownValue=F;
        this.run();
    }
    
    public void run(){
        if(this.BestKnownValue!=null && !this.Nodes[this.Index].Improves(this.BestKnownValue))
            return;
        double td;
        double tt;
        double DeliveryTime;
        double TraveledDistance=this.Data.getDistance(this.Data.getRestaurantIndexOrderedFrom(this.Solution.GiantTour[this.Index]),this.Data.getCostumerIndex(this.Solution.GiantTour[this.Index]));
        int costumer,n=0;
        BiObjectiveValue F;
        for(int j=this.Index;j<this.Solution.GiantTour.length;j++,n++){
            if(n==this.Data.MaxDeliveriesByRoute)
                return;
            costumer=this.Solution.GiantTour[j];
            for(int rider=0;rider<this.Data.RidersCount;rider++){
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
                        this.Nodes[j+1].VisitMoments[this.Solution.GiantTour[k]]=x;
                        if(k>this.Index){
                            x-=this.Data.getDistance(this.Data.getCostumerIndex(this.Solution.GiantTour[k-1]),this.Data.getCostumerIndex(this.Solution.GiantTour[k]));
                            x-=this.Data.DeliveryFixedTime;
                        }
                    }
                    this.Nodes[j+1].RidersAvailabilityPositions[rider]=this.Data.getCostumerIndex(costumer);
                    this.Nodes[j+1].RidersAvailabilityTime[rider]=this.Nodes[this.Index].RidersAvailabilityTime[rider]+td+this.Data.DeliveryFixedTime;
                }
            }
            if(j+1<this.Solution.GiantTour.length){
                if(this.Data.HasTheSameSupplier(costumer,this.Solution.GiantTour[j+1])){
                    TraveledDistance+=this.Data.getDistance(this.Data.getCostumerIndex(costumer),this.Data.getCostumerIndex(this.Solution.GiantTour[j+1]));
                    TraveledDistance+=this.Data.DeliveryFixedTime;
                }
                else
                    return;
            }
        }
    }
}