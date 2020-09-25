package Lab.Commands;

import Lab.Service.Work;

public class Clear extends Command {
    public Clear(){
        name="clear";
    }



    @Override
    public Meta validate(Work work) {
        if(work.getElement()!=null){
            return new Meta("Command "+name+" should not have any parameters");
        }
        return new Meta(name,null);
    }

    @Override
    public Meta validate(String argument, Work work) {
        return new Meta(name,null);
    }
}
