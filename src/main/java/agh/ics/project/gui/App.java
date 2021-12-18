package agh.ics.project.gui;

import agh.ics.project.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;


public class App extends Application {
    protected GridPane board = new GridPane();
    protected GridPane walledBoard = new GridPane();

    protected Scene simulationScene;
    protected Scene parametersScene;

    protected Vector2d upperRight;
    protected Vector2d bottomLeft;

    protected IWorldMap teleportMap;
    protected IWorldMap walledMap;

    protected Thread engineThread;
    protected Thread walledEngineThread;


    protected SimulationEngine engine;
    protected SimulationEngine walledEngine;

    protected boolean ifTeleportMapStopped = false;
    protected boolean ifWalledMapStopped = false;

    protected int startingAnimals;
    protected int width;
    protected int height;

    protected Map<String, Image> images = new HashMap<>(); //TO DO - to not reload every image in every refresh

    //simulation parameters
    protected boolean isMagicalForTeleported;
    protected boolean isMagicalForWalled;

    protected int startEnergy;
    protected int moveEnergyCost;
    protected int eatingGrassEnergyProfit;
    protected double jungleToSteppeRatio;
    protected boolean teleportEnabled;


    public void init() {
        try {
            images.put("src/main/resources/algs.png", new Image(new FileInputStream("src/main/resources/algs.png")));

            images.put("src/main/resources/highEnergyUp.png", new Image(new FileInputStream("src/main/resources/highEnergyUp.png")));
            images.put("src/main/resources/highEnergyRight.png", new Image(new FileInputStream("src/main/resources/highEnergyRight.png")));
            images.put("src/main/resources/highEnergyDown.png", new Image(new FileInputStream("src/main/resources/highEnergyDown.png")));
            images.put("src/main/resources/highEnergyLeft.png", new Image(new FileInputStream("src/main/resources/highEnergyLeft.png")));
            images.put("src/main/resources/highEnergyLeftUp.png", new Image(new FileInputStream("src/main/resources/highEnergyLeftUp.png")));
            images.put("src/main/resources/highEnergyRightUp.png", new Image(new FileInputStream("src/main/resources/highEnergyRightUp.png")));
            images.put("src/main/resources/highEnergyLeftDown.png", new Image(new FileInputStream("src/main/resources/highEnergyLeftDown.png")));
            images.put("src/main/resources/highEnergyRightDown.png", new Image(new FileInputStream("src/main/resources/highEnergyRightDown.png")));

            images.put("src/main/resources/up.png", new Image(new FileInputStream("src/main/resources/up.png")));
            images.put("src/main/resources/right.png", new Image(new FileInputStream("src/main/resources/right.png")));
            images.put("src/main/resources/down.png", new Image(new FileInputStream("src/main/resources/down.png")));
            images.put("src/main/resources/left.png", new Image(new FileInputStream("src/main/resources/left.png")));
            images.put("src/main/resources/leftUp.png", new Image(new FileInputStream("src/main/resources/leftUp.png")));
            images.put("src/main/resources/leftDown.png", new Image(new FileInputStream("src/main/resources/leftDown.png")));
            images.put("src/main/resources/rightUp.png", new Image(new FileInputStream("src/main/resources/rightUp.png")));
            images.put("src/main/resources/rightDown.png", new Image(new FileInputStream("src/main/resources/rightDown.png")));

            images.put("src/main/resources/lowEnergyUp.png", new Image(new FileInputStream("src/main/resources/lowEnergyUp.png")));
            images.put("src/main/resources/lowEnergyRight.png", new Image(new FileInputStream("src/main/resources/lowEnergyRight.png")));
            images.put("src/main/resources/lowEnergyDown.png", new Image(new FileInputStream("src/main/resources/lowEnergyDown.png")));
            images.put("src/main/resources/lowEnergyLeft.png", new Image(new FileInputStream("src/main/resources/lowEnergyLeft.png")));
            images.put("src/main/resources/lowEnergyLeftUp.png", new Image(new FileInputStream("src/main/resources/lowEnergyLeftUp.png")));
            images.put("src/main/resources/lowEnergyLeftDown.png", new Image(new FileInputStream("src/main/resources/lowEnergyLeftDown.png")));
            images.put("src/main/resources/lowEnergyRightUp.png", new Image(new FileInputStream("src/main/resources/lowEnergyRightUp.png")));
            images.put("src/main/resources/lowEnergyRightDown.png", new Image(new FileInputStream("src/main/resources/lowEnergyRightDown.png")));
        } catch (FileNotFoundException ex){
            System.out.println("FILE NOT FOUND");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Button beginningButton = new Button("Begin World Simulation!");

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

        TextField mapJungletoSteppe = new TextField("0.5");
        Label mapJungletoSteppeLabel = new Label("JungletoSteppeRatio");
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

        beginningButton.setOnAction(event -> {
            try {
                this.startingAnimals = Integer.parseInt(mapStartingAnimals.getText());
                this.width = Integer.parseInt(mapWidth.getText());
                this.height = Integer.parseInt(mapHeight.getText());

                //simulation parameters
                this.isMagicalForTeleported = isTeleportMapMagical.isSelected();
                this.isMagicalForWalled = isWallMapMagical.isSelected();

                this.startEnergy = Integer.parseInt(mapStartEnergy.getText());
                this.moveEnergyCost = Integer.parseInt(mapMoveEnergy.getText());
                this.eatingGrassEnergyProfit = Integer.parseInt(mapEatingEnergyProfit.getText());
                this.jungleToSteppeRatio = Double.parseDouble(mapJungletoSteppe.getText());
                this.teleportEnabled = true;
            } catch (IllegalArgumentException ex){
                System.out.println("ILLEGAL ARGUMENTS WERE PASSED!");
            }

            IWorldMap map = new UniversalMap(width,height,jungleToSteppeRatio, teleportEnabled, startEnergy,moveEnergyCost,eatingGrassEnergyProfit,startingAnimals);
            IWorldMap walledMap = new UniversalMap(width,height,jungleToSteppeRatio, !teleportEnabled, startEnergy,moveEnergyCost,eatingGrassEnergyProfit,startingAnimals);

            this.teleportMap = map;
            this.walledMap = walledMap;

            //diagnostic prints
            System.out.println("MAP AT DAY 0");
            System.out.println(map);
            System.out.println("--------------");

            upperRight = map.getCorners()[1];
            bottomLeft = map.getCorners()[0];

            this.engine = new SimulationEngine(map,isMagicalForTeleported);
            this.walledEngine = new SimulationEngine(walledMap,isMagicalForWalled);

            InitializeSimulationScene();
            primaryStage.setScene(simulationScene);
            this.engineThread = new Thread(engine);
            this.walledEngineThread = new Thread(walledEngine);

            engineThread.start();
            walledEngineThread.start();

            simulation(this.engine, this.board, this.teleportMap);
            simulation(this.walledEngine, this.walledBoard, this.walledMap);
        });
    }

    public void InitializeSimulationScene(){
        Button startStopButtonTP = new Button("Start/Stop TP");
        Button startStopButtonWL = new Button("Start/Stop WL");

        //maybe scrollboard will be added later
        ScrollPane scrollForTPBoard = new ScrollPane();
        scrollForTPBoard.setContent(this.board);
        scrollForTPBoard.setPrefViewportHeight(500);
        scrollForTPBoard.setPrefViewportWidth(650);

        ScrollPane scrollForWLBoard = new ScrollPane();
        scrollForWLBoard.setContent(this.walledBoard);
        scrollForWLBoard.setPrefViewportHeight(500);
        scrollForWLBoard.setPrefViewportWidth(650);

        Label teleportMapLabel = new Label("TELEPORT MAP");
        Label walledMapLabel = new Label("WALLED MAP");

        board.setPadding(new Insets(10, 20, 10, 100));
        walledBoard.setPadding(new Insets(10, 20, 10, 100));

        VBox tpMapUI = new VBox(10, teleportMapLabel, scrollForTPBoard, startStopButtonTP);
        tpMapUI.setAlignment(Pos.CENTER);

        VBox wlMapUI = new VBox(10,walledMapLabel,scrollForWLBoard,startStopButtonWL);
        wlMapUI.setAlignment(Pos.CENTER);

        HBox wholeUI = new HBox(40,tpMapUI, wlMapUI);
        wholeUI.setAlignment(Pos.CENTER);

        this.simulationScene = new Scene(wholeUI, 1350, 800);

        startStopButtonTP.setOnAction(event -> {
            if(ifTeleportMapStopped){
                this.engineThread = new Thread(engine);
                engineThread.start();
                simulation(this.engine, this.board, this.teleportMap);
                this.ifTeleportMapStopped = false;
            } else {
                this.engineThread.stop();
                this.ifTeleportMapStopped = true;
            }
        });

        startStopButtonWL.setOnAction(event -> {
            if(ifWalledMapStopped){
                this.walledEngineThread = new Thread(walledEngine);
                walledEngineThread.start();
                simulation(this.walledEngine, this.walledBoard, this.walledMap);
                this.ifWalledMapStopped= false;
            } else {
                this.walledEngineThread.stop();
                this.ifWalledMapStopped = true;
            }
        });

        GridChanger(this.board, engine, this.teleportMap);
        GridChanger(this.walledBoard, walledEngine, this.walledMap);
    }

    public void GridChanger(GridPane grid, SimulationEngine engine, IWorldMap map) {

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

                    GuiElementBox elem = new GuiElementBox((IMapElement) map.objectAt(testedPos), map, images);
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

    public void simulation(SimulationEngine engine, GridPane grid, IWorldMap map) {
        //diagnostic print
        System.out.println("THE SIMULATION HAS STARTED");

        Thread thread = new Thread(() -> {
            while(true) {

                try {
                    Thread.sleep(400);
                } catch (InterruptedException ex) {
                    System.out.println("THREAD WAS INTERRUPTED");
                }

                if (engine.getUpdateStatus()) {
                    //diagnostic print
                    System.out.println("THE GRID CHANGE OCCURS");

                    Platform.runLater(() -> {
                        grid.setGridLinesVisible(false);
                        grid.getColumnConstraints().clear();
                        grid.getRowConstraints().clear();
                        grid.getChildren().clear();
                        GridChanger(grid, engine, map);
                        grid.setGridLinesVisible(true);
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

