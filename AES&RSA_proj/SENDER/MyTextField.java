import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.KeyCode;


public class MyTextField extends TextField {
    static double scalar_factor = Sender.scalar_factor;

    MyTextField(TextArea console, StackPane stack, Rectangle r, Client agent) {
        setPrefWidth((int) (scalar_factor * 880));
        setPrefHeight((int) (scalar_factor * 40));
        setPromptText("TAGS...");
        setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));

        setOnMouseClicked(e -> {
            if (Sender.b1.isDisabled()) {
                console.clear();
                console.setText("CONSOLE...");
            }

            console.setStyle("-fx-text-fill: slategray");
            console.setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));
        });

        setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER && !Sender.b1.isDisable()) {
                try {
                    Sender.b1.sendPhoto(this, console, stack, r, agent);
                } catch (IOException ex) {
                    Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}

