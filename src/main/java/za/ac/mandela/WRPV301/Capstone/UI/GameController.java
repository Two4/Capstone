package za.ac.mandela.WRPV301.Capstone.UI;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import za.ac.mandela.WRPV301.Capstone.Action.Action;
import za.ac.mandela.WRPV301.Capstone.Event.*;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Item.Player.PlayerItem;
import za.ac.mandela.WRPV301.Capstone.Launcher;
import za.ac.mandela.WRPV301.Capstone.Main;
import za.ac.mandela.WRPV301.Capstone.Util.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;
import static za.ac.mandela.WRPV301.Capstone.Util.Utils.monoSpaceFonts;


/**
 * The controller for the main game scene
 */
@SuppressWarnings("JavaDoc")
public class GameController {
    public ScrollPane mainScrollPane;
    public ListView<PlayerItem> inventoryListView;
    public TextField userInputTextField;
    public Button userInputSubmitButton;
    public MenuItem newGameButton;
    public MenuItem saveButton;
    public MenuItem loadButton;
    public MenuItem closeButton;
    public CheckMenuItem cheatModeCheckBox;
    public Button centerButton;
    public TextFlow consoleOutputTextFlow;
    public ScrollPane consoleScrollPane;

    /**
     * {@link CanvasManager} instance to govern map rendering and associated operations
     */
    private CanvasManager canvasManager;
    /**
     * A store of previous user inputs; usually a {@link LinkedList} or {@link com.google.common.collect.EvictingQueue}
     * would be better, but the implication of having to use an iterator to scroll through means consecutive calls of
     * {@link Iterator#next()} and {@link Iterator#previous()} produces the unexpected behaviour of recalling the same
     * item
     */
    private final ArrayList<String> consoleInputHistory = new ArrayList<>();
    /**
     * The current console input history cursor value
     */
    private int consoleInputHistoryCursor = -1;

    /**
     * FXML initialisation method; called after controls have been injected
     */
    @FXML
    public void initialize() {
        canvasManager = new CanvasManager(mainScrollPane);
        inventoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        inventoryListView.setItems(FXCollections.observableList(Game.getPlayer().getInventory()));
        inventoryListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PlayerItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || Objects.isNull(item)) {
                    setText(null);
                } else {
                    setText(StringUtils.capitalize(item.getShortDescription()));
                    setOnContextMenuRequested(event -> {
                            ContextMenu menu = new ContextMenu();
                            menu.setOnAction(subEvent -> menu.hide());
                            MenuItem description = new MenuItem(item.getShortDescription());
                            description.setDisable(true);
                            menu.getItems().addAll(description, new SeparatorMenuItem());
                            for (Action action : item.getAvailableActions()) {
                                MenuItem menuItem = new MenuItem(action.getName());
                                menuItem.setOnAction(subEvent -> {
                                    action.execute();
                                    ConsoleEvent.output(action.getFeedBack());
                                    setContextMenu(null);
                                    subEvent.consume();
                                });
                                menu.getItems().add(menuItem);
                            }
                            setContextMenu(menu);
                            menu.show(this, event.getScreenX(), event.getScreenY());
                            event.consume();
                    });
                }
            }
        });
        consoleOutputTextFlow.heightProperty().addListener((observable, oldValue, newValue) -> consoleScrollPane.setVvalue(consoleScrollPane.getVmax()));
        MapChangeEvent.post();
        eventBus.register(this);
    }

    /**
     * submits the current user input to the {@link InputParser#parseAndExecute(String)} method, if not blank, and adds
     * it to the console input history ({@link #consoleInputHistory})
     * @param event the triggering event, which may be a button press or return key press
     */
    public void inputSubmit(ActionEvent event) {
        userInputTextField.appendText("\n");
        String input = userInputTextField.getText();
        userInputTextField.clear();
        if (!Strings.isNullOrEmpty(input.trim())) {
            ConsoleEvent.input(input);
            InputParser.parseAndExecute(input);
            consoleInputHistory.add(input);
            consoleInputHistoryCursor = consoleInputHistory.size() - 1;
        }
        event.consume();
    }

    /**
     * Confirms user intentions that may result in an unsaved game state being lost
     * @param callback the command to run upon confirmation
     */
    private void confirmUnsaved(Runnable callback) {
        Optional<ButtonType> confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure? Unsaved changes will be lost",
                ButtonType.YES,
                ButtonType.NO
        )
                .showAndWait();
        if (confirm.isPresent() && confirm.get().equals(ButtonType.YES)) {
            callback.run();
        }
    }

    /**
     * Creates a new game on confirmation by the user
     * @param event the {@link MenuItem} click event passed to this handler
     */
    public void newGame(ActionEvent event) {
        confirmUnsaved(Launcher::newGame);
        event.consume();
    }

    /**
     * Writes the current game state to file
     * @param event the {@link MenuItem} click event passed to this handler
     */
    public void save(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select save location");
        fileChooser.setInitialFileName("save.xml");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        //the extension filters on save dialogs seems to be bugged - the "save as type" dropdown is empty
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML save file", "*.xml"));
        File file = fileChooser.showSaveDialog(Launcher.getScene().getWindow());
        if (Objects.nonNull(file)) {
            FileIO.save(file.toURI());
        }
        event.consume();
    }

    /**
     * Loads a saved game state
     * @param event the {@link MenuItem} click event passed to this handler
     */
    public void load(ActionEvent event) {
        confirmUnsaved(() -> {
            if (Launcher.loadGame()) {
                MapChangeEvent.post();
            }
        });
        event.consume();
    }

    /**
     * Defers user requests to close the window to the internal {@link #close(Event)} method
     * @param closeRequest the close window request
     */
    @Subscribe
    public void closeRequestHandler(WindowEvent closeRequest) {
        close(closeRequest);
    }

    /**
     * Closes the application
     * @param event the {@link MenuItem} or window close button click event passed to this handler
     */
    public void close(Event event) {
        confirmUnsaved(Platform::exit);
        event.consume();
    }

    /**
     * Toggles cheat mode
     * @param event the {@link CheckMenuItem} toggle event passed to this handler
     */
    public void toggleCheatMode(ActionEvent event) {
        Game.setCheats(cheatModeCheckBox.isSelected());
        event.consume();
    }

    /**
     * Centers the main {@link ScrollPane} on the current player position
     * @param event the {@link Button} click event passed to this handler
     */
    public void centerCanvas(ActionEvent event) {
        centerCanvas((PlayerChangeEvent) null);
        event.consume();
    }

    /**
     * Centers the main {@link ScrollPane} on the current player position; this method is subscribed to {@link PlayerChangeEvent}s
     * on the application's shared {@link com.google.common.eventbus.EventBus} instance
     * @param event the received {@link PlayerChangeEvent}, or null if called elsewhere
     */
    @Subscribe
    private void centerCanvas(PlayerChangeEvent event) {
        Utils.XY position = canvasManager.getPlayerScrollCoordinates(mainScrollPane.getViewportBounds());
        mainScrollPane.setHvalue(Math.max(position.X, 0));
        mainScrollPane.setVvalue(Math.max(position.Y, 0));
    }

    /**
     * scrolls the console input history or clears the current input
     * @param event the {@link KeyEvent} passed to this handler
     */
    public void inputKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case ESCAPE:
                userInputTextField.clear();
                consoleInputHistoryCursor = consoleInputHistory.size() - 1;
                event.consume();
                break;
            case UP:
                if (--consoleInputHistoryCursor < 0) {
                    consoleInputHistoryCursor = consoleInputHistory.size() - 1;
                }
                if (consoleInputHistoryCursor > -1) {
                    userInputTextField.setText(consoleInputHistory.get(consoleInputHistoryCursor));
                    event.consume();
                }
                break;
            case DOWN:
                if (consoleInputHistory.size() > 0) {
                    if (++consoleInputHistoryCursor >= consoleInputHistory.size()) {
                        consoleInputHistoryCursor = 0;
                    }
                    userInputTextField.setText(consoleInputHistory.get(consoleInputHistoryCursor));
                    event.consume();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Consumes {@link ConsoleEvent}s and prints their contained messages to the UI console
     * @param event the {@link ConsoleEvent} consumed
     */
    @Subscribe
    public void outputToConsole(ConsoleEvent event) {
        Text text = new Text(event.getMessage());
        Text newLine = new Text("\n");
        if (!monoSpaceFonts.isEmpty()) {
            Font font = Font.font(monoSpaceFonts.get(0));
            text.setFont(font);
            newLine.setFont(font);
        }
        text.setFill(event.getTextColor());
        consoleOutputTextFlow.getChildren().addAll(text, newLine);
    }

    /**
     * Consumes {@link GameLossEvent}s from the shared {@link com.google.common.eventbus.EventBus} instance and feeds back to the user
     * @param event the {@link GameLossEvent} consumed
     */
    @Subscribe
    public void loseGame(GameLossEvent event) {
        new Alert(Alert.AlertType.INFORMATION, "Oh no! you've lost the game! Press OK to return to the launcher", ButtonType.OK).showAndWait();
        try {
            Launcher.getScene().setRoot(FXMLLoader.load(Main.class.getResource("Launcher.fxml")));
            Launcher.getScene().getWindow().sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Consumes {@link GameWinEvent}s from the shared {@link com.google.common.eventbus.EventBus} instance and feeds back to the user
     * @param event the {@link GameWinEvent} consumed
     */
    @Subscribe
    public void winGame(GameWinEvent event) {
        new Alert(Alert.AlertType.INFORMATION, "Congratulations! you've won the game! Press OK to return to the launcher", ButtonType.OK).showAndWait();
        try {
            Launcher.getScene().setRoot(FXMLLoader.load(Main.class.getResource("Launcher.fxml")));
            Launcher.getScene().getWindow().sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
