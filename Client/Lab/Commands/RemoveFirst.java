package Lab.Commands;

import Lab.Service.Work;

public class RemoveFirst extends Command{
    public RemoveFirst(){
        name="remove_first";
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
