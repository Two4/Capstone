package za.ac.mandela.WRPV301.Capstone.UI;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.WindowEvent;
import za.ac.mandela.WRPV301.Capstone.Event.GenerationEvent;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Small scene to give feedback while the map is being generated
 */
public class GenerateDialogController {
    @SuppressWarnings("JavaDoc")
    public Label outputLabel;

    /**
     * FXML initialisation method
     */
    @FXML
    public void initialize() {
        outputLabel.setText("initialising...");
        eventBus.register(this);
    }

    /**
     * Consumes {@link GenerationEvent}s from the shared {@link com.google.common.eventbus.EventBus} and outputs them
     * to the text of {@link #outputLabel}
     * @param event the consumed {@link GenerationEvent}
     */
    @Subscribe
    public void output(GenerationEvent event) {
        Platform.runLater(() -> outputLabel.setText(event.getMessage()));
    }

    /**
     * Literally just ignores requests to close the window like, "B!TCH I'M BUSY HERE"
     * @param ignored the ignored request, lol
     */
    @Subscribe
    public void closeRequestHandler(WindowEvent ignored) {
        ignored.consume();
    }

}
