package agh.ics.project;

import java.util.HashSet;
import java.util.Set;

public class Animal implements IMapElement, Comparable<Animal> {
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
        //idea
        //different colours for different energy levels
        //if(this.energy > 50 && this.energy < 200)
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
                    this.positionChanged(oldPosition, newPosition);

                } else {
                    if (this.map.getTeleportValue()) {
                        Vector2d uR = this.map.getCorners()[1];

                        if (newPosition.x > ((UniversalMap) this.map).width){
                            newPosition = new Vector2d(0,newPosition.y);
                        }
                        else if (newPosition.x < 0) {
                            newPosition = new Vector2d(uR.x, newPosition.y);
                        }
                        else if (newPosition.y > ((UniversalMap) this.map).height) {
                            newPosition = new Vector2d(newPosition.x, 0);
                        }
                        else {
                            newPosition = new Vector2d(newPosition.x, uR.y);
                        }
                        this.position = newPosition;
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

                        if (newPosition.x > ((UniversalMap) this.map).width) {
                            newPosition = new Vector2d(0,newPosition.y);
                        }
                        else if (newPosition.x < 0) {
                            newPosition = new Vector2d(uR.x, newPosition.y);
                        }
                        else if (newPosition.y > ((UniversalMap) this.map).height) {
                            newPosition = new Vector2d(newPosition.x, 0);
                        }
                        else {
                            newPosition = new Vector2d(newPosition.x, uR.y);
                        }
                        this.position = newPosition;
                        this.positionChanged(oldPosition, newPosition);
                    }
                }
            }
            case 5 -> this.orientation = this.orientation.doublePrevious().previous();
            case 6 -> this.orientation = this.orientation.doublePrevious();
            case 7 -> this.orientation = this.orientation.previous();
        }
    }

    // we assume, that the animal on which it was called was the stronger one and that both of them have enough energy to reproduce
    public Animal reproduce(Animal mother){
        int cubEnergy = (int) (this.energy * 0.25) + (int) (mother.energy * 0.25);
        int fathersEnergyProportion = this.energy / (this.energy + mother.energy);
        int mothersEnergyProportion = mother.energy / (this.energy + mother.energy);
        Genome cubGenome = new Genome(this.genotype, mother.genotype, fathersEnergyProportion, mothersEnergyProportion);

        this.energy = (int) (this.energy * 0.75);
        mother.energy = (int) (mother.energy * 0.75);

        return new Animal(this.map, this.position, cubEnergy,cubGenome);
    }

    @Override
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

