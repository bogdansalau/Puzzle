package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.bogdan.puzzle.Puzzle;
import com.bogdan.puzzle.hexagon.HexagonData;
import com.bogdan.puzzle.level.LevelController;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.HexagonalGridCalculator;
import org.hexworks.mixite.core.api.Rectangle;
import org.hexworks.mixite.core.vendor.Maybe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen{

    // Settings
    private static final boolean SHOW_EXTERNAL_BOUNDING_BOX = false;
    private static final boolean SHOW_INTERNAL_BOUNDING_BOX = false;
    private static final boolean SHOW_SELECTION_CIRCLE = false;
    private static final boolean SHOW_GRID_BOUNDING_BOX = false;

    // Rendering
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private BitmapFont font = new BitmapFont();
    private SpriteBatch batch = new SpriteBatch();
    private FPSLogger fpsLogger = new FPSLogger();
    private OrthographicCamera camera;

    // Game logic
    private ArrayList<Hexagon<HexagonData>> selectedHexagons = new ArrayList<>();
    private ArrayList<Hexagon<HexagonData>> nextPossibleSelection = new ArrayList<>();
    private HexagonalGrid<HexagonData> hexagonalGrid;
    private HexagonalGridCalculator gridCalculator;
    private GameController gameController;
    private LevelController levelController;
    private Timer timer = new Timer();

    private boolean isWon = false;

    private Puzzle game;

    public GameScreen(Puzzle game){
        this.game = game;
    }

    @Override
    public void show() {

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Create a new game controller and get the grid of the current level
        // The Level controller, on construction, sets the current level to the one mentioned in the preferences
        levelController = new LevelController();
        hexagonalGrid = levelController.getCurrentLevelHexagonalGrid();
        gridCalculator = levelController.getCurrentLevelHexagonalCalculator();

        // Create a new game controller to solve the game logic and the input
        gameController = new GameController();
        Gdx.input.setInputProcessor(gameController);

        centerCamera();

        font = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
        font = new BitmapFont(true);

        font.setColor(Color.RED);
    }

    private void centerCamera(){
        Matrix4 centerTranslationMatrix = new Matrix4().translate((Gdx.graphics.getWidth() - gameController.getGridWidth()) / 2,
                (Gdx.graphics.getHeight() - gameController.getGridHeight()) / 2, 0);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setTransformMatrix(centerTranslationMatrix);
        shapeRenderer.updateMatrices();
        batch.setProjectionMatrix(camera.combined);
        batch.setTransformMatrix(centerTranslationMatrix);
    }

    @Override
    public void render(float delta) {
        // Clear Screen
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw Hexagonal Grid
        for (Hexagon<HexagonData> hexagon : hexagonalGrid.getHexagons()) {

            // Draw hexagons from the hexagon grid
            Maybe<HexagonData> currHexDataMaybe = hexagon.getSatelliteData();
            if(currHexDataMaybe.isPresent()){
                HexagonData currHexData = currHexDataMaybe.get();

                // Draw the hexagon only if it is visible
                if(currHexData.isVisible()){
                    ScreenUtils.drawEmptyHexagon(shapeRenderer, hexagon);
                }
                if(currHexData.isFixed()){
                    CharSequence neighboursLeftToVisit = currHexData.getValue() - currHexData.getNrSelectedNeighbours() + "";
                    batch.begin();
                    font.draw(batch,
                            neighboursLeftToVisit,
                            (int)(hexagon.getCenterX() - font.getXHeight()/2),
                            (int)(hexagon.getCenterY() - font.getCapHeight()/2));
                    batch.end();
                }
            }


            if(SHOW_EXTERNAL_BOUNDING_BOX){
                Rectangle rect = hexagon.getExternalBoundingBox();
                ScreenUtils.drawCircle(shapeRenderer, (float)rect.getX(), (float)rect.getY(), 3, Color.SCARLET);
                ScreenUtils.drawRectangle(shapeRenderer, (float)rect.getX(), (float)rect.getY(), (float)rect.getWidth(),(float) rect.getHeight(), Color.RED);
                batch.begin();
                font.draw(batch, (int)rect.getY() + "", (float)rect.getX() + 5, (float)rect.getY() - 5);
                batch.end();
            }
            if(SHOW_INTERNAL_BOUNDING_BOX){
                Rectangle rect = hexagon.getInternalBoundingBox();
                ScreenUtils.drawRectangle(shapeRenderer, (float)rect.getX(), (float)rect.getY(), (float)rect.getWidth(),(float) rect.getHeight(), Color.RED);
            }
            if(SHOW_SELECTION_CIRCLE){
                ScreenUtils.drawCircle(shapeRenderer, (float)hexagon.getCenterX(), (float)hexagon.getCenterY(), gameController.getSelectionRadius(), Color.FOREST);
            }
        }

        if(SHOW_GRID_BOUNDING_BOX){
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(0, 0, gameController.getGridWidth(), gameController.getGridHeight());
            shapeRenderer.end();
        }


        // Draw a circle at the center of hexagons that can be selected next
        for (Hexagon<HexagonData> hexagon : nextPossibleSelection) {
            ScreenUtils.drawCircle(shapeRenderer, (float) hexagon.getCenterX(), (float) hexagon.getCenterY(), 10, Color.CHARTREUSE);
        }

        // Draw the hexagons that were already selected
        Iterator<Hexagon<HexagonData>> selectedHexagonsIterator = selectedHexagons.iterator();
        Hexagon<HexagonData> prevHexagon = null;
        while(selectedHexagonsIterator.hasNext()) {
            Hexagon<HexagonData> currHexagon = selectedHexagonsIterator.next();
            if(prevHexagon!= null){
                ScreenUtils.drawLine(shapeRenderer,
                        new Vector2((float) prevHexagon.getCenterX(), (float) prevHexagon.getCenterY()),
                        new Vector2((float) currHexagon.getCenterX(), (float) currHexagon .getCenterY()),
                        3,
                        Color.BLACK);
            }
            if(gameController.getLastHexID().getId().equals(currHexagon.getId())){
                ScreenUtils.drawCircle(shapeRenderer,
                        (float) currHexagon.getCenterX(),
                        (float) currHexagon.getCenterY(),
                        3, Color.GOLD);
            } else if(gameController.getBeforeLastHexID().getId().equals(currHexagon.getId())) {
                ScreenUtils.drawCircle(shapeRenderer,
                        (float) currHexagon.getCenterX(),
                        (float) currHexagon.getCenterY(),
                        3, Color.GOLDENROD);
            } else {
                ScreenUtils.drawCircle(shapeRenderer,
                        (float) currHexagon.getCenterX(),
                        (float) currHexagon.getCenterY(),
                        3, Color.BLACK);
            }
            prevHexagon = currHexagon;
        }

        if(isWon){
            ScreenUtils.drawLine(shapeRenderer,
                    new Vector2((float) gameController.firstHexID.getCenterX(), (float) gameController.firstHexID.getCenterY()),
                    new Vector2((float) gameController.lastHexID.getCenterX(), (float) gameController.lastHexID.getCenterY()),
                    3,
                    Color.BLACK);
            CharSequence str = "You won!";
            batch.begin();
            font.draw(batch, str, 20, 20);
            batch.end();
        }
    }

    private class GameController implements InputProcessor{

        private Hexagon<HexagonData> firstHexID = null;
        private Hexagon<HexagonData> beforeLastHexID = null;
        private Hexagon<HexagonData> lastHexID = null;

        private boolean createMode = true;

        // Hexagon will be selected on touchDown only if the input is in within the circle with center at the hex center and radius selectionRadius
        private int selectionRadius = (int) (hexagonalGrid.getGridData().getRadius()*(5.0/6.0));

        // Game coordinates
        private int gameX;
        private int gameY;

        // Grid dimensions
        private float gridWidth;
        private float gridHeight;

        private final int screenWidth = Gdx.graphics.getWidth();
        private final int screenHeight = Gdx.graphics.getHeight();

        private boolean isNextLevelLaunched = false;

        private ArrayList<Hexagon<HexagonData>> currentLevelFixedHexagons;

        GameController(){
            currentLevelFixedHexagons = levelController.getCurrentLevelFixedHexagons();
            initHexagonArrays();
        }
        /**
         * Clear the arrays holding selected and next possible selection Hexagons
         *
         * Bring possible selection array in initial phase
         *
         * Calculate grid boundaries and update gridWidth and gridHeight
         */
        private void initHexagonArrays(){
            selectedHexagons.clear();
            nextPossibleSelection.clear();
            float xMin = Float.MAX_VALUE;
            float xMax = 0;
            float yMin = Float.MAX_VALUE;
            float yMax = 0;

            // Initialise grid; all hexagons are candidates for the next move excepting the fixed ones
            for (Hexagon<HexagonData> hexagon : hexagonalGrid.getHexagons()) {
                // Collect in an array all the non-fixed hexagons
                hexagon.getSatelliteData().ifPresent(data -> {
                    if(isAvailable(data)){
                        nextPossibleSelection.add(hexagon);
                    }
                });

                // Find the grid boundaries
                float rectX = (float) hexagon.getExternalBoundingBox().getX();
                float rectY = (float) hexagon.getExternalBoundingBox().getY();
                float rectWidth = (float) hexagon.getExternalBoundingBox().getWidth();
                float rectHeight = (float) hexagon.getExternalBoundingBox().getHeight();

                if(xMin > rectX) xMin = rectX;
                if(xMax < rectX + rectWidth) xMax = rectX + rectWidth;
                if(yMin > rectY + rectHeight) yMin = rectY + rectHeight;
                if(yMax < rectY) yMax = rectY;
            }

            // Compute grid size
            gridWidth = xMax - xMin;
            gridHeight = yMax - yMin;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }
        @Override
        public boolean keyTyped(char character) {
            return false;
        }
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            createMode = true;
            return false;
        }
        @Override
        public boolean scrolled(int amount) {
            return false;
        }
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            updateMousePos(screenX, screenY);

            // Find the hovered hexagon
            Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(hexagonalGrid, gameX, gameY);
            if(hex != null) {
                if (isInsideSelectionCircle(hex)) {
                    // If a selected hexagon is clicked, backtrack the path to the respective step
                    if(selectedHexagons.contains(hex)){
                        createMode = false;
                        backtrackPath(hex);
                    } else {
                        handleInput(hex);
                        updateFixedHexagons();
                        checkWinCondition();
                    }
                }
            }
            return false;
        }
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            updateMousePos(screenX, screenY);
            // Find the hovered hexagon
            Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(hexagonalGrid, gameX, gameY);
            if(hex != null && createMode) {
                if (isInsideSelectionCircle(hex)) {
                    handleInput(hex);
                    updateFixedHexagons();
                    checkWinCondition();
                }
            }
            return false;
        }
        @Override
        public boolean keyDown(int keycode) {
            if(Input.Keys.R == keycode){
                initHexagonArrays();

                // Clear each hexagon
                // Mark the unfixed ones as not selected
                // Change nr of neighbours of fixed ones
                for (Hexagon<HexagonData> hexagon : hexagonalGrid.getHexagons()) {
                    HexagonData data = hexagon.getSatelliteData().get();
                    // Init fixed hexes
                    if(data.isFixed() ){
                        data.setNrSelectedNeighbours(0);
                    } else if(!data.isVisible()){
                        data.setSelected(false);
                    } else {
                        data.setSelected(false);
                        data.setNrSelectedNeighbours(0);
                    }
                }
                lastHexID = null;
                beforeLastHexID = null;
                firstHexID = null;
                isWon = false;
            }
            return false;
        }
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            updateMousePos(screenX, screenY);
//            System.out.println(gameX + " " + gameY);
            return false;
        }
        /**
         * Check the game state and act accordingly
         */
        private void handleInput(Hexagon<HexagonData> hexagon){

            if(hexagon != null && isAvailable(hexagon.getSatelliteData().get())) {
                if(isInsideSelectionCircle(hexagon)){
                    // Case in which the selected hex is first in the row
                    if (selectedHexagons.size() == 0) {
                        handleFirstHexagon(hexagon);
                    } else if (selectedHexagons.size() == 1) {
                        handleSecondHexagon(hexagon);
                    } else {
                        handleMultipleHexagons(hexagon);
                    }
                }
            }
        }
        private boolean isInsideSelectionCircle(Hexagon<HexagonData> hexagon){
            double centerX = hexagon.getCenterX();
            double centerY = hexagon.getCenterY();

            // Return true if the mouse is in the hexagon selection circle
            return ScreenUtils.distance(centerX, centerY, gameX, gameY) < selectionRadius;
        }
        private void handleFirstHexagon(Hexagon<HexagonData> hexagon){
            // Only proceed if the hexagon can be found in the next possible selection array
            if(nextPossibleSelection.contains(hexagon)){
                // Set the clicked hexagon isSelected property as "true"
                hexagon.getSatelliteData().get().setSelected(true);

                // Update firstHexID and lastHexID to keep the both ends of the path
                firstHexID = hexagon;
                beforeLastHexID = hexagon;
                lastHexID = hexagon;

                // Add the clicked hex to the selected hexagons array
                selectedHexagons.add(hexagon);

                updatePossibleMovesFirstHex(hexagon);
            }
        }
        private void handleSecondHexagon(Hexagon<HexagonData> hex){

            // Only proceed if the hexagon can be found in the next possible selection array
            if(nextPossibleSelection.contains(hex)){
                // Set the clicked hexagon isSelected property as "true"
                hex.getSatelliteData().get().setSelected(true);

                // Update lastHexID and beforeLastHexID to keep the head of the path and the hexagon before the head
                updateHexIDs(hex);

                // Add the clicked hex to the selected hexagons array
                selectedHexagons.add(hex);

                updatePossibleMoves(hex);
            }
        }
        private void handleMultipleHexagons(Hexagon<HexagonData> hex){
            // Only proceed if the hexagon can be found in the next possible selection array
            if(nextPossibleSelection.contains(hex) && !selectedHexagons.contains(hex)){
                // Set the clicked hexagon isSelected property as "true"
                hex.getSatelliteData().get().setSelected(true);

                // Update lastHexID and beforeLastHexID to keep the head of the path and the hexagon before the head
                updateHexIDs(hex);

                // Add the clicked hex to the selected hexagons array
                selectedHexagons.add(hex);

                updatePossibleMoves(hex);
            }
        }
        private void updateHexIDs(@NotNull Hexagon<HexagonData> hex){
            // Update lastHexID to keep the head of the path
            beforeLastHexID = lastHexID;
            lastHexID = hex;
        }
        private void updatePossibleMoves(@NotNull Hexagon<HexagonData> hex){
            // Update the next possible selection array with the neighbours of the current selection minus the neighbours of the last selection
            nextPossibleSelection.clear();
            Collection<Hexagon<HexagonData>> neighbours = hexagonalGrid.getNeighborsOf(hex);

            // Filter out impossible moves
            neighbours.removeAll(hexagonalGrid.getNeighborsOf(selectedHexagons.get(selectedHexagons.size() - 2)));

            // Remove the last selected hexagon as well
            neighbours.remove((selectedHexagons.get(selectedHexagons.size() - 2)));

            // Filter out non-null hexagons
            for (Hexagon<HexagonData> h : neighbours) {
                h.getSatelliteData().ifPresent(hexData -> {
                    if (isAvailable(hexData)) {
                        nextPossibleSelection.add(h);
                    }
                });
            }
        }
        private void updatePossibleMovesFirstHex(@NotNull Hexagon<HexagonData> hexagon){
            // Update the next possible selection array with the neighbours that don't have a null value
            nextPossibleSelection.clear();
            Collection<Hexagon<HexagonData>> neighbours = hexagonalGrid.getNeighborsOf(hexagon);
            for (Hexagon<HexagonData> h : neighbours) {
                h.getSatelliteData().ifPresent(hexData -> {
                    if (isAvailable(hexData)) {
                        nextPossibleSelection.add(h);
                    }
                });
            }
        }
        private void updatePossibleMovesInitialState(){
            nextPossibleSelection.clear();
            for (Hexagon<HexagonData> hexagon : hexagonalGrid.getHexagons()) {
                // Collect in an array all the non-fixed hexagons
                hexagon.getSatelliteData().ifPresent(hexData -> {
                    if (isAvailable(hexData)) {
                        nextPossibleSelection.add(hexagon);
                    }
                });
            }
        }
        Hexagon<HexagonData> getBeforeLastHexID() {
            return beforeLastHexID;
        }
        Hexagon<HexagonData> getLastHexID() {
            return lastHexID;
        }
        int getSelectionRadius() {
            return selectionRadius;
        }
        float getGridWidth() {
            return gridWidth;
        }
        float getGridHeight() {
            return gridHeight;
        }
        /**
         * Counts the selected neighbours of each fixed hexagon and updates the hexagon internal count
         */
        private void updateFixedHexagons(){
            for(Hexagon<HexagonData> hex: currentLevelFixedHexagons){
                int nrSelected = 0;
                for(Hexagon<HexagonData> nHex: hexagonalGrid.getNeighborsOf(hex)){
                    if(nHex.getSatelliteData().get().isSelected()) nrSelected++;
                }
                hex.getSatelliteData().get().setNrSelectedNeighbours(nrSelected);
            }
        }
        /**
         * Verifies the state of the fixed hexagons
         * @return true if all the fixed hexagons are satisfied
         */
        private boolean checkGameState(){
            for(Hexagon<HexagonData> hex: currentLevelFixedHexagons){
                HexagonData data = hex.getSatelliteData().get();
                if(data.getValue() != data.getNrSelectedNeighbours()) return false;
            }
            return true;
        }
        private void checkWinCondition(){
            // Check for win condition (all fixed hexagons are satisfied)
            if(checkGameState()){
                // Find the hovered hexagon
                Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(hexagonalGrid, gameX, gameY);
                if(hex != null){
                    // Check if among the hovered hexagon's neighbours is the first selected hexagon
                    for(Hexagon<HexagonData> hexagon: hexagonalGrid.getNeighborsOf(hex)){
                        if(hexagon.getId().equals(firstHexID.getId())) {
                            isWon = true;
                            if(!isNextLevelLaunched){
                                isNextLevelLaunched = true;
                                launchNextLevel();
                            }
                        }
                    }
                }
            }
        }
        private void launchNextLevel(){
            timer.scheduleTask(new Timer.Task(){
                @Override
                public void run() {
                    levelController.levelFinished();
                    hexagonalGrid = levelController.getCurrentLevelHexagonalGrid();
                    gridCalculator = levelController.getCurrentLevelHexagonalCalculator();
                    currentLevelFixedHexagons = levelController.getCurrentLevelFixedHexagons();
                    initHexagonArrays();
                    centerCamera();
                    isWon = false;
                    isNextLevelLaunched = false;
                }
            }, 1);
        }
        private void updateMousePos(int screenX, int screenY){
            gameX = screenX - (int)((screenWidth - gridWidth)/2);
            gameY = screenY - (int)((screenHeight - gridHeight)/2);
        }
        private void backtrackPath(Hexagon<HexagonData> hexagon){
            // If a selected hexagon is clicked, cut the path and remove every following selected hexagon
            int hexIndex = selectedHexagons.indexOf(hexagon);
            int size = selectedHexagons.size();

            List<Hexagon<HexagonData>> removedHexagons = selectedHexagons.subList(hexIndex, size);

            // Mark hexagon internal variable isSelected as false
            for(Hexagon<HexagonData> h: removedHexagons){
                h.getSatelliteData().get().setSelected(false);
            }

            // Remove the hexagons from the selected hexagons list
            selectedHexagons.subList(hexIndex, size).clear();
            if(hexIndex == 0){
                updatePossibleMovesInitialState();
                firstHexID = null;
                beforeLastHexID = null;
                lastHexID = null;
            } else if(hexIndex == 1){
                Hexagon<HexagonData> firstHexagon = selectedHexagons.get(0);
                updatePossibleMovesFirstHex(firstHexagon);
                firstHexID = firstHexagon;
                lastHexID = firstHexagon;
                beforeLastHexID = firstHexagon;
            } else if(hexIndex >= 2){
                Hexagon<HexagonData> newHeadHexagon = selectedHexagons.get(hexIndex-1);
                updatePossibleMoves(newHeadHexagon);
                lastHexID = newHeadHexagon;
                beforeLastHexID = selectedHexagons.get(hexIndex-2);
            }
            updateFixedHexagons();
            checkWinCondition();
        }

        // Verifies if the hexagon can be taken into consideration for the game logic algorithms
        private boolean isAvailable(HexagonData data){
            return !data.isFixed() && data.isVisible();
        }
    }

    @Override
    public void resize(int width, int height) {

    }
    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void hide() {

    }
    @Override
    public void dispose() {

    }
}
