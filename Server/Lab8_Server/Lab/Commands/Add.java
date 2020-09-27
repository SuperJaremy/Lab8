package Lab.Commands;


import Lab.Objects.MusicBand;
import Lab.Service.Answer;
import Lab.Service.Database;
import Lab.Service.Work;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Vector;


public class Add extends Command {
    {
        name="add";
        description="добавить новый элемент в коллекцию";
        rewrite=true;
    }
    private final static Logger logger = LogManager.getLogger();
    @Override
    public String describe() {
        return name+" {element}: "+description;
    }

    @Override
    public Answer act(Meta meta, Work work) {
        Vector<MusicBand> V = Database.GetCollection();
        int size = V.size();
        if(size==0||V.get(size-1).getId()<=Integer.MAX_VALUE) {
            MusicBand mb;
            mb = MusicBand.create(meta.getElement().getElem());
            try(Database db = new Database()) {
                MusicBand musicBand = db.Add(mb, meta.getUsername());
                if (musicBand!=null) {
                    Answer.addEditing(new Answer.Editing(Answer.Editing.ADDITION_CODE
                            ,musicBand.getId(),musicBand));
                    return new Answer("Элемент успешно добавлен в коллекцию", true, exit);
                }
            }
            catch (SQLException e){
                logger.error("Ошибка соединения с базой данных");
                return new Answer("Ошибка приложения",false,true);
            }
        }
        return new Answer("Слишком много элементов в коллекции",false,exit);
    }
}
