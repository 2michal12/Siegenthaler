package nks4;

import java.util.Arrays;

/**
 *
 * @author Michal
 */
public class NKS4 {
    
    static final int ID = 1;
    static final int N = 1800; //dlzka generovanej postupnosti
    static final double PROBABILITY = 0.7; //percentualna zhoda (zasumenie)
    static final int GENERATOR_REGISTER_COUNT = 17; //pocet registrev v ekvivalentom generatore

    public static void main(String[] args) {
        int[] init_lfsr = new int[]{0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1}; // velkost registra = stupen polynomu
        int[] polynom = new int[]{1,1,0,0,1,0,1,0,0,0,1,0,0}; //1 + x + x^4 + x^6 + x^10 + x^13
        int[] filter_f1 = new int[]{1,5};
        int[] filter_f2 = new int[]{3,8,10};
        int[] filter_f3 = new int[]{2,4,7,11};
        
        LFSR lfsr = new LFSR(init_lfsr, filter_f1, filter_f2, filter_f3, polynom);

        switch(ID){
            case 1: //generate sequence from filter function 1
                lfsr.reset_lfsr(init_lfsr);
                lfsr.generate_sequence(ID, N);
                //lfsr.print_sequence(ID);
                 
                lfsr.noise_sequences(PROBABILITY, ID); //noise sequences
                lfsr.printOrig();
                break;
            case 2://generate sequence from filter function 2
                lfsr.reset_lfsr(init_lfsr);
                lfsr.generate_sequence(ID, N);
                //lfsr.print_sequence(ID);
                
                lfsr.noise_sequences(PROBABILITY, ID); //noise sequences
                lfsr.printOrig();
                break;
            case 3://generate sequence from filter function 3
                lfsr.reset_lfsr(init_lfsr);
                lfsr.generate_sequence(ID, N);
                //lfsr.print_sequence(ID);
                
                lfsr.noise_sequences(PROBABILITY, ID); //noise sequences
                lfsr.printOrig();
                break;
            default:
                System.out.println("Zly vstup !");
                 break;
        }
        
        //----------------------------------- ATTACK
        Attack attack = new Attack(lfsr.get_sequence(ID), polynom, GENERATOR_REGISTER_COUNT);
        //attack.print_stream_key();
        attack.siegenthaler_correlation(); //vysledkom su naplnene nove registre pre ekvivalentny generator
        //attack.printNewLFSR();
        attack.combination_function();
        //attack.printCombinationStatistic();
        attack.simulate();

        //attack.printRecreateStream();
        System.out.println("\n"+attack.get_match(lfsr.get_orig()));
    }
}
