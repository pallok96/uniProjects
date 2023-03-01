import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class ImageViewer extends StackPane {
    private int dimension = DataBase.dimSquare;

    ImageViewer(String s) {
        File imgFile = new File(System.getProperty("user.dir") + "/Database/Photos/" + s);
        Image image = new Image(imgFile.toURI().toString());
        ImageView imageView = new ImageView(image);

        if (image.getWidth() == image.getHeight()) {
            imageView.setFitHeight(dimension - 1);
            imageView.setFitWidth(dimension - 1);
        } else if (((dimension - 1) / image.getWidth()) * image.getHeight() <= dimension - 1) {
            imageView.setFitWidth(dimension - 1);
            imageView.setFitHeight(((dimension - 1) / image.getWidth()) * image.getHeight());
        } else {
            imageView.setFitHeight(dimension - 1);
            imageView.setFitWidth(((dimension - 1) / image.getHeight()) * image.getWidth());
        }

        Rectangle sfondo = new Rectangle();
        sfondo.setWidth(dimension);
        sfondo.setHeight(dimension);
        sfondo.setFill(Color.WHITE);
        sfondo.setStroke(Color.BLACK);
        sfondo.setStrokeWidth(0.9);

        this.getChildren().addAll(sfondo, imageView);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && s != null) {
                    ExecutorService executorService;
                    BasicThreadFactory factory = new BasicThreadFactory.Builder()
                            .namingPattern("MyImageViewer")
                            .build();
                    executorService = Executors.newSingleThreadExecutor(factory);

                    if (Desktop.isDesktopSupported()) {
                        executorService.execute(() -> {
                            try {
                                Desktop.getDesktop().open(imgFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        });
    }
}

