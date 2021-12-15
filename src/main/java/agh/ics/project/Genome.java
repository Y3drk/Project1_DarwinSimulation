package agh.ics.project;

import java.util.ArrayList;
import java.util.Random;

public class Genome {

    private static Random randomGenerator = new Random();
    private ArrayList<Integer> genotype = new ArrayList<>();

    //constructor for Adams and Eves or magicaly spawned animals
    public Genome(){
        for (int i = 0 ; i < 32; i++){
            genotype.add(randomGenerator.nextInt(8));
        }
    }

    public int getRandomGen() {
        return genotype.get(randomGenerator.nextInt(32));
    }


    //constructor for child Animals,
    // we assume that father is the stronger animal, we also assume that fathersEnergyProportion is an int from 16 to 31 since he's stronger
    // and mothersEnergyProportion is an int as well, tho the smaller one.
    public Genome(Genome father, Genome mother, int fathersEnergyProportion, int mothersEnergyProportion) {
        Random generator = new Random();
        if (generator.nextInt() <= 0.5) { //we go from the left side
            for (int i = 0; i < fathersEnergyProportion; i++) genotype.add(father.genotype.get(i));
            for (int j = fathersEnergyProportion; j < 32; j++) genotype.add(mother.genotype.get(j));
        } else {
            for (int i = 0; i < mothersEnergyProportion; i++) genotype.add(mother.genotype.get(i));
            for (int j = mothersEnergyProportion; j < 32; j++) genotype.add(father.genotype.get(j));
        }
    }


}

