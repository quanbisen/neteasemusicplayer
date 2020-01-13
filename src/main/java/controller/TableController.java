package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Model;
import org.springframework.stereotype.Controller;

@Controller
public class TableController {
    public TableView tableView;
    public TableColumn<Model,Button> columnButton;
    public TableColumn<Model,String> columnString;
    public void initialize(){
        columnButton.setCellValueFactory(new PropertyValueFactory<>("button"));
        columnString.setCellValueFactory(new PropertyValueFactory<>("str"));
        ObservableList<Model> observableList = FXCollections.observableArrayList();
        observableList.add(new Model(new Button("test"),"hahaha"));
        tableView.setItems(observableList);
    }
}
