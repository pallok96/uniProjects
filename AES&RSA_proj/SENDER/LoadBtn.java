import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;


public class LoadBtn extends Button {
    static double scalar_factor = Sender.scalar_factor;
    static final int W_DIM = (int) (scalar_factor * 700);
    static final int H_DIM = (int) (scalar_factor * 400);

    LoadBtn(Stage s, TextField textField, StackPane stack, Rectangle r, SubmitBtn b, TextArea console) {
        setText("LOAD IMAGE");
        setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 18)));
        setPrefHeight((int) (scalar_factor * 40));
        setPrefWidth((int) (scalar_factor * 170));

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                b.setDisable(true);
		console.setText("CONSOLE...");
		console.setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));
		
		textField.clear();
                stack.getChildren().clear();
                stack.getChildren().add(r);

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("CHOOSE IMAGE");

                FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg", "*.gif", "*.JPG");
                fileChooser.getExtensionFilters().add(imageFilter);

                String currentDir = System.getProperty("user.home");
                File defaultDirectory = new File(currentDir);
                fileChooser.setInitialDirectory(defaultDirectory);
                File file = fileChooser.showOpenDialog(s);

                if (file != null) {
                    b.setDisable(false);

                    String fileLocation = file.toURI().toString();
                    Image image = new Image(fileLocation);
                    ImageView imageView = new ImageView(image);

                    if (image.getWidth() == image.getHeight()) {
                        imageView.setFitHeight(H_DIM - 1);
                        imageView.setFitWidth(H_DIM - 1);
                    } else if (((W_DIM - 1) / image.getWidth()) * image.getHeight() <= H_DIM - 1) {
                        imageView.setFitWidth(W_DIM - 1);
                        imageView.setFitHeight(((W_DIM - 1) / image.getWidth()) * image.getHeight());
                    } else {
                        imageView.setFitHeight(H_DIM - 1);
                        imageView.setFitWidth(((H_DIM - 1) / image.getHeight()) * image.getWidth());
                    }

                    stack.getChildren().add(imageView);

                    try {
                        Sender.imageArray = imageToByteArray(file);
                        console.setText("IMMAGINE CARICATA CORRETTAMENTE!\nORA INSERISCI I TAGS RELATIVI, RICORDANDOTI DI SEPARARLI CON UN SOLO SPAZIO E DI NON USARE IL\nCARATTERE RISERVATO '&'.");
                        console.setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));
                    } catch (IOException ex) {
                        Logger.getLogger(LoadBtn.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }


    public byte[] imageToByteArray(File file) throws IOException {
        String strFileExt;
        int intPos = file.getName().lastIndexOf(".");           // trova l'estensione del file

        if (intPos >= 0) {
            strFileExt = file.getName().substring(intPos + 1);
        } else {
            strFileExt = "jpg";       // in caso non avesse estensione, setta "jpg" di default
        }

        BufferedImage bImage = ImageIO.read(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, strFileExt, bos);
        byte[] imgArray = bos.toByteArray();

        return imgArray;
    }
}

