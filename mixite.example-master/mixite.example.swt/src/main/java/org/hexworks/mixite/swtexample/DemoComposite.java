package org.hexworks.mixite.swtexample;

import jdk.nashorn.tools.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.api.Point;
import org.hexworks.mixite.core.api.Rectangle;
import org.hexworks.mixite.core.vendor.Maybe;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static org.hexworks.mixite.core.api.HexagonOrientation.FLAT_TOP;
import static org.hexworks.mixite.core.api.HexagonOrientation.POINTY_TOP;
import static org.hexworks.mixite.core.api.HexagonalGridLayout.HEXAGONAL;
import static org.hexworks.mixite.core.api.HexagonalGridLayout.RECTANGULAR;


@SuppressWarnings("ALL")
public class DemoComposite extends Composite {

    private static final int DEFAULT_GRID_WIDTH = 9;
    private static final int DEFAULT_GRID_HEIGHT = 9;
    private static final int DEFAULT_RADIUS = 30;
    private static final HexagonOrientation DEFAULT_ORIENTATION = FLAT_TOP;
    private static final HexagonalGridLayout DEFAULT_GRID_LAYOUT = HEXAGONAL;
    private static final int CANVAS_WIDTH = 1000;
    private HexagonalGrid<SatelliteDataImpl> hexagonalGrid;
    private HexagonalGridCalculator<SatelliteDataImpl> hexagonalGridCalculator;
    private int gridWidth = DEFAULT_GRID_WIDTH;
    private int gridHeight = DEFAULT_GRID_HEIGHT;
    private int radius = DEFAULT_RADIUS;
    private HexagonOrientation orientation = DEFAULT_ORIENTATION;
    private HexagonalGridLayout hexagonGridLayout = DEFAULT_GRID_LAYOUT;
    private boolean showNeighbors = false;
    private boolean showMovementRange = false;
    private boolean showLineDrawing = false;
    private boolean showVisibilityDrawing = false;
    private Hexagon prevSelected = null;
    private Hexagon currSelected = null;
    private Hexagon currMouseOver = null;
    private int movementRange;
    private Font font;
    private int fontSize;
    private boolean drawCoordinates = true;

    private ArrayList<Hexagon<SatelliteDataImpl>> fixedHexes = new ArrayList<>();
    private ArrayList<Hexagon<SatelliteDataImpl>> hiddenHexes = new ArrayList<>();


    /**
     * Demo composite.
     */
    public DemoComposite(Shell parent, int style, final int shellWidth, final int shellHeight) {
        super(parent, style);
        GridLayout compositeLayout = new GridLayout(2, false);
        compositeLayout.horizontalSpacing = 0;
        compositeLayout.verticalSpacing = 0;
        compositeLayout.marginWidth = 0;
        compositeLayout.marginHeight = 0;
        setLayout(compositeLayout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        final Canvas canvas = new Canvas(this, SWT.DOUBLE_BUFFERED);
        canvas.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent event) {
            }
        });
        GridData canvasGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        canvasGridData.minimumWidth = CANVAS_WIDTH;
        canvas.setLayoutData(canvasGridData);
        canvas.setLayout(new GridLayout(1, false));
        final Group grpControls = new Group(this, SWT.NONE);
        grpControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout graphControlGridLayout = new GridLayout(2, false);
        graphControlGridLayout.marginHeight = 0;
        grpControls.setLayout(graphControlGridLayout);
        grpControls.setText("Controls:");

        // pointy radio
        final Button radioPointy = new Button(grpControls, SWT.RADIO);
        radioPointy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (radioPointy.getSelection()) {
                    orientation = POINTY_TOP;
                    regenerateHexagonGrid(canvas);
                }
            }
        });
        radioPointy.setSelection(true);
        radioPointy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        radioPointy.setText("Pointy");

        // flat radio
        final Button radioFlat = new Button(grpControls, SWT.RADIO);
        radioFlat.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        radioFlat.setText("Flat");
        radioFlat.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (radioFlat.getSelection()) {
                    orientation = FLAT_TOP;
                    regenerateHexagonGrid(canvas);
                }
            }
        });

        // layout
        Label lblLayout = new Label(grpControls, SWT.NONE);
        lblLayout.setText("Layout");
        final Combo layoutCombo = new Combo(grpControls, SWT.NONE);
        for (HexagonalGridLayout layout : HexagonalGridLayout.values()) {
            layoutCombo.add(layout.name());
        }
        layoutCombo.setText(DEFAULT_GRID_LAYOUT.name());
        layoutCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                hexagonGridLayout = HexagonalGridLayout.valueOf(layoutCombo.getText());
                regenerateHexagonGrid(canvas);
            }
        });
        layoutCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        // grid width
        Label lblNewLabel = new Label(grpControls, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        lblNewLabel.setText("Grid width");
        final Spinner gridWidthSpinner = new Spinner(grpControls, SWT.BORDER);
        gridWidthSpinner.setSelection(gridWidth);
        gridWidthSpinner.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                gridWidth = gridWidthSpinner.getSelection();
                regenerateHexagonGrid(canvas);
            }

        });

        // grid height
        Label lblGridy = new Label(grpControls, SWT.NONE);
        lblGridy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        lblGridy.setText("Grid height");
        final Spinner gridHeightSpinner = new Spinner(grpControls, SWT.BORDER);
        gridHeightSpinner.setSelection(gridHeight);
        gridHeightSpinner.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                gridHeight = gridHeightSpinner.getSelection();
                regenerateHexagonGrid(canvas);
            }

        });

        // radius
        Label lblRadius = new Label(grpControls, SWT.NONE);
        lblRadius.setText("Radius");
        final Spinner radiusSpinner = new Spinner(grpControls, SWT.BORDER);
        radiusSpinner.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                radius = radiusSpinner.getSelection();
                regenerateHexagonGrid(canvas);
            }

        });
        radiusSpinner.setSelection(radius);

        // movement range
        Label lblMovementRange = new Label(grpControls, SWT.NONE);
        lblMovementRange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblMovementRange.setText("Movement range");
        final Spinner movementRangeSpinner = new Spinner(grpControls, SWT.BORDER);
        movementRangeSpinner.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                movementRange = movementRangeSpinner.getSelection();
                canvas.redraw();
            }

        });

        // toggle neighbors
        final Button toggleNeighborsCheck = new Button(grpControls, SWT.CHECK);
        toggleNeighborsCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        toggleNeighborsCheck.setText("Toggle neighbors");

        final Button toggleVisibleHexFilling = new Button(grpControls, SWT.CHECK);
        toggleVisibleHexFilling.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        toggleVisibleHexFilling.setText("Toggle visibility ");
        toggleVisibleHexFilling.setToolTipText("Selected hexes will be considered obstacles. If the current mouseover "
                + "hex is visible from last selected hex, it will be filled in green. Red if not.");
        toggleVisibleHexFilling.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent event) {
                showVisibilityDrawing = toggleVisibleHexFilling.getSelection();
                canvas.redraw();
            }
        });

        final Button toggleLineDrawing = new Button(grpControls, SWT.CHECK);
        toggleLineDrawing.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        toggleLineDrawing.setText("Toggle line drawing");
        toggleLineDrawing.setToolTipText(
                "The hexes in path from last selected hex to the" + " mouseover hex will be drawn with a red border");
        toggleLineDrawing.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent event) {
                showLineDrawing = toggleLineDrawing.getSelection();
                canvas.redraw();
            }
        });

        final Button toggleMovementRangeCheck = new Button(grpControls, SWT.CHECK);
        toggleMovementRangeCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        toggleMovementRangeCheck.setText("Toggle movement range");
        toggleMovementRangeCheck.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent event) {
                showMovementRange = toggleMovementRangeCheck.getSelection();
                showNeighbors = !showMovementRange;
                toggleNeighborsCheck.setSelection(showNeighbors);
                canvas.redraw();
            }
        });
        toggleNeighborsCheck.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent event) {
                showNeighbors = toggleNeighborsCheck.getSelection();
                showMovementRange = !showNeighbors;
                toggleMovementRangeCheck.setSelection(showMovementRange);
                canvas.redraw();
            }
        });

        // toggle draw coordinates
        final Button toggleDrawCoordinatesCheck = new Button(grpControls, SWT.CHECK);
        toggleDrawCoordinatesCheck.setSelection(true);
        toggleDrawCoordinatesCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        toggleDrawCoordinatesCheck.setText("Draw coordinates");
        toggleDrawCoordinatesCheck.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent event) {
                drawCoordinates = toggleDrawCoordinatesCheck.getSelection();
                canvas.redraw();
            }
        });

        // reset button
        Button resetButton = new Button(grpControls, SWT.NONE);
        resetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        resetButton.setText("Reset");

        // position of mouse
        Label lblXPosition = new Label(grpControls, SWT.NONE);
        lblXPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblXPosition.setText("X position:");

        final Text xPositionText = new Text(grpControls, SWT.BORDER);
        xPositionText.setEditable(false);
        xPositionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblYPosition = new Label(grpControls, SWT.NONE);
        lblYPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblYPosition.setText("Y position:");

        final Text yPositionText = new Text(grpControls, SWT.BORDER);
        yPositionText.setEditable(false);
        yPositionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        // distance
        Label lblDistance = new Label(grpControls, SWT.NONE);
        lblDistance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDistance.setText("Distance");

        final Text distanceText = new Text(grpControls, SWT.BORDER);
        distanceText.setEditable(false);
        distanceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label label1 = new Label(grpControls, SWT.NONE);
        label1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        label1.setText("(between last 2 selected)");

        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent arg0) {
                fixedHexes.clear();
                hiddenHexes.clear();
                resetFields();
                resetControls();
                regenerateHexagonGrid(canvas);
            }

            private void resetFields() {
                orientation = DEFAULT_ORIENTATION;
                gridHeight = DEFAULT_GRID_HEIGHT;
                gridWidth = DEFAULT_GRID_WIDTH;
                radius = DEFAULT_RADIUS;
                showNeighbors = false;
                showMovementRange = false;
                hexagonGridLayout = DEFAULT_GRID_LAYOUT;
                prevSelected = null;
                currSelected = null;
                movementRange = 0;
                drawCoordinates = true;
            }

            private void resetControls() {
                radioPointy.setSelection(true);
                radioFlat.setSelection(false);
                gridHeightSpinner.setSelection(DEFAULT_GRID_HEIGHT);
                gridWidthSpinner.setSelection(DEFAULT_GRID_WIDTH);
                radiusSpinner.setSelection(DEFAULT_RADIUS);
                toggleNeighborsCheck.setSelection(false);
                toggleMovementRangeCheck.setSelection(false);
                distanceText.setText("");
                movementRangeSpinner.setSelection(0);
                toggleDrawCoordinatesCheck.setSelection(false);
                layoutCombo.setText(DEFAULT_GRID_LAYOUT.name());

            }
        });

        canvas.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent event) {
                xPositionText.setText(event.x + "");
                yPositionText.setText(event.y + "");
                Maybe<Hexagon<SatelliteDataImpl>> currMouseOverMaybe = hexagonalGrid.getByPixelCoordinate(event.x, event.y);
                if (currMouseOverMaybe.isPresent()) {
                    currMouseOver = currMouseOverMaybe.get();
                    canvas.redraw();
                }
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.character == 'g'){
                    generateOutput();
                }
            }
        });


        // drawing
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent event) {

                Hexagon<SatelliteDataImpl> hex = null;
                try {
                    hex = hexagonalGrid.getByPixelCoordinate(event.x, event.y).get();
                } catch (NoSuchElementException ex) {
                    ex.printStackTrace();
                }
                if (hex != null) {

                    if(event.button == 1){
                        if(fixedHexes.contains(hex)){
                            int value = hex.getSatelliteData().get().value;
                            hex.getSatelliteData().get().value = (value + 1)%7 ;
                            if(value == 6) fixedHexes.remove(hex);
                        } else {
                            hex.setSatelliteData(new SatelliteDataImpl());
                            hex.getSatelliteData().get().value++;
                            fixedHexes.add(hex);
                        }
                        System.out.println("Mouse button 1 clicked");
                    } else if(event.button == 3){
                        hiddenHexes.add(hex);
                        System.out.println("Mouse button 3 clicked");
                    }

                    prevSelected = currSelected;
                    currSelected = hex;
                    drawDistance();
                    Maybe<SatelliteDataImpl> dataMaybe = hex.getSatelliteData();
                    SatelliteDataImpl data;
                    if (dataMaybe.isPresent()) {
                        data = dataMaybe.get();
                    } else {
                        data = new SatelliteDataImpl();
                    }
                    if(data.value != 0){
                        data.setSelected(true);
                    } else data.setSelected(false);

                    data.setOpaque(data.isSelected());
                    hex.setSatelliteData(data);
                }
                canvas.redraw();
            }

            private void drawDistance() {
                if (prevSelected != null) {
                    distanceText.setText(hexagonalGridCalculator.calculateDistanceBetween(prevSelected, currSelected) + "");
                }
            }
        });
        canvas.addPaintListener(new PaintListener() {
            Color darkBlue = getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
            Color white = getDisplay().getSystemColor(SWT.COLOR_WHITE);
            Color darkGray = getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
            Color yellow = getDisplay().getSystemColor(SWT.COLOR_YELLOW);
            Color red = getDisplay().getSystemColor(SWT.COLOR_RED);
            Color green = getDisplay().getSystemColor(SWT.COLOR_GREEN);

            public void paintControl(final PaintEvent event) {
                event.gc.setLineWidth(2);
                event.gc.setForeground(darkBlue);
                event.gc.setBackground(white);
                event.gc.fillRectangle(new Rectangle(0, 0, shellWidth, shellHeight));

                final List<Hexagon> lineHexes;
                final boolean canDrawLine = (prevSelected != null || currSelected != null) && currMouseOver != null;
                if (canDrawLine && showLineDrawing) {
                    lineHexes = hexagonalGridCalculator.drawLine(currSelected != null ? currSelected : prevSelected,
                            currMouseOver);
                } else {
                    lineHexes = Collections.emptyList();
                }
                for (Hexagon<SatelliteDataImpl> hexagon : hexagonalGrid.getHexagons()) {
                    Maybe<SatelliteDataImpl> data = hexagon.getSatelliteData();
                    if (data.isPresent() && data.get().isSelected()) {
                        if (showNeighbors) {
                            for (Hexagon hex : hexagonalGrid.getNeighborsOf(hexagon)) {
                                drawNeighborHexagon(event.gc, hex);
                            }
                        }
                        if (showMovementRange) {
                            for (Hexagon hex : hexagonalGridCalculator.calculateMovementRangeFrom(hexagon, movementRange)) {
                                drawMovementRangeHexagon(event.gc, hex);
                            }
                        }
                    }
                    drawEmptyHexagon(event.gc, hexagon);
                }
                for (Hexagon<SatelliteDataImpl> hexagon : hexagonalGrid.getHexagons()) {
                    Maybe<SatelliteDataImpl> data = hexagon.<SatelliteDataImpl>getSatelliteData();
                    if (data.isPresent() && data.get().isSelected()) {
                        drawFilledHexagon(event.gc, hexagon);
                    }
                    if (drawCoordinates) {
                        drawCoordinates(event.gc, hexagon);
                    }
                    if (lineHexes.contains(hexagon)) {
                        drawLineHexagon(event.gc, hexagon);
                    }
                    if (canDrawLine && showVisibilityDrawing) {
                        boolean visible = hexagonalGridCalculator.isVisible(currMouseOver,
                                currSelected != null ? currSelected : prevSelected);
                        if (visible) {
                            drawVisibleHexagon(event.gc, currMouseOver);
                        } else {
                            drawNotVisibleHexagon(event.gc, currMouseOver);
                        }
                    }
                }
            }

            private void drawVisibleHexagon(GC gc, Hexagon hexagon) {
                gc.setBackground(green);
                gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
                int previousLineWidth = gc.getLineWidth();
                gc.setLineWidth(3);
                gc.setForeground(red);
                gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
                gc.setLineWidth(previousLineWidth);
            }

            private void drawNotVisibleHexagon(GC gc, Hexagon hexagon) {
                gc.setBackground(red);
                gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
                int previousLineWidth = gc.getLineWidth();
                gc.setLineWidth(3);
                gc.setForeground(red);
                gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
                gc.setLineWidth(previousLineWidth);
            }

            private void drawNeighborHexagon(GC gc, Hexagon hexagon) {
                gc.setForeground(white);
                gc.setBackground(darkGray);
                gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
                gc.setForeground(darkBlue);
                gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
            }

            private void drawMovementRangeHexagon(GC gc, Hexagon hexagon) {
                gc.setForeground(darkBlue);
                gc.setBackground(yellow);
                gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
                gc.setForeground(darkBlue);
                gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
            }

            private void drawLineHexagon(GC gc, Hexagon hexagon) {
                int previousLineWidth = gc.getLineWidth();
                gc.setLineWidth(3);
                gc.setForeground(red);
                gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
                gc.setLineWidth(previousLineWidth);
            }

            private void drawEmptyHexagon(GC gc, Hexagon hexagon) {
                gc.setForeground(darkBlue);
                gc.setBackground(white);
                gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
            }

            private void drawFilledHexagon(GC gc, Hexagon hexagon) {
                gc.setForeground(white);
                gc.setBackground(darkBlue);
                gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
                gc.setForeground(darkBlue);
                gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
            }

            private void drawCoordinates(GC gc, Hexagon<SatelliteDataImpl> hexagon) {
                int gridX = hexagon.getGridX();
                int gridY = hexagon.getGridY();
                int gridZ = -(gridX + gridY);

                String x = hexagon.getId().split(",")[0];
                String y = hexagon.getId().split(",")[1];
                String value = "0";
                if(hexagon.getSatelliteData().isPresent()){
                    value = hexagon.getSatelliteData().get().value + "";
                }


                gc.setFont(font);
                gc.setForeground(red);
                gc.drawString("x:" + x, (int) hexagon.getCenterX() - fontSize, (int) (hexagon.getCenterY() - fontSize * 2.5), true);
                gc.drawString("y:" + y, (int) hexagon.getCenterX() - fontSize, (int) (hexagon.getCenterY() - fontSize), true);

                gc.drawString("val: " + value, (int) hexagon.getCenterX() - fontSize, (int) (hexagon.getCenterY() + fontSize), true);
//                gc.drawString("x:" + gridX, (int) hexagon.getCenterX() - fontSize, (int) (hexagon.getCenterY() - fontSize * 2.5), true);
//                gc.drawString("y:" + gridY, (int) hexagon.getCenterX() - fontSize, (int) hexagon.getCenterY() - fontSize, true);
//                gc.drawString("z:" + gridZ, (int) hexagon.getCenterX() - fontSize, (int) (hexagon.getCenterY() + fontSize / 3), true);
            }

            private int[] convertToPointsArr(Collection<Point> points) {
                int[] pointsArr = new int[12];
                int idx = 0;
                for (Point point : points) {
                    pointsArr[idx] = (int) Math.round(point.getCoordinateX());
                    pointsArr[idx + 1] = (int) Math.round(point.getCoordinateY());
                    idx += 2;
                }
                return pointsArr;
            }
        });

        // fire it up
        regenerateHexagonGrid(canvas);
    }

    private void generateOutput(){
        for(Hexagon<SatelliteDataImpl> hex : fixedHexes){
            System.out.println("fixed " + hex.getId().split(",")[0] + " " + hex.getId().split(",")[1] + " " + hex.getSatelliteData().get().value);
        }

        for(Hexagon<SatelliteDataImpl> hex : hiddenHexes){
            System.out.println("hidden " + hex.getId().split(",")[0] + " " + hex.getId().split(",")[1]);
        }
    }

    private void regenerateHexagonGrid(Canvas canvas) {
        prevSelected = null;
        currSelected = null;
        FontData fd = canvas.getDisplay().getSystemFont().getFontData()[0];
        fontSize = (int) (radius / 3.5);
        font = new Font(canvas.getDisplay(), fd.getName(), fontSize, SWT.NONE);
        try {
            HexagonalGridBuilder builder = new HexagonalGridBuilder()
                    .setGridWidth(gridWidth)
                    .setGridHeight(gridHeight)
                    .setRadius(radius)
                    .setOrientation(orientation)
                    .setGridLayout(hexagonGridLayout);
            hexagonalGrid = builder.build();
            hexagonalGridCalculator = builder.buildCalculatorFor(hexagonalGrid);
        } catch (IllegalStateException e) {
            final Shell dialog = new Shell(canvas.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            dialog.setLayout(new RowLayout());
            Label msg = new Label(dialog, SWT.NONE);
            msg.setText(e.getMessage());
            final Button ok = new Button(dialog, SWT.PUSH);
            ok.setText("Ok");
            Listener listener = new Listener() {
                @Override
                public void handleEvent(Event event) {
                    dialog.close();
                }
            };
            ok.addListener(SWT.Selection, listener);
            dialog.pack();
            dialog.open();
        }
        canvas.redraw();
    }
}
