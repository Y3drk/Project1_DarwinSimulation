package agh.ics.project;

import java.util.HashSet;
import java.util.Set;

public class Animal implements IMapElement, Comparable<Animal> {
    //key element of the simulation
    // what attributes should it have : (fot now everything protected later, adaptation)
    protected Vector2d position;
    protected MapDirection orientation;
    protected int energy;
    protected Genome genotype;
    protected IWorldMap map;
    protected Set<IPositionChangeObserver> observers = new HashSet<>();

    //animal constructor for Adams and Eves
    public Animal(IWorldMap map, Vector2d position,int fullEnergy){
        this.map = map;
        this.position = position;
        this.energy = fullEnergy;
        this.genotype = new Genome();
        this.orientation = MapDirection.NORTH.getRandom();
    }

    //constructor for newly born animals and also magically spawned animals
    public Animal(IWorldMap map, Vector2d parentPosition, int parentsEnergy, Genome combinedGenotype){
        this.map = map;
        this.position = parentPosition;
        this.energy = parentsEnergy;
        this.genotype = combinedGenotype;
        this.orientation = MapDirection.NORTH.getRandom();
    }


    //IMapElement interface implementation
    public Vector2d getPosition() {
        return this.position;
    }

    public String toString() {
        return switch (orientation) {
            case NORTH -> "N";
            case EAST -> "E";
            case SOUTH -> "S";
            case WEST -> "W";
            case NORTHWEST -> "NW";
            case NORTHEAST -> "NE";
            case SOUTHWEST -> "SW";
            case SOUTHEAST -> "SE";
        };
    }

    public String imageAddress() {
        return switch (orientation) {
            case NORTH -> "src/main/resources/up.png";
            case EAST -> "src/main/resources/right.png";
            case SOUTH -> "src/main/resources/down.png";
            case WEST -> "src/main/resources/left.png";
            case NORTHWEST -> "src/main/resources/leftUp.png";
            case NORTHEAST -> "src/main/resources/rightUp.png";
            case SOUTHWEST -> "src/main/resources/leftDown.png";
            case SOUTHEAST -> "src/main/resources/rightDown.png";
        };
    }

    //what else should an animal do

    //move
    public void move(){
        Vector2d oldPosition = this.getPosition();

        int move = this.genotype.getRandomGen();

        System.out.println("ANIMAL FROM " + oldPosition.toString() + "WITH ORIENTATION: " + this.orientation.toString() + " HAS CHOSEN A MOVE: "+ move);

        switch (move) {
            case 0 ->{
                Vector2d newPosition = this.position.add(this.orientation.toUnitVector());

                System.out.println("CALCULATED NEW POSITION: "+ newPosition.toString());
                System.out.println("IF ANIMAL CAN MOVE THERE:" + this.map.canMoveTo(newPosition));

                if (this.map.canMoveTo(newPosition)) {
                    this.position = newPosition;
                    this.positionChanged(oldPosition, newPosition); //poking the map to reposition the animal

                } else { //considering a moment when an animal steps out of boundaries and may teleport to the other side if the map is in TeleportMode
                    if (this.map.getTeleportValue()) {
                        Vector2d uR = this.map.getCorners()[1];

                        //maybe we can make this easier using modulo, but generally it works
                        if (newPosition.x > ((UniversalMap) this.map).width) newPosition = new Vector2d(0,newPosition.y);
                        else if (newPosition.x < 0) newPosition = new Vector2d(uR.x, newPosition.y);
                        else if (newPosition.y > ((UniversalMap) this.map).height) newPosition = new Vector2d(newPosition.x, 0);
                        else newPosition = new Vector2d(newPosition.x, uR.y);
                        this.position = newPosition; //that should pretty much cover all the cases
                        this.positionChanged(oldPosition, newPosition);
                    }
                }
            }
            case 1 -> this.orientation = this.orientation.next();
            case 2 -> this.orientation = this.orientation.doubleNext();
            case 3 -> this.orientation = this.orientation.doubleNext().next();
            case 4 -> {
                Vector2d newPosition = this.position.subtract(this.orientation.toUnitVector());

                System.out.println("CALCULATED NEW POSITION: "+ newPosition.toString());
                System.out.println("IF ANIMAL CAN MOVE THERE:" + this.map.canMoveTo(newPosition));

                if (this.map.canMoveTo(newPosition)) {
                    this.position = newPosition;
                    this.positionChanged(oldPosition, newPosition);
                } else {
                    if (this.map.getTeleportValue()) {
                        Vector2d uR = (this.map).getCorners()[1];
                        if (newPosition.x > ((UniversalMap) this.map).width) newPosition = new Vector2d(0,newPosition.y);
                        else if (newPosition.x < 0) newPosition = new Vector2d(uR.x, newPosition.y);
                        else if (newPosition.y > ((UniversalMap) this.map).height) newPosition = new Vector2d(newPosition.x, 0);
                        else newPosition = new Vector2d(newPosition.x, uR.y);
                        this.position = newPosition; //that should pretty much cover all the cases
                        this.positionChanged(oldPosition, newPosition);
                    }
                }
            }
            case 5 -> this.orientation = this.orientation.doublePrevious().previous();
            case 6 -> this.orientation = this.orientation.doublePrevious();
            case 7 -> this.orientation = this.orientation.previous();
        }
    }

    //reproduce
    //again we assume, that the animal on which it was called was the stronger one and that both of them have enough energy to reproduce
    // and in effect of love... we get a new cub
    public Animal reproduce(Animal mother){
        int cubEnergy = (int) (this.energy * 0.25) + (int) (mother.energy * 0.25);
        int fathersEnergyProportion = this.energy / (this.energy + mother.energy);
        int mothersEnergyProportion = mother.energy / (this.energy + mother.energy);
        Genome cubGenome = new Genome(this.genotype, mother.genotype, fathersEnergyProportion, mothersEnergyProportion);

        //reducing parents energy
        this.energy = (int) (this.energy * 0.75);
        mother.energy = (int) (mother.energy * 0.75);

        //creating and returning a newborn
        return new Animal(this.map, this.position, cubEnergy,cubGenome);
    }

    @Override //important when placing animals
    public int compareTo(Animal o) {
        return -Integer.compare(this.energy, o.energy); //"- " because we want our order to be descending
    }

    //servicing observers
    public void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        this.observers.remove(observer);
    } //may prove useless

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for (IPositionChangeObserver obs : observers) {
            obs.positionChanged(oldPosition, newPosition, this);
        }
    }
}

