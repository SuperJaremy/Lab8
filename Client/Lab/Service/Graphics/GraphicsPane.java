package Lab.Service.Graphics;

import Lab.Objects.MusicBand;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;


public class GraphicsPane extends StackPane {
    private final ObservableList<MusicBand> list;
    private MusicBand clicked=null;
    private final int COLUMN_NUMBER=3;
    private final GridPane pane = new GridPane();
    public GraphicsPane(ObservableList<MusicBand> list1){
        getChildren().add(pane);
        list=list1;
        int count=0;
        for(TextBubble i : list1.stream().map(TextBubble::new).sorted().collect(Collectors.toList())) {
            pane.add(i,count%COLUMN_NUMBER,count/COLUMN_NUMBER);
            Adding(i);
            count++;
        }
        list.addListener((ListChangeListener<MusicBand>) c -> {
            while(c.next()){
                if(c.wasUpdated()){
                    for(int i = c.getFrom();i<c.getTo();i++){
                        MusicBand content1 = list.get(i);
                       Optional<Node> test =  pane.getChildren().stream().filter(node ->
                                ((TextBubble)node).content.getId().equals(content1.getId())).findAny();
                       test.ifPresent(node -> ((TextBubble)node).setContent(content1));
                    }
                }
                else{
                    for(TextBubble i : c.getAddedSubList().stream().map(TextBubble::new).sorted().collect(Collectors.toList())) {
                        pane.getChildren().add(i);
                    }
                    for(MusicBand i : c.getRemoved()){
                        Optional<TextBubble> test = pane.getChildren().stream().filter(node ->
                                ((TextBubble)node).content.equals(i)).map(node -> (TextBubble)node).findAny();
                        test.ifPresent(test1 -> {
                            FadeTransition removing = new FadeTransition(Duration.seconds(2));
                            removing.setFromValue(1);
                            removing.setToValue(0);
                            test1.text.setVisible(false);
                            FillTransition ft = new FillTransition();
                            ft.setDuration(Duration.seconds(1));
                            ft.setToValue(Color.BLACK);
                            ParallelTransition pt = new ParallelTransition(test1.bubble,removing,ft);
                            pt.play();
                            pt.setOnFinished(event -> {
                                pane.getChildren().remove(test1);
                                FXCollections.sort(pane.getChildren(), Comparator.comparing(o -> ((TextBubble) o)));
                                int cnt=0;
                                for(Node j : pane.getChildren()){
                                    GridPane.setColumnIndex(j,cnt%COLUMN_NUMBER);
                                    GridPane.setRowIndex(j,cnt/COLUMN_NUMBER);
                                    TextBubble textBubble = (TextBubble) j;
                                    if(textBubble.created){
                                        Adding(textBubble);
                                    }
                                    else if (textBubble.updated) {
                                        textBubble.updated = false;
                                        Updating(textBubble);
                                    }
                                    cnt++;
                                }
                            });
                        });
                        if(clicked!=null&&clicked.equals(i)){
                            clicked=null;
                            TextBubble.clicked=null;
                        }
                    }
                }
                FXCollections.sort(pane.getChildren(), Comparator.comparing(o -> ((TextBubble) o)));
                int cnt=0;
                for(Node i : pane.getChildren()){
                    GridPane.setColumnIndex(i,cnt%COLUMN_NUMBER);
                    GridPane.setRowIndex(i,cnt/COLUMN_NUMBER);
                    TextBubble textBubble = (TextBubble) i;
                    if(textBubble.created){
                        Adding((TextBubble)i);
                    }
                    else if (textBubble.updated) {
                            textBubble.updated = false;
                            Updating(textBubble);
                        }
                    cnt++;
                }
            }
        });
        setOnMouseClicked(event -> {
            if(clicked==null)
                clicked=TextBubble.clicked;
            else if(!clicked.equals(TextBubble.clicked)){
                Optional<Node> content = pane.getChildren().stream().filter(node ->
                        ((TextBubble)node).content.equals(clicked)).findAny();
                content.ifPresent(node -> ((TextBubble)node).noMoreClicked());
                clicked=TextBubble.clicked;
            }
        });
    }
    public MusicBand getSelectedItem(){
        return clicked;
    }

    public void selectFirst(){
        clicked=pane.getChildren().size()>0?((TextBubble)pane.getChildren().get(0)).content:null;
    }

    private void Adding(TextBubble textBubble){
        boolean visible = textBubble.text.isVisible();
        if(visible)
            textBubble.text.setVisible(false);
        textBubble.created=false;
        FadeTransition ft = new FadeTransition(Duration.seconds(2));
        ft.setFromValue(0);
        ft.setToValue(1);
        FillTransition fillTransition = new FillTransition(Duration.seconds(1));
        fillTransition.setFromValue(Color.BLACK);
        fillTransition.setToValue((Color)textBubble.bubble.getFill());
        ParallelTransition pt = new ParallelTransition(textBubble.bubble,ft,fillTransition);
        pt.setOnFinished(event -> {if(visible) textBubble.text.setVisible(true);});
        pt.play();
    }
    private void Updating(TextBubble textBubble){
        boolean visible = textBubble.text.isVisible();
        if(visible)
            textBubble.text.setVisible(false);
        FillTransition ft = new FillTransition(Duration.seconds(1));
        ft.setToValue(Color.BLACK);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ScaleTransition st = new ScaleTransition(Duration.seconds(2));
        st.setFromX(textBubble.growCoefficient);
        st.setFromY(textBubble.growCoefficient);
        st.setToX(1);
        st.setToY(1);
        ParallelTransition pt = new ParallelTransition(textBubble.bubble,ft,st);
        pt.setOnFinished(event -> {if(visible) textBubble.text.setVisible(true);});
        pt.play();
    }
}
