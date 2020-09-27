package Lab;

import Lab.Service.Server;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Set;


public class Main {
    private final static Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        // write your code here
        Server server = new Server();
        boolean success = server.start();
        if(!success){
            logger.error("Ошибка в работе программы");
        }
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for(Thread i : threads)
            System.out.println(i);
    }
}
