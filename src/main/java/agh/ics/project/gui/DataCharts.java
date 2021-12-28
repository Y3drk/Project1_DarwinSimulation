package agh.ics.project.gui;

import agh.ics.project.IWorldMap;
import agh.ics.project.SimulationEngine;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class DataCharts {
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

    public DataCharts(int startingAnimals, int startEnergy){
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

        try {
            Thread.sleep(23);
        } catch (InterruptedException ex){
            System.out.println("THREAD WAS INTERRUPTED");
        }
    }
}
