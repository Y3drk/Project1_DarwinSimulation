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

import java.io.*;
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

    protected Thread teleportEngineThread;
    protected Thread walledEngineThread;


    protected SimulationEngine teleportEngine;
    protected SimulationEngine walledEngine;

    protected boolean ifTeleportMapStopped = false;
    protected boolean ifWalledMapStopped = false;

    protected int startingAnimals;
    protected int width;
    protected int height;

    protected Map<String, Image> images = new HashMap<>();

    protected boolean isMagicalForTeleported;
    protected boolean isMagicalForWalled;

    protected int startEnergy;
    protected int moveEnergyCost;
    protected int eatingGrassEnergyProfit;
    protected double jungleToSteppeRatio;
    protected boolean teleportEnabled;

    protected DataCharts bothCharts;

    protected DominantController genomeDominants;

    protected Alert magicTP;
    protected Alert magicWL;

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

    protected DataFileService fileData;

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

        fileData = new DataFileService();
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

        TextField mapMoveEnergy = new TextField("1");
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

            upperRight = map.getCorners()[1];
            bottomLeft = map.getCorners()[0];

            this.teleportEngine = new SimulationEngine(map,isMagicalForTeleported);
            this.walledEngine = new SimulationEngine(walledMap,isMagicalForWalled);

            initializeSimulationScene();
            primaryStage.setScene(simulationScene);
            this.teleportEngineThread = new Thread(teleportEngine);
            this.walledEngineThread = new Thread(walledEngine);

            teleportEngineThread.start();
            walledEngineThread.start();

            simulation(this.teleportEngine, this.board, this.teleportMap);
            simulation(this.walledEngine, this.walledBoard, this.walledMap);
        });
    }

    public void initializeSimulationScene(){

        magicTP = new Alert(Alert.AlertType.CONFIRMATION);
        magicWL = new Alert(Alert.AlertType.CONFIRMATION);

        Button startStopButtonTP = new Button("Start/Stop TP");
        Button startStopButtonWL = new Button("Start/Stop WL");

        Button highlightDominateGenomTP = new Button("Highlight Dominating Genom");
        Button highlightDominateGenomWL = new Button("Highlight Dominating Genom");

        Button saveStatsTP = new Button("Save map statistics to file");
        Button saveStatsWL = new Button("Save map statistics to file");

        ScrollPane scrollForTPBoard = new ScrollPane();
        scrollForTPBoard.setContent(this.board);
        scrollForTPBoard.setPrefViewportHeight(500);
        scrollForTPBoard.setPrefViewportWidth(350);

        ScrollPane scrollForWLBoard = new ScrollPane();
        scrollForWLBoard.setContent(this.walledBoard);
        scrollForWLBoard.setPrefViewportHeight(500);
        scrollForWLBoard.setPrefViewportWidth(350);

        Label teleportMapLabel = new Label("TELEPORT MAP");
        Label walledMapLabel = new Label("WALLED MAP");

        HBox buttonsTP = new HBox(10,startStopButtonTP, highlightDominateGenomTP, saveStatsTP);
        buttonsTP.setAlignment(Pos.CENTER);

        HBox buttonsWL = new HBox(10,startStopButtonWL, highlightDominateGenomWL, saveStatsWL);
        buttonsWL.setAlignment(Pos.CENTER);

        board.setPadding(new Insets(10, 10, 10, 10));
        walledBoard.setPadding(new Insets(10, 10, 10, 10));

        genomeDominants = new DominantController();

        bothCharts = new DataCharts(startingAnimals, startEnergy);

        initializeTrackedStats();

        HBox chartsTP = new HBox(5,trackedAnimalInfoTP,bothCharts.teleportMapChart);
        chartsTP.setAlignment(Pos.CENTER);
        chartsTP.setPadding(new Insets(5, 5, 5, 5));

        HBox chartsWL = new HBox(5, trackedAnimalInfoWL, bothCharts.walledMapChart);
        chartsWL.setAlignment(Pos.CENTER);
        chartsWL.setPadding(new Insets(5, 5, 5, 5));

        VBox tpMapUI = new VBox(10, teleportMapLabel, scrollForTPBoard, buttonsTP, genomeDominants.dominantGenomeTP, chartsTP);
        tpMapUI.setAlignment(Pos.CENTER);

        VBox wlMapUI = new VBox(10,walledMapLabel,scrollForWLBoard,buttonsWL,genomeDominants.dominantGenomeWL, chartsWL);
        wlMapUI.setAlignment(Pos.CENTER);

        HBox wholeUI = new HBox(40,tpMapUI, wlMapUI);
        wholeUI.setAlignment(Pos.CENTER);

        this.simulationScene = new Scene(wholeUI, 1350, 800);

        startStopButtonTP.setOnAction(event -> {
            if(ifTeleportMapStopped){
                this.teleportEngineThread = new Thread(teleportEngine);
                teleportEngineThread.start();
                simulation(this.teleportEngine, this.board, this.teleportMap);
                this.ifTeleportMapStopped = false;
                genomeDominants.changeIfHighlighted(true, false);

            } else {
                    this.teleportEngineThread.stop();
                    this.ifTeleportMapStopped = true;

                    GridController.resetGrid(board);
                    GridController.changeGrid(board, teleportEngine,teleportMap,upperRight,bottomLeft,images,trackedTP,trackedWL);

                    setToggles(board,teleportMap, true, teleportEngine);
            }
        });


        highlightDominateGenomTP.setOnAction(event -> {
            if (ifTeleportMapStopped) {
                if (!genomeDominants.getIfHighligted(true)) {
                    GridController.resetGrid(board);

                    GridController.highlightDominant(board,teleportMap,true,upperRight, bottomLeft, images, genomeDominants.currentDominantTP,genomeDominants.currentDominantWL);

                    setToggles(board,teleportMap,true, teleportEngine);
                    genomeDominants.changeIfHighlighted(true, true);
                }
                else {
                    GridController.resetGrid(board);
                    GridController.changeGrid(board, teleportEngine,teleportMap,upperRight,bottomLeft,images,trackedTP,trackedWL);

                    setToggles(board,teleportMap,true, teleportEngine);
                    genomeDominants.changeIfHighlighted(true, false);
                }
            }
        });

        saveStatsTP.setOnAction(event -> {
            if(ifTeleportMapStopped){
                try {
                    fileData.saveDataToFile(teleportEngine,true);

                } catch (IOException e) {
                    System.out.println("IO STREAM EXCEPTION");
                }
            }
        });

        startStopButtonWL.setOnAction(event -> {
            if(ifWalledMapStopped){
                this.walledEngineThread = new Thread(walledEngine);
                walledEngineThread.start();
                simulation(this.walledEngine, this.walledBoard, this.walledMap);
                this.ifWalledMapStopped= false;
                genomeDominants.changeIfHighlighted(false, false);

            } else {
                synchronized (this) {
                    this.walledEngineThread.stop();
                    this.ifWalledMapStopped = true;

                    GridController.resetGrid(walledBoard);
                    GridController.changeGrid(walledBoard,walledEngine,walledMap,upperRight,bottomLeft,images,trackedTP,trackedWL);

                    setToggles(walledBoard,walledMap, false, walledEngine);
                }
            }
        });

        highlightDominateGenomWL.setOnAction(event -> {
            if (ifWalledMapStopped) {
                if (!genomeDominants.getIfHighligted(false)) {
                    GridController.resetGrid(walledBoard);


                    GridController.highlightDominant(walledBoard,walledMap,false,upperRight, bottomLeft, images, genomeDominants.currentDominantTP, genomeDominants.currentDominantWL);

                    setToggles(walledBoard,walledMap, false, walledEngine);
                    genomeDominants.changeIfHighlighted(false, true);

                } else {
                    GridController.resetGrid(walledBoard);
                    GridController.changeGrid(walledBoard,walledEngine,walledMap,upperRight,bottomLeft,images,trackedTP,trackedWL);

                    setToggles(walledBoard,walledMap, false, walledEngine);

                    genomeDominants.changeIfHighlighted(false, false);
                }
            }
        });

        saveStatsWL.setOnAction(event -> {
            if(ifWalledMapStopped){
                try {
                    fileData.saveDataToFile(walledEngine,false);
                }
                catch (IOException e) {
                    System.out.println("IO STREAM EXCEPTION");
                }
            }
        });
        GridController.changeGrid(board, teleportEngine,teleportMap,upperRight,bottomLeft,images,trackedTP,trackedWL);
        GridController.changeGrid(walledBoard,walledEngine,walledMap,upperRight,bottomLeft,images,trackedTP,trackedWL);
    }

    public void initializeTrackedStats(){
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

    public void setToggles(GridPane grid, IWorldMap map, boolean which, SimulationEngine engine) {

        for (int i = 1; i <= upperRight.x - bottomLeft.x + 1; i++) {
            for (int j = 1; j <= upperRight.y - bottomLeft.y + 1; j++) {
                Vector2d testedPos = new Vector2d(i + bottomLeft.x - 1, upperRight.y - j + 1);
                if (map.objectAt(testedPos) instanceof Animal trackedAnimal) {
                    if (which) {
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

    public void simulation(SimulationEngine engine, GridPane grid, IWorldMap map) {

        Thread thread = new Thread(() -> {
            while(true) {

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    System.out.println("THREAD WAS INTERRUPTED");
                }

                if (engine.getUpdateStatus()) {
                    Platform.runLater(() -> {
                        GridController.resetGrid(grid);
                        GridController.changeGrid(grid,engine,map,upperRight,bottomLeft,images,trackedTP,trackedWL);

                        bothCharts.updateChart(engine,map);

                        //updateDominant(map);
                        genomeDominants.updateDominant(map);

                        grid.setGridLinesVisible(true);

                        fileData.addData(engine, map, map.getTeleportValue());

                        if (map.getTeleportValue() && trackedTP != null){
                            setTrackingInfo(trackedTP,map,engine);
                        }
                        else if (!map.getTeleportValue() && trackedWL != null){
                            setTrackingInfo(trackedWL,map,engine);
                        }

                        if(engine.getMagicStatus() && engine.getMiracleStatus()){
                            if (map.getTeleportValue()) {
                                magicTP.setContentText("Magic happened on teleport map. " + engine.getMagicMiraclesLeft() + " miracles left!");
                                magicTP.show();
                            } else {
                                magicWL.setContentText("Magic happened on walled map. " + engine.getMagicMiraclesLeft() + " miracles left!");
                                magicWL.show();
                            }
                            engine.resetMiracleStatus();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void stop(){
        System.out.println("THE VISUALIZATION WINDOW WAS CLOSED");
        System.exit(0);
    }
}