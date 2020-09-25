package Lab.Service;

import Lab.Objects.MusicBand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Answer implements Serializable {
    private static final long serialVersionUID = 463274289482L;
    private final static Logger logger = LogManager.getLogger();
    transient String username;
    private String answer;
    private boolean success;
    private boolean exit;
    private List<MusicBand> collection;
    private LocalDateTime time;
    public Answer(String answer, boolean success, boolean exit){
        this.answer=answer;
        this.success=success;
        this.exit=exit;
        time=LocalDateTime.MIN;
    }

    public Answer(List<MusicBand> collection){
        this.success=true;
        this.collection=collection;
        this.exit=false;
    }
    boolean isSuccess(){
        return success;
    }

    private static final Queue<Editing> editings = new ConcurrentLinkedQueue<>();
    private Queue<Editing> editions;

    public static class Editing implements Serializable{
        private final static long serialVersionUID=7654567567854L;
        private LocalDateTime time;
        // 1 - Добавление, 2 - Удаление, 3 - Обновление
        public static final Byte ADDITION_CODE = 1;
        public static final Byte REMOVING_CODE = 2;
        public static final Byte UPDATING_CODE = 3;
        private final Byte code;
        private final Integer id;
        private final MusicBand musicBand;
        private static LocalDateTime lastEditing = LocalDateTime.MIN;
        public Editing(byte code, int id, MusicBand musicBand){
            this.code=code;
            this.id=id;
            this.musicBand=musicBand;
            time=LocalDateTime.now();
        }
    }

    public void prepare(){
        editions=editings;
        time=Editing.lastEditing;
    }

    public static synchronized void addEditing(Editing editing){
        editings.add(editing);
        editing.time=LocalDateTime.now();
        if(editing.time.equals(Editing.lastEditing))
            editing.time = editing.time.plusNanos(1L);
        Editing.lastEditing=editing.time;
        logger.info("Правка добавлена "+ editings.size());
        ScheduledExecutorService remover = Executors.newSingleThreadScheduledExecutor();
        remover.schedule((Runnable) editings::poll,1, TimeUnit.MINUTES);
        remover.shutdown();
    }
}
