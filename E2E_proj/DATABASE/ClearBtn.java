import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;


public class ClearBtn extends Button {
    static double scalar_factor = DataBase.scalar_factor;

    ClearBtn(StackPane stackPane, MyTree tree, TextField textField, Stage primaryStage) {
        setText("CLEAR DATABASE");
        setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 18)));
        setPrefHeight((int) (scalar_factor * 40));
        setPrefWidth((int) (scalar_factor * 220));

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Button yes = new Button();
                Button no = new Button();
                Stage stage = new Stage();
                
                Attention root = new Attention(stage, yes, no, stackPane, tree, textField);
                Scene scene = new Scene(root, (int) (scalar_factor * 760), (int) (scalar_factor * 385));
                scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
                no.requestFocus();

                stage.setTitle("AVVISO");
                stage.setScene(scene);
                stage.sizeToScene();
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(primaryStage);
                stage.setResizable(false);
                stage.show();
            }
        });
    }
}

