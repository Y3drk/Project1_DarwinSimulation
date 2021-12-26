package agh.ics.project.gui;

import agh.ics.project.Genome;
import agh.ics.project.IWorldMap;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DominantController {
    protected Label displayedGenomeTP;
    protected HBox dominantGenomeTP;

    protected Label displayedGenomeWL;
    protected HBox dominantGenomeWL;

    protected Genome currentDominantTP;
    protected Genome currentDominantWL;

    protected boolean genomeHighlightedTP = false;
    protected boolean genomeHighlightedWL = false;

    public DominantController(){
        Label dominantLabelTP = new Label("Dominant Genome:");
        this.displayedGenomeTP = new Label ("No dominant genome yet");
        this.dominantGenomeTP = new HBox(10, dominantLabelTP, displayedGenomeTP);
        dominantGenomeTP.setAlignment(Pos.CENTER);

        Label dominantLabelWL = new Label("Dominant Genome:");
        this.displayedGenomeWL = new Label ("No dominant genome yet");
        this.dominantGenomeWL = new HBox(10, dominantLabelWL, displayedGenomeWL);
        dominantGenomeWL.setAlignment(Pos.CENTER);

    }

    public void updateDominant(IWorldMap map){
        if (map.getTeleportValue()) {
            this.dominantGenomeTP.getChildren().remove(displayedGenomeTP);

            this.currentDominantTP = map.getDominantGenome();
            this.displayedGenomeTP = new Label(this.currentDominantTP.toString());
            this.dominantGenomeTP.getChildren().add(this.displayedGenomeTP);

        } else{
            this.dominantGenomeWL.getChildren().remove(displayedGenomeWL);

            this.currentDominantWL = map.getDominantGenome();
            this.displayedGenomeWL = new Label(this.currentDominantWL.toString());
            this.dominantGenomeWL.getChildren().add(this.displayedGenomeWL);
        }
    }

    public boolean getIfHighligted(boolean whichMap){
        if (whichMap){
            return genomeHighlightedTP;
        } else {
            return genomeHighlightedWL;
        }
    }

    public void changeIfHighlighted(boolean whichMap, boolean newValue) {
        if (whichMap) {
            this.genomeHighlightedTP = newValue;
        } else {
            this.genomeHighlightedWL = newValue;
        }
    }
}
