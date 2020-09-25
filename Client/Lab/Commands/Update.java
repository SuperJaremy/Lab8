package Lab.Commands;

import Lab.Objects.MusicBand;
import Lab.Objects.Validation.ConsoleValidation.ConsoleMusicBandValidator;
import Lab.Service.Work;

public class Update extends Command {
    public Update(){
        name="update";
    }
    @Override
    public Meta validate(Work work) {
        if(work.getElement()!=null){
            try{
                String line=work.getElement();
                int i = Integer.parseInt(line);
                if(i<=0)
                    throw new NumberFormatException();
                MusicBand mb = work.validateMusicBand();
                return new Meta(name,new Element(i,mb));
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
        Integer i = Integer.parseInt(argument);
        MusicBand mb = work.validateMusicBand();
        if(mb!=null)
            return new Meta(name,new Element(i,mb));
        return new Meta("");
    }
}
