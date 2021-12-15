package agh.ics.project.gui;


import agh.ics.project.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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
        Label label;

        if (lifeform instanceof Grass) {
            label = new Label("Algi");
        } else label = new Label("Z" + lifeform.getPosition().toString());

        verticalBox = new VBox(imageView, label);
        verticalBox.setAlignment(Pos.CENTER);

    }

}


