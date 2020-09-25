package Lab.Commands;

import Lab.Service.Work;

public class CountByNumberOfParticipants extends Command {
    public CountByNumberOfParticipants(){
        name="count_by_number_of_participants";
        hasArgument=true;
        condition="Enter number of participants";
    }


    @Override
    public Meta validate(Work work) {
        if(work.getElement()!=null){
            try{
                String line=work.getElement();
                int i = Integer.parseInt(line);
                if(i<=0)
                    throw new NumberFormatException();
                return new Meta(name, new Element(i));
            }
            catch (NumberFormatException e){
                return new Meta("Command "+name+" parameter should be a natural number");
            }
        }
        else
        return new Meta("Command "+name+" should have a parameter");
    }

    @Override
    public Meta validate(String argument, Work work) {
        return new Meta(name, new Element(Integer.parseInt(argument)));
    }

    @Override
    public String checkArgument(String argument) {
        try{
            int a = Integer.parseInt(argument);
            if(a<1)
                return "Should be a natural number";
            return null;
        }
        catch (NumberFormatException e){
            return "Should be a natural number";
        }
    }
}
