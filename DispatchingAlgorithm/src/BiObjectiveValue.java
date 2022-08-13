

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Othmane
 */
public class BiObjectiveValue{
    private final double TraveledDistance;
    private final double TotalTardiness;
    
    public BiObjectiveValue(double traveled_distance,double total_tardiness){
        this.TotalTardiness=total_tardiness;
        this.TraveledDistance=traveled_distance;
    }
    
    public BiObjectiveValue(){
        this(Double.POSITIVE_INFINITY);
    }
    
    public BiObjectiveValue(double x){
        this.TotalTardiness=this.TraveledDistance=x;
    }
    
    public BiObjectiveValue(BiObjectiveValue F1,BiObjectiveValue F2){
        this.TotalTardiness=F1.TotalTardiness+F2.TotalTardiness;
        this.TraveledDistance=F1.TraveledDistance+F2.TraveledDistance;
    }
    
    boolean EqualsTo(BiObjectiveValue F){
        return this.TotalTardiness==F.TotalTardiness && this.TraveledDistance==F.TraveledDistance;
    }
    
    boolean Improves(Solution s){
        return this.Improves(s.Fitness);
    }
    
    boolean Improves(AuxiliaryGraphNode node){
        return this.Improves(node.Label);
    }
    
    boolean Improves(BiObjectiveValue fitness){
        return this.TraveledDistance<fitness.TraveledDistance && this.TotalTardiness<fitness.TotalTardiness;
    }
    
    double getTraveledDistance(){
        return this.TraveledDistance;
    }
    
    double getTotalTardiness(){
        return this.TotalTardiness;
    }
    
    @Override
    public String toString(){
        return "(Traveled Distance = "+this.TraveledDistance+" , Total Tardiness = "+this.TotalTardiness+")";
    }
    
    @Override
    public BiObjectiveValue clone(){
        return new BiObjectiveValue(this);
    }
    
    public BiObjectiveValue(BiObjectiveValue fitness){
        this.TotalTardiness=fitness.TotalTardiness;
        this.TraveledDistance=fitness.TraveledDistance;
    }
}