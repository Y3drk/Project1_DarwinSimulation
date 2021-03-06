package agh.ics.project;

import java.util.*;

import static java.lang.Math.max;

public class UniversalMap implements IWorldMap, IPositionChangeObserver{
    //since both maps only differ in rules regarding crossing their borders, we will implement them by using the same class,
    //only with different value of a variable teleportEnabled

    //V1 - teleportEnabled = true
    //if an animal tries to move outside the map, it appears on the other side.

    //V2 - teleportEnabled = false
    //if an animal tries to move outside the map it stays in the same place and looses its turn.
    
    protected int width;
    protected int height;
    protected Vector2d bottomLeft;
    protected Vector2d upperRight;

    protected int jungleWidth;
    protected int jungleHeight;
    protected Vector2d bottomLeftJungleCorner;
    protected Vector2d upperRightJungleCorner;

    protected Map<Vector2d, Grass> grass;
    protected HashMap<Vector2d, ArrayList<Animal>> animals;
    protected ArrayList<Animal> animalStash = new ArrayList<>();

    protected int moveEnergyCost;
    protected int reproductionEnergyCost;
    
    protected int eatingEnergyProfit;
    protected int startEnergy;

    protected boolean teleportEnabled;
    
    public UniversalMap(int width, int height, double jungleToStepRatio, boolean teleportEnabled, int startingEnergy, int moveEnergyCost, int eatingEnergyProfit, int startingAnimals){
        this.width = (width-1);
        this.height = (height-1);
        this.startEnergy = startingEnergy;
        this.reproductionEnergyCost = (int) (startEnergy * 0.5);
        this.eatingEnergyProfit = eatingEnergyProfit;
        this.moveEnergyCost = moveEnergyCost;

        this.teleportEnabled = teleportEnabled;

        this.grass = new LinkedHashMap<>();
        this.animals = new LinkedHashMap<>();

        this.bottomLeft = new Vector2d(0,0);
        this.upperRight = new Vector2d(this.width,this.height);

        this.jungleHeight = (int) (height*jungleToStepRatio);
        this.jungleWidth = (int) (width*jungleToStepRatio);

        if (jungleToStepRatio > 1) {
            throw new IllegalArgumentException("INCORRECT PROPORTIONS!");
        }

        this.bottomLeftJungleCorner = new Vector2d((width-this.jungleWidth)/2,(height-this.jungleHeight)/2);
        this.upperRightJungleCorner = new Vector2d((this.bottomLeftJungleCorner.x + this.jungleWidth),((this.bottomLeftJungleCorner.y + this.jungleHeight)));

        Random generator = new Random();
        for (int i = 0; i < startingAnimals; i++) {
            Animal newAnimal = new Animal(this, new Vector2d(generator.nextInt(this.width),generator.nextInt(this.height)),startingEnergy);
            this.place(newAnimal);
        }
    }
    

    @Override
    public boolean canMoveTo(Vector2d position) {
        return (position.precedes(this.upperRight) && position.follows(bottomLeft));
    }

    @Override
    public boolean place(Animal animal) {
        if (animal == null) return false;

        Vector2d pos = animal.getPosition();
        ArrayList<Animal> fieldAnimals = animals.get(pos);
        this.animalStash.add(animal);

        if (fieldAnimals == null){
            ArrayList<Animal> tmp = new ArrayList<>();
            animal.addObserver(this);
            tmp.add(animal);
            animals.put(pos,tmp);
        } else if (fieldAnimals.size() == 0) {
            animal.addObserver(this);
            fieldAnimals.add(animal);
            animals.put(pos,fieldAnimals);
        } else {
            animal.addObserver(this);
            fieldAnimals.add(animal);
            Collections.sort(fieldAnimals); 
            animals.put(pos,fieldAnimals);
        }
        return true;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    @Override //we assume that after putting an animal in the list it is sorted.
    public Object objectAt(Vector2d position) {
        ArrayList<Animal> fieldAnimals = animals.get(position);
        if (fieldAnimals == null) return grass.get(position);
        else if (fieldAnimals.isEmpty()) return grass.get(position);
        else return fieldAnimals.get(0);
    }

    public int[] removeDeadAnimals(){
        ArrayList<Animal> animalsToRemove = new ArrayList<>();
        int sumDaysLived = 0;
        int deadAnimals = 0;
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : animals.entrySet()) {
            ArrayList<Animal> listOfAnimals = entry.getValue();
            if (listOfAnimals.size() > 0){
                for (Animal animal: listOfAnimals) {
                    if (animal.energy < 0) {
                        animalsToRemove.add(animal);
                        sumDaysLived += animal.daysAlive;
                        deadAnimals++;
                    }
                }
            }
        }
        for (Animal deadBody : animalsToRemove) {
            animals.get(deadBody.getPosition()).remove(deadBody);
            animalStash.remove(deadBody);
        }

        return new int[] {deadAnimals, sumDaysLived};
    }
    
    public void moveAllAnimals(){
        for (Animal animal: animalStash) {
            animal.energy -= moveEnergyCost;
            animal.move();
        }
    }

    public void eatingGrass(){
        LinkedList<Grass> toRemove = new LinkedList<>();

        grass.forEach((pos,grass) ->{
            ArrayList<Animal> fieldAnimals = animals.get(pos);
            if (fieldAnimals != null && fieldAnimals.size() > 0) {
                ArrayList<Animal> eatingAnimals = new ArrayList<>();
                boolean flag = true;
                int topEnergy = fieldAnimals.get(0).energy;
                eatingAnimals.add(fieldAnimals.get(0));
                int ind = 1;

                while(flag && ind < fieldAnimals.size()) {
                    Animal rival = fieldAnimals.get(ind);
                    if (rival.energy < topEnergy) flag = false;
                    else {
                        eatingAnimals.add(rival);
                        ind++;
                    }
                }
                for (Animal animal: eatingAnimals) {
                    animal.energy += this.eatingEnergyProfit / eatingAnimals.size();
                }
                toRemove.add(grass);
            }
        });
        for (Grass tuft: toRemove) {
            grass.remove(tuft.getPosition());
        }
    }

    public void reproduction(){
        ArrayList<Animal> children = new ArrayList<>();

        animals.forEach((pos,listOfAnimals) ->{
            if (listOfAnimals.size() >= 2) {
                if (listOfAnimals.get(0).energy > reproductionEnergyCost) {
                    ArrayList<Animal> potentialParents = new ArrayList<>();
                    potentialParents.add(listOfAnimals.get(0));
                    int topEnergy = listOfAnimals.get(0).energy;

                    for (int i=1; i< listOfAnimals.size(); i++){
                        Animal otherParent = listOfAnimals.get(i);

                        if (otherParent.energy < this.reproductionEnergyCost) break;
                        else if (otherParent.energy < topEnergy && potentialParents.size() == 1){
                            potentialParents.add(otherParent);
                            break;
                        } else if (otherParent.energy == topEnergy) {
                            potentialParents.add(otherParent);
                        }

                    }

                    if (potentialParents.size() == 2){
                        Animal child = potentialParents.get(0).reproduce(potentialParents.get(1));
                        if(potentialParents.get(0).isDescendant || potentialParents.get(1).isDescendant) child.isDescendant = true;
                        potentialParents.get(0).children += 1;
                        potentialParents.get(1).children += 1;
                        potentialParents.get(0).trackedChildren += 1;
                        potentialParents.get(1).trackedChildren += 1;
                        children.add(child);

                    } else if (potentialParents.size() > 2) {
                        Animal parent1, parent2;
                        int firstParentChosen;
                        Random generator = new Random();
                        firstParentChosen = generator.nextInt(potentialParents.size());
                        parent1 = potentialParents.get(firstParentChosen);
                        boolean secondParentFlag = true;

                        while (secondParentFlag) {
                            int secondParentChosen = generator.nextInt(potentialParents.size());
                            if (secondParentChosen != firstParentChosen) {
                                parent2 = potentialParents.get(secondParentChosen);
                                Animal child = parent1.reproduce(parent2);
                                if(parent1.isDescendant || parent2.isDescendant) child.isDescendant = true;
                                parent1.children += 1;
                                parent1.trackedChildren += 1;
                                parent2.children += 1;
                                parent2.trackedChildren +=1;
                                children.add(child);
                                secondParentFlag = false;
                            }
                        }
                    }
                }
            }
        });
        for (Animal child: children) {
            this.place(child);
        }
    }

    public void AddNewGrass() {
        Random generator = new Random();

        ArrayList<Vector2d> steppeFreeFields = new ArrayList<>();
        ArrayList<Vector2d> jungleFreeFields = new ArrayList<>();

        for (int i=0; i < this.height; i ++){
            for (int j=0; j < this.width; j++){
                Vector2d checker = new Vector2d(i,j);

                if (checker.precedes(upperRightJungleCorner) && checker.follows(bottomLeftJungleCorner) && !isOccupied(checker)) jungleFreeFields.add(checker);

                else if (!isOccupied(checker)) steppeFreeFields.add(checker);
            }
        }

        if (steppeFreeFields.size() > 0) {
            Vector2d chosenSteppe = steppeFreeFields.get(generator.nextInt(steppeFreeFields.size()));
            this.grass.put(chosenSteppe, new Grass(chosenSteppe));
        }

        if (jungleFreeFields.size() > 0) {
            Vector2d chosenJungle = jungleFreeFields.get(generator.nextInt(jungleFreeFields.size()));
            this.grass.put(chosenJungle, new Grass(chosenJungle));
        }
    }

    public int countAnimals() {
        int counter = 0;
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : animals.entrySet()) {
            ArrayList<Animal> value = entry.getValue();
            counter += value.size();
        }
        return counter;
    }

    public int countGrass(){
        return this.grass.size();
    }

    public void cloneAnimals(){
        ArrayList<Animal> clones = new ArrayList<>();

        for (Animal animal: animalStash) {
            Random generator = new Random();
            Vector2d clonePosition = new Vector2d(generator.nextInt(width),generator.nextInt(height));
            Animal clone = new Animal(this, clonePosition, this.startEnergy, animal.genotype);
            clones.add(clone);
        }

        for (Animal clone: clones) {
            this.place(clone);
        }
    }

    public Vector2d[] getCorners(){
        return new Vector2d[] {this.bottomLeft, this.upperRight};
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        ArrayList<Animal> oldFieldList = animals.get(oldPosition);
        oldFieldList.remove(animal);

        ArrayList<Animal> newFieldList = animals.get(newPosition);
        if (newFieldList == null) {
            ArrayList<Animal> tmp = new ArrayList<>();
            tmp.add(animal);
            animals.put(newPosition, tmp);
        } else if (newFieldList.size() == 0) {
            newFieldList.add(animal);
            animals.put(newPosition,newFieldList);
        } else {
            newFieldList.add(animal);
            Collections.sort(newFieldList);
            animals.put(newPosition,newFieldList);
        }
    }

    public boolean getTeleportValue() {return this.teleportEnabled;}

    public Vector2d[] getJungleCorners() {return new Vector2d[] {this.bottomLeftJungleCorner, this.upperRightJungleCorner};}

    public int getAverageEnergy(){
        int energySum = 0;
        for (Animal animal: animalStash) {
            energySum += animal.energy;
        }
        return energySum / animalStash.size();
    }

    public int getAverageChildren(){
        int childrenSum = 0;
        for (Animal animal: animalStash) {
            childrenSum += animal.children;
        }
        return childrenSum / animalStash.size();
    }

    public Genome getDominantGenome() {
        Map<Genome,Integer> genotypes = new HashMap<>();
        for (Animal animal: animalStash) {
           if (genotypes.containsKey(animal.genotype)) {
               int occurrences = genotypes.get(animal.genotype);
               genotypes.put(animal.genotype, occurrences+1);
           } else {
               genotypes.put(animal.genotype, 1);
           }
        }
        int biggest = 0;
        Genome dominant = new Genome();

        for (Map.Entry<Genome, Integer> entry : genotypes.entrySet()){
            int amountOfGenotypes = entry.getValue();
            if (amountOfGenotypes > biggest){
                biggest = amountOfGenotypes;
                dominant = entry.getKey();
            }
        }
        return dominant;
    }

    public boolean checkBeingAlive(Animal animal){ return animalStash.contains(animal);}

    public int getDescendants(){
        int counter = -1;
        for (Animal animal: animalStash) {
            if (animal.isDescendant) counter++;
        }
        return max(counter,0);
    }

    public void clearDescendants(){
        for (Animal animal: animalStash) {
            animal.isDescendant = false;
        }
    }

    @Override //used purely for testing purposes
    public String toString() {
        MapVisualizer drawing = new MapVisualizer(this);
        Vector2d[] corners = this.getCorners();
        return drawing.draw(corners[0], corners[1]);
    }
}

