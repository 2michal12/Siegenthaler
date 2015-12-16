package nks4;

import java.util.Random;

/**
 *
 * @author Michal
 */
public class LFSR {
    int[] lfsr;
    int[] polynom;
    int[] filter_f1, filter_f2, filter_f3;
    int[] sequence_f1, sequence_f2, sequence_f3, orig;
    private int lfsrSize, output, tmp;
    int n;
    
    //--CONSTRUCTOR
    public LFSR(int size, int[] f1, int[] f2, int[] f3){
        if(size > 0){
            this.lfsrSize = size;
            lfsr = new int[lfsrSize];
            Random randomGenerator = new Random();
            for(int i = 0; i < lfsrSize; i++){
                lfsr[i] = randomGenerator.nextInt(100) % 2;
            }
            lfsr[lfsrSize-1] = 1; //nikdy nemoze byt cely register nulovy
            
            filter_f1 = new int[f1.length];
            filter_f2 = new int[f2.length];
            filter_f3 = new int[f3.length];
            System.arraycopy(f1, 0, filter_f1, 0, f1.length);
            System.arraycopy(f2, 0, filter_f2, 0, f2.length);
            System.arraycopy(f3, 0, filter_f3, 0, f3.length);
        }
    }
    
    public LFSR(int[] reg_init, int[] f1, int[] f2, int[] f3, int[] polynom){
        if(reg_init.length > 0){
            this.lfsrSize = reg_init.length;
            lfsr = new int[lfsrSize];
            System.arraycopy(reg_init, 0, lfsr, 0, lfsrSize);
            
            filter_f1 = new int[f1.length];
            filter_f2 = new int[f2.length];
            filter_f3 = new int[f3.length];
            System.arraycopy(f1, 0, filter_f1, 0, f1.length);
            System.arraycopy(f2, 0, filter_f2, 0, f2.length);
            System.arraycopy(f3, 0, filter_f3, 0, f3.length);
            
            this.polynom = new int[polynom.length];
            System.arraycopy(polynom, 0, this.polynom, 0, polynom.length);
        }
    }
    //--FUNCTION
    public int output(){
        output = lfsr[0];
        tmp = 0;
        for(int i = 0; i < lfsrSize - 1; i++){
            lfsr[i] = lfsr[i+1];
        }
        //lfsr[lfsrSize-1] = (lfsr[0]+lfsr[1]+lfsr[4]+lfsr[6]+lfsr[10]+lfsr[13]) % 2; //statiscke pouzitie polynomu 1 + x + x^4 + x^6 + x^10 + x^13
        
        for(int j = 0; j < polynom.length-1; j++){
            tmp += lfsr[j]*polynom[j]; //automaticke pouzitie polynomu podla zadaneho pola
        }
        lfsr[lfsrSize-1] = tmp % 2;
        
        return output;
    }
    
    public void generate_sequence(int function_id, int n){
        this.n = n;
        switch( function_id ){
            case 1: 
                sequence_f1 = new int[n];
                for(int i = 0; i < n; i++){ //zmena na i = 1 , predtym i = 0
                    sequence_f1[i] = lfsr[filter_f1[0]]*lfsr[filter_f1[1]];
                    this.output();
                }
                break;
            case 2: 
                sequence_f2 = new int[n];
                for(int i = 0; i < n; i++){
                    sequence_f2[i] = lfsr[filter_f2[0]]*lfsr[filter_f2[1]]*lfsr[filter_f2[2]];
                    this.output();
                }
                break;
            case 3:
                sequence_f3 = new int[n];
                for(int i = 0; i < n; i++){
                    sequence_f3[i] = lfsr[filter_f3[0]]*lfsr[filter_f3[1]]*lfsr[filter_f3[2]]*lfsr[filter_f3[3]];
                    this.output();
                }
                break;
            default:
                System.out.println("Nepovoleny prvy parameter funkcie 'generate_sequence' !");
        }
        copy_sequence(function_id);
    }
    
    public void reset_lfsr(int[] init_reg){
        System.arraycopy(init_reg, 0, lfsr, 0, lfsrSize);
    }
    
    public void noise_sequences(double probability, int seq_id){
        int noise_count = (int)( n -( n * probability ));
        int step = n / noise_count;
        int index = 0;
        for(int i = 0; i <= noise_count; i++){
            if( i*noise_count > 0 ){
                index += step;
                if( i == noise_count )index--;
                if( seq_id == 1 ){
                    sequence_f1[index] = switch_value(sequence_f1[index]);
                }
                if( seq_id == 2 ){
                    sequence_f2[index] = switch_value(sequence_f2[index]);
                }
                if( seq_id == 3 ){
                    sequence_f3[index] = switch_value(sequence_f3[index]);
                }
            }
        }
    }
    
    private int switch_value(int val){
        if(val == 1){
            return 0;
        }else{
            return 1;
        }
    }
    
    public int[] get_sequence(int id){
        if( id == 1 )return sequence_f1;
        if( id == 2 )return sequence_f2;
        if( id == 3 )return sequence_f3;
        return new int[0];
    }
    
    public int[] get_orig(){
        return orig;
    }
    
    private void copy_sequence(int seq_id){
        if( seq_id == 1 ){
            orig = new int[sequence_f1.length];
            System.arraycopy(sequence_f1, 0, orig, 0, sequence_f1.length);
        }else if( seq_id == 2 ){
            orig = new int[sequence_f2.length];
            System.arraycopy(sequence_f2, 0, orig, 0, sequence_f2.length);
        }else if( seq_id == 3 ){
            orig = new int[sequence_f3.length];
            System.arraycopy(sequence_f3, 0, orig, 0, sequence_f3.length);
        }else{
            System.out.println("Pouzita funkcia 'copy_sequence' s nevyhovujucim parametrom '"+seq_id+"' !");
        }
    }
    
    //--PRINT
    public void print_lfsr(){
        for(int i = 0; i < lfsrSize; i++){
            System.out.print(lfsr[i]);
        }
        System.out.println();
    }
    
    public void print_lfsr_size(){
        System.out.println("\n"+lfsrSize);
    }
    
    public void print_sequence(int seq_id){
        System.out.print("f"+seq_id+": ");
        if( seq_id == 1 ){
            for(int i = 0; i < sequence_f1.length; i++){
                System.out.print(sequence_f1[i]);
            }
            System.out.println();
        }else if( seq_id == 2 ){
            for(int i = 0; i < sequence_f2.length; i++){
                System.out.print(sequence_f2[i]);
            }
            System.out.println();
        }else if( seq_id == 3 ){
            for(int i = 0; i < sequence_f3.length; i++){
                System.out.print(sequence_f3[i]);
            }
            System.out.println();
        }else{
            System.out.println("Pouzita funkcia 'print_sequence' s nevyhovujucim parametrom '"+seq_id+"' !");
        }
    }
    
    public void printOrig(){
        for(int i = 0; i < orig.length; i++)
            System.out.print(orig[i]);
    }
    
}
