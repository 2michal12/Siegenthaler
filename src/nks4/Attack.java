package nks4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Michal
 */
public class Attack {
    int new_lfsr_count;
    int[] stream_key;
    int[] polynom;
    int N;
    int lfsr_arrays[][];
    int count;
    String streamKey, binaryString;
    int[] tmp_init;
    int[] tmp_sequence;
    int[][] all_sequence;
    int output, tmp;
    int[] statistic;
    int[] newLFSR;
    int[][] sequence_array;
    int[][] combination_statistic;
    char []cA;
    int result;
    int[] recreate_stream;
    StringBuilder sb;
    
    public Attack(int[] key, int[] polynom, int count_new_registers){ //poznam char. polynom a odchyteny prudovy kluc  //1 + x + x^4 + x^6 + x^10 + x^13
        new_lfsr_count = count_new_registers;
        N = key.length;
        stream_key = new int[N];
        System.arraycopy(key, 0, stream_key, 0, N);
        streamKey = Arrays.toString(stream_key);
        this.polynom = new int[polynom.length];
        System.arraycopy(polynom, 0, this.polynom, 0, polynom.length);
        count = (int)Math.pow(2, polynom.length); //pocet moznych naplneni registra
    }
   
    public void siegenthaler_correlation(){
        alloc_tmp_init(polynom.length);
        statistic = new int[count];
        all_sequence = new int[count][N];
        for(int i = 1; i < count; i++){ //vsetky moznosti pociatocneho naplnenia registra okrem nuloveho
            intToBinaryString(i, polynom.length); //in binaryString
            stringToIntArray(polynom.length); //in tmp_init[]
            generateSequence(i); //to tmp_sequence[]
            statistic[i] = compareSequences();
        }
        newLFSRs(); //vytvori a naplni nove registre 
    }
    
    public void combination_function(){
        alloc_tmp_init(new_lfsr_count); //alokovat nanovo miesto 
        create_combination_statistic();         
        sb = new StringBuilder();
        for(int i = 0; i < N; i++){ 
            sb.setLength(0);
            sb.append("");
            for(int x = 0; x < new_lfsr_count; x++){
                sb.append(sequence_array[x][i]);
            }
            set_statistic( binaryToInt( sb.toString() ), stream_key[i] );
        }
    }
    
    public void simulate(){
        recreate_stream = new int[N];
        sb = new StringBuilder();
        for(int i = 0; i < N; i++){ 
            sb.setLength(0);
            sb.append("");
            for(int x = 0; x < new_lfsr_count; x++){
                sb.append(sequence_array[x][i]);
            }
            recreate_stream[i] = get_statistic( binaryToInt( sb.toString() ) );
        }
    }
    
    private void intToBinaryString(int value, int length){
        binaryString = "";
        for(int i = length-1; i >= 0; i--) {
            binaryString = (value&1) + binaryString;
            value = value >>>=1;
        }   
    }
    
    private void stringToIntArray(int length){
        for(int i = 0; i < length; i++) {
            tmp_init[i] = Integer.parseInt(String.valueOf(binaryString.charAt(i)));
        }
    }
    
    private int binaryToInt(String binary){
    cA = binary.toCharArray();
    result = 0;
    for (int i = cA.length-1;i>=0;i--){
        if(cA[i]=='1') result+=Math.pow(2, cA.length-i-1);
    }
    return result;
}
    
    private void generateSequence(int index){
        tmp_sequence = new int[N];
        for(int i = 0; i < N; i++){
            tmp_sequence[i] = shift();
            all_sequence[index][i] = tmp_sequence[i];
        }
    }
    
    private int shift(){ //ako output() v LFSR.java
        output = tmp_init[0];
        tmp = 0;
        for(int i = 0; i < tmp_init.length - 1; i++){
            tmp_init[i] = tmp_init[i+1];
        }
        //lfsr[lfsrSize-1] = (lfsr[0]+lfsr[1]+lfsr[4]+lfsr[6]+lfsr[10]+lfsr[13]) % 2; //statiscke pouzitie polynomu 1 + x + x^4 + x^6 + x^10 + x^13
        
        for(int j = 0; j < polynom.length-1; j++){
            tmp += tmp_init[j]*polynom[j]; //automaticke pouzitie polynomu podla zadaneho pola
        }
        tmp_init[tmp_init.length-1] = tmp % 2;
        return output;
    }
    
    private int compareSequences(){
        tmp = 0;
        for(int i = 0; i < N; i++){
            if(stream_key[i] == tmp_sequence[i])
                tmp++;
        }
        return tmp;
    }
    
    private void newLFSRs(){
        create_lfsr_array();
        tmp_sequence = new int[N];
        for(int i = 0; i < new_lfsr_count; i++){
            tmp = getIndexOfMaxValue(maxValue()); 
            System.arraycopy(all_sequence[tmp], 0, tmp_sequence, 0, N);
            intToBinaryString(tmp, polynom.length);
            stringToIntArray(polynom.length);
            copyTwoDimToOneDim(i);
        }
    }
    
    private int maxValue(){
        List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < statistic.length; i++) {
              list.add(statistic[i]);
            }
        return Collections.max(list);
    }
    
    private int getIndexOfMaxValue(int maxValue){
        for(int i = 0; i < count; i++){
            if(statistic[i] == maxValue){
                statistic[i] = 0; //nulujem pre hladanie dalsieho maxima
                return i;
            }
        }
        return 0; //nikdy nenastane
    }
    
    private void create_lfsr_array(){
        lfsr_arrays = new int[new_lfsr_count][polynom.length]; //vstupne naplnenia vybratych registrov
        sequence_array = new int[new_lfsr_count][N]; //vybrate postupnosti
    }
    
    private void create_combination_statistic(){
        combination_statistic = new int[(int)Math.pow(2, new_lfsr_count)][3]; // napr. [ 2^3 = 8 moznosti {000,001,010,..,111} ][2 = pre 0 a 1]
        for(int i = 0; i < (int)Math.pow(2, new_lfsr_count); i++){
            combination_statistic[i][0] = i;
        }
    }
    
    private void set_statistic(int value_mix, int value_init){
       for(int i = 0; i < (int)Math.pow(2, new_lfsr_count); i++ ){
           if( value_mix == i ){
               if( value_init == 0 ){
                    combination_statistic[i][1]++;
               }else{
                    combination_statistic[i][2]++;
               }
           }
       }
    }
    
    private int get_statistic(int value_mix){
        for(int i = 0; i < (int)Math.pow(2, new_lfsr_count); i++ ){
           if( value_mix == i ){
               if( combination_statistic[i][1] > combination_statistic[i][2] )
                   return 0;
               else
                   return 1;
           }
       }
       return -1; //nemalo by nikdy nastat
    }
    
    public double get_match(int[] orig){
        int match = 0;
        for(int i = 0; i < N; i++){
            if( recreate_stream[i] == orig[i]) match++;
        }
        return (match*100)/N;
    }
    
    private void copyTwoDimToOneDim(int index){
        System.arraycopy(tmp_init, 0, lfsr_arrays[index], 0, polynom.length);    
        System.arraycopy(tmp_sequence, 0, sequence_array[index], 0, N);
    }  
    
    private void alloc_tmp_init(int size){
        tmp_init = new int[size];
    }
    
    public void print_stream_key(){
        for(int i = 0; i < N; i++){
            System.out.print(stream_key[i]);
        }
    }
    
    public void printNewLFSR(){
        System.out.println("\n");
        for(int i = 0; i < new_lfsr_count; i++){
            for(int j = 0; j < polynom.length; j++){
                System.out.print(lfsr_arrays[i][j]);
            }
            /*System.out.print("  :  ");
            for(int j = 0; j < N; j++){
                System.out.print(sequence_array[i][j]);
            }*/
            System.out.println();
        }
    }
    
    public void printCombinationStatistic(){
        System.out.println();
        for(int i = 0; i < combination_statistic.length; i++){
            for(int j = 0; j < combination_statistic[0].length; j++){
                System.out.print(combination_statistic[i][j] +"  ");
            }
            System.out.println();
        }
    }
    
    public void printRecreateStream(){
        System.out.println();
        for(int i = 0; i < N; i++){
            System.out.print(recreate_stream[i]);
        }
    }
}
