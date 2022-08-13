

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author Othmane
 */
public final class InputData {
    public final String FileName;
    public final double DeliveryFixedTime=1d;
    public final double ProductPreparingTime=15d;
    public final int MaxDeliveriesByRoute=3;
    public int CostumersCount;
    public int RidersCount;
    public int RestaurantsCount;
    private final int[][] Coordinates;
    public final double[] CostumersDueDates;
    public final double[] RidersLogInTimes;
    private double[][] DistancMatrix;
    
    public InputData(File file,int CC,int RiC,int RsC){
        this.FileName=file.getName();
        this.CostumersCount=CC;
        this.RidersCount=RiC;
        this.RestaurantsCount=RsC;
        this.CostumersDueDates=new double[this.CostumersCount];
        this.RidersLogInTimes=new double[this.RidersCount];
        int StopsCount=this.CostumersCount+this.RidersCount+this.RestaurantsCount;
        this.Coordinates=new int[StopsCount][2];
        this.DistancMatrix=new double[StopsCount][StopsCount];
        Scanner FileScanner=null;
        try{
            FileScanner=new Scanner(new FileInputStream(file));
        }catch(FileNotFoundException ex) {
            System.out.println("File not found");
            System.exit(0);
        }
        FileScanner.nextLine();
        FileScanner.nextLine();
        FileScanner.nextLine();
        FileScanner.nextLine();
        FileScanner.nextLine();
        FileScanner.nextLine();
        FileScanner.nextLine();
        FileScanner.nextLine();
        FileScanner.nextLine();
        StringTokenizer st;
        String line;
        for(int i=0;i<this.RidersCount;i++){
            line=FileScanner.nextLine();
            st=new StringTokenizer(line);
            st.nextToken();
            this.Coordinates[i][0]=Integer.valueOf(st.nextToken());
            this.Coordinates[i][1]=Integer.valueOf(st.nextToken());
            st.nextToken();
            this.RidersLogInTimes[i]=Integer.valueOf(st.nextToken());
        }
        for(int i=0;i<this.RestaurantsCount;i++){
            line=FileScanner.nextLine();
            st=new StringTokenizer(line);
            st.nextToken();
            this.Coordinates[this.getRestaurantIndex(i)][0]=Integer.valueOf(st.nextToken());
            this.Coordinates[this.getRestaurantIndex(i)][1]=Integer.valueOf(st.nextToken());
        }
        for(int i=0;i<this.CostumersCount;i++){
            line=FileScanner.nextLine();
            st=new StringTokenizer(line);
            st.nextToken();
            this.Coordinates[this.getCostumerIndex(i)][0]=Integer.valueOf(st.nextToken());
            this.Coordinates[this.getCostumerIndex(i)][1]=Integer.valueOf(st.nextToken());
            this.CostumersDueDates[i]=this.getDistance(this.getRestaurantIndexOrderedFrom(i),this.getCostumerIndex(i));
        }
        FileScanner.close();
    }
    
    int getRestaurantIndex(int restaurant){
        return this.RidersCount+restaurant;
    }
    
    int getCostumerIndex(int costumer){
        return this.RidersCount+this.RestaurantsCount+costumer;
    }
    
    int getRestaurantIndexOrderedFrom(int costumer){
        return this.RidersCount+costumer%this.RestaurantsCount;
    }
    
    boolean HasTheSameSupplier(int costumer1,int costumer2){
        return costumer1%this.RestaurantsCount==costumer2%this.RestaurantsCount;
    }
    
    double getDistance(int i,int j){
        if(this.DistancMatrix[i][j]==0)
            this.DistancMatrix[i][j]=this.DistancMatrix[j][i]=Math.sqrt(Math.pow(this.Coordinates[j][0]-this.Coordinates[i][0],2)
                                                                +Math.pow(this.Coordinates[j][1]-this.Coordinates[i][1],2));
        return this.DistancMatrix[i][j];
    }
}