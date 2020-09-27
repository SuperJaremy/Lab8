package Lab.Service;

import Lab.Commands.*;
import Lab.Objects.*;
import Lab.Objects.Validation.InterfaceValidation.MusicBandInterfaceValidation;
import Lab.Service.Graphics.GraphicsPane;
import Lab.Service.Graphics.TextBubble;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;


public class InterfaceWork extends Work {
    private Language locale = Language.ENGLISH;
    private final ObservableList<MusicBand> list = FXCollections.observableList(new ArrayList<>(),
            (MusicBand mb) -> new Observable[]{mb.getUpdated()});
    private final FilteredList<MusicBand> filteredList = new FilteredList<>(list);
    private final SortedList<MusicBand> sortedList = new SortedList<>(filteredList);
    private final ObservableList<Meta> metas = FXCollections.observableList(new ArrayList<>());
    private final Stage primaryStage;
    private LocalDateTime time = LocalDateTime.MIN;
    private final Button addButton = new Button("Add");
    private final Button addIfMaxButton = new Button("Add if max");
    private final Button removeButton = new Button("Remove");
    private final Button removeFirstButton = new Button("Remove first");
    private final Button updateButton = new Button("Update");
    private final Button clearButton = new Button("Clear");
    private final Button countButton = new Button("Count by number of participants");
    private final Button sumButton = new Button("Sum of number of participants");
    private final Button descendingButton = new Button("Print field descending number of participants");
    private final Button historyButton = new Button("History");
    private final Button helpButton = new Button("Help");
    private final Button infoButton = new Button("Info");
    private final Button scriptButton = new Button("Execute script");
    private final Text username = new Text();
    private final Tab table = new Tab(locale.getWords().getString("Table"));
    private final Tab graphics = new Tab(locale.getWords().getString("Graphics"));
    private final GraphicsPane graphicsPane = new GraphicsPane(list);
    private final ComboBox<Language> language = new ComboBox<>();
    private final TextArea messageWindow = new TextArea();
    private final TabPane tabs = new TabPane();
    private final TableView<MusicBand> tableView = new TableView<>();
    private final TableColumn<MusicBand, Integer> id = new TableColumn<>("id");
    private final TableColumn<MusicBand, String> name = new TableColumn<>("name");
    private final TableColumn<MusicBand, Float> x = new TableColumn<>("x");
    private final TableColumn<MusicBand, Long> y = new TableColumn<>("y");
    private final TableColumn<MusicBand, LocalDate> creationDate = new TableColumn<>("creation date");
    private final TableColumn<MusicBand, Integer> numberOfParticipants = new TableColumn<>
            ("number of participants");
    private final TableColumn<MusicBand, Long> albumsCount = new TableColumn<>("albums count");
    private final TableColumn<MusicBand, Date> establishmentDate = new TableColumn<>
            ("establishment date");
    private final TableColumn<MusicBand, MusicGenre> genre = new TableColumn<>("genre");
    private final TableColumn<MusicBand, String> albumName = new TableColumn<>("name");
    private final TableColumn<MusicBand, Long> length = new TableColumn<>("length");
    private final TableColumn coordinates = new TableColumn("coordinates");
    private final TableColumn bestAlbum = new TableColumn("best album");
    private final Timeline scheduledTask = new Timeline(new KeyFrame(Duration.seconds(1), event ->
            metas.add(new Show().getMeta())));
    private final Label filter = new Label(locale.getWords().getString("Filter"));
    private final ComboBox<TableColumn<MusicBand,?>> columns = new ComboBox<>();
    private final TextField filterField = new TextField();


    public InterfaceWork() {
        this.primaryStage = new Stage();
        primaryStage.setTitle("Collection Manager");
        VBox leftSide = prepareLeftSide();
        GridPane rightSide = prepareRightSide();
        GridPane mainWindow = new GridPane();
        mainWindow.setVgap(10);
        mainWindow.setHgap(10);
        mainWindow.add(leftSide, 0, 0);
        mainWindow.add(rightSide, 1, 0);
        mainWindow.setHgap(20);
        mainWindow.setAlignment(Pos.CENTER);
        mainWindow.setMinSize(1000, 600);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(960);
        primaryStage.setScene(new Scene(mainWindow, 1030, 600));
        prepareButtons();
    }
    @Override
    public boolean start() {
        metas.addListener((ListChangeListener<Meta>) c -> {
            while (c.next()) {
                for (Meta i : c.getAddedSubList()) {
                    executeCommand(i);
                    metas.remove(i);
                }
            }
        });
        try {
            communicator.open();
            boolean success = false;
            do {
                if (!Authorizator.authorize())
                    return true;
                communicator.communicatorSend(new Meta());
                Answer answer = communicator.communicatorReceive();
                success = answer.isSuccess();
                if (success) {
                    for (MusicBand mb : answer.getCollection()) {
                        mb.setUpdated(false);
                        list.add(mb);
                    }
                    time = answer.getTime();
                }
                if (!success)
                    showError("Wrong username/password");
            } while (!success);
            username.setText(Authorizator.getUsername());
            username.setFill(TextBubble.getColor(Authorizator.getUsername()));
            scheduledTask.setCycleCount(Animation.INDEFINITE);
            scheduledTask.play();
            primaryStage.showAndWait();
            communicator.close();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Connection lost");
            alert.setContentText("Please try later");
            alert.show();
            return false;
        }
    }


    public MusicBand validateMusicBand(MusicBand musicBand) {
        ResourceBundle words = locale.getWords();
        String nameFiller = "";
        String xFiller = "";
        String yFiller = "";
        String numberFiller = "";
        String countFiller = "";
        String dateFiller = "";
        MusicGenre genreFiller = null;
        String albNameFiller = "";
        String albLengthFiller = "";
        if (!musicBand.equals(MusicBand.EMPTY_MUSIC_BAND)) {
            nameFiller = musicBand.getName();
            xFiller = musicBand.getX().toString();
            yFiller = Long.valueOf(musicBand.getY()).toString();
            numberFiller = musicBand.getNumberOfParticipants() != null ?
                    musicBand.getNumberOfParticipants().toString() : "";
            countFiller = Long.valueOf(musicBand.getAlbumsCount()).toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            dateFiller = musicBand.getEstablishmentDate() != null ?
                    sdf.format(musicBand.getEstablishmentDate()) : "";
            genreFiller = musicBand.getGenre();
            albNameFiller = musicBand.getAlbumName() != null ?
                    musicBand.getAlbumName() : "";
            albLengthFiller = musicBand.getLength() != null ?
                    musicBand.getLength().toString() : "";
        }
        Dialog<MusicBand> dialog = new Dialog<>();
        dialog.getDialogPane().setPrefSize(370, 541);
        dialog.setTitle(words.getString("Validation"));
        dialog.setHeaderText(words.getString("Enter new music band"));
        ButtonType validate = new ButtonType("OK", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType(locale.getWords().getString("Cancel"),ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validate, cancel);
        VBox box = new VBox();
        TextField name = new TextField(nameFiller);
        name.setPromptText(words.getString("Name"));
        Label nameError = new Label();
        nameError.setLabelFor(name);
        nameError.setWrapText(true);
        nameError.setTextFill(Color.RED);
        TextField xCoordinate = new TextField(xFiller);
        xCoordinate.setPromptText("x");
        Label xError = new Label();
        xError.setWrapText(true);
        xError.setLabelFor(xCoordinate);
        xError.setTextFill(Color.RED);
        TextField yCoordinate = new TextField(yFiller);
        yCoordinate.setPromptText("y");
        Label yError = new Label();
        yError.setWrapText(true);
        yError.setLabelFor(yCoordinate);
        yError.setTextFill(Color.RED);
        TextField numberOf = new TextField(numberFiller);
        numberOf.setPromptText(words.getString("Number of participants"));
        Label numberError = new Label();
        numberError.setWrapText(true);
        numberError.setLabelFor(numberOf);
        numberError.setTextFill(Color.RED);
        TextField albumsCount = new TextField(countFiller);
        albumsCount.setPromptText(words.getString("Albums count"));
        Label countError = new Label();
        countError.setWrapText(true);
        countError.setLabelFor(albumsCount);
        countError.setTextFill(Color.RED);
        TextField establishmentDate = new TextField(dateFiller);
        establishmentDate.setPromptText(words.getString("Establishment date (dd.MM.yyyy)"));
        Label dateError = new Label();
        dateError.setWrapText(true);
        dateError.setLabelFor(establishmentDate);
        dateError.setTextFill(Color.RED);
        ComboBox<MusicGenre> genre = new ComboBox<>();
        genre.getItems().addAll(MusicGenre.values());
        genre.setCellFactory(new Callback<ListView<MusicGenre>, ListCell<MusicGenre>>() {
            @Override
            public ListCell<MusicGenre> call(ListView<MusicGenre> param) {
                return new ListCell<MusicGenre>() {
                    @Override
                    protected void updateItem(MusicGenre item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setText(null);
                        else {
                            String value = null;
                            if (item != null)
                                value = locale.getWords().getString(item.toString());
                            setText(value);
                        }
                    }
                };
            }
        });
        genre.setPromptText(words.getString("Choose music genre"));
        if (genreFiller != null) {
            genre.getSelectionModel().select(genreFiller);
        }
        Label genreError = new Label();
        genreError.setWrapText(true);
        genreError.setLabelFor(genre);
        genreError.setTextFill(Color.RED);
        TextField albumName = new TextField(albNameFiller);
        albumName.setPromptText(words.getString("Best album name"));
        if (albNameFiller.equals(""))
            albumName.setVisible(false);
        Label albumNameError = new Label();
        albumNameError.setWrapText(true);
        albumNameError.setLabelFor(albumName);
        albumNameError.setTextFill(Color.RED);
        TextField albumLength = new TextField(albLengthFiller);
        albumLength.setPromptText(words.getString("Best album length"));
        if (albLengthFiller.equals(""))
            albumLength.setVisible(false);
        Label lengthError = new Label();
        lengthError.setWrapText(true);
        lengthError.setLabelFor(lengthError);
        lengthError.setTextFill(Color.RED);
        Button enterAlbum = new Button(words.getString("Enter the best album"));
        enterAlbum.setOnAction(ae -> {
            if (albumLength.isVisible()) {
                albumLength.setVisible(false);
                albumName.setVisible(false);
                albumNameError.setVisible(false);
                lengthError.setVisible(false);
            } else {
                albumLength.setVisible(true);
                albumName.setVisible(true);
                albumNameError.setVisible(true);
                lengthError.setVisible(true);
            }
        });
        box.setSpacing(5);
        box.getChildren().addAll(name, nameError, xCoordinate, xError, yCoordinate, yError,
                numberOf, numberError, albumsCount, countError, establishmentDate, dateError,
                genre, genreError, enterAlbum, albumName, albumNameError, albumLength, lengthError);
        dialog.getDialogPane().setContent(box);
        Button validateButton = (Button) dialog.getDialogPane().lookupButton(validate);
        validateButton.addEventFilter(ActionEvent.ACTION, ae -> {
            boolean correct = true;
            MusicBandInterfaceValidation mbiv = new MusicBandInterfaceValidation(words);
            String nameErr = mbiv.validateName(name.getText());
            nameError.setText(nameErr);
            if (nameErr != null) correct = false;
            String xErr = mbiv.validateX(xCoordinate.getText());
            xError.setText(xErr);
            if (xErr != null) correct = false;
            String yErr = mbiv.validateY(yCoordinate.getText());
            yError.setText(yErr);
            if (yErr != null) correct = false;
            String numberErr = mbiv.validateNumberOfParticipants(numberOf.getText());
            numberError.setText(numberErr);
            if (numberErr != null) correct = false;
            String countErr = mbiv.validateAlbumsCount(albumsCount.getText());
            countError.setText(countErr);
            if (countErr != null) correct = false;
            String dateErr = mbiv.validateEstablishmentDate(establishmentDate.getText());
            dateError.setText(dateErr);
            if (dateErr != null) correct = false;
            String genreErr = mbiv.validateGenre(genre.getValue() != null ? genre.getValue().toString() : "");
            genreError.setText(genreErr);
            if (genreErr != null) correct = false;
            if (albumName.isVisible()) {
                String albumNameErr = mbiv.validateAlbumName(albumName.getText());
                albumNameError.setText(albumNameErr);
                if (albumNameErr != null) correct = false;
                String lengthErr = mbiv.validateAlbumLength(albumLength.getText());
                lengthError.setText(lengthErr);
                if (lengthErr != null) correct = false;
            }
            if (!correct)
                ae.consume();
        });
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == validate) {
                String alName = "";
                String alLength = "";
                if (albumName.isVisible()) {
                    alName = albumName.getText();
                    alLength = albumLength.getText();
                }
                MusicBandInterfaceValidation mbiv = new MusicBandInterfaceValidation(words);
                return mbiv.validate(name.getText(), xCoordinate.getText(), yCoordinate.getText(),
                        numberOf.getText(), albumsCount.getText(), establishmentDate.getText(),
                        genre.getValue(), alName, alLength);
            }
            return null;
        });
        Optional<MusicBand> mb = dialog.showAndWait();
        return mb.orElse(null);
    }

    @Override
    public MusicBand validateMusicBand() {
        return validateMusicBand(MusicBand.EMPTY_MUSIC_BAND);
    }

    private VBox prepareLeftSide() {
        VBox leftSide = new VBox();
        username.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.add(filter,0,0);
        pane.add(columns,1,0);
        pane.add(filterField,2,0);
        columns.setItems(tableView.getColumns());
        columns.setCellFactory(lv->newCellFactory());
        columns.setButtonCell(newCellFactory());
        columns.getSelectionModel().select(id);
        filterField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate(new Predicate<MusicBand>() {
                private boolean findData(TableColumn<MusicBand,?> column, MusicBand mb,
                                         String newValue){
                    if(column.getColumns().size()==0) {
                        Object data = column.getCellData(mb);
                        return data != null && data.toString().toLowerCase().contains(newValue);
                    }
                    else {
                        boolean contains = false;
                        for (TableColumn<MusicBand, ?> i : column.getColumns()) {
                            contains |= findData(i, mb, newValue);
                        }
                        return contains;
                    }
                }
                @Override
                public boolean test(MusicBand musicBand) {
                    if(newValue==null||newValue.isEmpty())
                        return true;
                    String lowerCaseFilter = newValue.toLowerCase();
                    TableColumn<MusicBand,?> selectedItem = columns.getSelectionModel().getSelectedItem();
                    return findData(selectedItem,musicBand,lowerCaseFilter);
                }
            });
        }));
        leftSide.getChildren().addAll(username, tabs, pane);
        leftSide.setSpacing(10);
        coordinates.getColumns().addAll(x, y);
        id.setCellFactory(intValue);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        x.setCellFactory(floatValue);
        x.setCellValueFactory(new PropertyValueFactory<>("x"));
        y.setCellFactory(longValue);
        y.setCellValueFactory(new PropertyValueFactory<>("y"));
        creationDate.setCellFactory(creationDateValue);
        creationDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        numberOfParticipants.setCellFactory(intValue);
        numberOfParticipants.setCellValueFactory(new PropertyValueFactory<>("numberOfParticipants"));
        albumsCount.setCellFactory(longValue);
        albumsCount.setCellValueFactory(new PropertyValueFactory<>("albumsCount"));
        establishmentDate.setCellFactory(establishmnentDateValue);
        establishmentDate.setCellValueFactory(new PropertyValueFactory<>("establishmentDate"));
        genre.setCellFactory(new Callback<TableColumn<MusicBand, MusicGenre>, TableCell<MusicBand, MusicGenre>>() {
            @Override
            public TableCell<MusicBand, MusicGenre> call(TableColumn<MusicBand, MusicGenre> param) {
                return new TableCell<MusicBand, MusicGenre>() {
                    @Override
                    protected void updateItem(MusicGenre item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setText(null);
                        else {
                            String value = null;
                            if (item != null)
                                value = locale.getWords().getString(item.toString());
                            setText(value);
                        }
                    }
                };
            }
        });
        genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        albumName.setCellValueFactory(new PropertyValueFactory<>("albumName"));
        length.setCellFactory(longValue);
        length.setCellValueFactory(new PropertyValueFactory<>("length"));

        bestAlbum.getColumns().addAll(albumName, length);
        TableColumn[] columns = new TableColumn[]{id, name, coordinates, creationDate, numberOfParticipants, albumsCount,
                establishmentDate, genre, bestAlbum};
        for (TableColumn i : columns) {
            tableView.getColumns().add(i);
        }
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        tableView.getSelectionModel().selectFirst();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setContent(tableView);
        ScrollPane scroller = new ScrollPane();
        scroller.setContent(graphicsPane);
        graphics.setContent(scroller);
        tabs.getTabs().addAll(table, graphics);
        tabs.setPrefSize(710, 500);
        leftSide.setMinSize(710, 580);
        return leftSide;
    }

    private GridPane prepareRightSide() {
        GridPane rightSide = new GridPane();
        Button[] buttons = new Button[]{addButton, addIfMaxButton, removeButton,
                removeFirstButton, updateButton, clearButton, countButton, sumButton, descendingButton,
                historyButton, helpButton, infoButton, scriptButton};
        rightSide.setHgap(5);
        rightSide.setVgap(10);
        language.setPromptText("Choose language");
        language.getItems().addAll(Language.values());
        language.setPrefSize(245, 20);
        language.valueProperty().addListener((observable, oldValue, newValue) -> {
            locale = newValue;
            changeLanguage(newValue);
        });
        rightSide.add(language, 1, 1, 2, 1);
        descendingButton.setFont(Font.font(7));
        countButton.setFont(Font.font(9));
        int rows = 2;
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setWrapText(true);
            buttons[i].setPrefSize(120, 45);
            rightSide.add(buttons[i], i % 2 + 1, rows);
            if (i % 2 == 1)
                rows++;
        }
        messageWindow.setEditable(false);
        messageWindow.setPrefSize(250, 145);
        messageWindow.setWrapText(true);
        rightSide.add(messageWindow, 1, rows + 1, 2, 3);
        rightSide.setMinSize(300, 580);
        return rightSide;
    }

    private void prepareButtons() {
        helpButton.setOnAction(ae -> setFunction(help));
        countButton.setOnAction(ae -> setFunction(count));
        addButton.setOnAction(ae -> setFunction(add));
        addIfMaxButton.setOnAction(ae -> setFunction(ifMax));
        clearButton.setOnAction(ae -> setFunction(clear));
        historyButton.setOnAction(ae -> setFunction(history));
        infoButton.setOnAction(ae -> setFunction(info));
        descendingButton.setOnAction(ae -> setFunction(printFieldDescendingNumberOfParticipants));
        sumButton.setOnAction(ae -> setFunction(sum));
        removeButton.setOnAction(ae -> {
            MusicBand mb = getMusicBand();
            if (mb != null) {
                Integer id = mb.getId();
                executeCommand(remove_id.validate(id.toString(), this));
            } else {
                ResourceBundle words = locale.getWords();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(words.getString("Item is not chosen"));
                alert.showAndWait();
            }
        });
        removeFirstButton.setOnAction(ae -> {
            tableView.getSelectionModel().selectFirst();
            graphicsPane.selectFirst();
            MusicBand mb = getMusicBand();
            if (mb != null) {
                Integer id = mb.getId();
                executeCommand(remove_id.validate(id.toString(), this));
            } else {
                ResourceBundle words = locale.getWords();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(words.getString("There are no items"));
                alert.showAndWait();
            }
        });
        updateButton.setOnAction(ae -> {
            MusicBand mb = getMusicBand();
            if (mb != null) {
                Integer id = mb.getId();
                executeCommand(uid.validate(id.toString(), new InterfaceWork() {
                    public MusicBand validateMusicBand() {
                        return validateMusicBand(mb);
                    }
                }));
            } else {
                ResourceBundle words = locale.getWords();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(words.getString("Item is not chosen"));
                alert.showAndWait();
            }
        });
        scriptButton.setOnAction(ae -> {
            FileChooser chooser = new FileChooser();
            ResourceBundle words = locale.getWords();
            chooser.setTitle(words.getString("Select script"));
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text files", "*.txt"));
            Stage fileChooser = new Stage();
            File selectedFile = chooser.showOpenDialog(fileChooser);
            if (selectedFile != null)
                executeCommand(script.validate(selectedFile.getPath(), this));
        });
    }

    private void setFunction(Command command) {
        if (command.hasArgument()) {
            ResourceBundle words = locale.getWords();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(command.getName());
            ((Button)dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(locale.getWords().getString("Cancel"));
            dialog.setContentText(words.getString(command.getCondition()));
            boolean success = false;
            boolean checked = false;
            Optional<String> result = Optional.empty();
            while (!success) {
                dialog.setResult(null);
                result = dialog.showAndWait();
                if (result.isPresent()) {
                    String err = command.checkArgument(result.get());
                    if (err != null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText(words.getString(err));
                        alert.showAndWait();
                    } else {
                        success = true;
                        checked = true;
                    }
                } else
                    success = true;
            }
            if (checked)
                metas.add(command.validate(result.get(), this));
        } else
            metas.add(command.validate(null, this));
    }

    @Override
    protected synchronized boolean executeCommand(Meta meta) {
        if (!meta.isSuccessful())
            return true;
        if(!communicator.isOpened())
            return false;
        if (meta.getName().equals(new Show().getName())) {
            try {
                communicator.communicatorSend(new Show().getMeta());
                Answer answer = communicator.communicatorReceive();
                if (answer.getEditings() != null) {
                    for (Answer.Editing i : answer.getEditings()) {
                        if (i.compareTo(time) > 0) {
                            time = i.getTime();
                            switch (i.getCode()) {
                                case 1:
                                    i.getMusicBand().setUpdated(false);
                                    list.add(i.getMusicBand());
                                    break;
                                case 2:
                                    list.removeIf(musicBand ->
                                            musicBand.getId().equals(i.getId()));
                                    break;
                                case 3:
                                    Optional<MusicBand> mb = list.stream().filter(musicBand ->
                                            musicBand.getId().equals(i.getId())).findAny();
                                    if (mb.isPresent()) {
                                        int index1 = tableView.getSelectionModel().getSelectedIndex();
                                        int index = list.indexOf(mb.get());
                                        mb.get().copy(i.getMusicBand());
                                        if (index == index1)
                                            tableView.getSelectionModel().select(index1);
                                    }
                                    tableView.refresh();
                                    break;
                            }
                        }
                    }
                }
                return true;
            } catch (IOException | ClassNotFoundException e) {
                scheduledTask.stop();
                ResourceBundle words = locale.getWords();
                showError(words.getString("Connection lost"));
                primaryStage.close();
                return false;
            } finally {
            }
        }
        try {
            communicator.communicatorSend(meta);
        } catch (IOException e) {
            ResourceBundle words = locale.getWords();
            showError(words.getString("Connection lost") + "\n" + e.getMessage());
            return false;
        }
        try {
            Answer answer = communicator.communicatorReceive();
            messageWindow.setText(answer.getAnswer());
            return true;
        } catch (IOException | ClassNotFoundException e) {
            ResourceBundle words = locale.getWords();
            showError(words.getString("Connection lost"));
            return false;
        }
    }

    private void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(error);
        alert.show();
    }

    private MusicBand getMusicBand() {
        int index = tabs.getSelectionModel().getSelectedIndex();
        if (index == 0)
            return tableView.getSelectionModel().getSelectedItem();
        else
            return graphicsPane.getSelectedItem();
    }

    private void changeLanguage(Language language) {
        TextBubble.locale.set(language);
        ResourceBundle words = ResourceBundle.getBundle("GUILocale", language.getLocale());
        table.setText(words.getString("Table"));
        graphics.setText(words.getString("Graphics"));
        addButton.setText(words.getString("Add"));
        addIfMaxButton.setText(words.getString("Add if max"));
        removeButton.setText(words.getString("Remove"));
        removeFirstButton.setText(words.getString("Remove first"));
        updateButton.setText(words.getString("Update"));
        clearButton.setText(words.getString("Clear"));
        countButton.setText(words.getString("Count by number of participants"));
        sumButton.setText(words.getString("Sum of number of participants"));
        descendingButton.setText(words.getString("Print field descending number of participants"));
        historyButton.setText(words.getString("History"));
        helpButton.setText(words.getString("Help"));
        infoButton.setText(words.getString("Info"));
        scriptButton.setText(words.getString("Execute script"));
        name.setText(words.getString("name"));
        creationDate.setText(words.getString("creation date"));
        numberOfParticipants.setText(words.getString("number of participants"));
        albumsCount.setText(words.getString("albums count"));
        establishmentDate.setText(words.getString("establishment date"));
        genre.setText(words.getString("genre"));
        albumName.setText(words.getString("album name"));
        length.setText(words.getString("length"));
        coordinates.setText(words.getString("coordinates"));
        bestAlbum.setText(words.getString("best album"));
        filter.setText(words.getString("Filter"));
        tableView.autosize();
        tableView.refresh();
        columns.setCellFactory(lv->newCellFactory());
        columns.setButtonCell(newCellFactory());
    }

    Callback<TableColumn<MusicBand, LocalDate>, TableCell<MusicBand, LocalDate>> creationDateValue = new Callback<TableColumn<MusicBand, LocalDate>, TableCell<MusicBand, LocalDate>>() {
        @Override
        public TableCell<MusicBand, LocalDate> call(TableColumn<MusicBand, LocalDate> param) {
            return new TableCell<MusicBand, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setText(null);
                    else {
                        String date = null;
                        if (item != null)
                            date = item.format(locale.getLocalFormat());
                        setText(date);
                    }
                }
            };
        }
    };
    Callback<TableColumn<MusicBand, Float>, TableCell<MusicBand, Float>> floatValue = new Callback<TableColumn<MusicBand, Float>, TableCell<MusicBand, Float>>() {
        @Override
        public TableCell<MusicBand, Float> call(TableColumn<MusicBand, Float> param) {
            return new TableCell<MusicBand, Float>() {
                @Override
                protected void updateItem(Float item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setText(null);
                    else {
                        String number = null;
                        if (item != null)
                            number = locale.getDecimalFormat().format(item);
                        setText(number);
                    }
                }
            };
        }
    };
    Callback<TableColumn<MusicBand, Long>, TableCell<MusicBand, Long>> longValue = new Callback<TableColumn<MusicBand, Long>, TableCell<MusicBand, Long>>() {
        @Override
        public TableCell<MusicBand, Long> call(TableColumn<MusicBand, Long> param) {
            return new TableCell<MusicBand, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setText(null);
                    else {
                        String number = null;
                        if (item != null)
                            number = locale.getDecimalFormat().format(item);
                        setText(number);
                    }
                }
            };
        }
    };
    Callback<TableColumn<MusicBand, Integer>, TableCell<MusicBand, Integer>> intValue = new Callback<TableColumn<MusicBand, Integer>, TableCell<MusicBand, Integer>>() {
        @Override
        public TableCell<MusicBand, Integer> call(TableColumn<MusicBand, Integer> param) {
            return new TableCell<MusicBand, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setText(null);
                    else {
                        String number = null;
                        if (item != null)
                            number = locale.getDecimalFormat().format(item);
                        setText(number);
                    }
                }
            };
        }
    };
    Callback<TableColumn<MusicBand, Date>, TableCell<MusicBand, Date>> establishmnentDateValue = new Callback<TableColumn<MusicBand, Date>, TableCell<MusicBand, Date>>() {
        @Override
        public TableCell<MusicBand, Date> call(TableColumn<MusicBand, Date> param) {
            return new TableCell<MusicBand, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setText(null);
                    else {
                        String date = null;
                        if (item != null)
                            date = locale.getDateFormat().format(item);
                        setText(date);
                    }
                }
            };
        }
    };
    private ListCell<TableColumn<MusicBand,?>> newCellFactory(){
        return new ListCell<TableColumn<MusicBand, ?>>(){
            @Override
            protected void updateItem(TableColumn<MusicBand, ?> item, boolean empty) {
                super.updateItem(item, empty);
                if(empty)
                    setText(null);
                else
                    setText(item.getText());
            }
        };
    }
}