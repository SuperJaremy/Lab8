package Lab.Commands;

import Lab.Objects.MusicBand;
import Lab.Service.Work;

public class Add extends Command {
    public Add(){
        name="add";
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
