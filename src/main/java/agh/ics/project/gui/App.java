package agh.ics.project.gui;

import agh.ics.project.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class App extends Application {
    //right now it's somewhere between lab7a and lab8, in the next few days I will try to push it further
    protected GridPane board = new GridPane();
    protected Scene scene = new Scene(board, 600, 600);

    protected Vector2d upperRight;
    protected Vector2d bottomLeft;

    protected IWorldMap teleportMap;

    protected Thread engineThread;
    protected SimulationEngine engine;

    protected boolean ifTeleportMapStopped = false;


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

            this.teleportMap = map;

            //diagnostic prints
            System.out.println("MAP AT DAY 0");
            System.out.println(map);
            System.out.println("--------------");

            upperRight = map.getCorners()[1];
            bottomLeft = map.getCorners()[0];

            this.engine = new SimulationEngine(map,isMagical);
            //engine.run();


        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
            System.exit(0);

        }
    }

    @Override
    public void start(Stage primaryStage) {

        //setting up the start/stop button
        Button startStopButton = new Button("Start/Stop");

        //maybe scrollboard will be added later
        //ScrollPane scrollForBoard = new ScrollPane();
        //scrollForBoard.setContent(this.board);

        VBox wholeUI = new VBox(10, this.board, startStopButton);
        wholeUI.setAlignment(Pos.CENTER);
        wholeUI.setPadding(new Insets(10, 20, 10, 20));

        this.scene = new Scene(wholeUI, 700, 700);

        //ISSUES
        startStopButton.setOnAction(event -> {
            if(ifTeleportMapStopped){
                this.engineThread = new Thread(engine);
                engineThread.start();
                simulation();
                this.ifTeleportMapStopped = false;
            } else {
                this.engineThread.stop();
                this.ifTeleportMapStopped = true;
            }
        });


        //------------------------------------------------

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

                if (this.teleportMap.isOccupied(testedPos)) {
                    GuiElementBox elem = new GuiElementBox((IMapElement) this.teleportMap.objectAt(testedPos), this.teleportMap);
                    board.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);

                } else {
                    GuiElementBox elem = new GuiElementBox(this.teleportMap, testedPos);
                    board.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);
                }
            }
        }

        board.setGridLinesVisible(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.engineThread = new Thread(engine);
        engineThread.start();
        simulation();
    }

    public void GridChanger(GridPane grid) {

        this.engine.resetUpdateStatus();

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
                if (this.teleportMap.isOccupied(testedPos)) {

                    GuiElementBox elem = new GuiElementBox((IMapElement) this.teleportMap.objectAt(testedPos), this.teleportMap);
                    grid.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);

                } else {
                    GuiElementBox elem = new GuiElementBox(this.teleportMap, testedPos);
                    grid.add(elem.verticalBox, i, j);
                    GridPane.setHalignment(elem.verticalBox, HPos.CENTER);
                }
            }
        }

        grid.setGridLinesVisible(true);
    }

    public void simulation() {
        //diagnostic print
        System.out.println("THE SIMULATION HAS STARTED");

        Thread thread = new Thread(() -> {
            while(true) {

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }

                if (this.engine.getUpdateStatus()) {
                    //diagnostic print
                    System.out.println("THE GRID CHANGE OCCURS");

                    Platform.runLater(() -> {
                        board.setGridLinesVisible(false);
                        board.getColumnConstraints().clear();
                        board.getRowConstraints().clear();
                        board.getChildren().clear();
                        GridChanger(this.board);
                        board.setGridLinesVisible(true);
                    });

                }
            }
        });
        thread.start();
    }

    @Override
    public void stop(){
        System.out.println("THE VISUALIZATION WINDOW WAS CLOSED");
        System.exit(0); //temporary adaptation
    }
}

