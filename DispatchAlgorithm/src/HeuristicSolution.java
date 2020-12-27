/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Frame;
import java.io.File;
import java.util.Date;
import java.util.Vector;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Othmane
 */
public class HeuristicSolution {
    public BiObjectiveValue Fitness;
    public int RoutesCounter;
    public int[] GiantRoute,Routes,RoutesAssignementToRiders;
    public double[] VisitMoments;
    public static Vector<HeuristicSolution> GeneticAlgorithm(InputData d,int IterationsCounter) throws InterruptedException, CloneNotSupportedException{
        long begining=System.currentTimeMillis();
        int PopulationLength=30;
        HeuristicSolution[] population=HeuristicSolution.InitialPopulation(d,PopulationLength);
        HeuristicSolution.QuickSort(population,0,population.length-1); 
        HeuristicSolution s;
        int half=population.length/2,j;
        System.out.println("\t"+population[0].Fitness.toString()+"\t\tat the moment "+(System.currentTimeMillis()-begining)+" ms");
        for(int i=1;i<=IterationsCounter;i++){
            if(Math.random()<0.8)
                s=NewSolution(d,population);
            else{
                if(Math.random()<0.1d)
                    s=new HeuristicSolution(d);
                else{
                    j=(int)(Math.random()*half);
                    s=population[j].ExchangeRoutes(d,(int)(Math.random()*population[j].RoutesCounter),(int)(Math.random()*population[j].RoutesCounter));
                }
            }
            s.LocalSearch(d);
            if(s.Improves(population[PopulationLength-1]) || !population[PopulationLength-1].Improves(s)){
                if(s.Improves(population[0]))
                    System.out.println("\t"+s.Fitness.toString()+"\t\tat the moment "+(System.currentTimeMillis()-begining)+" ms");
                population[half+(int)(Math.random()*(population.length-half))]=s;
                HeuristicSolution.QuickSort(population,0,population.length-1); 
            }
        }
        return HeuristicSolution.PremierFrontSolution(population);
    }
    public static Vector<HeuristicSolution> PremierFrontSolution(HeuristicSolution[] population){
        Vector<HeuristicSolution> v=new Vector<>();
        double max=Double.POSITIVE_INFINITY;
        for(HeuristicSolution s:population){
            if(s.Fitness.getTraveledDistance()<max){
                max=s.Fitness.getTraveledDistance();
                v.addElement(s);
            }
            else
                return v;
        }
        return null;
    }
    public static void QuickSort(HeuristicSolution[] population,int x,int y){
        if(x<y){
            int p=partition(population,x,y);
            QuickSort(population,x,p-1);
            QuickSort(population,p+1,y);
        }
    }
    public static int partition(HeuristicSolution[] population,int x,int y){
        double pivot=population[y].Fitness.getTraveledDistance();
        int i=x;
        for(int j=x;j<y;j++)
            if(population[j].Fitness.getTraveledDistance()<pivot){
                new SwapMovement(i,j).Execute(population);
                i++;
            }
        new SwapMovement(i,y).Execute(population);
        return i;
    }
    public static HeuristicSolution[] InitialPopulation(InputData d,int PopulationLength) throws InterruptedException, CloneNotSupportedException{
        HeuristicSolution[] population=new HeuristicSolution[PopulationLength];
        int i=0;
//        System.out.println("Initial population:");
        while(i<PopulationLength){
            population[i]=new HeuristicSolution(d);
            if(Math.random()<0.3d)
                population[i].LocalSearch(d);
//            System.out.println(population[i].Fitness.toString());
            i++;
        }
        return population;
    }
    public static HeuristicSolution NewSolution(InputData d,HeuristicSolution[] population) throws InterruptedException, CloneNotSupportedException{
        int half=population.length/2,father=(int)(Math.random()*half),mother;
        if(Math.random()<0.7d){
            do{
                mother=(int)(Math.random()*half);
            }while(mother==father);
        }
        else
            mother=half+(int)(Math.random()*(population.length-half));
        return new CrossoverOperation(d,population[mother],population[father]).getBestChild();
    }
    public static void Mutation(Vector<Integer> tab,boolean mutation){
        if(mutation){
            int x=(int)(Math.random()*tab.size()),y=(int)(Math.random()*tab.size());
            for(int i=Math.min(x,y),j=Math.max(x,y);i<j;i++,j--)
                new SwapMovement(i,j).Execute(tab);
        }
    }
    public HeuristicSolution Crossover(HeuristicSolution pere,InputData d,int n,boolean mutation) throws InterruptedException, CloneNotSupportedException{
        Vector<Integer> GT=new Vector<>();
        int k=0;
        for(int i=0;i<n;i++)
            GT.addElement(pere.GiantRoute[i]);
        for(int i=n;i<this.GiantRoute.length;i++){
            if(!GT.contains(this.GiantRoute[i])){
                if(GT.size()<this.GiantRoute.length-n)
                    GT.addElement(this.GiantRoute[i]);
                else{
                    GT.insertElementAt(this.GiantRoute[i],k);
                    k++;
                }
            }    
        }
        for(int i=0;i<n;i++){
            if(!GT.contains(this.GiantRoute[i])){
                if(GT.size()<this.GiantRoute.length-n)
                    GT.addElement(this.GiantRoute[i]);
                else{
                    GT.insertElementAt(this.GiantRoute[i],k);
                    k++;
                }
            }    
        }
        Mutation(GT,mutation);
        return new HeuristicSolution(d,GT);
    }
    public HeuristicSolution Crossover(HeuristicSolution pere,InputData d,int n,int p,boolean mutation) throws InterruptedException, CloneNotSupportedException{
        Vector<Integer> GT=new Vector<>();
        int k=0;
        for(int i=n;i<p;i++)
            GT.addElement(pere.GiantRoute[i]);
        for(int i=p;i<this.GiantRoute.length;i++){
            if(!GT.contains(this.GiantRoute[i])){
                if(GT.size()<this.GiantRoute.length-n)
                    GT.addElement(this.GiantRoute[i]);
                else{
                    GT.insertElementAt(this.GiantRoute[i],k);
                    k++;
                }
            }    
        }
        for(int i=0;i<p;i++){
            if(!GT.contains(this.GiantRoute[i])){
                if(GT.size()<this.GiantRoute.length-n)
                    GT.addElement(this.GiantRoute[i]);
                else{
                    GT.insertElementAt(this.GiantRoute[i],k);
                    k++;
                }
            }    
        }
        HeuristicSolution.Mutation(GT,mutation);
        return new HeuristicSolution(d,GT);
    }
    @Override
    public HeuristicSolution clone() throws CloneNotSupportedException{
        return new HeuristicSolution(this);
    }
    public HeuristicSolution(HeuristicSolution s){
        this.Fitness=s.Fitness.clone();
        this.RoutesCounter=s.RoutesCounter;
        this.Routes=s.Routes.clone();
        this.GiantRoute=s.GiantRoute.clone();
        this.VisitMoments=s.VisitMoments.clone();
        this.RoutesAssignementToRiders=s.RoutesAssignementToRiders.clone();
    }
    public HeuristicSolution(InputData d,int[] GR,BiObjectiveValue F) throws InterruptedException{
        this.GiantRoute=GR;
        this.Split(d,F);
    } 
    public HeuristicSolution(InputData d,Vector<Integer> GT) throws InterruptedException, CloneNotSupportedException{
        this.GiantRoute=new int[GT.size()];
        for(int i=0;i<GT.size();i++)
            this.GiantRoute[i]=GT.elementAt(i);
        this.Split(d,null);
        this.LS(d);
    }
    public HeuristicSolution(InputData d) throws InterruptedException, CloneNotSupportedException{
        this.GiantRoute=new int[d.CostumersCounter];
        for(int i=0;i<this.GiantRoute.length;i++)
            this.GiantRoute[i]=i;
        for(int i=0;i<this.GiantRoute.length;i++)
            new SwapMovement(i,(int)(Math.random()*this.GiantRoute.length)).Execute(this.GiantRoute);
        this.Split(d,null);
        this.LS(d);
    }
    public boolean Improves(HeuristicSolution s){
        return this.Fitness.Improves(s.Fitness);
    }
    public boolean Improves(BiObjectiveValue F){
        return this.Fitness.Improves(F);
    }
    private void Split(InputData d,BiObjectiveValue F) throws InterruptedException{
        AuxiliaryGraphNode[] Nodes=new AuxiliaryGraphNode[this.GiantRoute.length+1];
        for(int i=0;i<Nodes.length;i++)
            Nodes[i]=new AuxiliaryGraphNode(d,i);
        Nodes[0].Posterior=0;
        Nodes[0].Label=new BiObjectiveValue(0d);
        Nodes[0].RidersAvailabilityTime=d.RidersLogInTimes;
        Nodes[0].RidersAvailabilityPositions=new int[d.RidersCounter];
        for(int i=0;i<Nodes[0].RidersAvailabilityPositions.length;i++)
            Nodes[0].RidersAvailabilityPositions[i]=i;
        for(int i=0;i<this.GiantRoute.length;i++){
            while(AuxiliaryGraphNode.Wait(Nodes,i))
                Thread.sleep(0,1);
            new AuxiliaryGraphArcs(d,this,Nodes,i,F);
        }
        int i=this.GiantRoute.length;
        while(AuxiliaryGraphNode.Wait(Nodes,i))
            Thread.sleep(0,1);
        this.Fitness=Nodes[i].Label.clone();
        this.VisitMoments=Nodes[i].VisitMoments.clone();
        Vector<Integer> v=new Vector<>();
        v.addElement(i);
        do{
            i=Nodes[i].Posterior;
            v.addElement(i);
        }while(i>0);
        this.Routes=new int[this.GiantRoute.length];   
        this.RoutesAssignementToRiders=new int[v.size()-1];  
        int j=0,k=v.size()-1;
        this.RoutesCounter=0;
        while(j<this.GiantRoute.length){
            if(j<v.elementAt(k)){
                this.Routes[this.GiantRoute[j]]=this.RoutesCounter;
                j++;
            }
            else{
                k--;
                this.RoutesAssignementToRiders[this.RoutesCounter]=Nodes[v.elementAt(k)].Rider+1;
                this.RoutesCounter++;
            }
        }
    }
    public void ShowSolution(InputData d){
        int route=1,j=0,i=0;
        System.out.println("This is the best known solution:");         
        System.out.println("The solution contains "+this.RoutesCounter+" routes made by "+d.RidersCounter+" riders\n");
        System.out.println("The route number "+route+" is assigned to the rider number "+this.RoutesAssignementToRiders[route-1]+":");
        int costumer;
        while(i<this.GiantRoute.length){ 
            costumer=this.GiantRoute[i];
            if(this.Routes[this.GiantRoute[i]]==route){
                System.out.println("\tThe costumer "+(costumer+1)+" is visited at the moment "+(int)this.VisitMoments[costumer]);
                i++;
            }
            else{
                route++;
                j=i;
                System.out.println("The route number "+route+" is assigned to the rider number "+this.RoutesAssignementToRiders[route-1]+":");
            }    
        }       
        System.out.println("The value of the objective functions equals to: "+this.Fitness.toString());
        System.out.println();
    }
    public static void Insertion(int[] comb,int i,int j){
        if(i<j){
            int aux=comb[j];
            for(int k=j;k>i;k--)
                comb[k]=comb[k-1];
            comb[i]=aux;
        }
    }
    public static void InverseInsertion(int[] comb,int i,int j){
        if(i<j){
            int aux=comb[i];
            for(int k=i;k<j;k++)
                comb[k]=comb[k+1];
            comb[j]=aux;
        }
    }
    public HeuristicSolution ExchangeRoutes(InputData d,int r1,int r2) throws InterruptedException,CloneNotSupportedException{
        if(this.RoutesCounter==1)
            return this.clone();
        int k;
        HeuristicSolution s;
        int[] NewGiantRoute,order;
        NewGiantRoute=new int[this.GiantRoute.length];
        order=new int[this.RoutesCounter];
        for(int i=0;i<this.RoutesCounter;i++)
            order[i]=i+1;
        new SwapMovement(r1,r2).Execute(order);
        k=0;
        for(int route=0;route<this.RoutesCounter;route++)
            for(int i=0;i<this.GiantRoute.length;i++)
                if(this.Routes[this.GiantRoute[i]]==order[route]){
                    NewGiantRoute[k]=this.GiantRoute[i];
                    k++;
                }
        s=new HeuristicSolution(d,NewGiantRoute,this.Fitness);
        if(!this.Improves(s))
            return s;
        return this.clone();
    }
    public int getOneCostumer(int route){
        for(int costumer:this.GiantRoute)
            if(this.Routes[costumer]==route+1)
                return costumer;
        return -1;
    }
    public void ExchangeRoutes(InputData d) throws InterruptedException, CloneNotSupportedException{
        if(this.RoutesCounter==1)
            return;
        HeuristicSolution s;
        int[] order=new int[this.RoutesCounter];
        for(int i=0;i<this.RoutesCounter;i++)
            order[i]=i;
        for(int i=0;i<order.length;i++)
            new SwapMovement(i,(int)(Math.random()*order.length)).Execute(order);
        for(int r1=0;r1<this.RoutesCounter;r1++)
            for(int r2=r1+1;r2<this.RoutesCounter;r2++)
                if(this.RoutesAssignementToRiders[order[r1]]==this.RoutesAssignementToRiders[order[r2]]
                        || d.HasTheSameSupplier(this.getOneCostumer(order[r1]),this.getOneCostumer(order[r2]))){
                    s=this.ExchangeRoutes(d,order[r1],order[r2]);
                    if(!s.Fitness.Equals(this.Fitness)){
                        this.Fitness=s.Fitness.clone();
                        this.RoutesCounter=s.RoutesCounter;
                        this.Routes=s.Routes.clone();
                        this.GiantRoute=s.GiantRoute.clone();
                        this.VisitMoments=s.VisitMoments.clone();
                        this.RoutesAssignementToRiders=s.RoutesAssignementToRiders.clone();
                        if(this.RoutesCounter!=order.length){
                            order=new int[this.RoutesCounter];
                            for(int i=0;i<this.RoutesCounter;i++)
                                order[i]=i;
                            for(int i=0;i<order.length;i++)
                                new SwapMovement(i,(int)(Math.random()*order.length)).Execute(order);
                        }
                    }
                }
    }
    public void LS(InputData d) throws InterruptedException, CloneNotSupportedException{
        BiObjectiveValue F=null;
        for(int i=0;i<this.GiantRoute.length-1;i++)
            for(int j=i+1;j<this.GiantRoute.length;j++)
                if(d.HasTheSameSupplier(this.GiantRoute[i],this.GiantRoute[j])){
                    F=this.Fitness.clone();
                    if(j>i+1){
                        for(int k=i,l=j;k<l;k++,l--)
                            new SwapMovement(k,l).Execute(this.GiantRoute);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            for(int k=i,l=j;k<l;k++,l--)
                                new SwapMovement(k,l).Execute(this.GiantRoute);
                            this.Fitness=F;
                        }
                        else
                            continue;
                        HeuristicSolution.Insertion(this.GiantRoute,i,j);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            HeuristicSolution.InverseInsertion(this.GiantRoute,i,j);
                            this.Fitness=F;
                        }
                        else
                            continue;
                        HeuristicSolution.InverseInsertion(this.GiantRoute,i,j);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            HeuristicSolution.Insertion(this.GiantRoute,i,j);
                            this.Fitness=F;
                        }
                        else
                            continue;
                    }
                    new SwapMovement(i,j).Execute(this.GiantRoute);
                    this.Split(d,F);
                    if(F.Improves(this)){
                        new SwapMovement(i,j).Execute(this.GiantRoute);
                        this.Fitness=F;
                    }
                }
        this.Split(d,F);
    }
    public void LocalSearch(InputData d) throws InterruptedException, CloneNotSupportedException{
        BiObjectiveValue F=null;
        for(int i=0;i<this.GiantRoute.length-1;i++)
            for(int j=i+1;j<this.GiantRoute.length;j++)
                if(d.HasTheSameSupplier(this.GiantRoute[i],this.GiantRoute[j])){
                    F=this.Fitness.clone();
                    if(j>i+1){
                        for(int k=i,l=j;k<l;k++,l--)
                            new SwapMovement(k,l).Execute(this.GiantRoute);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            for(int k=i,l=j;k<l;k++,l--)
                                new SwapMovement(k,l).Execute(this.GiantRoute);
                            this.Fitness=F;
                        }
                        else{
                            this.ExchangeRoutes(d);
                            continue;
                        }
                        HeuristicSolution.Insertion(this.GiantRoute,i,j);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            HeuristicSolution.InverseInsertion(this.GiantRoute,i,j);
                            this.Fitness=F;
                        }
                        else{
                            this.ExchangeRoutes(d);
                            continue;
                        }
                        HeuristicSolution.InverseInsertion(this.GiantRoute,i,j);
                        this.Split(d,F);
                        if(F.Improves(this)){
                            HeuristicSolution.Insertion(this.GiantRoute,i,j);
                            this.Fitness=F;
                        }
                        else{
                            this.ExchangeRoutes(d);
                            continue;
                        }
                    }
                    new SwapMovement(i,j).Execute(this.GiantRoute);
                    this.Split(d,F);
                    if(F.Improves(this)){
                        new SwapMovement(i,j).Execute(this.GiantRoute);
                        this.Fitness=F;
                    }
                    else
                        this.ExchangeRoutes(d);
                }
        this.Split(d,F);
    }
    public void GraphDefinition(InputData d){
        String[] colors={"orange","green","blue","red","yellow","Grey"};
        if(d.RidersCounter>colors.length)
            return; 
        int route=0,costumer;
        boolean[] conditions=new boolean[colors.length];
        GraphViz gv=new GraphViz();
        gv.addln(gv.start_graph());
        gv.addln("node [color=white];");
        for(int i=0;i<this.GiantRoute.length;i++){
            costumer=this.GiantRoute[i];
            if(this.Routes[costumer]==route)
                gv.addln((1+this.GiantRoute[i-1])+" -> "+(1+costumer)+"[color="+colors[this.RoutesAssignementToRiders[route-1]-1]+"];");
            else{
                if(conditions[this.RoutesAssignementToRiders[route]-1])
                    for(int j=i-1;;j--){
                        int r=this.Routes[this.GiantRoute[j]]-1;
                        if(this.RoutesAssignementToRiders[r]==this.RoutesAssignementToRiders[route]){
                            gv.addln((1+this.GiantRoute[j])+"-> R"+(1+d.getRestaurantIndexOrderedFrom(costumer)-d.RidersCounter)+"[color="+colors[this.RoutesAssignementToRiders[route]-1]+"];");
                            break;
                        }
                    }
                else{
                    conditions[this.RoutesAssignementToRiders[route]-1]=true;
                    gv.addln("C"+this.RoutesAssignementToRiders[route]+"-> R"+(1+d.getRestaurantIndexOrderedFrom(costumer)-d.RidersCounter)+" [color="+colors[this.RoutesAssignementToRiders[route]-1]+"];");
                }
                gv.addln("R"+(1+d.getRestaurantIndexOrderedFrom(costumer)-d.RidersCounter)+" -> "+(1+costumer)+" [color="+colors[this.RoutesAssignementToRiders[route]-1]+"];");
                route++; 
            }            
        }
        for(int rider=0;rider<d.RidersCounter;rider++)
            if(conditions[rider])
                gv.addln("C"+(1+rider)+" [color="+colors[rider]+",style=filled];");
        for(int r=1;r<=d.RestaurantsCounter;r++)
            gv.addln("R"+r+" [color=lightblue2,shape=box,style=filled];");
        gv.end_graph();
        String desktop_path=System.getProperty("user.home") + "/Desktop";
        File out=new File(desktop_path.replace("/","\\")+"\\graphe "+this.Fitness.toString()+".jpg");
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(),"jpg"),out);         
    }
    public void GanttDiagramm(InputData d){
        GanttDemoCollection demo=new GanttDemoCollection("Gantt Diagram",d,this);
        demo.pack();
        demo.setExtendedState(Frame.MAXIMIZED_BOTH);
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);          
    }
    public IntervalCategoryDataset createDataset(InputData d){
        TaskSeriesCollection collection=new TaskSeriesCollection();
        int route=1,FirstCostumer=0,LastCostumer;
        double start,end;
        String TaskName;
        for(int i=0;i<this.GiantRoute.length;i++)
            if(this.Routes[this.GiantRoute[i]]!=route || i+1==this.GiantRoute.length){
                LastCostumer=i-1;
                if(i+1==this.GiantRoute.length)
                    LastCostumer++;
                start=this.VisitMoments[this.GiantRoute[FirstCostumer]];
                start-=d.getDistance(d.getRestaurantIndexOrderedFrom(this.GiantRoute[FirstCostumer]),d.getCostumerIndex(this.GiantRoute[FirstCostumer]));
                end=this.VisitMoments[this.GiantRoute[LastCostumer]];
                TaskName="R"+(1+d.getRestaurantIndexOrderedFrom(this.GiantRoute[FirstCostumer])-d.RidersCounter)+"-";
                for(int k=FirstCostumer;k<LastCostumer;k++)
                    TaskName+=(1+this.GiantRoute[k])+"-";
                TaskName+=(1+this.GiantRoute[LastCostumer]);
                TaskSeries ts=new TaskSeries(TaskName);
                ts.add(new Task("Rider "+this.RoutesAssignementToRiders[route-1],new Date((int)start),new Date((int)end))); 
                collection.add(ts);
                route++;
                FirstCostumer=i;
            }
        return collection;
    }
}