package Lab.Commands;

import Lab.Service.Work;

public abstract class Command{
    protected String name;
    protected boolean hasArgument=false;
    protected String condition;

    public String getName(){
        return name;
    }

    public abstract Meta validate(Work work);

    public abstract Meta validate(String argument, Work work);

    public boolean hasArgument(){
        return hasArgument;
    }

    public String getCondition(){
        if(hasArgument)
            return condition;
        else return null;
    }

    public String checkArgument(String argument){
        return null;
    }
}
