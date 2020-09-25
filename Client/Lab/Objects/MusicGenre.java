package Lab.Objects;

import java.io.Serializable;

public enum MusicGenre implements Serializable {
    JAZZ("Jazz"),
    PUNK_ROCK("Punk rock"),
    BRIT_POP("Britpop");
    private final String string;
    private MusicGenre(String string){
        this.string=string;
    }
    static public void list(){
        for(MusicGenre i:values())
            System.out.println(i.toString());
    }
    private final static long serialVersionUID=32456738276545267L;

    @Override
    public String toString() {
        return string;
    }
}
