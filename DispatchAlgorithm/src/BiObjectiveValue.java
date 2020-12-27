

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
    private final double TraveledDistance,TotalTardiness;
    public BiObjectiveValue(double td,double tt){
        this.TotalTardiness=tt;
        this.TraveledDistance=td;
    }
    public BiObjectiveValue(){
        this.TotalTardiness=Double.POSITIVE_INFINITY;
        this.TraveledDistance=Double.POSITIVE_INFINITY;
    }
    public BiObjectiveValue(double x){
        this.TotalTardiness=this.TraveledDistance=x;
    }
    public BiObjectiveValue(BiObjectiveValue F1,BiObjectiveValue F2){
        this.TotalTardiness=F1.TotalTardiness+F2.TotalTardiness;
        this.TraveledDistance=F1.TraveledDistance+F2.TraveledDistance;
    }
    public boolean Equals(BiObjectiveValue F){
        return this.TotalTardiness==F.TotalTardiness && this.TraveledDistance==F.TraveledDistance;
    }
    public boolean Improves(HeuristicSolution s){
        return this.Improves(s.Fitness);
    }
    public boolean Improves(AuxiliaryGraphNode node){
        return this.Improves(node.Label);
    }
    public boolean Improves(BiObjectiveValue fitness){
        return this.TraveledDistance<fitness.TraveledDistance && this.TotalTardiness<fitness.TotalTardiness;
    }
    public double getTraveledDistance(){
        return this.TraveledDistance;
    }
    public double getTotalTardiness(){
        return this.TotalTardiness;
    }
    @Override
    public String toString(){
        return "( Traveled Distance = "+this.TraveledDistance+" , Total Tardiness = "+this.TotalTardiness+")";
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