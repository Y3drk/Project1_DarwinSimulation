package agh.ics.project.gui;

import agh.ics.project.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    protected Map<String, Image> images = new HashMap<>();

    protected boolean isMagicalForTeleported;
    protected boolean isMagicalForWalled;

    protected int startEnergy;
    protected int moveEnergyCost;
    protected int eatingGrassEnergyProfit;
    protected double jungleToSteppeRatio;
    protected boolean teleportEnabled;

    protected LineChart<Number, Number> teleportMapChart;
    protected XYChart.Series<Number,Number> aliveAnimalsTP = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> aliveGrassTP = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> averageEnergyTP = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> averageChildrenAmountTP = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> averageDaysLivedTP = new XYChart.Series<>();

    protected LineChart<Number, Number> walledMapChart;
    protected XYChart.Series<Number,Number> aliveAnimalsWL = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> aliveGrassWL = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> averageEnergyWL = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> averageChildrenAmountWL = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> averageDaysLivedWL = new XYChart.Series<>();

    protected Label displayedGenomeTP;
    protected HBox dominantGenomeTP;

    protected Label displayedGenomeWL;
    protected HBox dominantGenomeWL;

    protected Genome currentDominantTP;
    protected Genome currentDominantWL;

    protected boolean genomeHighlightedTP = false;
    protected boolean genomeHighlightedWL = false;

    protected Alert magicTP;
    protected Alert magicWL;

    //---tracking & toggle buttons
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
    //-------------

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
//            System.out.println("MAP AT DAY 0");
//            System.out.println(map);
//            System.out.println("--------------");

            upperRight = map.getCorners()[1];
            bottomLeft = map.getCorners()[0];

            this.engine = new SimulationEngine(map,isMagicalForTeleported);
            this.walledEngine = new SimulationEngine(walledMap,isMagicalForWalled);

            initializeSimulationScene();
            primaryStage.setScene(simulationScene);
            this.engineThread = new Thread(engine);
            this.walledEngineThread = new Thread(walledEngine);

            engineThread.start();
            walledEngineThread.start();

            simulation(this.engine, this.board, this.teleportMap);
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

        initializeDominant();

        initializeCharts();

        initializeTrackedStats();

        HBox chartsTP = new HBox(5,trackedAnimalInfoTP,teleportMapChart);
        chartsTP.setAlignment(Pos.CENTER);
        chartsTP.setPadding(new Insets(5, 5, 5, 5));

        HBox chartsWL = new HBox(5, trackedAnimalInfoWL, walledMapChart);
        chartsWL.setAlignment(Pos.CENTER);
        chartsWL.setPadding(new Insets(5, 5, 5, 5));

        VBox tpMapUI = new VBox(10, teleportMapLabel, scrollForTPBoard, buttonsTP, dominantGenomeTP, chartsTP);
        tpMapUI.setAlignment(Pos.CENTER);

        VBox wlMapUI = new VBox(10,walledMapLabel,scrollForWLBoard,buttonsWL,dominantGenomeWL, chartsWL);
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

                    resetGrid(board);
                    changeGrid(board,engine, teleportMap);
                    setToggles(board,teleportMap, true, engine);
            }
        });


        highlightDominateGenomTP.setOnAction(event -> {
            if (ifTeleportMapStopped) {
                if (!genomeHighlightedTP) {
                    resetGrid(board);
                    highlightDominant(board, teleportMap, true);
                    genomeHighlightedTP = true;
                }
                else {
                    resetGrid(board);
                    changeGrid(board,engine,teleportMap);
                    setToggles(board,teleportMap,true, engine);
                    genomeHighlightedTP = false;
                }
            }
        });

        startStopButtonWL.setOnAction(event -> {
            if(ifWalledMapStopped){
                this.walledEngineThread = new Thread(walledEngine);
                walledEngineThread.start();
                simulation(this.walledEngine, this.walledBoard, this.walledMap);
                this.ifWalledMapStopped= false;
            } else {
                synchronized (this) {
                    this.walledEngineThread.stop();
                    this.ifWalledMapStopped = true;

                    resetGrid(walledBoard);
                    changeGrid(walledBoard,walledEngine, walledMap);
                    setToggles(walledBoard,walledMap, false, walledEngine);
                }
            }
        });

        highlightDominateGenomWL.setOnAction(event -> {
            if (ifWalledMapStopped) {
                if (!genomeHighlightedWL) {
                    resetGrid(walledBoard);
                    highlightDominant(walledBoard, walledMap, false);
                    genomeHighlightedWL = true;
                } else {
                    resetGrid(walledBoard);
                    changeGrid(walledBoard,walledEngine,walledMap);

                    resetGrid(walledBoard);
                    changeGrid(walledBoard,walledEngine, walledMap);
                    setToggles(walledBoard,walledMap, false, walledEngine);

                    genomeHighlightedWL = false;
                }
            }
        });

        changeGrid(this.board, engine, this.teleportMap);
        changeGrid(this.walledBoard, walledEngine, this.walledMap);
    }

    public void initializeCharts(){
        NumberAxis xAxisTP = new NumberAxis();
        xAxisTP.setLabel("Day");

        NumberAxis yAxisTP = new NumberAxis();
        yAxisTP.setLabel("Statistic");

        NumberAxis xAxisWL = new NumberAxis();
        xAxisWL.setLabel("Day");

        NumberAxis yAxisWL = new NumberAxis();
        yAxisWL.setLabel("Statistic");

        this.teleportMapChart = new LineChart<>(xAxisTP,yAxisTP);
        teleportMapChart.setCreateSymbols(false);

        this.aliveAnimalsTP.getData().add(new XYChart.Data<>(0,startingAnimals));
        this.aliveAnimalsTP.setName("Alive animals");

        this.aliveGrassTP.getData().add(new XYChart.Data<>(0,0));
        this.aliveGrassTP.setName("Present Grass");

        this.averageEnergyTP.getData().add(new XYChart.Data<>(0,startEnergy));
        this.averageEnergyTP.setName("Average Energy");

        this.averageChildrenAmountTP.getData().add(new XYChart.Data<>(0,0));
        this.averageChildrenAmountTP.setName("Average Children Amount");

        this.averageDaysLivedTP.getData().add(new XYChart.Data<>(0,0));
        this.averageDaysLivedTP.setName("Average Life Length");


        this.walledMapChart = new LineChart<>(xAxisWL,yAxisWL);
        walledMapChart.setCreateSymbols(false);

        this.aliveAnimalsWL.getData().add(new XYChart.Data<>(0,startingAnimals));
        this.aliveAnimalsWL.setName("Alive animals");

        this.aliveGrassWL.getData().add(new XYChart.Data<>(0,0));
        this.aliveGrassWL.setName("Present Grass");

        this.averageEnergyWL.getData().add(new XYChart.Data<>(0,startEnergy));
        this.averageEnergyWL.setName("Average Energy");

        this.averageChildrenAmountWL.getData().add(new XYChart.Data<>(0,0));
        this.averageChildrenAmountWL.setName("Average Children Amount");

        this.averageDaysLivedWL.getData().add(new XYChart.Data<>(0,0));
        this.averageDaysLivedWL.setName("Average Life Length");


        teleportMapChart.getData().add(aliveAnimalsTP);
        teleportMapChart.getData().add(aliveGrassTP);
        teleportMapChart.getData().add(averageEnergyTP);
        teleportMapChart.getData().add(averageChildrenAmountTP);
        teleportMapChart.getData().add(averageDaysLivedTP);

        walledMapChart.getData().add(aliveAnimalsWL);
        walledMapChart.getData().add(aliveGrassWL);
        walledMapChart.getData().add(averageEnergyWL);
        walledMapChart.getData().add(averageChildrenAmountWL);
        walledMapChart.getData().add(averageDaysLivedWL);
    }

    public void initializeTrackedStats(){
        Label trackedChildrenLabelTP = new Label("Children: " + this.currentChildrenTP);
        this.currentGenomeTP = new Label("Genome: No animal chosen yet");
        Label trackedDescendantsTP = new Label("Descendants: " + this.currentDescendantsTP);
        Label ifDeadTP = new Label("No animal chosen");
        this.trackedAnimalInfoTP = new VBox(5,  currentGenomeTP, trackedChildrenLabelTP, trackedDescendantsTP, ifDeadTP);
        trackedAnimalInfoTP.setAlignment(Pos.CENTER_LEFT);

        Label trackedChildrenLabelWL = new Label("Children: " + this.currentChildrenWL);
        this.currentGenomeWL = new Label("Genome: No animal chosen yet");
        Label trackedDescendantsWL = new Label("Descendants: " + this.currentDescendantsWL);
        Label ifDeadWL = new Label("No animal chosen");
        this.trackedAnimalInfoWL = new VBox(5,  currentGenomeWL, trackedChildrenLabelWL, trackedDescendantsWL, ifDeadWL);
        trackedAnimalInfoWL.setAlignment(Pos.CENTER_LEFT);
    }

    public void updateChart(SimulationEngine engine, IWorldMap map){
        if (map.getTeleportValue()) {
            this.aliveAnimalsTP.getData().add(new XYChart.Data<>(engine.getDay(),map.countAnimals()));
            this.aliveGrassTP.getData().add(new XYChart.Data<>(engine.getDay(),map.countGrass()));
            this.averageEnergyTP.getData().add(new XYChart.Data<>(engine.getDay(),map.getAverageEnergy()));
            this.averageChildrenAmountTP.getData().add(new XYChart.Data<>(engine.getDay(), map.getAverageChildren()));
            this.averageDaysLivedTP.getData().add(new XYChart.Data<>(engine.getDay(), engine.getAverageLifeLength()));
        } else{
            this.aliveAnimalsWL.getData().add(new XYChart.Data<>(engine.getDay(),map.countAnimals()));
            this.aliveGrassWL.getData().add(new XYChart.Data<>(engine.getDay(),map.countGrass()));
            this.averageEnergyWL.getData().add(new XYChart.Data<>(engine.getDay(),map.getAverageEnergy()));
            this.averageChildrenAmountWL.getData().add(new XYChart.Data<>(engine.getDay(), map.getAverageChildren()));
            this.averageDaysLivedWL.getData().add(new XYChart.Data<>(engine.getDay(), engine.getAverageLifeLength()));
        }
    }

    public void setTrackingInfo(Animal animal, IWorldMap map, SimulationEngine engine) {
        if (map.getTeleportValue()) {
            this.trackedAnimalInfoTP.getChildren().clear();
            this.currentGenomeTP = new Label(animal.getGenome().toString().replaceAll(", ",""));
            currentGenomeTP.setMaxWidth(220.0);
            Label trackedChildrenLabelTP = new Label("Children: " + animal.getTrackedChildren());
            Label trackedDescendantsTP = new Label("Descendants: " + map.getDescendants());
            Label ifDeadTP = new Label();
            if (map.checkBeingAlive(animal)){
                ifDeadTP.setText("The animal is alive");
            } else {
                ifDeadTP.setText(("The animal died on day:" + engine.getDay()));
                trackedTP = null;
            }
            this.trackedAnimalInfoTP.getChildren().addAll(currentGenomeTP,trackedChildrenLabelTP,trackedDescendantsTP, ifDeadTP);
            trackedAnimalInfoTP.setAlignment(Pos.CENTER_LEFT);
        } else {
            this.trackedAnimalInfoWL.getChildren().clear();
            this.currentGenomeWL = new Label(animal.getGenome().toString().replaceAll(", ",""));
            currentGenomeWL.setMaxWidth(220.0);
            Label trackedChildrenLabelWL = new Label("Children: " + animal.getTrackedChildren());
            Label trackedDescendantsWL = new Label("Descendants: " + map.getDescendants());
            Label ifDeadWL = new Label();
            if (map.checkBeingAlive(animal)){
                ifDeadWL.setText("The animal is alive");
            } else {
                ifDeadWL.setText(("The animal died on day:" + engine.getDay()));
                trackedWL = null;
            }
            this.trackedAnimalInfoWL.getChildren().addAll(currentGenomeWL,trackedChildrenLabelWL,trackedDescendantsWL, ifDeadWL);
            trackedAnimalInfoWL.setAlignment(Pos.CENTER_LEFT);
        }
    }

    public void initializeDominant(){
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

    public void highlightDominant(GridPane grid, IWorldMap map, boolean which){

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
                        if (lifeform instanceof Animal && ((Animal) lifeform).getGenome().equals(this.currentDominantTP)) {
                            elem.verticalBox.setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    } else {
                        if (lifeform instanceof Animal && ((Animal) lifeform).getGenome().equals(this.currentDominantWL)) {
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

    public void changeGrid(GridPane grid, SimulationEngine engine, IWorldMap map) {

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

    public void simulation(SimulationEngine engine, GridPane grid, IWorldMap map) {
        //diagnostic print
        //System.out.println("THE SIMULATION HAS STARTED");

        Thread thread = new Thread(() -> {
            while(true) {

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    System.out.println("THREAD WAS INTERRUPTED");
                }

                if (engine.getUpdateStatus()) {
                    Platform.runLater(() -> {
                        resetGrid(grid);
                        changeGrid(grid, engine, map);
                        updateChart(engine, map);
                        updateDominant(map);
                        grid.setGridLinesVisible(true);

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

    public void resetGrid(GridPane grid){
        grid.setGridLinesVisible(false);
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
    }
}

