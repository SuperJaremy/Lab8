package Lab.Commands;

import Lab.Objects.MusicBand;
import Lab.Objects.Validation.ConsoleValidation.ConsoleMusicBandValidator;
import Lab.Service.Work;

public class AddIfMax extends Command {
    public AddIfMax(){
        name="add_if_max";
    }


    @Override
    public Meta validate(Work work) {
        if(work.getElement()!=null){
            return new Meta("Command "+name+" should not have any parameters");
        }
        MusicBand mb = work.validateMusicBand();
        return new Meta(name,new Element(null,mb));
    }

    @Override
    public Meta validate(String argument, Work work) {
        MusicBand mb = work.validateMusicBand();
        if(mb!=null)
            return new Meta(name,new Element(null,mb));
        return new Meta("");
    }
}
