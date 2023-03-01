import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class DataBase extends Application {
    static double scalar_factor = 0.9;		// questo è un fattore di scala che serve per ridimensionare la finestra interattiva nel caso lo schermo abbia una risoluzione troppo bassa per contenerla
    static final int W_DIM = (int) (scalar_factor * 999);
    static final int dimSquare = (int) (W_DIM / 3 - 7);
    static final int H_DIM = (int) (dimSquare * 2 + 4);

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException, IOException {
        MyTree myTree = new MyTree();      // creazione dell'albero binario, sulla base del file txt "data.txt"
        myTree.update();

        Rectangle r = new Rectangle(W_DIM, H_DIM);
        r.setFill(Color.DIMGRAY);
        r.setOpacity(0.6);
        r.setStroke(Color.WHITE);
        StackPane rect_box = new StackPane();
        rect_box.getChildren().add(r);
        rect_box.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setId("pane");

        Text t = new Text("DATABASE");
        t.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 70)));
        t.setFill(Color.WHITE);

        HBox hb_text = new HBox();
        hb_text.getChildren().add(t);
        hb_text.setAlignment(Pos.CENTER);

        TextField textField = new TextField();
        textField.setPrefWidth((int) (scalar_factor * 500));
        textField.setPrefHeight((int) (scalar_factor * 40));
        textField.setPromptText("INSERT FILTER TAGS...");
        textField.setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));

        SearchBtn b1 = new SearchBtn(rect_box, myTree, textField);
        b1.requestFocus();
        ClearBtn b2 = new ClearBtn(rect_box, myTree, textField, primaryStage);

        textField.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                b1.search(rect_box, myTree, textField);
            }
        });

        VBox bottomBox = new VBox((int) (scalar_factor * 30));
        bottomBox.getChildren().addAll(rect_box, b2);
        VBox.setMargin(b2, new Insets(0, 0, 0, (int) (scalar_factor * ((1250 - 999) / 2 + 999 - 220))));

        HBox hb_string = new HBox();
        hb_string.setAlignment(Pos.CENTER);
        hb_string.getChildren().addAll(textField, b1);
        hb_string.setSpacing((int) (scalar_factor * 35));

        root.setTop(hb_text);
        BorderPane.setMargin(hb_text, new Insets((int) (scalar_factor * 40), 0, 0, 0));

        root.setCenter(hb_string);
        BorderPane.setMargin(hb_string, new Insets((int) (scalar_factor * (-20)), 0, 0, 0));

        root.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(0, 0, (int) (scalar_factor * 50), 0));

        Scene scene = new Scene(root, (int) (scalar_factor * 1250), (int) (scalar_factor * 990));
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("USER");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest((WindowEvent we) -> {
             try {
                 myTree.update();
             } catch (IOException ex) {
                 Logger.getLogger(MyTree.class.getName()).log(Level.SEVERE, null, ex);
             }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}

