import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
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


public class Sender extends Application {
    static double scalar_factor = 0.9;		// questo è un fattore di scala che serve per ridimensionare la finestra interattiva nel caso lo schermo abbia una risoluzione troppo bassa per contenerla
    static byte[] imageArray;
    static SubmitBtn b1;
    LoadBtn b2;
    MyTextField textField;
    TextArea console = new TextArea();

    @Override
    public void start(Stage primaryStage) throws UnknownHostException, IOException {
        Client agent = new Client(InetAddress.getLocalHost().getHostAddress(), 5000);

        BorderPane root = new BorderPane();
        root.setId("pane");

        Text t = new Text("AGENT");
        t.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 70)));
        t.setFill(Color.WHITE);

        HBox hb_text = new HBox();
        hb_text.getChildren().add(t);
        hb_text.setAlignment(Pos.CENTER);

        Rectangle r = new Rectangle((int) (scalar_factor * 700), (int) (scalar_factor * 400));
        r.setFill(Color.DIMGRAY);
        r.setOpacity(0.6);
        r.setStroke(Color.WHITE);

        StackPane stack = new StackPane(r);

	textField = new MyTextField (console, stack, r, agent);
        b1 = new SubmitBtn(textField, console, stack, r, agent);
        b1.setDisable(true);
        b2 = new LoadBtn(primaryStage, textField, stack, r, b1, console);

        VBox Vb_top = new VBox((int) (scalar_factor * 25));
        Vb_top.getChildren().addAll(hb_text, stack);
        Vb_top.setAlignment(Pos.CENTER);

        HBox hb_string = new HBox((int) (scalar_factor * 30));
        hb_string.getChildren().addAll(textField, b1);
        hb_string.setAlignment(Pos.CENTER);

        VBox Vb_center = new VBox((int) (scalar_factor * 50));
        Vb_center.getChildren().addAll(b2, hb_string);
        Vb_center.setAlignment(Pos.CENTER);

        console.setPrefHeight((int) (scalar_factor * 130));
        console.setMaxWidth((int) (scalar_factor * 1030));
        console.setText("CONSOLE...");
        console.setStyle("-fx-text-fill: slategray");
        console.setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));
        console.setEditable(false);

        root.setTop(Vb_top);
        BorderPane.setMargin(Vb_top, new Insets((int) (scalar_factor * 40), 0, 0, 0));

        root.setCenter(Vb_center);
        BorderPane.setMargin(Vb_center, new Insets((int) (scalar_factor * (-100)), 0, (int) (scalar_factor * (-100)), 0));

        root.setBottom(console);
        BorderPane.setMargin(console, new Insets(0, 0, (int) (scalar_factor * 50), 0));
        BorderPane.setAlignment(console, Pos.CENTER);

        Scene scene = new Scene(root, (int) (scalar_factor * 1300), (int) (scalar_factor * 900));
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("SENDER");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                try {
                    agent.stopMessages();           // avviso che smetterò di mandare messaggi 
                } catch (IOException ex) {
                    Logger.getLogger(SubmitBtn.class.getName()).log(Level.SEVERE, null, ex);
                }
		
		System.out.println("Disconnected.");
                agent.close();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}

