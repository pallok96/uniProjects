import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class Attention extends BorderPane {
    static double scalar_factor = DataBase.scalar_factor;

    Attention(Stage stage, Button yes, Button no, StackPane stackPane, MyTree tree, TextField textField) {
        Text t1 = new Text("ATTENZIONE");
        t1.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 70)));
        t1.setFill(Color.RED);
        t1.setTextAlignment(TextAlignment.CENTER);

        Text t2 = new Text("SEI SICURO DI VOLER SVUOTARE L'INTERO\nDATABASE E PERDERE TUTTE LE FOTO?\n(QUESTA OPERAZIONE É IRREVERSIBILE)");
        t2.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 27)));
        t2.setTextAlignment(TextAlignment.CENTER);
        t2.setFill(Color.WHITE);

        VBox vb_text = new VBox((int) (scalar_factor * 35));
        vb_text.getChildren().addAll(t1, t2);
        vb_text.setAlignment(Pos.CENTER);

        yes.setText("SI");
        yes.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 30)));
        yes.setPrefHeight((int) (scalar_factor * 60));
        yes.setPrefWidth((int) (scalar_factor * 110));

        no.setText("NO");
        no.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 30)));
        no.setPrefHeight((int) (scalar_factor * 60));
        no.setPrefWidth((int) (scalar_factor * 110));
        no.setDefaultButton(true);

        HBox buttonBox = new HBox((int) (scalar_factor * 200));
        buttonBox.getChildren().addAll(yes, no);
        buttonBox.setAlignment(Pos.CENTER);

        this.setTop(vb_text);
        BorderPane.setMargin(vb_text, new Insets((int) (scalar_factor * 25), 0, 0, 0));
        this.setCenter(buttonBox);
        BorderPane.setMargin(buttonBox, new Insets(0, 0, 0, 0));
	this.setStyle("-fx-background-color: #4d4d4d;");

        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    tree.updateTree();	     // questa operazione serve per mettere nel database tutte le nuove immagini non ancora caricate, in modo tale da svuotare i nuovi messaggi, prima di cancellare tutto
                } catch (IOException ex) {   // nel caso si volesse cancellare SOLO il database già esistente e mantenere le foto arrivate nel frattempo, basta togliere tale riga di codice
                    Logger.getLogger(ClearBtn.class.getName()).log(Level.SEVERE, null, ex);
                }

                tree.clear();

                try {
                    tree.updateData();
                } catch (IOException ex) {
                    Logger.getLogger(ClearBtn.class.getName()).log(Level.SEVERE, null, ex);
                }

                File directory = new File(System.getProperty("user.dir") + "/Database/Photos/");

                for (File f : directory.listFiles()) {
                    f.delete();
                }

                Rectangle r = new Rectangle(DataBase.W_DIM, DataBase.H_DIM);
                r.setFill(Color.DIMGRAY);
                r.setOpacity(0.6);
                r.setStroke(Color.WHITE);

                stackPane.getChildren().clear();
                stackPane.getChildren().add(r);

                textField.clear();
                stage.close();
            }
        });

        no.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
    }
}

