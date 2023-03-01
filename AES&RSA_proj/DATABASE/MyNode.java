import java.util.TreeSet;
import java.util.Objects;


public class MyNode implements Comparable {
    String tag;
    TreeSet<String> imageNames = new TreeSet<String>();

    MyNode(String line) {
        String[] split = line.split(" ");
        tag = split[0];

        for (int i = 1; i <= split.length - 1; i++) {
            imageNames.add(split[i]);
        }
    }


    @Override
    public String toString() {
        return tag;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()) {
            return this.tag.equals(((MyNode) obj).tag);
        } else {
            System.out.println("MyNode uncomparable with other object which is not a MyNode");
        }

        return false;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.tag);
        return hash;
    }


    @Override
    public int compareTo(Object obj) {
        if (obj.getClass() == this.getClass()) {
            return this.tag.compareTo(((MyNode) obj).tag);
        } else {
            System.out.println("MyNode uncomparable with other object which is not a MyNode");
        }

        return -100;
    }
}

