package agh.ics.project.gui;

import agh.ics.project.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;


public class App extends Application {
    protected GridPane board = new GridPane();
    protected Scene simulationScene;
    protected Scene parametersScene;

    protected Vector2d upperRight;
    protected Vector2d bottomLeft;

    protected IWorldMap teleportMap;

    protected Thread engineThread;
    protected SimulationEngine engine;

    protected boolean ifTeleportMapStopped = false;

    protected int startingAnimals;
    protected int width;
    protected int height;

    protected Map<String, Image> images = new HashMap<>(); //TO DO - to not reload every image in every refresh

    //simulation parameters
    protected boolean isMagicalForTeleported;
    protected int startEnergy;
    protected int moveEnergyCost;
    protected int eatingGrassEnergyProfit;
    protected double jungleToSteppeRatio;
    protected boolean teleportEnabled;


    public void init() {
    }

    @Override
    public void start(Stage primaryStage) {
        Button beginningButton = new Button("Begin World Simulation!");

        beginningButton.setOnAction(event -> {
            primaryStage.setScene(simulationScene);
            this.engineThread = new Thread(engine);
            engineThread.start();
            simulation();
        });

        TextField mapWidth = new TextField("10");
        Label mapWidthLabel = new Label("Width");
        HBox widthBox = new HBox(5,mapWidthLabel,mapWidth);
        widthBox.setAlignment(Pos.CENTER);

        TextField mapHeight = new TextField("10");
        Label mapHeightLabel = new Label("Height");
        HBox heightBox = new HBox(5,mapHeightLabel,mapHeight);
        heightBox.setAlignment(Pos.CENTER);

        TextField mapStartEnergy = new TextField("100");
        Label mapStartEnergyLabel = new Label("StartEnergy");
        HBox startEnergyBox = new HBox(5,mapStartEnergyLabel,mapStartEnergy);
        startEnergyBox.setAlignment(Pos.CENTER);

        TextField mapMoveEnergy = new TextField("15");
        Label mapMoveEnergyLabel = new Label("MoveEnergy");
        HBox moveEnergyBox = new HBox(5,mapMoveEnergyLabel,mapMoveEnergy);
        moveEnergyBox.setAlignment(Pos.CENTER);

        TextField mapEatingEnergyProfit = new TextField("30");
        Label mapEatingEnergyProfitLabel = new Label("EatingEnergyProfit");
        HBox eatingEnergyProfitBox = new HBox(5,mapEatingEnergyProfitLabel,mapEatingEnergyProfit);
        eatingEnergyProfitBox.setAlignment(Pos.CENTER);

        TextField mapJungletoSteppe = new TextField("100");
        Label mapJungletoSteppeLabel = new Label("StartEnergy");
        HBox jungletoSteppeBox = new HBox(5,mapJungletoSteppeLabel,mapJungletoSteppe);
        jungletoSteppeBox.setAlignment(Pos.CENTER);

        TextField mapStartingAnimals = new TextField("10");
        Label mapStartingAnimalsLabel = new Label("StartingAnimals");
        HBox startingAnimalsBox = new HBox(5,mapStartingAnimalsLabel,mapStartingAnimals);
        startingAnimalsBox.setAlignment(Pos.CENTER);

        CheckBox isTeleportMapMagical = new CheckBox("IsTeleportMapMagical");
        HBox isTPMapMagicalBox = new HBox(5,isTeleportMapMagical);
        isTPMapMagicalBox.setAlignment(Pos.CENTER);

        CheckBox isWallMapMagical = new CheckBox("IsWallMapMagical");
        HBox isWallMapMagicalBox = new HBox(5,isWallMapMagical);
        isWallMapMagicalBox.setAlignment(Pos.CENTER);

        VBox choosingPoint = new VBox(10, widthBox, heightBox, startEnergyBox, moveEnergyBox,eatingEnergyProfitBox, jungletoSteppeBox, startingAnimalsBox, isTPMapMagicalBox, isWallMapMagicalBox, beginningButton);
        choosingPoint.setAlignment(Pos.CENTER);

        parametersScene = new Scene(choosingPoint, 700, 700);
        primaryStage.setScene(parametersScene);
        primaryStage.show();

        //to add -> service of all the inputs!!!!!



        //---------------------------------------------------
        try {
            //for now we will write all starting conditions here to check if the backend works
            //map parameters
            this.startingAnimals = 10;
            this.width = 10;
            this.height = 10;

            //simulation parameters
            this.isMagicalForTeleported = false;
            this.startEnergy = 100;
            this.moveEnergyCost = 1;
            this.eatingGrassEnergyProfit = 20;
            this.jungleToSteppeRatio = 0.4;
            this.teleportEnabled = true;

            IWorldMap map = new UniversalMap(width,height,jungleToSteppeRatio, teleportEnabled, startEnergy,moveEnergyCost,eatingGrassEnergyProfit,startingAnimals);

            this.teleportMap = map;

            //diagnostic prints
            System.out.println("MAP AT DAY 0");
            System.out.println(map);
            System.out.println("--------------");

            upperRight = map.getCorners()[1];
            bottomLeft = map.getCorners()[0];

            this.engine = new SimulationEngine(map,isMagicalForTeleported);
            //engine.run();


        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
            System.exit(0);

        }
        //moving the engine initialization to start so init can have a scene with choices

        Button startStopButton = new Button("Start/Stop");

        //maybe scrollboard will be added later
        //ScrollPane scrollForBoard = new ScrollPane();
        //scrollForBoard.setContent(this.board);

        VBox wholeUI = new VBox(10, this.board, startStopButton);
        wholeUI.setAlignment(Pos.CENTER);
        board.setPadding(new Insets(10, 20, 10, 100));

        this.simulationScene = new Scene(wholeUI, 700, 750);

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

        board.getColumnConstraints().add(new ColumnConstraints(40));
        board.getRowConstraints().add(new RowConstraints(40));
        Label xyLabel = new Label("y/x");
        board.add(xyLabel, 0, 0);
        GridPane.setHalignment(xyLabel, HPos.CENTER);


        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            Label axisLabel = new Label(Integer.toString(i + bottomLeft.x - 1));
            board.getColumnConstraints().add(new ColumnConstraints(40));
            board.add(axisLabel, i, 0);
            GridPane.setHalignment(axisLabel, HPos.CENTER);

        }

        for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
            Label axisLabel = new Label(Integer.toString(upperRight.y - j + 1));
            board.add(axisLabel, 0, j);
            board.getRowConstraints().add(new RowConstraints(40));
            GridPane.setHalignment(axisLabel, HPos.CENTER);
        }
        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
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
    }

    public void GridChanger(GridPane grid) {

        this.engine.resetUpdateStatus();

        grid.getColumnConstraints().add(new ColumnConstraints(40));
        grid.getRowConstraints().add(new RowConstraints(40));

        Label xyLabel = new Label("y/x");
        board.add(xyLabel, 0, 0);
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

