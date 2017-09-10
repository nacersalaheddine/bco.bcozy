package org.openbase.bco.bcozy.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import org.openbase.bco.bcozy.model.LanguageSelection;
import org.openbase.bco.bcozy.util.AuthorizationGroups;
import org.openbase.bco.bcozy.util.ExceptionHelper;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.InfoPane;
import org.openbase.bco.bcozy.view.ObserverButton;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType;

/**
 * @author vdasilva
 */
public class AuthorizationGroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationGroupController.class);


    @FXML
    private ObserverButton saveButton;
    @FXML
    private TextField label;
    @FXML
    private TableColumn<UnitConfigType.UnitConfig, String> removeColumn;

    @FXML
    private TableColumn<UnitConfigType.UnitConfig, String> nameColumn;

    @FXML
    private TableView<UnitConfigType.UnitConfig> groupsTable;

    @FXML
    private Pane root;

    private ObservableList<UnitConfigType.UnitConfig> groups = AuthorizationGroups.getAuthorizationGroups();

    @FXML
    public void initialize() {

        saveButton.setApplyOnNewText(String::toUpperCase);

        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLabel()));
        LanguageSelection.addObserverFor("groupLabel", (locale, text) -> nameColumn.setText(text));


        removeColumn.setCellFactory(cellFactory());


        groupsTable.widthProperty().addListener((observable, oldValue, newValue) ->
                nameColumn.setPrefWidth(newValue.doubleValue() - removeColumn.getWidth() - 2)
        );

        groupsTable.setEditable(false);
        nameColumn.setResizable(false);
        removeColumn.setResizable(false);
        groupsTable.setItems(groups);

        LanguageSelection.addObserverFor("groupLabel", (locale, text) -> label.setPromptText(text));
    }

    public Pane getRoot() {
        return root;
    }

    private Callback<TableColumn<UnitConfigType.UnitConfig, String>, TableCell<UnitConfigType.UnitConfig, String>> cellFactory() {
        return new Callback<TableColumn<UnitConfigType.UnitConfig, String>, TableCell<UnitConfigType.UnitConfig, String>>() {
            @Override
            public TableCell<UnitConfigType.UnitConfig, String> call(final TableColumn<UnitConfigType.UnitConfig, String> param) {
                final TableCell<UnitConfigType.UnitConfig, String> cell = new TableCell<UnitConfigType.UnitConfig, String>() {

                    final Button btn = new Button();

                    {
                        btn.setGraphic(new SVGIcon(FontAwesomeIcon.TIMES, Constants.EXTRA_SMALL_ICON, true));
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> removeGroup(getTableView().getItems().get(getIndex())));
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }

        };
    }

    private void removeGroup(UnitConfigType.UnitConfig group) {
        try {
            AuthorizationGroups.removeAuthorizationGroup(group);
            InfoPane.info("deleteSuccess")
                    .backgroundColor(Color.GREEN)
                    .hideAfter(Duration.seconds(5));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER);

            String message = LanguageSelection.getLocalized("deleteErrorWithMessage", ExceptionHelper.getCauseMessage(ex));

            InfoPane.info(message)
                    .backgroundColor(Color.RED)
                    .hideAfter(Duration.seconds(5));
        }
    }

    @FXML
    private void addGroup(ActionEvent actionEvent) {

        try {
            AuthorizationGroups.addAuthorizationGroup(label.getText());
            label.clear();
            InfoPane.info("saveSuccess")
                    .backgroundColor(Color.GREEN)
                    .hideAfter(Duration.seconds(5));

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();

        } catch (CouldNotPerformException ex) {
            label.getStyleClass().add("text-field-wrong");

            ExceptionPrinter.printHistory(ex, LOGGER);

            String message = LanguageSelection.getLocalized("saveErrorWithMessage", ExceptionHelper.getCauseMessage(ex));

            InfoPane.info(message)
                    .backgroundColor(Color.RED)
                    .hideAfter(Duration.seconds(5));
        }

    }

}
