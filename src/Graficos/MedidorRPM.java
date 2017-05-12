/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graficos;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.TickMarkType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 *
 * @author practicas
 */
public class MedidorRPM extends Region {

    private static final double PREFERRED_WIDTH = 320;
    private static final double PREFERRED_HEIGHT = 320;
    private static final double MINIMUM_WIDTH = 5;
    private static final double MINIMUM_HEIGHT = 5;
    private static final double MAXIMUM_WIDTH = 1024;
    private static final double MAXIMUM_HEIGHT = 1024;
    private double size;
    private Gauge rpmGauge;
    private Gauge tempGauge;
    private Gauge oilGauge;
    private Pane pane;

    public MedidorRPM() {
        init();
        initGraphics();
        registerListeners();
    }

    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0
                || Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        rpmGauge = GaugeBuilder.create()
                .borderPaint(Color.WHITE)
                .foregroundBaseColor(Color.WHITE)
                .prefSize(400, 400)
                .startAngle(290)
                .angleRange(220)
                .minValue(0)
                .maxValue(4000)
                .valueVisible(false)
                .minorTickMarksVisible(false)
                .majorTickMarkType(TickMarkType.BOX)
                .mediumTickMarkType(TickMarkType.BOX)
                .title("RPM\nx100")
                .needleShape(NeedleShape.ROUND)
                .needleSize(NeedleSize.THICK)
                .needleColor(Color.rgb(234, 67, 38))
                .knobColor(Gauge.DARK_COLOR)
                .customTickLabelsEnabled(true)
                .customTickLabelFontSize(40)
                .customTickLabels("0", "", "10", "", "20", "", "30", "", "40")
                .animated(true)
                .build();

        tempGauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .prefSize(170, 170)
                .autoScale(false)
                .foregroundBaseColor(Color.WHITE)
                .title("TEMP")
                .valueVisible(false)
                .angleRange(90)
                .needleShape(NeedleShape.ROUND)
                .needleSize(NeedleSize.THICK)
                .needleColor(Color.rgb(234, 67, 38))
                .minorTickMarksVisible(false)
                .mediumTickMarksVisible(false)
                .majorTickMarkType(TickMarkType.BOX)
                .knobColor(Gauge.DARK_COLOR)
                .customTickLabelsEnabled(true)
                .customTickLabelFontSize(36)
                .customTickLabels("100", "", "", "", "", "175", "", "", "", "", "250")
                .animated(true)
                .build();

        oilGauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .prefSize(170, 170)
                .foregroundBaseColor(Color.WHITE)
                .title("ACEITE")
                .valueVisible(false)
                .angleRange(90)
                .needleShape(NeedleShape.ROUND)
                .needleSize(NeedleSize.THICK)
                .needleColor(Color.rgb(234, 67, 38))
                .minorTickMarksVisible(false)
                .mediumTickMarksVisible(false)
                .majorTickMarkType(TickMarkType.BOX)
                .knobColor(Gauge.DARK_COLOR)
                .customTickLabelsEnabled(true)
                .customTickLabelFontSize(36)
                .customTickLabels("0", "", "", "", "", "50", "", "", "", "", "100")
                .animated(true)
                .build();
        pane = new Pane(tempGauge, oilGauge, rpmGauge);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(observable -> resize());
        heightProperty().addListener(observable -> resize());
    }

    public Gauge getRpmGauge() {
        return rpmGauge;
    }

    public Gauge getTempGauge() {
        return tempGauge;
    }

    public Gauge getOilGauge() {
        return oilGauge;
    }

    private void resize() {
        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size = width < height ? width : height;

        if (size > 0) {
            pane.setMaxSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            rpmGauge.setPrefSize(size, size);

            tempGauge.setPrefSize(size * 0.425, size * 0.425);
            tempGauge.relocate(size * 0.1, size * 0.5625);

            oilGauge.setPrefSize(size * 0.425, size * 0.425);
            oilGauge.relocate(size * 0.475, size * 0.5625);
        }
    }

}
