package Lab.Service;

import Lab.Objects.MusicBand;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;

public class Answer implements Serializable {
    private static final long serialVersionUID = 463274289482L;
    private String answer;
    private boolean exit;
    private boolean success;
    List<MusicBand> collection;
    private LocalDateTime time;

    private Answer(){}

    public String getAnswer() {
        return answer;
    }

    public boolean isExit(){
        return exit;
    }

    public boolean isSuccess(){
        return success;
    }

    public List<MusicBand> getCollection(){
        return collection;
    }

    private Queue<Editing> editions;

    public Queue<Editing> getEditings() {
        return editions;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public static class Editing implements Serializable, Comparable<LocalDateTime> {
        private final static long serialVersionUID=7654567567854L;
        private Byte code;
        private Integer id;
        private MusicBand musicBand;
        private LocalDateTime time;

        public LocalDateTime getTime(){
            return time;
        }

        public Byte getCode() {
            return code;
        }

        public Integer getId() {
            return id;
        }

        public MusicBand getMusicBand() {
            return musicBand;
        }

        @Override
        public int compareTo(LocalDateTime time) {
            return this.time.compareTo(time);
        }
    }

}
