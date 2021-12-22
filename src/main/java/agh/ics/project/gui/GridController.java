package agh.ics.project.gui;

import agh.ics.project.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Map;

public class GridController {
    public static void changeGrid(GridPane grid, SimulationEngine engine, IWorldMap map, Vector2d upperRight, Vector2d bottomLeft, Map<String, Image> images, Animal trackedTP, Animal trackedWL) {
        engine.resetUpdateStatus();

        grid.getColumnConstraints().add(new ColumnConstraints(40));
        grid.getRowConstraints().add(new RowConstraints(40));

        Label xyLabel = new Label("y/x");
        grid.add(xyLabel, 0, 0);
        GridPane.setHalignment(xyLabel, HPos.CENTER);


        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            Label axisLabel = new Label(Integer.toString(i + bottomLeft.x - 1));
            grid.getColumnConstraints().add(new ColumnConstraints(40));
            grid.add(axisLabel, i, 0);
            GridPane.setHalignment(axisLabel, HPos.CENTER);

        }

        for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
            Label axisLabel = new Label(Integer.toString(upperRight.y - j + 1));
            grid.add(axisLabel, 0, j);
            grid.getRowConstraints().add(new RowConstraints(40));
            GridPane.setHalignment(axisLabel, HPos.CENTER);
        }
        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
                Vector2d testedPos = new Vector2d(i + bottomLeft.x - 1, upperRight.y - j + 1);
                if (map.isOccupied(testedPos)) {

                    GuiElementBox elem = new GuiElementBox((IMapElement) map.objectAt(testedPos), map, images, trackedTP, trackedWL);
                    grid.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);

                } else {
                    GuiElementBox elem = new GuiElementBox(map, testedPos);
                    grid.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);
                }
            }
        }
        grid.setGridLinesVisible(true);
    }

    public static void highlightDominant(GridPane grid, IWorldMap map, boolean which, Vector2d upperRight, Vector2d bottomLeft, Map<String, Image> images, Genome currentDominantTP, Genome currentDominantWL){

        grid.getColumnConstraints().add(new ColumnConstraints(40));
        grid.getRowConstraints().add(new RowConstraints(40));

        Label xyLabel = new Label("y/x");
        grid.add(xyLabel, 0, 0);
        GridPane.setHalignment(xyLabel, HPos.CENTER);


        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            Label axisLabel = new Label(Integer.toString(i + bottomLeft.x - 1));
            grid.getColumnConstraints().add(new ColumnConstraints(40));
            grid.add(axisLabel, i, 0);
            GridPane.setHalignment(axisLabel, HPos.CENTER);

        }

        for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
            Label axisLabel = new Label(Integer.toString(upperRight.y - j + 1));
            grid.add(axisLabel, 0, j);
            grid.getRowConstraints().add(new RowConstraints(40));
            GridPane.setHalignment(axisLabel, HPos.CENTER);
        }
        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
                Vector2d testedPos = new Vector2d(i + bottomLeft.x - 1, upperRight.y - j + 1);
                if (map.isOccupied(testedPos)) {
                    var lifeform = map.objectAt(testedPos);
                    GuiElementBox elem = new GuiElementBox((IMapElement) lifeform, map, images, null, null);
                    grid.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);

                    if (which) {
                        if (lifeform instanceof Animal && ((Animal) lifeform).getGenome().equals(currentDominantTP)) {
                            elem.verticalBox.setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    } else {
                        if (lifeform instanceof Animal && ((Animal) lifeform).getGenome().equals(currentDominantWL)) {
                            elem.verticalBox.setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }

                } else {
                    GuiElementBox elem = new GuiElementBox(map, testedPos);
                    grid.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);
                }
            }
        }
        grid.setGridLinesVisible(true);
    }

    public static void resetGrid(GridPane grid){
        grid.setGridLinesVisible(false);
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
    }
}
