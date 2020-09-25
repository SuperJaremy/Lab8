package Lab.Service.Graphics;

import Lab.Objects.MusicBand;
import Lab.Service.Language;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class TextBubble extends StackPane implements Comparable<TextBubble> {
        Circle bubble;
        MusicBand content;
        Double distance;
        Text text;
        Double growCoefficient=1.0;
        boolean created=true;
        boolean updated=false;
        static MusicBand clicked=null;
        public static SimpleObjectProperty<Language> locale = new SimpleObjectProperty<>(null);
        private static final Insets insets = new Insets(5);
        private static final Map<String,Color> colorsByUser = new HashMap<>();

        public TextBubble(MusicBand content) {
            this.content = content;
            if(!colorsByUser.containsKey(content.getUsername())) {
                Random random = new Random();
                Color color;
                do {
                    double red = random.nextDouble();
                    double green = random.nextDouble();
                    double blue = random.nextDouble();
                    color = Color.color(red, green, blue);
                }while (colorsByUser.containsValue(color));
                colorsByUser.put(content.getUsername(), color);
            }
            makeBubble();
            calculateDistance();
            text=new Text(content.toString());
            text.setVisible(false);
            text.setWrappingWidth(bubble.getRadius()*1.5);
            text.setTextAlignment(TextAlignment.CENTER);
            getChildren().addAll(bubble, text);
            setMargin(bubble, insets);
            setOnMouseClicked(event -> {
                clicked = content;
                bubble.setFill(Color.WHITE);
                text.setVisible(true);
            });
            locale.addListener((observable, oldValue, newValue) -> {
                boolean visible = text.isVisible();
                text=new Text(content.toString(newValue));
                text.setVisible(visible);
                text.setWrappingWidth(bubble.getRadius()*1.5);
                text.setTextAlignment(TextAlignment.CENTER);
                getChildren().set(1,text);

            });
        }

        @Override
        public int compareTo(TextBubble o) {
            return distance.compareTo(o.distance);
        }

    public void setContent(MusicBand content) {
            boolean visible = text.isVisible();
            double oldRadius = bubble.getRadius();
            this.content = content;
            if(locale.get()==null)
                text= new Text(content.toString());
            else
                text = new Text(content.toString(locale.get()));
            text.setTextAlignment(TextAlignment.CENTER);
            if(!visible)
                text.setVisible(false);
            makeBubble();
            text.setWrappingWidth(bubble.getRadius()*1.5);
            calculateDistance();
            getChildren().set(0,bubble);
            getChildren().set(1,text);
            growCoefficient=oldRadius/bubble.getRadius();
            updated=true;
    }

    private void makeBubble(){
       bubble = new Circle( Math.log(content.getAlbumsCount())*5+100);
       if(clicked==null||!clicked.equals(this.content))
            bubble.setFill(colorsByUser.get(content.getUsername()));
       else
           bubble.setFill(Color.WHITE);
    }

    private void calculateDistance() {
        distance = Math.sqrt(Math.pow(content.getX(), 2) + Math.pow(content.getY(), 2));
    }

    void noMoreClicked(){
            text.setVisible(false);
            bubble.setFill(colorsByUser.get(content.getUsername()));
    }

    public static Color getColor(String username){
            if(!colorsByUser.containsKey(username)){
                Color color;
                do{
                    Random random = new Random();
                    double red = random.nextDouble();
                    double green = random.nextDouble();
                    double blue = random.nextDouble();
                    color = Color.color(red,green,blue);
                }while(colorsByUser.containsValue(color));
                colorsByUser.put(username,color);
            }
            return colorsByUser.get(username);
    }
}
