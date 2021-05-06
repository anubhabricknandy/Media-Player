//package Media-Player;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.text.Text;


import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;
    private String path;

    @FXML
    private Slider progressBar;

    @FXML
    private Slider volumeSlider;

    public void chooseFileMethod(ActionEvent event)
    {
        FileChooser fileChooser=new FileChooser();
        File file=fileChooser.showOpenDialog(null);
        path=file.toURI().toString();
        if(path!=null)
        {
            Text t1=new Text();
            t1.setText("Test");
            Media media=new Media(path);
            mediaPlayer=new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            // for height and width
            DoubleProperty widthProperty=mediaView.fitWidthProperty();
            DoubleProperty heightProperty=mediaView.fitHeightProperty();

            // for resizing
            widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
            heightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));

            // for volume  control
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
        mediaPlayer.setRate(2.0);
    }
    public void slowRate()
    {
        mediaPlayer.setRate(0.5);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
