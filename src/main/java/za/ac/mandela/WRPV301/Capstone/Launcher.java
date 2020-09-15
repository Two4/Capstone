package za.ac.mandela.WRPV301.Capstone;

import com.google.common.base.Strings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Util.FileIO;
import za.ac.mandela.WRPV301.Capstone.Event.PlayerChangeEvent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Entry point for the JavaFX Application thread
 */
@SuppressWarnings("JavaDoc")
public class Launcher extends Application {
    public Button newGameButton;
    public Button loadGameButton;
    private static Scene scene;

    /**
     * The main application window
     */
    private Stage stage;
    /**
     * The {@link FXMLLoader} used to load {@link Scene}s into the window
     */
    private final FXMLLoader loader = new FXMLLoader();

    /**
     * JavaFX start method
     * @param primaryStage window used to render the scene
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setTitle("UNTITLED GAME");
            scene = new Scene(loader.load(Main.class.getResourceAsStream("Launcher.fxml")));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new game and switches scene contents to the main game scene when done, with an interleading generation dialog
     * Involves a little bit of thread f#ckery, because JavaFX be like that. I'm not proud of this example of concurrency.
     */
    public static void newGame() {
        TextInputDialog dialog = new TextInputDialog("30");
        dialog.setTitle("New Game");
        dialog.setHeaderText("Please enter the size of the grid");
        dialog.setContentText("Resultant grid will be n by n, where n is your input\n30 is a good default value.\n");
        Optional<?> result;
        do {
            result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    String input = ((String) result.get());
                    if (Strings.isNullOrEmpty(input)) {
                        new Alert(Alert.AlertType.ERROR, "Size too small; must be larger than 10.", ButtonType.OK)
                                .showAndWait();
                        result = Optional.empty();
                    } else {
                        int i = Integer.parseInt(input);
                        if (i < 10) {
                            new Alert(Alert.AlertType.ERROR, "Size too small; must be larger than 10.", ButtonType.OK)
                                    .showAndWait();
                            result = Optional.empty();
                        } else if (i > 100) {
                            Optional<ButtonType> confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                    String.format("Are you sure? Generation may take long with an input value of %d", i),
                                    ButtonType.YES,
                                    ButtonType.NO
                            )
                                    .showAndWait();
                            if (confirm.orElseThrow().equals(ButtonType.NO)) {
                                result = Optional.empty();
                            } else {
                                result = Optional.of(i);
                            }
                        } else {
                            result = Optional.of(i);
                        }
                    }
                } catch (Exception ignored) {
                    new Alert(Alert.AlertType.ERROR, "That doesn't seem to be a valid number", ButtonType.OK).showAndWait();
                    result = Optional.empty();
                }
            } else {
                break;
            }
        } while (result.isEmpty());
        if (result.isPresent()) {
            int gridSize = ((Integer) result.get());
            try {
                FXMLLoader loader = new FXMLLoader();
                scene.setRoot(loader.load(Main.class.getResourceAsStream("Generate.fxml")));
                scene.getWindow().sizeToScene();
                scene.getWindow().setOnCloseRequest(eventBus::post);
                new Thread(new Task<>() {
                    @Override
                    protected Object call() {
                        Game.newGame(gridSize);
                        Platform.runLater(() -> {
                            try {
                                scene.setRoot(FXMLLoader.load(Main.class.getResource("Game.fxml")));
                                scene.getWindow().sizeToScene();
                                Game.newGameInit();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        return null;
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the user clicking 'New Game'. Defers to the {@link #newGame()} static method
     * @param event the {@link Button} press event
     */
    public void newGame(ActionEvent event) {
        newGame();
        event.consume();
    }

    /**
     * Handles the user clicking 'Load Game'. Defers actual loading to the {@link #loadGame()} static method, then
     * switches the {@link #scene} contents to the main game scene
     * @param event the {@link Button} press event
     */
    public void loadGame(ActionEvent event) {
        if (loadGame()) {
            try {
                scene.setRoot(loader.load(Main.class.getResourceAsStream("Game.fxml")));
                stage.sizeToScene();
                stage.setOnCloseRequest(eventBus::post);
                PlayerChangeEvent.post();
                Game.newGameInit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        event.consume();
    }

    /**
     * Loads a saved game state from a chosen file
     * @return true if loading was successful
     */
    public static boolean loadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open save file...");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("save.xml");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML save file", "*.xml", "*.XML"));
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (Objects.nonNull(file)) {
            FileIO.XMLResult result = FileIO.load(file.toURI());
            if (result.isNotSuccessful()) {
                new Alert(Alert.AlertType.ERROR, "An error occurred while loading", ButtonType.OK);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 10 points to Gryffindor if you can tell me what this method does.
     * @param event the {@link Button} press event
     */
    public void exit(ActionEvent event) {
        event.consume();
        stage.close();
    }

    /**
     * @return the base window Scene
     */
    public static Scene getScene() {
        return scene;
    }
}
