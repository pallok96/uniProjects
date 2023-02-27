import java.io.IOException;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class SearchBtn extends Button {
    static double scalar_factor = DataBase.scalar_factor;

    SearchBtn(StackPane stackPane, MyTree tree, TextField textField) {
        setText("SEARCH");
        setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 18)));
        setPrefHeight((int) (scalar_factor * 40));
        setPrefWidth((int) (scalar_factor * 120));

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                search(stackPane, tree, textField);
            }
        });
    }


    void search(StackPane stackPane, MyTree tree, TextField textField) {
        try {
            tree.updateTree();
        } catch (IOException ex) {
            Logger.getLogger(SearchBtn.class.getName()).log(Level.SEVERE, null, ex);
        }

        photosOfMultipleTags(stackPane, tree, textField);
    }


    private void photosOfMultipleTags(StackPane stackPane, MyTree tree, TextField textField) {
        String tags = textField.getText();
        Set<String> tagsSet;

        if (checkTagsCorrectness(stackPane, textField)) {
            if (!tags.contains(" ") && !tags.contains("&")) {
                photosOfOneTag(stackPane, tags, tree);
            } else if (tags.contains(" ") && !tags.contains("&")) {
                Function<String, Stream<String>> lineSplitterUnion = line -> Stream.of(line.split(" "));   // la funzione lineSplitter(*) trasforma una stringa (line) in uno Stream di stringhe utilizzando come 
                tagsSet = Stream.of(tags).flatMap(lineSplitterUnion).collect(Collectors.toSet());	   // carattere separatore lo spazio (o & nel caso dell'intersezione). Lo Stream lavora sulla stringa 
                unionOfImages(stackPane, tagsSet, tree);						   // ottenuta dal textField per trasformarla prima in uno stream di stringhe (parole) e successivamente in 
            } else if (tags.contains("&") && !tags.contains(" ")) {					   // un insieme di stringhe
                Function<String, Stream<String>> lineSplitterIntersection = line -> Stream.of(line.split("&"));
                tagsSet = Stream.of(tags).flatMap(lineSplitterIntersection).collect(Collectors.toSet());
                intersectionOfImages(stackPane, tagsSet, tree);
            } else {
                updateRectangle(stackPane, "NON PUOI USARE SPAZIO ED '&' NELLA STESSA RICERCA!");
            }
        }
    }


    private void photosOfOneTag(StackPane stackPane, String tags, MyTree tree) {
        MyNode n = new MyNode(tags);		// Costruiamo un MyNode con l'unica stringa disponibile  

        if (tree.contains(n)) {					   // così possiamo controllare, grazie all'implementazione del metodo equals presente in MyNode, se il nodo appena creato è presente o meno
            TreeSet<String> photos = tree.search(n).imageNames;    // nell'albero, e in quel caso recuperiamo la lista (TreeSet) di immagini collegati a quel nodo  			
            visualizzaImmagini(stackPane, photos);
        } else {
            visualizzaErrore(stackPane, "IL TAG");
        }
    }


    private void unionOfImages(StackPane stackPane, Set<String> tagsSet, MyTree tree) {
        HashMap<MyNode, TreeSet<String>> photosListOfMultipleTags = new HashMap<>();    // Costruiamo una mappa (MyNode, listaDelleImmagini) per riempirla solo con Nodi basati su tags già esistenti e con la 
        for (String tag : tagsSet) {							// lista delle immagini relativa 
            MyNode n = new MyNode(tag);
            
	    if (tree.contains(n)) {
                photosListOfMultipleTags.put(n, tree.search(n).imageNames);
            }
        }

        TreeSet<String> setOfPhotos = new TreeSet<String>();		// inseriamo tutte le foto presenti nelle varie liste delle immagini in un unico insieme
        for (MyNode n : photosListOfMultipleTags.keySet()) {
            for (String s : photosListOfMultipleTags.get(n)) {
                setOfPhotos.add(s);
            }
        }

        if (!setOfPhotos.isEmpty()) {
            visualizzaImmagini(stackPane, setOfPhotos);
        } else {
            visualizzaErrore(stackPane, "L'UNIONE");
        }
    }


    private void intersectionOfImages(StackPane stackPane, Set<String> tagsSet, MyTree tree) {
        HashMap<MyNode, TreeSet<String>> photosListOfMultipleTags = new HashMap<>();    // Costruiamo una mappa (MyNode, listaDelleImmagini) per riempirla solo con Nodi basati su tags già esistenti e con la  
        for (String tag : tagsSet) {							// relativa lista delle immagini  
            MyNode n = new MyNode(tag);

            if (tree.contains(n)) {
                photosListOfMultipleTags.put(n, tree.search(n).imageNames);
            } else {
                visualizzaErrore(stackPane, "L'INTERSEZIONE");
                return;
            }
        }

	/* Utilizzando il metodo reduce dello Stream riusciamo ad ottenere l'intersezione delle foto presenti in differenti liste associate a diversi tags.
	   Tale metodo trasforma ogni lista presente nel HashMap photosListOfMultipleTags in un TreeSet, successivamente prende i primi due nuovi TreeSet e ne restituisce l'intersezione dei 2. 
	   Questo nuovo TreeSet sarà nuovamente intersecato con il 3 TreeSet dell'HashMeap e così via. */

        TreeSet<String> photos = photosListOfMultipleTags.values()
                .stream()
                .reduce(
                        new TreeSet<>(photosListOfMultipleTags.values().stream().findAny().get()),    // l'insieme/caso-base dell'induzione sull'intersezione viene pescato casualmente tra l'insieme dei TreeSet
                        (list1, list2) -> {
                            list1.retainAll(list2);						      // e poi qua parte la ricorsione
                            return list1;
                        }
                );

        if (photos.isEmpty()) {
            visualizzaErrore(stackPane, "L'INTERSEZIONE");
        } else {
            visualizzaImmagini(stackPane, photos);
        }
    }


    private void visualizzaImmagini(StackPane stackPane, TreeSet<String> photos) {
        ScrollPane scroll = new ScrollPane();
        scroll.setPrefHeight(DataBase.H_DIM);
        scroll.setMaxWidth(DataBase.W_DIM);
        scroll.setStyle("-fx-background: rgb(230, 248, 255);\n -fx-background-color: rgb(105, 105, 105);");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        GridPane gridPane = new GridPane();
        int i = 0;

        for (String s : photos) {
            ImageViewer blocco = new ImageViewer(s);
            gridPane.add(blocco, i % 3, i / 3);
            i += 1;
        }

        if (i <= 3) {
            for (int j = i; j < 6; j++) {
                ImageViewer blocco = new ImageViewer(null);
                gridPane.add(blocco, j % 3, j / 3);
            }
        } else if (i % 3 != 0) {
            int n = 3 - i % 3;
            
	    for (int j = n; j > 0; j--) {
                ImageViewer blocco = new ImageViewer(null);
                gridPane.add(blocco, 3 - j, i / 3);
            }
        }

        scroll.setContent(gridPane);
        stackPane.getChildren().clear();
        stackPane.getChildren().add(scroll);
    }


    private void visualizzaErrore(StackPane stackPane, String tipo) {
        String testo = null;
        
	if (null != tipo) {
            switch (tipo) {
                case "IL TAG":
                    testo = tipo + " INSERITO NON É PRESENTE ALL'INTERNO DEL DATABASE!";
                    break;
                case "L'UNIONE":
                    testo = tipo + " DEI TAG RISULTA VUOTA!";
                    break;
                case "L'INTERSEZIONE":
                    testo = tipo + " DEI TAG RISULTA VUOTA!";
                    break;
                default:
                    break;
            }
        }

        updateRectangle(stackPane, testo);
    }


    private boolean checkTagsCorrectness(StackPane stackPane, TextField textField) {
        String tagsString = textField.getText();
        int l = tagsString.length();
        String errMex = null;
        boolean flag = true;

        if (textField.getText().equalsIgnoreCase("")) {
            errMex = "INSERISCI ALMENO UN TAG!";
            flag = false;
        }
	
	if (flag) {
            if (tagsString.charAt(0) == ' ' || tagsString.charAt(l - 1) == ' ' || tagsString.charAt(0) == '&' || tagsString.charAt(l - 1) == '&') {
                errMex = "IL PRIMO E/O L'ULTIMO CARATTERE NON POSSONO ESSERE NÉ\nUN '&' NÉ UNO SPAZIO!";
                flag = false;
            }
        }

        if (flag) {
            for (int i = 0; i < l - 1; i++) {
                if (tagsString.charAt(i) == ' ' || tagsString.charAt(i) == '&') {
                    if (tagsString.charAt(i + 1) == ' ' || tagsString.charAt(i + 1) == '&') {
                        errMex = "NON É POSSIBILE INSERIRE NÉ DUE SPAZI DI SEGUITO NÉ DUE '&'\nDI SEGUITO NÉ UNO SPAZIO E UNA '&' VICINI!";
                        flag = false;
                        break;
                    }
                }
            }
        }

        updateRectangle(stackPane, errMex);
        return flag;
    }


    public void updateRectangle(StackPane stackPane, String testo) {
        Rectangle r = new Rectangle(DataBase.W_DIM, DataBase.H_DIM);
        r.setFill(Color.DIMGRAY);
        r.setOpacity(0.85);
        r.setStroke(Color.WHITE);

        Text t1 = new Text();
        t1.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 22)));
        t1.setFill(Color.WHITE);
        t1.setText(testo);

        Text t2 = new Text();
        t2.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 22)));
        t2.setFill(Color.web("rgb(96,247,247)"));
        t2.setText("RICORDA:");

        Text t3 = new Text();
        t3.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 22)));
        t3.setFill(Color.WHITE);
        t3.setText("PUOI CERCARE L'UNIONE DI DUE O PIÙ TAG SEPARANDOLI CON UN\nSINGOLO SPAZIO, OPPURE L'INTERSEZIONE DI DUE O PIÙ TAG\nCONCATENANDOLI CON IL SIMBOLO '&', MA NON PUOI ESEGUIRE\nENTRAMBE LE OPERAZIONI CONTEMPORANEAMENTE.");

        VBox vb_testi = new VBox((int) (scalar_factor * 30));
        VBox vb_ricorda = new VBox((int) (scalar_factor * 5));

        vb_ricorda.getChildren().addAll(t2, t3);
        vb_testi.getChildren().addAll(t1, vb_ricorda);
        vb_testi.setAlignment(Pos.CENTER_LEFT);

        stackPane.getChildren().clear();
        stackPane.getChildren().addAll(r, vb_testi);
        stackPane.setAlignment(Pos.CENTER);
        StackPane.setMargin(vb_testi, new Insets(0, 0, 0, (int) (scalar_factor * 210)));
    }
}

