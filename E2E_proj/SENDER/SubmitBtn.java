import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class SubmitBtn extends Button {
    static double scalar_factor = Sender.scalar_factor;
    byte[] messaggio;

    SubmitBtn(TextField textField, TextArea console, StackPane stack, Rectangle r, Client agent) {
        setText("SUBMIT");
        setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, (int) (scalar_factor * 18)));
        setPrefHeight((int) (scalar_factor * 40));
        setPrefWidth((int) (scalar_factor * 120));

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    sendPhoto(textField, console, stack, r, agent);
                } catch (IOException ex) {
                    Logger.getLogger(SubmitBtn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }


    public void sendPhoto(TextField textField, TextArea console, StackPane stack, Rectangle r, Client agent) throws IOException {
        if (checkTagsCorrectness(textField, console)) {
            prepareMessage(textField);
            resetStage(textField, console, stack, r);
            agent.newMessage();
            agent.keyExchange();
            agent.sendMessage(messaggio);
        }
    }


    private boolean checkTagsCorrectness(TextField textField, TextArea console) {
        String tagsString = textField.getText();
        int l = tagsString.length();
        String errMex = null;
        boolean flag = true;

        if (textField.getText().equalsIgnoreCase("")) {
            errMex = "INSERISCI ALMENO UN TAG!";
            flag = false;
        }

        if (textField.getText().length() > 100) {
            errMex = "HAI INSERITO TROPPI CARATTERI! (LIMITE MASSIMO: 100)";        // il limite serve per impedire che la lunghezza della stringa di tag crei errori a runtime, poichè, per come abbiamo pensato
            flag = false;                                                           // la trasmissione dell'informazione, la lunghezza della stringa dei tag vien salvata nel primo byte del messaggio inviato
        }									    // e quindi se essa fosse più grande 127 verrebbe alterata dal casting in byte, diventando negativa o comunque sballata,
										    // sforando a mo' di gruppo ciclico.				
        if (flag) {
            if (tagsString.contains("&")) {
                errMex = "IL CARATTERE '&' É RISERVATO PER OPERAZIONI DI DATABASE E QUINDI NON PUÒ ESSERE USATO\nALL'INTERNO DEI TAGS!";
                flag = false;
            }
        }
	
	if (flag) {
            if (tagsString.charAt(0) == ' ' || tagsString.charAt(l - 1) == ' ') {
                errMex = "IL PRIMO E/O L'ULTIMO CARATTERE NON POSSONO ESSERE UNO SPAZIO!";
                flag = false;
            }
        }

        if (flag) {
            for (int i = 0; i < l - 1; i++) {
                if (tagsString.charAt(i) == ' ') {
                    if (tagsString.charAt(i + 1) == ' ') {
                        errMex = "NON É POSSIBILE INSERIRE DUE SPAZI DI SEGUITO!";
                        flag = false;
                        break;
                    }
                }
            }
        }

        console.setText(errMex);
        console.setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));

        return flag;
    }


    private void prepareMessage(TextField textField) {
        byte[] tagsArray = textField.getText().getBytes();
        byte tagsLength = (new Integer(tagsArray.length)).byteValue();
        byte imageLength = (new Integer(Sender.imageArray.length)).byteValue();
        
        messaggio = new byte[1 + tagsArray.length + Sender.imageArray.length];
        messaggio[0] = tagsLength;
        System.arraycopy(tagsArray, 0, messaggio, 1, tagsArray.length);
        System.arraycopy(Sender.imageArray, 0, messaggio, 1 + tagsArray.length, Sender.imageArray.length);
    }


    private void resetStage(TextField textField, TextArea console, StackPane stack, Rectangle r) {
        textField.clear();

        stack.getChildren().clear();
        stack.getChildren().add(r);

        console.setText("FOTO E TAGS INVIATI CORRETTAMENTE!");
        console.setFont(Font.font("Verdana", FontWeight.NORMAL, (int) (scalar_factor * 19)));

        disableProperty().set(true);
    }
}
