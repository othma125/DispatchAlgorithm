
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Othmane
 */
public abstract class MetaHeuristic{
    InputData Data;
    Long RunTime;// Run Time in milliseconds
    Long StartTime;// Start Time in milliseconds
    Long BestSolutionReachingTime;
    Solution BestSolution;

    MetaHeuristic(InputData data){
        this.Data=data;
    }
    
    abstract ArrayList<Solution> Run(int RunTime);
}

class GeneticAlgorithm extends MetaHeuristic{
    ReentrantLock GlobalLock=new ReentrantLock();
    ArrayList<Thread> AliveThreads=new ArrayList<>();
    final int PopulationSize=20;
    final double MutationRate=0.1d;
    final double CrossoverRate=0.8d;
    final Solution[] Population;
    
    GeneticAlgorithm(InputData data){
        super(data);
        this.Population=new Solution[this.PopulationSize];
    }
    
    @Override
    ArrayList<Solution> Run(int RunTime){
        this.RunTime=RunTime*1000l;
        System.out.println("Testing file = "+this.Data.FileName);
        System.out.println("Solution approach = Genetic Algorithm");
        System.out.println();
        this.StartTime=System.currentTimeMillis();
        this.InitialPopulation();
        System.out.println();
        this.BestSolutionReachingTime=System.currentTimeMillis()-this.StartTime;
        System.out.println(this.Population[0].Fitness.toString()+" after "+this.BestSolutionReachingTime+" ms");
        this.BestSolution=this.Population[0];
        while(System.currentTimeMillis()-this.StartTime<this.RunTime){
            this.RunCrossover();
            synchronized(this){
                while(this.AliveThreads.stream().filter(Thread::isAlive).count()>2)
                    try{
                        this.wait();
                    }catch(InterruptedException ex){
                        Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        }
        this.join();
        this.BestSolution=this.Population[0];
        return this.getParetoSet();
    }
    
    ArrayList<Solution> getParetoSet(){
        ArrayList<Solution> ParetoSet=new ArrayList<>();
        double max=Double.POSITIVE_INFINITY;
        for(Solution s:this.Population){
            if(s.Fitness.getTraveledDistance()<max){
                max=s.Fitness.getTraveledDistance();
                ParetoSet.add(s);
            }
            else
                return ParetoSet;
        }
        return null;
    }
    
    private void InitialPopulation(){
        System.out.println("Initial population");
        IntStream.range(0,this.PopulationSize)
                .forEach(i->{
                    synchronized(this){
                        while(this.AliveThreads.stream().filter(Thread::isAlive).count()>2)
                            try{
                                this.wait();
                            }catch(InterruptedException ex){
                                Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }
                    Thread t=new Thread(()->{
                            this.Population[i]=new Solution(this.Data);
                        synchronized(this){
                            this.notify();
                        }
                        System.out.println(this.Population[i].Fitness.toString());
                    });
                    t.start();
                    this.AliveThreads.add(t);
                });
        this.join();
        this.AliveThreads.clear();
        Arrays.sort(this.Population,(s1,s2)->(int)(s1.Fitness.getTraveledDistance()*1000)
                                            -(int)(s2.Fitness.getTraveledDistance()*1000));
    }
    
    private void RunCrossover(){
        int half=this.PopulationSize/2;
        int i=(int)(Math.random()*half);
        int j;
        if(Math.random()<0.7d)
            do{
                j=(int)(Math.random()*half);
            }while(i==j);
        else
            j=half+(int)(Math.random()*(this.PopulationSize-half));
        this.Crossover(Math.random()<this.MutationRate,this.Population[i],this.Population[j]);
    }
    
    private void Crossover(boolean mutation,Solution ... parents){
        Thread t1,t2;
        boolean CrossoverCondition=Math.random()<this.CrossoverRate;
        int CutPoint1=(int)(this.Data.CostumersCount*Math.random());
        int CutPoint2=(Math.random()<0.7d)?CutPoint1:CutPoint1+(int)((this.Data.CostumersCount-CutPoint1)*Math.random());
        t1=new Thread(()->{
            Solution Child=(CrossoverCondition)?parents[0].Crossover(this.Data,parents[1],mutation,CutPoint1,CutPoint2):new Solution(this.Data);
            Child.LocalSearch(Data);
            this.UpdatePopulation(Child);
            synchronized(this){
                this.notify();
            }
        });
        t1.start();
        this.AliveThreads.add(t1);
        t2=new Thread(()->{
            Solution Child=(CrossoverCondition)?parents[1].Crossover(this.Data,parents[0],mutation,CutPoint1,CutPoint2):new Solution(this.Data);
            Child.LocalSearch(this.Data);
            this.UpdatePopulation(Child);
            synchronized(this){
                this.notify();
            }
        });
        t2.start();
        this.AliveThreads.add(t2);
    }
    
    private void UpdatePopulation(Solution newSolution){
        this.GlobalLock.lock();
        try{
            if(newSolution.Improves(this.Population[this.PopulationSize-1])
                    || !this.Population[this.PopulationSize-1].Improves(newSolution)){
                int half=this.PopulationSize/2;
                int i=half+(int)(Math.random()*(this.Population.length-half));
                this.Population[i]=newSolution;
                if(newSolution.Improves(this.Population[0])){
                    this.BestSolutionReachingTime=System.currentTimeMillis()-this.StartTime;
                    System.out.println(newSolution.Fitness.toString()+" after "+this.BestSolutionReachingTime+" ms");
                }
                Arrays.sort(this.Population,(s1,s2)->(int)(s1.Fitness.getTraveledDistance()*1000)
                                                    -(int)(s2.Fitness.getTraveledDistance()*1000));
            }
        }finally{
            this.GlobalLock.unlock();
        }
    }
    
    private void join(){
        this.AliveThreads
            .stream()
            .filter(Thread::isAlive)
            .forEach(t->{
                try{
                    t.join();
                }catch(InterruptedException ex){
                    Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
    }
}