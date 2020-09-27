package Lab.Service;

import Lab.Commands.Meta;
import Lab.Communication.Communicator;
import Lab.Communication.ExitException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;


public class Server {
    private static final Communicator communicator = new Communicator();
    List<Future<Answer>> answers = new ArrayList<>();
    ExecutorService executionPool;
    ExecutorService sendingPool;
    private final static Logger logger = LogManager.getLogger(Server.class);
    private static final Answer authorized = new Answer("Вы авторизованы",
            true,false);
    private static final Answer wrong = new Answer("Неверный пароль",false,false);
    public boolean start() {
        try {
            if(!Authorizator.create())
                return false;
            Scanner input = new Scanner(System.in);
            System.out.println("Введите логин");
            String username = Helper.checkWithExit(input);
            if(username==null)
                return true;
            System.out.println("Введите пароль");
            String password = Helper.checkWithExit(input);
            if(!Database.authorize(username,password))
                return false;
            try(Database db = new Database()){
                if(!db.uploadCollection())
                    return false;
            }
            try {
                communicator.open();
            } catch (IOException e) {
                logger.error("Не удалось открыть соединение");
                return false;
            }
            logger.info("Сервер начал работу");
            while (true) {
                try {
                    int messagesCount=communicator.receiveMessage();
                    if (messagesCount!=0) {
                        List<Callable<Answer>>tasks = new ArrayList<>();
                        for(int i=0;i<messagesCount;i++)
                            tasks.add(task);
                        try {
                            executionPool = Executors.newCachedThreadPool();
                            answers = executionPool.invokeAll(tasks, 5, TimeUnit.SECONDS);
                            executionPool.shutdown();
                            executionPool.awaitTermination(5, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException e){
                            logger.warn("Не все запросы обработаны");
                        }
                        finally {
                            if(executionPool!=null)
                                executionPool.shutdown();
                        }
                        List<Callable<Boolean>> sends = new ArrayList<>();
                        for(int i=0;i<answers.size();i++)
                            sends.add(sendingTask);
                        try {
                            sendingPool = Executors.newFixedThreadPool(10);
                            sendingPool.invokeAll(sends);
                            sendingPool.shutdown();
                        }
                        catch (InterruptedException e){
                            logger.error("Прерван процесс передачи сообщения");
                        }
                        finally {
                            if(sendingPool!=null)
                                sendingPool.shutdown();
                        }
                    }
                }
                catch (ExitException e){
                    logger.info("Инициализирован выход с сервера");
                    return true;
                }
            }
        }
        catch (Exception e) {
            logger.error("Несовместимое с жизнью исключение",e);
            try {
                if(communicator.isOpened())
                    communicator.close();
            } catch (IOException err) {
                logger.error("Что-то ужасное случилось",err);
            }
            return false;
        }
    }
    Callable<Answer> task =()->{
        Answer answer=null;
        Meta meta=null;
        try {
            meta = communicator.getMeta();
            if(meta!=null) {
                boolean isAuthorized;
                isAuthorized = Authorizator.authorize(meta);
                if (isAuthorized && meta.getName() != null) {
                    Work work = new Work(meta.getUsername());
                    answer = work.execute(meta);
                } else if (isAuthorized) {
                    answer = new Answer(Database.GetCollection());
                    logger.info("Пользователь " + meta.getUsername() +
                            " авторизован");
                } else {
                    answer = wrong;
                    logger.info("Пользователь " + meta.getUsername() +
                            " не авторизован");
                }
                if (answer.isSuccess() && meta.getName() != null)
                    logger.info("Выполнена команда " + meta.getName());
                else if (meta.getName() != null)
                    logger.warn("Команда " + meta.getName() + " не смогла быть выполнена");
            }
            else {
                logger.warn("Не удалось выполнить запрос");
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("С клиента пришло неверное сообщение");
        }
        if(meta!=null&&answer!=null)
            answer.username=meta.getUsername();
        return answer;
    };
    Callable<Boolean> sendingTask = ()->{
        Answer answer;
        Future<Answer> answerFuture;
        synchronized (answers){
          answerFuture = answers.get(0);
          answers.remove(0);
        }
        if(answerFuture!=null&&!answerFuture.isCancelled()){
            answer=answerFuture.get();
            return communicator.sendAnswer(answer.username, answer);
        }
        return false;
    };
}