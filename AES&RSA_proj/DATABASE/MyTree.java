import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.TreeSet;


public class MyTree extends TreeSet<MyNode> {

    MyTree() throws IOException {
        try (Scanner txt_scan = new Scanner(Paths.get(System.getProperty("user.dir") + "/Database/data.txt"))) {
            while (txt_scan.hasNext()) {
                MyNode node = new MyNode(txt_scan.nextLine());
                this.add(node);
            }
        }
    }


    public void addToTree(String line) {
        String[] split = line.split(" ");   // aggiorno l'albero

        for (int i = 0; i < split.length - 1; i++) {
            MyNode n = new MyNode(split[i]);                 // per ogni tag creo il nodo con quel tag e
            n.imageNames.add(split[split.length - 1]);       // aggiungo la relativa immagine alla lista delle immagini presente nel nodo.

            if (this.contains(n)) {
                (this.search(n)).imageNames.add(split[split.length - 1]);    // se il tag esiste già (e lo verifico con il metodo contains) cerco il nodo preesistente con quel tag e 
            } else {                                                         // aggiorno la relativa lista di immagini
                this.add(n);                                                 // altrimenti creo un nuovo nodo avente quel tag (che non esisteva ancora) 
            }
        }
    }


    public void updateTree() throws IOException {
        try (Scanner txt_scan = new Scanner(Paths.get(System.getProperty("user.dir") + "/New Messages/tags&names.txt"))) {
            while (txt_scan.hasNext()) {
                this.addToTree(txt_scan.nextLine());    // leggo il file avente per righe il nome di una foto e i relativi tag e aggiorno l'albero con il metodo addToTree
            }
        }

        PrintWriter pw = new PrintWriter(System.getProperty("user.dir") + "/New Messages/tags&names.txt");     // visto che ho letto tutto dal file txt contenente i tag e i nomi 
        pw.close();                                                                                            // pulisco il suo contenuto

        File sourceDir = new File(System.getProperty("user.dir") + "/New Messages/New Photos");

        for (File f : sourceDir.listFiles()) {
            Files.move(f.toPath(), Paths.get(System.getProperty("user.dir") + "/Database/Photos/" + f.getName())/*QUA PUOI SCEGLIERE IL NOME*/, StandardCopyOption.REPLACE_EXISTING);
        }
    }


    public void updateData() throws IOException {
        try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/Database/data.txt")) { // aggiorna il file "data.txt" con i nuovi tag e i relativi nomi delle immagini
            for (MyNode n : this) {
                writer.write(n.tag);

                for (String s : n.imageNames) {
                    writer.write(" " + s);
                }
                writer.write("\n");
            }
            writer.close();
        }
    }


    public void update() throws IOException {
        updateTree();
        updateData();
    }


    MyNode search(MyNode target) {
        MyNode ceil = this.ceiling(target);            // ricerca il più piccolo elemento >= target (il tempo O(log n))
        MyNode floor = this.floor(target);             // ricerca il più grande  elemento <= target (il tempo O(log n))

        return ceil.equals(floor) ? ceil : null;       // restituisce il nodo cercato solo se ceil e flooor sono uguali
    }
}

