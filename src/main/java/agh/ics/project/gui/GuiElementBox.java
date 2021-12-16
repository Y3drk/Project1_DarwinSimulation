package agh.ics.project.gui;


import agh.ics.project.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GuiElementBox {
    //for now it's in state in which I left it after labs -> we will change a few thing along the way (turtle brightness, labels and so on)

    Image image;
    ImageView imageView;
    VBox verticalBox;

    public GuiElementBox(IMapElement lifeform, IWorldMap map){
        try {
            image = new Image(new FileInputStream(lifeform.imageAddress()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);

        verticalBox = new VBox(imageView);
        verticalBox.setAlignment(Pos.CENTER);

        Vector2d[] jungleCorners = map.getJungleCorners();
        Vector2d bottomJungle = jungleCorners[0];
        Vector2d upperJungle = jungleCorners[1];

        if (lifeform.getPosition().precedes(upperJungle) && lifeform.getPosition().follows(bottomJungle))
        verticalBox.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        else verticalBox.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public GuiElementBox(IWorldMap map, Vector2d position){
        imageView = new ImageView();
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);


        verticalBox = new VBox(imageView);
        verticalBox.setAlignment(Pos.CENTER);

        Vector2d[] jungleCorners = map.getJungleCorners();
        Vector2d bottomJungle = jungleCorners[0];
        Vector2d upperJungle = jungleCorners[1];

        if (position.precedes(upperJungle) && position.follows(bottomJungle))
        verticalBox.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        else verticalBox.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

}


