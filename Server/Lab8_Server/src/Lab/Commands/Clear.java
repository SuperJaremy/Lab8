package Lab.Commands;

import Lab.Service.Answer;
import Lab.Service.Database;
import Lab.Service.Work;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Vector;

public class Clear extends Command{
    private final static Logger logger = LogManager.getLogger();
    {
        name="clear";
        description="очистить коллекцию";
        rewrite=true;
    }
    @Override
    public String describe() {
        return name+": "+description;
    }

    @Override
    public Answer act(Meta meta, Work work) {
        try(Database db = new Database()){
            Vector<Integer> ids = db.clear(meta.getUsername());
            if(ids!=null)
                for(Integer id : ids)
                    Answer.addEditing(new Answer.Editing(Answer.Editing.REMOVING_CODE,
                            id,null));
            return new Answer("Коллекция очищена",true,exit);
        }
        catch (SQLException e){
            logger.error("Ошибка соединения с базой данных");
            return new Answer("Ошибка приложения",false,true);
        }
    }
}
