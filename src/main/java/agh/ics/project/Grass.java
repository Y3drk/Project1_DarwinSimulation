package agh.ics.project;

public class Grass implements IMapElement {
    private Vector2d grassPosition;

    public Grass(Vector2d pos){
        this.grassPosition=pos;
    }

    public Vector2d getPosition(){
        return this.grassPosition;
    }

    @Override
    public String toString(){
        return "*";
    }

    @Override  //TO DO
    public String imageAddress() {
        return "src/main/resources/algs.png";
    }
}