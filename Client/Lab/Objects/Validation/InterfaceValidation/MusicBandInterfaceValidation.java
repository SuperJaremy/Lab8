package Lab.Objects.Validation.InterfaceValidation;


import Lab.Objects.Album;
import Lab.Objects.Coordinates;
import Lab.Objects.MusicBand;
import Lab.Objects.MusicGenre;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class MusicBandInterfaceValidation {
    private final ResourceBundle words;
    public MusicBandInterfaceValidation(ResourceBundle resourceBundle){
        words=resourceBundle;
    }
    public String validateName(String name){
        if(name.length()==0)
            return words.getString("Field cannot be empty");
        return null;
    }
    public String validateX(String x){
        if(x.length()==0)
            return words.getString("Field cannot be empty");
        try {
            Float a = Float.parseFloat(x);
            if(a.compareTo(-801f)<0)
                return words.getString("Number should be greater than -801");
            return null;
        }
        catch (NumberFormatException e){
            return words.getString("Should be a number greater than -801");
        }
    }
    public String validateY(String y){
        if(y.length()==0)
            return words.getString("Field cannot be empty");
        try{
            Long.parseLong(y);
            return null;
        }
        catch (NumberFormatException e){
            return words.getString("Should be an integer");
        }
    }
    public String validateNumberOfParticipants(String numberOf){
        if(numberOf.length()==0)
            return null;
        try {
            int a = Integer.parseInt(numberOf);
            if(a>0)
                return null;
            return words.getString("Should be a natural number or empty");
        }
        catch (NumberFormatException e){
            return words.getString("Should be a natural number or empty");
        }
    }
    public String validateAlbumsCount(String count){
        if(count.length()==0)
            return words.getString("Field cannot be empty");
        try{
            int a = Integer.parseInt(count);
            if(a>0)
                return null;
            return words.getString("Should be a natural number or empty");
        }
        catch (NumberFormatException e){
            return words.getString("Should be a natural number or empty");
        }
    }
    public String validateEstablishmentDate(String date){
        if(date.length()==0)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(false);
        try{
            sdf.parse(date);
            return null;
        }
        catch (ParseException e){
            return words.getString("Field should be in format \"dd.MM.yyyy\" or empty");
        }
    }
    public String validateGenre(String genre){
        if(genre.length()==0)
            return words.getString("Genre should be chosen");
        return null;
    }
    public String validateAlbumName(String name){
        if(name.length()==0)
            return words.getString("Field cannot be empty");
        return null;
    }
    public String validateAlbumLength(String length){
        if(length.length()==0)
            return words.getString("Field cannot be empty");
        try{
            long a = Long.parseLong(length);
            if(a<1)
                return words.getString("Should be a natural number");
            return null;
        }
        catch (NumberFormatException e){
            return words.getString("Should be a natural number");
        }
    }
    public MusicBand validate(String name, String x, String y, String numberOfParticipants,
                              String albumsCount, String establishmentDate, MusicGenre genre,
                              String albumName, String albumLength){
        Integer number  = numberOfParticipants.length()==0?null:Integer.parseInt(numberOfParticipants);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date =null;
        try {
            date = establishmentDate.length()==0?null : sdf.parse(establishmentDate);
        }
        catch (ParseException ignored){ }
        boolean correct = (albumLength.length()!=0&&albumName.length()!=0)||
                albumLength.length()==0&&albumName.length()==0;
        Album album=null;
        if(albumLength.length()!=0&&albumName.length()!=0){
            album=new Album(albumName,Long.parseLong(albumLength));
        }
        if(correct)
            return new MusicBand(name,new Coordinates(Float.parseFloat(x),Long.parseLong(y)),
                    number, Integer.parseInt(albumsCount), date, genre,album);
        else
            return null;
    }
}
