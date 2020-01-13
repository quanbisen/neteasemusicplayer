package model;

import javafx.scene.control.Button;

public class Model {
    private Button button;
    private String str;

    public Model(Button button, String str) {
        this.button = button;
        this.str = str;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
