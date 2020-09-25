package Lab.Objects;





import Lab.Service.Language;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Album implements Serializable {
    private final static long serialVersionUID=98537287843L;
    private String name;
    private long length=-1;

    public Album() {

    }

    String getName(){
        return name;
    }
    long getLength(){
        return length;
    }

    public Album(String name, long length){
        this.name=name;
        this.length=length;
    }

    @Override
    public String toString() {
        return "Album name: " + name +
                "\nlength: " + length;
    }

    String toString(Language language){
        ResourceBundle words = language.getWords();
        DecimalFormat format = language.getDecimalFormat();
        return words.getObject("album name")+": " + name + "\n" +
                words.getObject("length")+": " + format.format(length);
    }

    void copy(Album album){
        name=album.name;
        length=album.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return length == album.length &&
                name.equals(album.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, length);
    }


}

