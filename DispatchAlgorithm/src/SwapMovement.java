





import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
public class SwapMovement {
    private int Index1,Index2;
    public void afficher_permutation(){
        System.out.println("( "+Index1+" , "+Index2+" )");
    }
    public SwapMovement(int a,int b){
        Index1=a;
        Index2=b;
    }
    public void Execute(HeuristicSolution[] array){
        if(this==null || Index1==Index2)
            return;
       HeuristicSolution aux=array[Index1];
       array[Index1]=array[Index2];
       array[Index2]=aux;   
    }
    public void Execute(int[] array){
        if(this==null || Index1==Index2)
            return;
       int aux=array[Index1];
       array[Index1]=array[Index2];
       array[Index2]=aux; 
    }  
    public void Execute(Vector v){
        if(Index1<v.size() && Index2<v.size() && Index1!=Index2){
            Object aux;
            aux=v.elementAt(Index1);
            v.setElementAt(v.elementAt(Index2),Index1);
            v.setElementAt(aux,Index2);
        }
    }
}