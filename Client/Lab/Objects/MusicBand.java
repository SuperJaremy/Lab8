package Lab.Objects;


import Lab.Service.Authorizator;
import Lab.Service.Language;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;


public class MusicBand implements Serializable {
    private final static long serialVersionUID=9878675667368546L;
    public final static MusicBand EMPTY_MUSIC_BAND = new MusicBand();
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private Integer numberOfParticipants;
    private long albumsCount;
    private java.util.Date establishmentDate;
    private MusicGenre genre;
    private Album bestAlbum;
    private transient SimpleBooleanProperty updated = new SimpleBooleanProperty(false);
    private String username = Authorizator.getUsername();
    public MusicBand(String name, Coordinates coordinates, Integer numberOfParticipants,
    long albumsCount, Date establishmentDate, MusicGenre genre, Album bestAlbum){
        this.name=name;
        this.coordinates=coordinates;
        this.numberOfParticipants=numberOfParticipants;
        this.albumsCount=albumsCount;
        this.establishmentDate=establishmentDate;
        this.genre=genre;
        this.bestAlbum=bestAlbum;
    }

    private MusicBand(){}

    public Integer getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Float getX(){return  coordinates.getX();}
    public long getY(){return coordinates.getY();}
    Coordinates getCoordinates(){
        return coordinates;
    }
    public LocalDate getCreationDate(){
        return creationDate;
    }
    public Integer getNumberOfParticipants(){
        return numberOfParticipants;
    }
    public long getAlbumsCount() {
        return albumsCount;
    }
    public Date getEstablishmentDate() {
        return establishmentDate;
    }
    public MusicGenre getGenre(){
        return genre;
    }
    Album getBestAlbum(){
        return bestAlbum;
    }
    public String getAlbumName(){
        if(bestAlbum!=null)
            return bestAlbum.getName();
        return null;
    }
    public Long getLength(){
        if(bestAlbum!=null)
            return bestAlbum.getLength();
        return null;
    }
    public SimpleBooleanProperty getUpdated(){
        return updated;
    }
    public String getUsername(){
        return username;
    }

    public void setId(Integer id){
        this.id=id;
    }

    public void setUpdated(boolean updated){
        if(this.updated==null)
           this.updated=new SimpleBooleanProperty(updated);
        this.updated.set(updated);
    }

    public void copy(MusicBand mb){
        name=mb.name;
        coordinates=mb.coordinates;
        numberOfParticipants=mb.numberOfParticipants;
        albumsCount=mb.albumsCount;
        establishmentDate=mb.establishmentDate;
        bestAlbum=mb.bestAlbum;
        genre=mb.genre;
        updated.set(!updated.get());
    }

    @Override
    public String toString() {
        return "name: " + name +
                "\ncoordinates: " + coordinates +
                "\ncreationDate: "+creationDate +
                "\nnumberOfParticipants: " + numberOfParticipants +
                "\nalbumsCount: " + albumsCount +
                "\nestablishmentDate: " + establishmentDate +
                "\ngenre: " + genre +
                "\nbestAlbum: " + bestAlbum;
    }

    public String toString(Language language){
        ResourceBundle words = language.getWords();
        SimpleDateFormat dateFormat = language.getDateFormat();
        DecimalFormat decimalFormat = language.getDecimalFormat();
        String ba = bestAlbum==null?null:bestAlbum.toString(language);
        String date = establishmentDate==null?null:dateFormat.format(establishmentDate);
        String nop = numberOfParticipants==null?null:decimalFormat.format(numberOfParticipants);
        return words.getObject("name")+": " + name + "\n" +
                words.getObject("coordinates")+": " + coordinates.toString(language) + "\n" +
                words.getObject("creation date")+": "+creationDate.format(language.getLocalFormat())+"\n"+
                words.getObject("number of participants")+": " + nop + "\n" +
                words.getObject("albums count")+": " + decimalFormat.format(albumsCount) + "\n" +
                words.getObject("establishment date")+": " + date + "\n" +
                words.getObject("genre")+": " + words.getString(genre.toString()) + "\n" +
                words.getObject("best album")+": " + ba + "\n";
    }
}
