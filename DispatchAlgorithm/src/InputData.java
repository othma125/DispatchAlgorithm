

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
    public final double DeliveryFixedTime=1d,ProductPreparingTime=15d;
    public final int MaxDeliveriesByRoute=3;
    public int CostumersCounter,RidersCounter,RestaurantsCounter;
    private final int[][] Coordinates;
    public final double[] CostumersDueDates,RidersLogInTimes;
    private double[][] DistancMatrix;
    public InputData(File file,int CC,int RiC,int RsC){
        this.CostumersCounter=CC;
        this.RidersCounter=RiC;
        this.RestaurantsCounter=RsC;
        this.CostumersDueDates=new double[this.CostumersCounter];
        this.RidersLogInTimes=new double[this.RidersCounter];
        int NodesCounter=this.CostumersCounter+this.RidersCounter+this.RestaurantsCounter;
        this.Coordinates=new int[NodesCounter][2];
        this.DistancMatrix=new double[NodesCounter][NodesCounter];
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
        for(int i=0;i<this.RidersCounter;i++){
            line=FileScanner.nextLine();
            st=new StringTokenizer(line);
            st.nextToken();
            this.Coordinates[i][0]=Integer.valueOf(st.nextToken());
            this.Coordinates[i][1]=Integer.valueOf(st.nextToken());
            st.nextToken();
            this.RidersLogInTimes[i]=Integer.valueOf(st.nextToken());
        }
        for(int i=0;i<this.RestaurantsCounter;i++){
            line=FileScanner.nextLine();
            st=new StringTokenizer(line);
            st.nextToken();
            this.Coordinates[this.getRestaurantIndex(i)][0]=Integer.valueOf(st.nextToken());
            this.Coordinates[this.getRestaurantIndex(i)][1]=Integer.valueOf(st.nextToken());
        }
        for(int i=0;i<this.CostumersCounter;i++){
            line=FileScanner.nextLine();
            st=new StringTokenizer(line);
            st.nextToken();
            this.Coordinates[this.getCostumerIndex(i)][0]=Integer.valueOf(st.nextToken());
            this.Coordinates[this.getCostumerIndex(i)][1]=Integer.valueOf(st.nextToken());
            this.CostumersDueDates[i]=this.getDistance(this.getRestaurantIndexOrderedFrom(i),this.getCostumerIndex(i));
        }
        FileScanner.close();
    }
    public int getRestaurantIndex(int restaurant){
        return this.RidersCounter+restaurant;
    }
    public int getCostumerIndex(int costumer){
        return this.RidersCounter+this.RestaurantsCounter+costumer;
    }
    public int getRestaurantIndexOrderedFrom(int costumer){
        return this.RidersCounter+costumer%this.RestaurantsCounter;
    }
    public boolean HasTheSameSupplier(int costumer1,int costumer2){
        return costumer1%this.RestaurantsCounter==costumer2%this.RestaurantsCounter;
    }
    public double getDistance(int i,int j){
        if(this.DistancMatrix[i][j]==0)
            this.DistancMatrix[i][j]=this.DistancMatrix[j][i]=Math.sqrt(Math.pow(this.Coordinates[j][0]-this.Coordinates[i][0],2)+Math.pow(this.Coordinates[j][1]-this.Coordinates[i][1],2));
        return this.DistancMatrix[i][j];
    }
}