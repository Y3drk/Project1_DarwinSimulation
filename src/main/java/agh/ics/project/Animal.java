package agh.ics.project;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Animal implements IMapElement, Comparable<Animal> {
    protected Vector2d position;
    protected MapDirection orientation;
    protected int energy;
    protected int children = 0;
    protected int daysAlive = 0;
    protected Genome genotype;
    protected IWorldMap map;
    protected Set<IPositionChangeObserver> observers = new HashSet<>();
    protected int trackedChildren = 0;
    protected boolean isDescendant = false;

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
        if (this.energy > 200) {
            return switch (orientation){
                case NORTH -> "src/main/resources/highEnergyUp.png";
                case EAST -> "src/main/resources/highEnergyRight.png";
                case SOUTH -> "src/main/resources/highEnergyDown.png";
                case WEST -> "src/main/resources/highEnergyLeft.png";
                case NORTHWEST -> "src/main/resources/highEnergyLeftUp.png";
                case NORTHEAST -> "src/main/resources/highEnergyRightUp.png";
                case SOUTHWEST -> "src/main/resources/highEnergyLeftDown.png";
                case SOUTHEAST -> "src/main/resources/highEnergyRightDown.png";
            };
        }
        else if (this.energy < 50){
            return switch (orientation) {
                case NORTH -> "src/main/resources/lowEnergyUp.png";
                case EAST -> "src/main/resources/lowEnergyRight.png";
                case SOUTH -> "src/main/resources/lowEnergyDown.png";
                case WEST -> "src/main/resources/lowEnergyLeft.png";
                case NORTHWEST -> "src/main/resources/lowEnergyLeftUp.png";
                case NORTHEAST -> "src/main/resources/lowEnergyRightUp.png";
                case SOUTHWEST -> "src/main/resources/lowEnergyLeftDown.png";
                case SOUTHEAST -> "src/main/resources/lowEnergyRightDown.png";
            };
        }
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
        this.daysAlive++;
        Vector2d oldPosition = this.getPosition();

        int move = this.genotype.getRandomGen();
        //diagnostic print
        //System.out.println("ANIMAL FROM " + oldPosition.toString() + "WITH ORIENTATION: " + this.orientation.toString() + " HAS CHOSEN A MOVE: "+ move);

        switch (move) {
            case 0 ->{
                Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
                //diagnostic prints
                //System.out.println("CALCULATED NEW POSITION: "+ newPosition.toString());
                //System.out.println("IF ANIMAL CAN MOVE THERE:" + this.map.canMoveTo(newPosition));

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

                //diagnostic prints
                //System.out.println("CALCULATED NEW POSITION: "+ newPosition.toString());
                //System.out.println("IF ANIMAL CAN MOVE THERE:" + this.map.canMoveTo(newPosition));
                //System.out.println(this.genotype.toString());

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animal animal)) return false;
        return energy == animal.energy && children == animal.children && daysAlive == animal.daysAlive && trackedChildren == animal.trackedChildren && position.equals(animal.position) && orientation == animal.orientation && genotype.equals(animal.genotype) && map.equals(animal.map) && observers.equals(animal.observers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, orientation, energy, children, daysAlive, genotype, map, observers, trackedChildren);
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

    public void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    public Genome getGenome() {return this.genotype;}

    public int getTrackedChildren() {return this.trackedChildren;}

    public void setAsDescendant(){ this.isDescendant = true;}

    public void resetTrackingChildren(){
        this.trackedChildren = 0;
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for (IPositionChangeObserver obs : observers) {
            obs.positionChanged(oldPosition, newPosition, this);
        }
    }
}

