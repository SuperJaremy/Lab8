package Lab.Commands;

import Lab.Service.Work;

public class RemoveByID extends Command {
    public RemoveByID(){
        name="remove_by_id";
        hasArgument=true;
        condition="Enter id";
    }
    @Override
    public Meta validate(Work work) {
        if(work.getElement()!=null){
            try{
                String line=work.getElement();
                int i = Integer.parseInt(line);
                if(i<=0)
                    throw new NumberFormatException();
                return new Meta(name,new Element(i,null));
            }
            catch (NumberFormatException e){
                return new Meta("Command "+name+" parameter should be an integer");
            }
        }
        else
            return new Meta("Command "+name+" should have a parameter");
    }

    @Override
    public Meta validate(String argument, Work work) {
        return new Meta(name,new Element(Integer.parseInt(argument),null));
    }

    @Override
    public String checkArgument(String argument) {
        try{
            Integer.parseInt(argument);
            return null;
        }
        catch (NumberFormatException e){
            return "Parameter should be an integer";
        }
    }
}
