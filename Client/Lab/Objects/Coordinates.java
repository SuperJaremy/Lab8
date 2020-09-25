package Lab.Objects;




import Lab.Service.Language;

import java.io.Serializable;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.util.*;

public class Coordinates implements Serializable {
    private final static long serialVersionUID=436274532780L;
    private Float x;
    private long y;
    public Coordinates(Float x, long y) {
        this.x=x;
        this.y=y;
    }

    Float getX() {
        return x;
    }

    long getY() {
        return y;
    }

    void copy(Coordinates coordinates){
        x = coordinates.x;
        y = coordinates.y;
    }

    @Override
    public String toString() {
        return "x: " + x +
                "\ny: " + y;
    }

    String toString(Language language){
        DecimalFormat format = language.getDecimalFormat();
        return "x:"+format.format(x)+
                "\ny: "+format.format(y);
    }

}
