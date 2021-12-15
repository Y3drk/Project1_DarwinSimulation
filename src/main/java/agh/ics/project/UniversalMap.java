package agh.ics.project;

import java.util.*;

public class UniversalMap implements IWorldMap, IPositionChangeObserver{
    //since both maps only differ in rules regarding crossing their borders, we will implement them by using the same class,
    //only with different value of a variable teleportEnabled

    //V1 - teleportEnabled = true
    //with connected / rolled edges -> if an animal tries to move outside the map, it appears on the other side.

    //V2 - teleportEnabled = false
    // with The Wall build around its edges -> if an animal tries to move outside the map it stays in the same place and looses its turn

    //what do we want it to have:
    protected int width;
    protected int height;
    protected Vector2d bottomLeft;
    protected Vector2d upperRight; //sizes, and corners of the map

    protected int jungleWidth;
    protected int jungleHeight;
    protected Vector2d bottomLeftJungleCorner;
    protected Vector2d upperRightJungleCorner; // sizes, and corners ~ placement of the jungle

    protected Map<Vector2d, Grass> grass; //we can later write getters
    protected HashMap<Vector2d, ArrayList<Animal>> animals; //it's grass and animals
    protected ArrayList<Animal> animalStash = new ArrayList<>();

    protected int moveEnergyCost;
    protected int reproductionEnergyCost;// costs of a move and reproduction


    // energy profit from eating a grass and the beginning energy of Adams and Eves.
    protected int eatingEnergyProfit;
    protected int startEnergy;

    protected boolean teleportEnabled;

    //Map Constructor
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
            throw new IllegalArgumentException("Incorrect proportions!");
        }

        this.bottomLeftJungleCorner = new Vector2d((width-this.jungleWidth)/2,(height-this.jungleHeight)/2);
        this.upperRightJungleCorner = new Vector2d((this.bottomLeftJungleCorner.x + this.jungleWidth),((this.bottomLeftJungleCorner.y + this.jungleHeight)));

        Random generator = new Random();
        for (int i = 0; i < startingAnimals; i++) {
            Animal newAnimal = new Animal(this, new Vector2d(generator.nextInt(this.width),generator.nextInt(this.height)),startingEnergy);
            this.place(newAnimal);
        }
    }



    //IWorldMap interface implementation

    @Override // in our case animal can move freely, so we will use false to indicate that an animal has to teleport
    public boolean canMoveTo(Vector2d position) {
        return (position.precedes(this.upperRight) && position.follows(bottomLeft));
    }

    @Override //now we gotta put our assumptions into reality when it comes to inserting animals
    //when it comes to placing, we don't care if there is an animal or a grass on that particular field, we just place the animal
    public boolean place(Animal animal) {
        if (animal == null) return false; //the only option when placing can fail

        Vector2d pos = animal.getPosition();
        ArrayList<Animal> fieldAnimals = animals.get(pos);
        this.animalStash.add(animal);

        if (fieldAnimals == null){
            ArrayList<Animal> tmp = new ArrayList<>();
            animal.addObserver(this); //making map the observer of the animal
            tmp.add(animal);
            animals.put(pos,tmp);
        } else if (fieldAnimals.size() == 0) {
            animal.addObserver(this);
            fieldAnimals.add(animal);
            animals.put(pos,fieldAnimals);
        } else {
            animal.addObserver(this);
            fieldAnimals.add(animal);
            Collections.sort(fieldAnimals); //if i'm correct it will now use that overridden compareTo function
            animals.put(pos,fieldAnimals); //im doing it just in case, because I don't remember whether get() returns reference or value
        }
        return true;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    @Override //we assume that after putting an animal in the list it is sorted,
    // also if there are many animals in one field we return the strongest
    public Object objectAt(Vector2d position) {
        ArrayList<Animal> fieldAnimals = animals.get(position);
        if (fieldAnimals == null) return grass.get(position);
        else if (fieldAnimals.isEmpty()) return grass.get(position);
        else return fieldAnimals.get(0);
    }


    //what else should our map provide?

    //removing dead animals
    public int removeDeadAnimals(){
        ArrayList<Animal> animalsToRemove = new ArrayList<>(); //we collect all the dead animals
        int deadAnimals = 0;
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : animals.entrySet()) {
            ArrayList<Animal> listOfAnimals = entry.getValue();
            if (listOfAnimals.size() > 0){
                for (Animal animal: listOfAnimals) {
                    if (animal.energy < 0) {
                        animalsToRemove.add(animal);
                        deadAnimals++;
                    }
                }
            }
        }
        for (Animal deadBody : animalsToRemove) {  //and then remove them
            animals.get(deadBody.getPosition()).remove(deadBody);
            animalStash.remove(deadBody);
            //animalsToRemove.remove(i); for now lets hope that GC collects and thrashes that list
        }

        return deadAnimals;
    }

    //moving all animals - what we will trigger from our engine
    public void moveAllAnimals(){

        for (Animal animal: animalStash) {
            animal.energy -= moveEnergyCost;
            animal.move();
        }
    }

    //eating the grass
    public void eatingGrass(){
        LinkedList<Grass> toRemove = new LinkedList<>();

        grass.forEach((pos,grass) ->{
            ArrayList<Animal> fieldAnimals = animals.get(pos);
            if (fieldAnimals != null && fieldAnimals.size() > 0) {
                //some necessities for eating
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
                //after we collected all of the strongest animals, we can feed them
                for (Animal animal: eatingAnimals) {
                    animal.energy += this.eatingEnergyProfit / eatingAnimals.size();

                    //diagnostic prints
                    System.out.println("A GRASS WAS EATEN");
                }

                //we also need to remember to remove the grass, since we cant do it inside this loop we will do it the other way
                toRemove.add(grass);
            }
        });

        //after checking all fields we can remove the eaten tufts
        for (Grass tuft: toRemove) {
            grass.remove(tuft.getPosition());
        }
    }

    //the reproduction of the animals
    public void reproduction(){
        ArrayList<Animal> children = new ArrayList<>(); //we need to aadd them later to not invoke concurrentModificationException

        animals.forEach((pos,listOfAnimals) ->{
            if (listOfAnimals.size() >= 2) { //checking if we even have enough animals to reproduce
                if (listOfAnimals.get(0).energy > reproductionEnergyCost) { //to know if the strongest animal is fit enough to be a parent
                    ArrayList<Animal> potentialParents = new ArrayList<>(); //we should consider top 2 animals, but we may face a situation
                    //, when a lot of them have the same energy, and then we have to pick them randomly
                    // to get started
                    potentialParents.add(listOfAnimals.get(0));
                    int topEnergy = listOfAnimals.get(0).energy;
                    int ind = 1;
                    boolean flag = true;
                    int differentEnergies = 1;

                    while (flag && ind < listOfAnimals.size() && differentEnergies < 2){
                        Animal otherParent = listOfAnimals.get(ind);
                        if (otherParent.energy < this.reproductionEnergyCost) flag = false; //when other animal is not strong enough
                        else if (otherParent.energy < topEnergy && potentialParents.size() < 2) {
                            //when the other animal is weaker but still strong enough, and we didn't choose more than one animal before
                            differentEnergies = 2;
                            potentialParents.add(otherParent);
                        }
                        else if (otherParent.energy == topEnergy) {
                            // if we have a couple of animals with the same energy, big enough to reproduce
                            potentialParents.add(otherParent);
                            ind++;
                        }
                    } //so now that we've collected potential parents, we should decide what to do with them
                    if (potentialParents.size() == 2){ //if we only have 2, then it's easy
                        Animal child = potentialParents.get(0).reproduce(potentialParents.get(1));
                        children.add(child);

                    } else if (potentialParents.size() > 2) { //now it gets tricky, because we have to choose them randomly
                        Animal parent1, parent2;
                        int choosen1;
                        Random generator = new Random();
                        choosen1 = generator.nextInt(potentialParents.size());
                        parent1 = potentialParents.get(choosen1);
                        boolean scndflag = true;

                        while (scndflag) { //making sure that we don't choose one animal twice
                            int choosen2 = generator.nextInt(potentialParents.size());
                            if (choosen2 != choosen1) {
                                parent2 = potentialParents.get(choosen2);
                                Animal child = parent1.reproduce(parent2);
                                children.add(child);
                                scndflag = false;
                            }
                        }
                    }
                }
            }
        });

        for (Animal child: children) {  //adding children later to avoid concurrent modification error
            this.place(child);

            //diagnostic prints
            System.out.println("A CHILD WAS BORN ON FIELD" + child.getPosition() + " AND HAS: " + child.energy + " ENERGY");
        }
    }

    //adding two new tufts of grass: one on Steppe and one in the Jungle
    public void AddNewGrass() {
        //now we have to address three difficulties: one, that we can't place a new grass if any animal stands on the particular field
        // two, that we have to place each tuft in different ecosystem (Steppe and Jungle respectively),
        // and three, that if the Jungle is full we don't place a tuft there
        //also it would be stupid to put a new tuft on top of the other one

        Random generator = new Random();

        ArrayList<Vector2d> steppeFreeFields = new ArrayList<>();
        ArrayList<Vector2d> jungleFreeFields = new ArrayList<>();

        for (int i=0; i < this.height; i ++){  //selecting fields where we can put new tufts of grass
            for (int j=0; j < this.width; j++){

                Vector2d checker = new Vector2d(i,j);

                if (checker.precedes(upperRightJungleCorner) && checker.follows(bottomLeftJungleCorner) && !isOccupied(checker)) jungleFreeFields.add(checker);

                else if (!isOccupied(checker)) steppeFreeFields.add(checker);
            }
        }

        //adding a new grass on one of the steppe fields
        if (steppeFreeFields.size() > 0) {
            Vector2d chosenSteppe = steppeFreeFields.get(generator.nextInt(steppeFreeFields.size()));
            this.grass.put(chosenSteppe, new Grass(chosenSteppe));
        }

        //doing the same for jungle
        if (jungleFreeFields.size() > 0) {
            Vector2d chosenJungle = jungleFreeFields.get(generator.nextInt(jungleFreeFields.size()));
            this.grass.put(chosenJungle, new Grass(chosenJungle));
        }
    }

    //for general purposes ->
    public int countAnimals() {
        int counter = 0;
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : animals.entrySet()) {
            ArrayList<Animal> value = entry.getValue();
            counter += value.size();
        }
        return counter;
    }

    //implementing magical strategy
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

    //for teleporting moves
    public Vector2d[] getCorners(){
        return new Vector2d[] {this.bottomLeft, this.upperRight};
    }

    @Override //implementing IPositionChangedObserver interface
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
        //diagnostic prints
        System.out.println("ANIMAL HAS MOVED FROM:" + oldPosition.toString() + " TO " + newPosition.toString());
        System.out.println("----------------");
    }

    //used in animal to decide what to do about reckless animals, crossing the borders
    public boolean getTeleportValue() {
        return this.teleportEnabled;
    }

    @Override //used purely for testing purposes
    public String toString() {
        MapVisualizer drawing = new MapVisualizer(this);
        Vector2d[] corners = this.getCorners();
        return drawing.draw(corners[0], corners[1]);
    }
}

