/**
 * ==================================================================
 *
 * This file is part of org.openbase.bco.bcozy.
 *
 * org.openbase.bco.bcozy is free software: you can redistribute it and modify
 * it under the terms of the GNU General Public License (Version 3)
 * as published by the Free Software Foundation.
 *
 * org.openbase.bco.bcozy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.openbase.bco.bcozy. If not, see <http://www.gnu.org/licenses/>.
 * ==================================================================
 */
package org.openbase.bco.bcozy.view.unitpanes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.openbase.bco.bcozy.view.Constants;
import org.openbase.bco.bcozy.view.SVGIcon;
import org.openbase.bco.dal.remote.unit.PowerConsumptionSensorRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.jul.pattern.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.dal.PowerConsumptionSensorDataType.PowerConsumptionSensorData;

/**
 * Created by andi on 08.04.16.
 */
public class PowerConsumptionSensorPane extends AbstractUnitPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(PowerConsumptionSensorPane.class);

    private final PowerConsumptionSensorRemote powerConsumptionSensorRemote;
    private final SVGIcon powerConsumptionIcon;
    private final Text currentPowerConsumption;
    private final BorderPane headContent;
    private final Text sumPowerConsumption;
    private final Text voltagePowerConsumption;
    private final Text sumLabelText;
    private final Text voltageLabelText;
    private final GridPane bodyContent;

    /**
     * Constructor for the PowerConsumptionSensorPane.
     * @param powerConsumptionSensorRemote powerConsumptionSensorRemote
     */
    public PowerConsumptionSensorPane(final UnitRemote powerConsumptionSensorRemote) {
        this.powerConsumptionSensorRemote = (PowerConsumptionSensorRemote) powerConsumptionSensorRemote;

        headContent = new BorderPane();
        bodyContent = new GridPane();
        powerConsumptionIcon = new SVGIcon(MaterialDesignIcon.POWER, Constants.SMALL_ICON, true);
        currentPowerConsumption = new Text();
        sumPowerConsumption = new Text();
        voltagePowerConsumption = new Text();
        sumLabelText = new Text("Power Consumption:");
        voltageLabelText = new Text("Current Voltage:");

        initUnitLabel();
        initTitle();
        initContent();
        createWidgetPane(headContent, bodyContent, false);
        initEffect();
        tooltip.textProperty().bind(observerText.textProperty());

        addObserverAndInitDisableState(this.powerConsumptionSensorRemote);
    }

    private void initEffect() {
        double currentPowerConsumption = 0.0;
        double sumPowerConsumption = 0.0;
        double voltagePowerConsumption = 0.0;

        try {
            currentPowerConsumption = powerConsumptionSensorRemote.getPowerConsumptionState().getCurrent();
            sumPowerConsumption = powerConsumptionSensorRemote.getPowerConsumptionState().getConsumption();
            voltagePowerConsumption = powerConsumptionSensorRemote.getPowerConsumptionState().getVoltage();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setPowerConsumptionIconAndText(currentPowerConsumption, sumPowerConsumption,
                voltagePowerConsumption);
    }

    private void setPowerConsumptionIconAndText(final double currentPowerConsumption, final double sumPowerConsumption,
                                                final double voltagePowerConsumption) {
        if (currentPowerConsumption == 0.0) {
            powerConsumptionIcon.changeForegroundIcon(MaterialDesignIcon.POWER);
            observerText.setIdentifier("powerOff");
        } else {
            powerConsumptionIcon.setForegroundIconColor(Color.GREEN);
            observerText.setIdentifier("powerOn");
        }

        this.currentPowerConsumption.setText(currentPowerConsumption + Constants.WATT);
        this.sumPowerConsumption.setText(sumPowerConsumption + Constants.WATT);
        this.voltagePowerConsumption.setText(voltagePowerConsumption + "V");
    }

    @Override
    protected void initTitle() {
        this.currentPowerConsumption.getStyleClass().add(Constants.ICONS_CSS_STRING);

        iconPane.add(powerConsumptionIcon, 0, 0);
        iconPane.add(currentPowerConsumption, 1, 0);
        iconPane.setHgap(Constants.INSETS);

        headContent.setCenter(getUnitLabel());
        headContent.setAlignment(getUnitLabel(), Pos.CENTER_LEFT);
        headContent.prefHeightProperty().set(powerConsumptionIcon.getSize() + Constants.INSETS);
    }

    @Override
    protected void initContent() {
        this.sumPowerConsumption.getStyleClass().add(Constants.ICONS_CSS_STRING);
        this.voltagePowerConsumption.getStyleClass().add(Constants.ICONS_CSS_STRING);

        bodyContent.add(sumLabelText, 0, 0);
        bodyContent.add(sumPowerConsumption, 1, 0);
        bodyContent.add(voltageLabelText, 0, 1);
        bodyContent.add(voltagePowerConsumption, 1, 1);

        //CHECKSTYLE.OFF: MagicNumber
        bodyContent.prefHeightProperty().set(50 + Constants.INSETS);
        //CHECKSTYLE.ON: MagicNumber
    }

    @Override
    protected void initUnitLabel() {
        String unitLabel = Constants.UNKNOWN_ID;
        try {
            unitLabel = this.powerConsumptionSensorRemote.getData().getLabel();
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, LOGGER, LogLevel.ERROR);
        }
        setUnitLabelString(unitLabel);
    }

    @Override
    public UnitRemote getDALRemoteService() {
        return powerConsumptionSensorRemote;
    }

    @Override
    void removeObserver() {
        this.powerConsumptionSensorRemote.removeObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object powerConsumption) throws java.lang.Exception {
        Platform.runLater(() -> {
            final double voltagePowerConsumption = ((PowerConsumptionSensorData)
                    powerConsumption).getPowerConsumptionState().getVoltage();
            final double currentPowerConsumption = ((PowerConsumptionSensorData)
                    powerConsumption).getPowerConsumptionState().getCurrent();
            final double sumPowerConsumption = ((PowerConsumptionSensorData)
                    powerConsumption).getPowerConsumptionState().getConsumption();

            setPowerConsumptionIconAndText(currentPowerConsumption, sumPowerConsumption,
                    voltagePowerConsumption);
        });
    }
}
