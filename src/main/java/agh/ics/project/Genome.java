package agh.ics.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class Genome {

    private static final Random randomGenerator = new Random();
    private final ArrayList<Integer> genotype = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genome genome = (Genome) o;
        return genotype.equals(genome.genotype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genotype);
    }

    //constructor for Adams and Eves or magically spawned animals
    public Genome(){
        for (int i = 0 ; i < 32; i++){
            genotype.add(randomGenerator.nextInt(8));
        }
        Collections.sort(genotype);
    }

    public int getRandomGen() {
        return genotype.get(randomGenerator.nextInt(32));
    }

    public String toString() {
        return this.genotype.toString();
    }


    //constructor for child Animals,
    // we assume that father is the stronger animal,
    public Genome(Genome father, Genome mother, int fathersEnergyProportion, int mothersEnergyProportion) {
        Random generator = new Random();
        if (generator.nextInt() <= 0.5) {
            for (int i = 0; i < fathersEnergyProportion; i++) genotype.add(father.genotype.get(i));
            for (int j = fathersEnergyProportion; j < 32; j++) genotype.add(mother.genotype.get(j));
        } else {
            for (int i = 0; i < mothersEnergyProportion; i++) genotype.add(mother.genotype.get(i));
            for (int j = mothersEnergyProportion; j < 32; j++) genotype.add(father.genotype.get(j));
        }
    }


}

