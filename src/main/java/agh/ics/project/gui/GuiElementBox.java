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

    public GuiElementBox(IMapElement lifeform){
        try {
            image = new Image(new FileInputStream(lifeform.imageAddress()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        //Label label; in the project the label is redundant so I will probably delete it later on

//        if (lifeform instanceof Grass) {
//            label = new Label("Algi");
//        } else label = new Label("Z" + lifeform.getPosition().toString());

        verticalBox = new VBox(imageView);
        verticalBox.setAlignment(Pos.CENTER);
        verticalBox.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

    }

    public GuiElementBox(){
        imageView = new ImageView();
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);


        verticalBox = new VBox(imageView);
        verticalBox.setAlignment(Pos.CENTER);
        verticalBox.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

}


