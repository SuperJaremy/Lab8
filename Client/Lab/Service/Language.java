package Lab.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public enum Language {
    RUSSIAN (new Locale("ru","RU"),"Русский"),
    SPANISH(new Locale("es","MX"),"Español"),
    SLOVENIAN(new Locale("sl","SI"),"Slovenščina"),
    CROATIAN(new Locale("hr","HR"),"Hrvatski"),
    ENGLISH(new Locale("en","US"),"English");
    private final Locale locale;
    private final SimpleDateFormat dateFormat;
    private final String name;
    private final DecimalFormat decimalFormat;
    private final ResourceBundle words;
    private final DateTimeFormatter localFormat;
    Language(Locale locale, String name){
        this.locale=locale;
        this.dateFormat=(SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,locale);
        this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        this.name=name;
        words = ResourceBundle.getBundle("GUILocale",locale);
        localFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public SimpleDateFormat getDateFormat(){
        return dateFormat;
    }

    public DecimalFormat getDecimalFormat(){
        return decimalFormat;
    }

    public ResourceBundle getWords(){
        return words;
    }

    public DateTimeFormatter getLocalFormat(){
        return localFormat;
    }

    @Override
    public String toString() {
        return name;
    }
}
