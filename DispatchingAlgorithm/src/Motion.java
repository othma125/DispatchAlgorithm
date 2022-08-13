





import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
public class Motion <T>{
    private final int Index1;
    private final int Index2;
    
    public Motion(int index1,int index2){
        Index1=index1;
        Index2=index2;
    }
    
    void Show(){
        System.out.println("( "+this.Index1+" , "+this.Index2+" )");
    }
    
    void Swap(int[] array){
        if(this.Index1<array.length && this.Index2<array.length && this.Index1!=this.Index2){
           int aux=array[this.Index1];
           array[this.Index1]=array[this.Index2];
           array[this.Index2]=aux;   
        }
    }
    
    void Swap(T[] array){
        if(this.Index1<array.length && this.Index2<array.length && this.Index1!=this.Index2){
           T aux=array[this.Index1];
           array[this.Index1]=array[this.Index2];
           array[this.Index2]=aux;   
        }
    }
    
    void Swap(Vector<T> vector){
        if(this.Index1<vector.size() && this.Index2<vector.size() && this.Index1!=this.Index2){
            T aux=vector.elementAt(this.Index1);
            vector.setElementAt(vector.elementAt(this.Index2),Index1);
            vector.setElementAt(aux,this.Index2);
        }
    }
    
    void Insertion(int[] array){
        if(this.Index1<array.length && this.Index2<array.length && this.Index1<this.Index2){
            int aux=array[this.Index2];
            for(int k=this.Index2;k>this.Index1;k--)
                array[k]=array[k-1];
            array[this.Index1]=aux;
        }
    }
    
    void InverseInsertion(int[] array){
        if(this.Index1<array.length && this.Index2<array.length && this.Index1<this.Index2){
            int aux=array[this.Index1];
            for(int k=this.Index1;k<this.Index2;k++)
                array[k]=array[k+1];
            array[this.Index2]=aux;
        }
    }
}