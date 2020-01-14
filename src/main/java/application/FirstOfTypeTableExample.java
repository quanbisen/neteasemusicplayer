package application;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class FirstOfTypeTableExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Product> table = new TableView<>() ;

        ObservableList<Product> products = FXCollections.observableArrayList();
        table.setItems(new SortedList<>(products, Comparator.comparing(Product::getType)));

        TableColumn<Product, Product.Type> typeColumn = column("Type", Product::typeProperty);
        typeColumn.setCellFactory(c -> {
            TableCell<Product, Product.Type> cell = new TableCell<Product, Product.Type>() {
                @Override
                public void updateItem(Product.Type type, boolean empty) {
                    super.updateItem(type, empty);
                    if (type == null) {
                        setText(null);
                    } else {
                        setText(type.toString());
                    }
                }
            };
            cell.getStyleClass().add("type-cell");
            return cell ;
        });

        table.getColumns().add(typeColumn);
        table.getColumns().add(column("Name", Product::nameProperty));
        table.getColumns().add(column("Price", Product::priceProperty));

        PseudoClass firstOfTypePseudoclass = PseudoClass.getPseudoClass("first-of-type");

        table.setRowFactory(t -> {
            TableRow<Product> row = new TableRow<>();
            InvalidationListener listener = obs ->
                    row.pseudoClassStateChanged(firstOfTypePseudoclass,
                            isFirstOfType(table.getItems(), row.getIndex()));
            table.getItems().addListener(listener);
            row.indexProperty().addListener(listener);
            return row ;
        });

        products.addAll(
                new Product("Chips", 1.99, Product.Type.FOOD),
                new Product("Ice Cream", 3.99, Product.Type.FOOD),
                new Product("Ice Cream", 3.99, Product.Type.FOOD),
                new Product("Ice Cream", 3.99, Product.Type.FOOD),
                new Product("Beer", 8.99, Product.Type.DRINK),
                new Product("Laptop", 1099.99, Product.Type.OTHER));

        GridPane editor = createEditor(products);

        BorderPane root = new BorderPane(table, null, null, editor, null) ;
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("/css/first-of-type-table.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean isFirstOfType(List<Product> products, int index) {
        if (index < 0 || index >= products.size()) {
            return false ;
        }
        if (index == 0) {
            return true ;
        }
        if (products.get(index).getType().equals(products.get(index-1).getType())) {
            return false ;
        } else {
            return true ;
        }
    }

    private GridPane createEditor(ObservableList<Product> products) {
        ComboBox<Product.Type> typeSelector = new ComboBox<>(FXCollections.observableArrayList(Product.Type.values()));
        TextField nameField = new TextField();
        TextField priceField = new TextField();
        Button add = new Button("Add");
        add.setOnAction(e -> {
            Product product = new Product(nameField.getText(),
                    Double.parseDouble(priceField.getText()), typeSelector.getValue());
            products.add(product);
            nameField.setText("");
            priceField.setText("");
        });

        GridPane editor = new GridPane();
        editor.addRow(0, new Label("Type:"), typeSelector);
        editor.addRow(1, new Label("Name:"), nameField);
        editor.addRow(2, new Label("Price:"), priceField);
        editor.add(add, 3, 0, 2, 1);

        GridPane.setHalignment(add, HPos.CENTER);
        ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setHalignment(HPos.RIGHT);
        leftCol.setHgrow(Priority.NEVER);

        editor.getColumnConstraints().addAll(leftCol, new ColumnConstraints());
        editor.setHgap(10);
        editor.setVgap(5);
        return editor;
    }


    private <S,T> TableColumn<S,T> column(String title, Function<S, ObservableValue<T>> property) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
        return col ;
    }


    public  static class Product {


        public enum Type {FOOD, DRINK, OTHER }

        private final ObjectProperty<Type> type = new SimpleObjectProperty<>();
        private final StringProperty name = new SimpleStringProperty();
        private final DoubleProperty price = new SimpleDoubleProperty();

        public Product(String name, double price, Type type) {
            setName(name);
            setPrice(price);
            setType(type);
        }

        public final StringProperty nameProperty() {
            return this.name;
        }
        public final java.lang.String getName() {
            return this.nameProperty().get();
        }
        public final void setName(final java.lang.String name) {
            this.nameProperty().set(name);
        }
        public final DoubleProperty priceProperty() {
            return this.price;
        }
        public final double getPrice() {
            return this.priceProperty().get();
        }
        public final void setPrice(final double price) {
            this.priceProperty().set(price);
        }
        public final ObjectProperty<Type> typeProperty() {
            return this.type;
        }
        public final FirstOfTypeTableExample.Product.Type getType() {
            return this.typeProperty().get();
        }
        public final void setType(final FirstOfTypeTableExample.Product.Type type) {
            this.typeProperty().set(type);
        }


    }


    public static void main(String[] args) {
        launch(args);
    }
}
