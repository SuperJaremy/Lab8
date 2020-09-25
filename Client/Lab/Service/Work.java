package Lab.Service;


import Lab.Commands.*;
import Lab.Communication.Communicator;
import Lab.Objects.MusicBand;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public abstract class Work {
    protected Communicator communicator = new Communicator();
    protected String element;
    protected boolean inProcess;
    protected Path pathOfScript;
    protected Vector<Integer> Scripts=new Vector<>();
    protected Vector<String> Contents;
    protected int currentLine;
    protected Map<String, Command> Commands = new HashMap<>();
    protected static Command info = new Info();
    protected static Command help = new Help();
    protected static Command show = new Show();
    protected static Command add = new Add();
    protected static Command clear = new Clear();
    protected static Command exit = new Exit();
    protected static Command remove=new RemoveFirst();
    protected static Command ifMax = new AddIfMax();
    protected static Command history = new History();
    protected static Command sum = new SumOfNumberOfParticipants();
    protected static Command count=new CountByNumberOfParticipants();
    protected static Command printFieldDescendingNumberOfParticipants= new PrintFieldDescendingNumberOfParticipants();
    protected static Command uid=new Update();
    protected static Command remove_id = new RemoveByID();
    protected static Command script = new ExecuteScript();
    {
        Commands.put(info.getName(), info);
        Commands.put(help.getName(), help);
        Commands.put(show.getName(),show);
        Commands.put(add.getName(), add);
        Commands.put(clear.getName(),clear);
        Commands.put(exit.getName(), exit);
        Commands.put(remove.getName(),remove);
        Commands.put(ifMax.getName(),ifMax);
        Commands.put(history.getName(),history);
        Commands.put(sum.getName(),sum);
        Commands.put(count.getName(),count);
        Commands.put(printFieldDescendingNumberOfParticipants.getName(),printFieldDescendingNumberOfParticipants);
        Commands.put(uid.getName(),uid);
        Commands.put(remove_id.getName(),remove_id);
        Commands.put(script.getName(),script);
        }

    public abstract boolean start();


    public Communicator getCommunicator() {
        return communicator;
    }

    protected Meta validateLine(String line) throws NullPointerException{
        line=line.trim();
        String [] words=line.split(" ");
        String com = words[0].toLowerCase();
        if (words.length > 1) {
            element = line.substring(line.indexOf(" ") + 1);
        }
        if (!Commands.containsKey(com)) {
            System.out.println("Такой команды не существует: "+com);
            throw new NullPointerException();
        }
        Meta meta;
        meta= Commands.get(com).validate(this);
        if(meta==null)
            throw new NullPointerException();
        else {
            return meta;
        }
    }

    protected boolean executeCommand(Meta meta) throws IOException{
        if(!meta.isSuccessful())
            return true;
        try{
            communicator.communicatorSend(meta);
        }
        catch (IOException e){
            System.out.println("Сервер недоступен");
            communicator.close();
            throw new IOException();
        }
        try{
           Answer answer = communicator.communicatorReceive();
           System.out.println(answer.getAnswer());
           return answer.isExit();
        }
        catch (IOException e){
            System.out.println("Сервер не отвечает");
            throw new IOException();
        }
        catch (ClassNotFoundException e){
            System.out.println("Целостность данных была нарушена");
            return true;
        }
    }


    public abstract MusicBand validateMusicBand();


    public String getElement(){
        return element;
    }

    public void setInProcess(boolean inProcess) {
        this.inProcess = inProcess;
    }

    public Vector<Integer> getScripts() {
        return Scripts;
    }

    public Map<String,Command> getCommands(){
        return Commands;
    }

    public boolean isInProcess() {
        return inProcess;
    }
}
