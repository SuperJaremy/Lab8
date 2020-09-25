package Lab.Commands;

import Lab.Service.FileWork;
import Lab.Service.Work;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;


import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.Vector;

public class ExecuteScript extends Command {
    public ExecuteScript(){
        name="execute_script";
        hasArgument=true;
        condition="Enter path to a script";
    }
    private boolean recursion(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Recursion");
        alert.setContentText("Enter the recursion?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @Override
    public Meta validate(Work work) {
        Vector<Integer> scripts = work.getScripts();
        if(work.getElement()==null){
            return new Meta("Command "+name+" should have a parameter");
        }
        else{
            boolean completed;
            if(!scripts.contains(Paths.get(work.getElement()).hashCode()))
                scripts.add(Paths.get(work.getElement()).hashCode());
            else{
                if(recursion())
                    scripts.add(Paths.get(work.getElement()).hashCode());
                else
                    return new Meta("");
            }
            Work innerWork = new FileWork(work.getElement(), scripts, work.getCommunicator());
            completed = innerWork.start();
            if(completed)
                work.setInProcess(innerWork.isInProcess());
            scripts.remove(scripts.size()-1);
            System.out.println(completed?"Script completed"
                    :"Script interrupted");
        }
        return new Meta("");
    }

    @Override
    public Meta validate(String argument, Work work) {
        boolean completed;
        work.getScripts().add(Paths.get(argument).hashCode());
        Work innerWork = new FileWork(argument, work.getScripts(), work.getCommunicator());
        completed = innerWork.start();
        if(completed)
            work.setInProcess(innerWork.isInProcess());
        work.getScripts().remove(work.getScripts().size()-1);
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(completed?"Script completed"
                :"Script interrupted");
        info.show();
        return new Meta("");
    }
}
