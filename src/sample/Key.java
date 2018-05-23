package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

class Key {
    private int id;
    private Image unclicked;
    private Image clicked;
    private ImageView keyImage;
    private boolean isPressed;

    Key(int num) throws FileNotFoundException {
        this.id = num;

        String path = "src/resources/";
        String fileName1;
        String fileName2;

        switch (num) {
            case 0:
            case 5:
                fileName1 = "left";
                fileName2 = fileName1 + "_clicked";
                break;
            case 2:
            case 7:
            case 9:
                fileName1 = "middle";
                fileName2 = fileName1 + "_clicked";
                break;
            case 4:
            case 11:
                fileName1 = "right";
                fileName2 = fileName1 + "_clicked";
                break;
            default:
                fileName1 = "black";
                fileName2 = fileName1 + "_clicked";
                break;
        }

        unclicked = new Image(new FileInputStream(path + fileName1 + ".png"));
        clicked = new Image(new FileInputStream(path + fileName2 + ".png"));

        isPressed = false;
    }

    boolean getStatus() {
        return isPressed;
    }

    void changeStatus(boolean status) {
        isPressed = status;
    }

    ImageView getImageView() {
        return keyImage;
    }

    int getId() {
        return id;
    }

    void changeImage() {
        if (isPressed)
            keyImage.setImage(clicked);
        else
            keyImage.setImage(unclicked);
    }

    void setKey(int x, int y) {
        keyImage = new ImageView(unclicked);
        keyImage.setLayoutX(x);
        keyImage.setLayoutY(y);
    }
}
