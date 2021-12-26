package agh.ics.project.gui;

import agh.ics.project.Animal;
import agh.ics.project.IWorldMap;
import agh.ics.project.SimulationEngine;
import agh.ics.project.Vector2d;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class TrackingService {
    protected ToggleGroup togglesTP = new ToggleGroup();
    protected ToggleGroup togglesWL = new ToggleGroup();

    protected Animal trackedTP;
    protected int currentChildrenTP = 0;
    protected Label currentGenomeTP;
    protected int currentDescendantsTP = 0;
    protected VBox trackedAnimalInfoTP;

    protected Animal trackedWL;
    protected int currentChildrenWL = 0;
    protected Label currentGenomeWL;
    protected int currentDescendantsWL = 0;
    protected VBox trackedAnimalInfoWL;

    public TrackingService(){
        Label trackedChildrenLabelTP = new Label("All Children: " + this.currentChildrenTP);
        this.currentGenomeTP = new Label("Genome: No animal chosen yet");
        Label trackedDescendantsTP = new Label("Living Descendants: " + this.currentDescendantsTP);
        Label ifDeadTP = new Label("No animal chosen");
        this.trackedAnimalInfoTP = new VBox(5,  currentGenomeTP, trackedChildrenLabelTP, trackedDescendantsTP, ifDeadTP);
        trackedAnimalInfoTP.setAlignment(Pos.CENTER_LEFT);

        Label trackedChildrenLabelWL = new Label("All Children: " + this.currentChildrenWL);
        this.currentGenomeWL = new Label("Genome: No animal chosen yet");
        Label trackedDescendantsWL = new Label("Living Descendants: " + this.currentDescendantsWL);
        Label ifDeadWL = new Label("No animal chosen");
        this.trackedAnimalInfoWL = new VBox(5,  currentGenomeWL, trackedChildrenLabelWL, trackedDescendantsWL, ifDeadWL);
        trackedAnimalInfoWL.setAlignment(Pos.CENTER_LEFT);
    }

    public void setTrackingInfo(Animal animal, IWorldMap map, SimulationEngine engine) {
        if (map.getTeleportValue()) {
            this.trackedAnimalInfoTP.getChildren().clear();
            this.currentGenomeTP = new Label(animal.getGenome().toString().replaceAll(", ",""));
            currentGenomeTP.setMaxWidth(220.0);
            Label trackedChildrenLabelTP = new Label("All Children: " + animal.getTrackedChildren());
            Label trackedDescendantsTP = new Label("Living Descendants: " + map.getDescendants());
            Label ifDeadTP = new Label();
            if (map.checkBeingAlive(animal)){
                ifDeadTP.setText("The animal is alive");
            } else {
                ifDeadTP.setText(("The animal died on day: " + engine.getDay()));
                trackedTP = null;
            }
            this.trackedAnimalInfoTP.getChildren().addAll(currentGenomeTP,trackedChildrenLabelTP,trackedDescendantsTP, ifDeadTP);
            trackedAnimalInfoTP.setAlignment(Pos.CENTER_LEFT);
        } else {
            this.trackedAnimalInfoWL.getChildren().clear();
            this.currentGenomeWL = new Label(animal.getGenome().toString().replaceAll(", ",""));
            currentGenomeWL.setMaxWidth(220.0);
            Label trackedChildrenLabelWL = new Label("All Children: " + animal.getTrackedChildren());
            Label trackedDescendantsWL = new Label("Living Descendants: " + map.getDescendants());
            Label ifDeadWL = new Label();
            if (map.checkBeingAlive(animal)){
                ifDeadWL.setText("The animal is alive");
            } else {
                ifDeadWL.setText(("The animal died on day: " + engine.getDay()));
                trackedWL = null;
            }
            this.trackedAnimalInfoWL.getChildren().addAll(currentGenomeWL,trackedChildrenLabelWL,trackedDescendantsWL, ifDeadWL);
            trackedAnimalInfoWL.setAlignment(Pos.CENTER_LEFT);
        }
    }

    public void setToggles(GridPane grid, IWorldMap map, boolean whichMap, SimulationEngine engine) {
        Vector2d upperRight = map.getCorners()[1];
        Vector2d bottomLeft = map.getCorners()[0];

        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
                Vector2d testedPos = new Vector2d(i + bottomLeft.x - 1, upperRight.y - j + 1);
                if (map.objectAt(testedPos) instanceof Animal trackedAnimal) {
                    if (whichMap) {
                        ToggleButton toggleButtonTP = new ToggleButton();
                        toggleButtonTP.setBackground(null);
                        toggleButtonTP.setToggleGroup(togglesTP);

                        toggleButtonTP.setOnAction(event -> {
                            if (trackedTP != null) trackedTP.resetTrackingChildren();
                            map.clearDescendants();
                            trackedAnimal.resetTrackingChildren();
                            trackedTP = trackedAnimal;
                            trackedTP.setAsDescendant();
                            setTrackingInfo(trackedAnimal,map, engine);
                        });

                        grid.add(toggleButtonTP, i, j);
                        GridPane.setHalignment(toggleButtonTP, HPos.CENTER);
                    }
                    else {
                        ToggleButton toggleButtonWL = new ToggleButton();
                        toggleButtonWL.setBackground(null);
                        toggleButtonWL.setToggleGroup(togglesWL);

                        toggleButtonWL.setOnAction(event -> {
                            if (trackedWL != null) trackedWL.resetTrackingChildren();
                            map.clearDescendants();
                            trackedAnimal.resetTrackingChildren();
                            trackedWL = trackedAnimal;
                            trackedWL.setAsDescendant();
                            setTrackingInfo(trackedAnimal,map, engine);
                        });

                        grid.add(toggleButtonWL, i, j);
                        GridPane.setHalignment(toggleButtonWL, HPos.CENTER);
                    }
                }
            }
        }
    }
}
