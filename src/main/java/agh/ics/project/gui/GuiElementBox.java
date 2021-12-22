package agh.ics.project.gui;

import agh.ics.project.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Map;

public class GuiElementBox {

    Image image;
    ImageView imageView;
    VBox verticalBox;

    public GuiElementBox(IMapElement lifeform, IWorldMap map, Map<String,Image> images, Animal trackedTP, Animal trackedWL){
        image = images.get(lifeform.imageAddress());

        imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);


        verticalBox = new VBox(imageView);
        verticalBox.setAlignment(Pos.CENTER);

        Vector2d[] jungleCorners = map.getJungleCorners();
        Vector2d bottomJungle = jungleCorners[0];
        Vector2d upperJungle = jungleCorners[1];

        if (lifeform.equals(trackedTP) || lifeform.equals(trackedWL)) verticalBox.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));

        else if (lifeform.getPosition().precedes(upperJungle) && lifeform.getPosition().follows(bottomJungle))
        verticalBox.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        else verticalBox.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public GuiElementBox(IWorldMap map, Vector2d position){
        imageView = new ImageView();
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);


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


