package agh.ics.project.gui;

import agh.ics.project.IWorldMap;
import agh.ics.project.SimulationEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataFileService {
    protected List<String[]> csvDataTP = new ArrayList<>();
    protected int sumAliveAnimalsTP = 0;
    protected int sumPresentGrassTP = 0;
    protected int sumAverageEnergyTP = 0;
    protected int sumAverageChildrenTP = 0;
    protected int sumAverageLifeLengthTP = 0;

    protected List<String[]> csvDataWL = new ArrayList<>();
    protected int sumAliveAnimalsWL = 0;
    protected int sumPresentGrassWL = 0;
    protected int sumAverageEnergyWL = 0;
    protected int sumAverageChildrenWL = 0;
    protected int sumAverageLifeLengthWL = 0;

    public DataFileService(){
        String[] header = {"day","living animals","present grass", "average energy","average children","average life lenght"};
        csvDataTP.add(header);
        csvDataWL.add(header);
    }

    public void addData(SimulationEngine engine, IWorldMap map, boolean whichMap){
        int dayNumber = engine.getDay();
        int animalsToday = map.countAnimals();
        int grassToday = map.countGrass();
        int averageEnergyToday = map.getAverageEnergy();
        int averageChildrenToday = map.getAverageChildren();
        int averageLifeLengthToday = engine.getAverageLifeLength();
        String[] newRecord = {Integer.toString(dayNumber),Integer.toString(animalsToday),Integer.toString(grassToday), Integer.toString(averageEnergyToday), Integer.toString(averageChildrenToday), Integer.toString(averageLifeLengthToday)};
        if (whichMap){
            csvDataTP.add(newRecord);
            sumAliveAnimalsTP += animalsToday;
            sumPresentGrassTP += grassToday;
            sumAverageEnergyTP += averageEnergyToday;
            sumAverageChildrenTP += averageChildrenToday;
            sumAverageLifeLengthTP += averageLifeLengthToday;
        } else {
            csvDataWL.add(newRecord);
            sumAliveAnimalsWL += animalsToday;
            sumPresentGrassWL += grassToday;
            sumAverageEnergyWL += averageEnergyToday;
            sumAverageChildrenWL += averageChildrenToday;
            sumAverageLifeLengthWL += averageLifeLengthToday;
        }
    }

    public void saveDataToFile(SimulationEngine engine, boolean whichMap) throws IOException {
        int dayNumber = engine.getDay();
        if (whichMap){
            int animalsTotal = sumAliveAnimalsTP/dayNumber;
            int grassTotal = sumPresentGrassTP/dayNumber;
            int averageEnergyTotal = sumAverageEnergyTP/dayNumber;
            int averageChildrenTotal = sumAverageChildrenTP/dayNumber;
            int averageLifeLengthTotal = sumAverageLifeLengthTP/dayNumber;

            String[] newRecord = {"Summary",Integer.toString(animalsTotal),Integer.toString(grassTotal), Integer.toString(averageEnergyTotal), Integer.toString(averageChildrenTotal), Integer.toString(averageLifeLengthTotal)};
            csvDataTP.add(newRecord);

            File statisticsTP = new File("statisticsTP.csv");
            try (PrintWriter pwTP = new PrintWriter(statisticsTP)){
                csvDataTP.stream()
                        .map(this::convertToCSV)
                        .forEach(pwTP::println);
            } catch (IOException e){
                System.out.println("IO STREAM EXCEPTION");
            }

        } else {
            int animalsTotal = sumAliveAnimalsWL/dayNumber;
            int grassTotal = sumPresentGrassWL/dayNumber;
            int averageEnergyTotal = sumAverageEnergyWL/dayNumber;
            int averageChildrenTotal = sumAverageChildrenWL/dayNumber;
            int averageLifeLengthTotal = sumAverageLifeLengthWL/dayNumber;

            String[] newRecord = {"Summary",Integer.toString(animalsTotal),Integer.toString(grassTotal), Integer.toString(averageEnergyTotal), Integer.toString(averageChildrenTotal), Integer.toString(averageLifeLengthTotal)};
            csvDataWL.add(newRecord);

            File statisticsWL = new File("statisticsWL.csv");
            try (PrintWriter pwWL = new PrintWriter(statisticsWL)){
                csvDataWL.stream()
                        .map(this::convertToCSV)
                        .forEach(pwWL::println);
            } catch (IOException e){
                System.out.println("IO STREAM EXCEPTION");
            }
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
