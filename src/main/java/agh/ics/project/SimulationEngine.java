package agh.ics.project;

public class SimulationEngine implements Runnable {
    protected IWorldMap map;

    boolean isMagical;
    int magicMiracles;

    int days = -1;

    protected boolean ifUpdate = false;

    public SimulationEngine(IWorldMap map, boolean isMagical){
        this.map = map;
        this.isMagical = isMagical;

        if (isMagical) {
            this.magicMiracles = 3;
        }
    }


    public void run() {
        int totalDeaths = 0;
        while (this.map.countAnimals() > 0) { //for now we have const. as limit but it will be removed later
            this.days++;
            totalDeaths += this.map.removeDeadAnimals(); //concurrent modification error

            this.map.moveAllAnimals();

            this.map.eatingGrass();

            this.map.reproduction();

            this.map.AddNewGrass();

            //diagnostic prints
            System.out.println("AFTER DAY NUMBER: " + days);
            System.out.println("TOTAL NUMBER OF DEAD ANIMALS: " + totalDeaths);
            System.out.println("NUMBER OF ALIVE ANIMALS: " + this.map.countAnimals());
            System.out.println(this.map);
            System.out.println("----------------------");

            if (isMagical && this.map.countAnimals() == 5 && this.magicMiracles > 0){
                this.magicMiracles--;
                this.map.cloneAnimals();
            }

            this.ifUpdate = true;
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getUpdateStatus() {return this.ifUpdate;}

    public void resetUpdateStatus() { this.ifUpdate = false;}
}

