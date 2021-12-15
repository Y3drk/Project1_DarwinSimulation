package agh.ics.project;

public class SimulationEngine {
    //what we want engine to do is to guide the whole process of simulation, every day
    //so what do we need inside of it
    protected IWorldMap map; //map which is our world basically

    //if the simulation is magical
    boolean isMagical;
    int magicMiracles;

    //we also should count the days of our simulation
    int days = -1;

    //we assume all the necessary initial information were passed to the Map which is an attribute of out Engine

    //simulation engine constructor
    public SimulationEngine(IWorldMap map, boolean isMagical){
        this.map = map;
        this.isMagical = isMagical;

        if (isMagical) {
            this.magicMiracles = 3;
        }
    }


    public void run() {
        int totalDeaths = 0;
        //repeat every day:
        while (days < 20 && this.map.countAnimals() > 0) { //for now we have const. as limit but it will be removed later
            this.days++;
            //removing all the dead animals
            totalDeaths += this.map.removeDeadAnimals(); //concurrent modification error


            //move all animals
            this.map.moveAllAnimals();              //concurrent Modification error

            //eating the grass
            this.map.eatingGrass();

            //reproduction of animals
            this.map.reproduction();

            //adding new tufts of grass to the map
            this.map.AddNewGrass();                    //probably an infinite loop

            System.out.println("AFTER DAY NUMBER: " + days);
            System.out.println("TOTAL NUMBER OF DEAD ANIMALS: " + totalDeaths);
            System.out.println("NUMBER OF ALIVE ANIMALS: " + this.map.countAnimals());
            System.out.println(this.map);
            System.out.println("----------------------");

            //if the simulation is magical we can potentially spawn extra 5 animals
            if (isMagical && this.map.countAnimals() == 5){
                this.magicMiracles--;
                this.map.cloneAnimals();
            }
        }
    }
}

