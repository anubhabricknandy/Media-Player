package sample;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.text.Text;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.time.format.DateTimeFormatter;

public class Controller implements Initializable
{
    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;
    private String path;
    private File file;

    @FXML
    private Slider progressBar;

    @FXML
    private Slider volumeSlider;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    public void chooseFileMethod(ActionEvent event) throws IOException {
        FileChooser fileChooser=new FileChooser();
        file=fileChooser.showOpenDialog(null);
        path=file.toURI().toString();
        if(path!=null)
        {
            Text t1=new Text();
            t1.setText("Test");
            Media media=new Media(path);
            mediaPlayer=new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            DoubleProperty widthProperty=mediaView.fitWidthProperty();
            DoubleProperty heightProperty=mediaView.fitHeightProperty();

            widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
            heightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));

            volumeSlider.setValue(mediaPlayer.getVolume()*100);
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volumeSlider.getValue()/100);
                }
            });

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1)
                {
                    progressBar.setValue(t1.toSeconds());

                }
            });
            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    Duration total=media.getDuration();
                    progressBar.setMax(total.toSeconds());

                    if(mediaPlayer.getCurrentTime().toSeconds()==total.toSeconds())
                    {
                        mediaPlayer.stop();
                    }
                }
            });
            mediaPlayer.play();
        }
    }
    public void play()
    {
        mediaPlayer.play();
        mediaPlayer.setRate(1);

    }
    public void pause()
    {
        mediaPlayer.pause();
    }
    public void stop()
    {
        mediaPlayer.stop();
    }
    public void fastForward()
    {
        Double rate = mediaPlayer.getRate();
        if(rate<1.0){          //If rate is less than 1 then set to normal rate.
            mediaPlayer.setRate(1.0);
        }
        else{                  //Else increase rate by 0.2
            mediaPlayer.setRate(mediaPlayer.getRate()+0.2);
        }

    }
    public void slowRate()
    {
        Double rate= mediaPlayer.getRate();
        if(rate>1.0){  //If current rate > 1 then set rate as 1.
            rate= mediaPlayer.getRate();
            mediaPlayer.setRate(1.0);
        }
        else if(rate>0.25){//If current rate > 0.25 and < 1 then reduce the rate by 0.2.
            rate= mediaPlayer.getRate();
            mediaPlayer.setRate(mediaPlayer.getRate()-0.2);
        }
    }

    //Property Button
    public void showProperties() throws IOException {
        Path p = Paths.get(file.getAbsolutePath());
        BasicFileAttributes basicAttr = Files.readAttributes(p, BasicFileAttributes.class);

        Stage s = new Stage();
        s.setTitle("Properties");
        TilePane r = new TilePane();

        FileTime lastModifiedTime = basicAttr.lastModifiedTime();
        LocalDateTime localDateTime = lastModifiedTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String s1 = localDateTime.format(DATE_FORMATTER);

        FileTime creationTime = basicAttr.creationTime();
        LocalDateTime l2 = creationTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String s2 = localDateTime.format(DATE_FORMATTER);


        FileTime lastAccessTime = basicAttr.lastAccessTime();
        LocalDateTime l3= lastAccessTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String s3 = localDateTime.format(DATE_FORMATTER);

        Label l = new Label("File name: "+p.getFileName().toString()+"\n");
        l.autosize();
        Label l_1 = new Label("Created on: "+s2+"\n");
        l_1.autosize();
        Label l_2= new Label("Last accessed on: "+s3+"\n");
        Label l_3= new Label("Last modified on: "+s1+"\n");
        Label l_4= new Label("Size: " + basicAttr.size()+"\n");
        r.getChildren().add(l);
        r.getChildren().add(l_1);
        r.getChildren().add(l_2);
        r.getChildren().add(l_3);
        r.getChildren().add(l_4);
        Scene sc = new Scene(r, 400, 200);
        // set the scene
        s.setScene(sc);
        s.show();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}