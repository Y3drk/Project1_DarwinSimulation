package agh.ics.project;

public class SimulationEngine implements Runnable {
    protected IWorldMap map;

    protected boolean isMagical;
    protected int magicMiracles;
    protected boolean didMiracleHappen = false;

    protected int days = -1;
    protected int daysLived = 0;
    protected int totalDeaths = 0;

    protected boolean ifUpdate = false;
    protected static int moveDelay = 300;

    public SimulationEngine(IWorldMap map, boolean isMagical){
        this.map = map;
        this.isMagical = isMagical;

        if (isMagical) {
            this.magicMiracles = 3;
        }
    }


    public void run() {
        while (this.map.countAnimals()>0) {
                this.days++;
                int[] temporary = this.map.removeDeadAnimals();
                totalDeaths += temporary[0];
                daysLived += temporary[1];

                this.map.moveAllAnimals();

                this.map.eatingGrass();

                this.map.reproduction();

                this.map.AddNewGrass();

                if (isMagical && this.map.countAnimals() == 5 && this.magicMiracles > 0) {
                    this.magicMiracles--;
                    this.didMiracleHappen = true;
                    this.map.cloneAnimals();
                }

                this.ifUpdate = true;

                try {
                    Thread.sleep(moveDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    public boolean getUpdateStatus() {return this.ifUpdate;}

    public void resetUpdateStatus() { this.ifUpdate = false;}

    public int getDay() {return this.days;}

    public int getAverageLifeLength() {
        if (this.totalDeaths != 0) return (daysLived / totalDeaths);
        else return 0;
    }

    public int getMagicMiraclesLeft(){
        return this.magicMiracles;
    }

    public boolean getMagicStatus() {return this.isMagical;}

    public boolean getMiracleStatus() {return this.didMiracleHappen;}

    public void resetMiracleStatus() {this.didMiracleHappen = false;}
}

