/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.stream.IntStream;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Othmane
 */
public class Solution {
    public BiObjectiveValue Fitness;
    public int RoutesCounter;
    public int[] GiantTour,Routes,RoutesAssignementToRiders;
    public double[] VisitMoments;
    
    public Solution(InputData d,int[] GR,BiObjectiveValue F){
        this.GiantTour=GR;
        this.Split(d,F);
    } 
    
    public Solution(InputData d,Vector<Integer> GT){
        this.GiantTour=new int[GT.size()];
        for(int i=0;i<GT.size();i++)
            this.GiantTour[i]=GT.elementAt(i);
        this.Split(d,null);
        this.LS(d);
    }
    
    public Solution(InputData d){
        this.GiantTour=IntStream.range(0,d.CostumersCount).toArray();
        for(int i=0;i<this.GiantTour.length;i++)
            new Motion(i,(int)(Math.random()*this.GiantTour.length)).Swap(this.GiantTour);
        this.Split(d,null);
        this.LS(d);
    }
        
    static void Mutation(Vector<Integer> tab,boolean mutation){
        if(mutation){
            int x=(int)(Math.random()*tab.size()),y=(int)(Math.random()*tab.size());
            for(int i=Math.min(x,y),j=Math.max(x,y);i<j;i++,j--)
                new Motion(i,j).Swap(tab);
        }
    }
    
    Solution Crossover(InputData d,Solution Father,boolean mutation,int ... cut_points){
        int n=(cut_points.length==0)?0:cut_points[0];
        int p=cut_points[(cut_points.length==1)?0:1];
        Vector<Integer> giant_tour=new Vector<>();
        IntStream.range(n,p)
                .map(j->Father.GiantTour[j])
                .forEach(giant_tour::addElement);
        int i=0;
        for(int j=p;j<this.GiantTour.length;j++)
            if(!giant_tour.contains(this.GiantTour[j]))
                if(giant_tour.size()<this.GiantTour.length-n)
                    giant_tour.addElement(this.GiantTour[j]);
                else{
                    giant_tour.insertElementAt(this.GiantTour[j],i);
                    i++;
                }
        for(int j=0;j<p;j++)
            if(!giant_tour.contains(this.GiantTour[j]))
                if(giant_tour.size()<this.GiantTour.length-n)
                    giant_tour.addElement(this.GiantTour[j]);
                else{
                    giant_tour.insertElementAt(this.GiantTour[j],i);
                    i++;
                }
        Mutation(giant_tour,mutation);
        return new Solution(d,giant_tour);
    }  
    
    void Split(InputData d,BiObjectiveValue F){
        AuxiliaryGraphNode[] Nodes=new AuxiliaryGraphNode[this.GiantTour.length+1];
        for(int i=0;i<Nodes.length;i++)
            Nodes[i]=new AuxiliaryGraphNode(d,i);
        Nodes[0].Posterior=0;
        Nodes[0].Label=new BiObjectiveValue(0d);
        Nodes[0].RidersAvailabilityTime=d.RidersLogInTimes;
        Nodes[0].RidersAvailabilityPositions=new int[d.RidersCount];
        for(int i=0;i<Nodes[0].RidersAvailabilityPositions.length;i++)
            Nodes[0].RidersAvailabilityPositions[i]=i;
        for(int i=0;i<this.GiantTour.length;i++)
            new AuxiliaryGraphArcs(d,this,Nodes,i,F);
        int i=this.GiantTour.length;
        this.Fitness=Nodes[i].Label.clone();
        this.VisitMoments=Nodes[i].VisitMoments.clone();
        Vector<Integer> v=new Vector<>();
        v.addElement(i);
        do{
            i=Nodes[i].Posterior;
            v.addElement(i);
        }while(i>0);
        this.Routes=new int[this.GiantTour.length];   
        this.RoutesAssignementToRiders=new int[v.size()-1];  
        int j=0,k=v.size()-1;
        this.RoutesCounter=0;
        while(j<this.GiantTour.length){
            if(j<v.elementAt(k)){
                this.Routes[this.GiantTour[j]]=this.RoutesCounter;
                j++;
            }
            else{
                k--;
                this.RoutesAssignementToRiders[this.RoutesCounter]=Nodes[v.elementAt(k)].Rider+1;
                this.RoutesCounter++;
            }
        }
    }
    
    void ShowSolution(InputData d){
        int route=1,i=0;
        System.out.println("This is the best known solution:");         
        System.out.println("The solution contains "+this.RoutesCounter+" routes made by "+d.RidersCount+" riders\n");
        System.out.println("The route number "+route+" is assigned to the rider number "+this.RoutesAssignementToRiders[route-1]+":");
        int costumer;
        while(i<this.GiantTour.length){ 
            costumer=this.GiantTour[i];
            if(this.Routes[this.GiantTour[i]]==route){
                System.out.println("\tThe costumer "+(costumer+1)+" is visited at the moment "+(int)this.VisitMoments[costumer]);
                i++;
            }
            else{
                route++;
                System.out.println("The route number "+route+" is assigned to the rider number "+this.RoutesAssignementToRiders[route-1]+":");
            }    
        }       
        System.out.println("The value of the objective functions equals to: "+this.Fitness.toString());
        System.out.println();
    }
    
    Solution ExchangeRoutes(InputData d,int r1,int r2){
        if(this.RoutesCounter==1)
            return this;
        int k;
        Solution s;
        int[] NewGiantRoute,order;
        NewGiantRoute=new int[this.GiantTour.length];
        order=new int[this.RoutesCounter];
        for(int i=0;i<this.RoutesCounter;i++)
            order[i]=i+1;
        new Motion(r1,r2).Swap(order);
        k=0;
        for(int route=0;route<this.RoutesCounter;route++)
            for(int i=0;i<this.GiantTour.length;i++)
                if(this.Routes[this.GiantTour[i]]==order[route]){
                    NewGiantRoute[k]=this.GiantTour[i];
                    k++;
                }
        s=new Solution(d,NewGiantRoute,this.Fitness);
        if(!this.Improves(s))
            return s;
        return this;
    }
    
    int getOneCostumer(int route){
        for(int costumer:this.GiantTour)
            if(this.Routes[costumer]==route+1)
                return costumer;
        return -1;
    }
    
    void ExchangeRoutes(InputData d){
        if(this.RoutesCounter==1)
            return;
        Solution s;
        int[] order=new int[this.RoutesCounter];
        for(int i=0;i<this.RoutesCounter;i++)
            order[i]=i;
        for(int i=0;i<order.length;i++)
            new Motion(i,(int)(Math.random()*order.length)).Swap(order);
        for(int r1=0;r1<this.RoutesCounter;r1++)
            for(int r2=r1+1;r2<this.RoutesCounter;r2++)
                if(this.RoutesAssignementToRiders[order[r1]]==this.RoutesAssignementToRiders[order[r2]]
                        || d.HasTheSameSupplier(this.getOneCostumer(order[r1]),this.getOneCostumer(order[r2]))){
                    s=this.ExchangeRoutes(d,order[r1],order[r2]);
                    if(!s.Fitness.EqualsTo(this.Fitness)){
                        this.Fitness=s.Fitness.clone();
                        this.RoutesCounter=s.RoutesCounter;
                        this.Routes=s.Routes.clone();
                        this.GiantTour=s.GiantTour.clone();
                        this.VisitMoments=s.VisitMoments.clone();
                        this.RoutesAssignementToRiders=s.RoutesAssignementToRiders.clone();
                        if(this.RoutesCounter!=order.length){
                            order=new int[this.RoutesCounter];
                            for(int i=0;i<this.RoutesCounter;i++)
                                order[i]=i;
                            for(int i=0;i<order.length;i++)
                                new Motion(i,(int)(Math.random()*order.length)).Swap(order);
                        }
                    }
                }
    }
    
    void LS(InputData d){
        BiObjectiveValue F=null;
        for(int i=0;i<this.GiantTour.length-1;i++)
            for(int j=i+1;j<this.GiantTour.length;j++)
                if(d.HasTheSameSupplier(this.GiantTour[i],this.GiantTour[j])){
                    F=this.Fitness.clone();
                    if(j>i+1){
                        for(int k=i,l=j;k<l;k++,l--)
                            new Motion(k,l).Swap(this.GiantTour);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            for(int k=i,l=j;k<l;k++,l--)
                                new Motion(k,l).Swap(this.GiantTour);
                            this.Fitness=F;
                        }
                        else
                            continue;
                        new Motion(i,j).Insertion(this.GiantTour);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            new Motion(i,j).InverseInsertion(this.GiantTour);
                            this.Fitness=F;
                        }
                        else
                            continue;
                        new Motion(i,j).InverseInsertion(this.GiantTour);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            new Motion(i,j).Insertion(this.GiantTour);
                            this.Fitness=F;
                        }
                        else
                            continue;
                    }
                    new Motion(i,j).Swap(this.GiantTour);
                    this.Split(d,F);
                    if(F.Improves(this)){
                        new Motion(i,j).Swap(this.GiantTour);
                        this.Fitness=F;
                    }
                }
        this.Split(d,F);
    }
    
    void LocalSearch(InputData d){
        BiObjectiveValue F=null;
        for(int i=0;i<this.GiantTour.length-1;i++)
            for(int j=i+1;j<this.GiantTour.length;j++)
                if(d.HasTheSameSupplier(this.GiantTour[i],this.GiantTour[j])){
                    F=this.Fitness.clone();
                    if(j>i+1){
                        for(int k=i,l=j;k<l;k++,l--)
                            new Motion(k,l).Swap(this.GiantTour);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            for(int k=i,l=j;k<l;k++,l--)
                                new Motion(k,l).Swap(this.GiantTour);
                            this.Fitness=F;
                        }
                        else{
                            this.ExchangeRoutes(d);
                            continue;
                        }
                        new Motion(i,j).Insertion(this.GiantTour);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            new Motion(i,j).InverseInsertion(this.GiantTour);
                            this.Fitness=F;
                        }
                        else{
                            this.ExchangeRoutes(d);
                            continue;
                        }
                        new Motion(i,j).InverseInsertion(this.GiantTour);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            new Motion(i,j).Insertion(this.GiantTour);
                            this.Fitness=F;
                        }
                        else{
                            this.ExchangeRoutes(d);
                            continue;
                        }
                    }
                    new Motion(i,j).Swap(this.GiantTour);
                    this.Split(d,F);
                    if(F.Improves(this)){
                        new Motion(i,j).Swap(this.GiantTour);
                        this.Fitness=F;
                    }
                    else
                        this.ExchangeRoutes(d);
                }
        this.Split(d,F);
    }
    
    void GraphDefinition(InputData d){
        String[] colors={"orange","green","blue","red","yellow","Grey"};
        if(d.RidersCount>colors.length)
            return; 
        int route=0,costumer;
        boolean[] conditions=new boolean[colors.length];
        GraphViz gv=new GraphViz();
        gv.addln(gv.start_graph());
        gv.addln("node [color=white];");
        for(int i=0;i<this.GiantTour.length;i++){
            costumer=this.GiantTour[i];
            if(this.Routes[costumer]==route)
                gv.addln((1+this.GiantTour[i-1])+" -> "+(1+costumer)+"[color="+colors[this.RoutesAssignementToRiders[route-1]-1]+"];");
            else{
                if(conditions[this.RoutesAssignementToRiders[route]-1])
                    for(int j=i-1;;j--){
                        int r=this.Routes[this.GiantTour[j]]-1;
                        if(this.RoutesAssignementToRiders[r]==this.RoutesAssignementToRiders[route]){
                            gv.addln((1+this.GiantTour[j])+"-> R"+(1+d.getRestaurantIndexOrderedFrom(costumer)-d.RidersCount)+"[color="+colors[this.RoutesAssignementToRiders[route]-1]+"];");
                            break;
                        }
                    }
                else{
                    conditions[this.RoutesAssignementToRiders[route]-1]=true;
                    gv.addln("C"+this.RoutesAssignementToRiders[route]+"-> R"+(1+d.getRestaurantIndexOrderedFrom(costumer)-d.RidersCount)+" [color="+colors[this.RoutesAssignementToRiders[route]-1]+"];");
                }
                gv.addln("R"+(1+d.getRestaurantIndexOrderedFrom(costumer)-d.RidersCount)+" -> "+(1+costumer)+" [color="+colors[this.RoutesAssignementToRiders[route]-1]+"];");
                route++; 
            }            
        }
        for(int rider=0;rider<d.RidersCount;rider++)
            if(conditions[rider])
                gv.addln("C"+(1+rider)+" [color="+colors[rider]+",style=filled];");
        for(int r=1;r<=d.RestaurantsCount;r++)
            gv.addln("R"+r+" [color=lightblue2,shape=box,style=filled];");
        gv.end_graph();
        String desktop_path=System.getProperty("user.home") + "/Desktop";
        File out=new File(desktop_path.replace("/","\\")+"\\graphe "+this.Fitness.toString()+".jpg");
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(),"jpg"),out);         
    }
    
    void GanttDiagramm(InputData d){
        GanttDemoCollection demo=new GanttDemoCollection("Gantt Diagram",d,this);
        demo.pack();
        demo.setExtendedState(Frame.MAXIMIZED_BOTH);
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);          
    }
    
    IntervalCategoryDataset createDataset(InputData d){
        TaskSeriesCollection collection=new TaskSeriesCollection();
        int route=1,FirstCostumer=0,LastCostumer;
        double start,end;
        String TaskName;
        for(int i=0;i<this.GiantTour.length;i++)
            if(this.Routes[this.GiantTour[i]]!=route || i+1==this.GiantTour.length){
                LastCostumer=i-1;
                if(i+1==this.GiantTour.length)
                    LastCostumer++;
                start=this.VisitMoments[this.GiantTour[FirstCostumer]];
                start-=d.getDistance(d.getRestaurantIndexOrderedFrom(this.GiantTour[FirstCostumer]),d.getCostumerIndex(this.GiantTour[FirstCostumer]));
                end=this.VisitMoments[this.GiantTour[LastCostumer]];
                TaskName="R"+(1+d.getRestaurantIndexOrderedFrom(this.GiantTour[FirstCostumer])-d.RidersCount)+"-";
                for(int k=FirstCostumer;k<LastCostumer;k++)
                    TaskName+=(1+this.GiantTour[k])+"-";
                TaskName+=(1+this.GiantTour[LastCostumer]);
                TaskSeries ts=new TaskSeries(TaskName);
                ts.add(new Task("Rider "+this.RoutesAssignementToRiders[route-1],new Date((int)start),new Date((int)end))); 
                collection.add(ts);
                route++;
                FirstCostumer=i;
            }
        return collection;
    }
    
    boolean Improves(Solution s){
        return this.Fitness.Improves(s.Fitness);
    }
    
    boolean Improves(BiObjectiveValue F){
        return this.Fitness.Improves(F);
    }
}