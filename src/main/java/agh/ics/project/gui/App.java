package agh.ics.project.gui;

import agh.ics.project.*;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;


public class App extends Application {
    //right now it's somewhere between lab7a and lab8, in the next few days I will try to push it further
    protected GridPane board = new GridPane();
    protected Scene scene = new Scene(board, 600, 600);

    protected Vector2d upperRight;
    protected Vector2d bottomLeft;

    protected IWorldMap map1;


    public void init() {

        try {
            //for now we will write all starting conditions here to check if the backend works
            //map parameters
            int startingAnimals = 10;
            int width = 10;
            int height = 10;

            //simulation parameters
            boolean isMagical = false;
            int startEnergy = 100;
            int moveEnergyCost = 1;
            int eatingGrassEnergyProfit = 20;
            double jungleToSteppeRatio = 0.4;
            boolean teleportEnabled = true;

            IWorldMap map = new UniversalMap(width,height,jungleToSteppeRatio, teleportEnabled, startEnergy,moveEnergyCost,eatingGrassEnergyProfit,startingAnimals);

            this.map1 = map;

            System.out.println("MAP AT DAY 0");
            System.out.println(map);
            System.out.println("--------------");

            upperRight = map.getCorners()[1];
            bottomLeft = map.getCorners()[0];

            SimulationEngine engine = new SimulationEngine(map,isMagical);
            engine.run();


        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
            System.exit(0);

        }
    }

    @Override
    public void start(Stage primaryStage) {

        board.getColumnConstraints().add(new ColumnConstraints(50));
        board.add(new Label("y/x"), 0, 0);


        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            Label axisLabel = new Label(Integer.toString(i + bottomLeft.x - 1));
            board.getColumnConstraints().add(new ColumnConstraints(50));
            board.add(axisLabel, i, 0);
            GridPane.setHalignment(axisLabel, HPos.CENTER);

        }

        for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
            Label axisLabel = new Label(Integer.toString(upperRight.y - j + 1));
            board.add(axisLabel, 0, j);
            board.getRowConstraints().add(new RowConstraints(50));
            GridPane.setHalignment(axisLabel, HPos.CENTER);
        }
        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) { //bład przeliczania
            for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {

                Vector2d testedPos = new Vector2d(i + bottomLeft.x - 1, upperRight.y - j + 1);

                if (this.map1.isOccupied(testedPos)) {
                    GuiElementBox elem = new GuiElementBox((IMapElement) this.map1.objectAt(testedPos));
                    board.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);

                } else board.add(new Label(" "), i, j);
            }
        }

        board.setGridLinesVisible(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void GridChanger(GridPane grid) {

        grid.getColumnConstraints().add(new ColumnConstraints(50));
        grid.add(new Label("y/x"), 0, 0);


        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            Label axisLabel = new Label(Integer.toString(i + bottomLeft.x - 1));
            grid.getColumnConstraints().add(new ColumnConstraints(50));
            grid.add(axisLabel, i, 0);
            GridPane.setHalignment(axisLabel, HPos.CENTER);

        }

        for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
            Label axisLabel = new Label(Integer.toString(upperRight.y - j + 1));
            grid.add(axisLabel, 0, j);
            grid.getRowConstraints().add(new RowConstraints(50));
            GridPane.setHalignment(axisLabel, HPos.CENTER);
        }
        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
                Vector2d testedPos = new Vector2d(i + bottomLeft.x - 1, upperRight.y - j + 1);
                if (this.map1.isOccupied(testedPos)) {

                    GuiElementBox elem = new GuiElementBox((IMapElement) this.map1.objectAt(testedPos));
                    grid.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);

                } else grid.add(new Label(" "), i, j);
            }
        }

        grid.setGridLinesVisible(true);
    }
}

