package Lab;





import Lab.Service.InterfaceWork;
import Lab.Service.Work;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        Work work = new InterfaceWork();
        work.start();
    }
}
