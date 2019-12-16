package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Timer;
import com.bogdan.puzzle.Constants;
import com.bogdan.puzzle.Puzzle;
import com.bogdan.puzzle.hexagon.HexagonData;
import com.bogdan.puzzle.hud.TutorialHud;
import com.bogdan.puzzle.level.LevelController;
import net.dermetfan.gdx.physics.box2d.PositionController;
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

public class GameScreen implements Screen, Constants {

    // Rendering
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private SpriteBatch batch = new SpriteBatch();
    private FPSLogger fpsLogger = new FPSLogger();
    private OrthographicCamera camera;
    private ResolutionFileResolver fileResolver;
    private Grid grid;
    private ObjectSet<Sprite> redDot = new ObjectSet<>();

    // Game logic
    private ArrayList<Hexagon<HexagonData>> selectedHexagons = new ArrayList<>();
    private ArrayList<Hexagon<HexagonData>> nextPossibleSelection = new ArrayList<>();
    private HexagonalGrid<HexagonData> hexagonalGrid;
    private HexagonalGridCalculator gridCalculator;
    private GameController gameController;
    private LevelController levelController;
    private Timer timer = new Timer();
    private final float MINIMUM_VIEWPORT_WIDTH = 19.0f;
//    private final float VIEWPORT_ASPECT_RATIO = 9.0f/18.5f;
    private final float VIEWPORT_ASPECT_RATIO = Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
    static final float GRID_WIDTH = 9.0f;
    static final float GRID_HEIGHT = GRID_WIDTH*GRID_3X3X3_RATIO;
    private boolean isWon = false;

    private Puzzle game;

    public GameScreen(Puzzle game) {
        this.game = game;
    }

    @Override
    public void show() {

        // Texture
        fileResolver = new ResolutionFileResolver(new InternalFileHandleResolver(), new ResolutionFileResolver.Resolution(1440, 2960, "1440x2960"));
        grid = new Grid(new Sprite(new Texture(fileResolver.resolve("grids/grid_3x3x3.png"))));
        grid.setPosition(0,0);

        batch = new SpriteBatch();

        // Camera
        camera = new OrthographicCamera();



        // Game
        // Create a new game controller and get the grid of the current level
        // The Level controller, on construction, sets the current level to the one mentioned in the preferences
        levelController = new LevelController();
        hexagonalGrid = levelController.getCurrentLevelHexagonalGrid();
        gridCalculator = levelController.getCurrentLevelHexagonalCalculator();
        // Create a new game controller to solve the game logic and the input
        gameController = new GameController();

        boolean first = true;
        for(Hexagon<HexagonData> hex: hexagonalGrid.getHexagons()) {
            if(first) {
                shapeRenderer.translate(0, -(float)hex.getExternalBoundingBox().getHeight()/2, 0);
                shapeRenderer.translate(-gameController.getGridWidth()/2, -gameController.getGridHeight()/2, 0);
                first = false;
            }

            System.out.println(hex.getCenterX() + " " + hex.getCenterY() + " X:" + hex.getGridX() + " Y:" + hex.getGridY());
            Sprite dot = new Sprite(new Texture(fileResolver.resolve("red_dot.png")));
            dot.setSize(7.0f, 7.0f);
            setPosition(dot, (float)hex.getCenterX(), (float)hex.getCenterY());
            redDot.add(dot);
        }



        // Input processor
        Gdx.input.setInputProcessor(gameController);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clearScreen(0.2f, 0, 0.6f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined); //set the spriteBatch to draw what our stageViewport sees
        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.setT
        batch.begin();
        grid.draw(batch);
        batch.end();

        // Draw Hexagonal Grid
        for (Hexagon<HexagonData> hexagon : hexagonalGrid.getHexagons()) {

            // Draw hexagons from the hexagon grid
            Maybe<HexagonData> currHexDataMaybe = hexagon.getSatelliteData();
            if (currHexDataMaybe.isPresent()) {
                HexagonData currHexData = currHexDataMaybe.get();

                // Draw the hexagon only if it is visible
                if (currHexData.isVisible()) {
//                    ScreenUtils.drawCenteredHexagon(shapeRenderer, hexagon, hexagonalGrid);
                    ScreenUtils.drawEmptyHexagon(shapeRenderer, hexagon);
                }
                if (currHexData.isFixed()) {
                    CharSequence neighboursLeftToVisit = currHexData.getValue() - currHexData.getNrSelectedNeighbours() + "";
                    batch.begin();
                    batch.end();
                }
            }


            if (SHOW_EXTERNAL_BOUNDING_BOX) {
                Rectangle rect = hexagon.getExternalBoundingBox();
                ScreenUtils.drawRectangle(shapeRenderer, (float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight(), Color.RED);
            }
            if (SHOW_INTERNAL_BOUNDING_BOX) {
                Rectangle rect = hexagon.getInternalBoundingBox();
                ScreenUtils.drawRectangle(shapeRenderer, (float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight(), Color.RED);
            }
        }

        if (SHOW_GRID_BOUNDING_BOX) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(0, 0, gameController.getGridWidth(), gameController.getGridHeight());
            shapeRenderer.end();
        }
        // Draw a circle at the center of hexagons that can be selected next
        for (Hexagon<HexagonData> hexagon : nextPossibleSelection) {
            ScreenUtils.drawCircle(shapeRenderer, (float) hexagon.getCenterX(), (float) hexagon.getCenterY(), (float)hexagon.getInternalBoundingBox().getWidth()/2, Color.CHARTREUSE);
        }

        // Draw the hexagons that were already selected
        Iterator<Hexagon<HexagonData>> selectedHexagonsIterator = selectedHexagons.iterator();
        Hexagon<HexagonData> prevHexagon = null;
        while (selectedHexagonsIterator.hasNext()) {
            Hexagon<HexagonData> currHexagon = selectedHexagonsIterator.next();
            if (prevHexagon != null) {
                ScreenUtils.drawLine(shapeRenderer,
                        new Vector2((float) prevHexagon.getCenterX(), (float) prevHexagon.getCenterY()),
                        new Vector2((float) currHexagon.getCenterX(), (float) currHexagon.getCenterY()),
                        3,
                        Color.BLACK);
            }
            if (gameController.getLastHexID().getId().equals(currHexagon.getId())) {
                ScreenUtils.drawCircle(shapeRenderer,
                        (float) currHexagon.getCenterX(),
                        (float) currHexagon.getCenterY(),
                        3, Color.GOLD);
            } else if (gameController.getBeforeLastHexID().getId().equals(currHexagon.getId())) {
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

        if (isWon) {
            ScreenUtils.drawLine(shapeRenderer,
                    new Vector2((float) gameController.firstHexID.getCenterX(), (float) gameController.firstHexID.getCenterY()),
                    new Vector2((float) gameController.lastHexID.getCenterX(), (float) gameController.lastHexID.getCenterY()),
                    3,
                    Color.BLACK);
            CharSequence str = "You won!";
//            batch.begin();
//            font.draw(batch, str, 20, 20);
//            batch.end();
        }

        batch.begin();
        for (Sprite s: redDot) {
            s.draw(batch);
        }
        batch.end();
    }

    public static class Grid {

        private final Sprite gridSprite;

        public Grid(Sprite gridSprite) {
            gridSprite.setSize(GRID_WIDTH, GRID_HEIGHT);
            this.gridSprite = gridSprite;
        }

        public void setPosition(float x, float y) {
            gridSprite.setPosition(x - 0.5f * gridSprite.getWidth(), y - 0.5f * gridSprite.getHeight());
        }

        public void draw(Batch batch) {
           gridSprite.draw(batch);
        }
    }

    private class GameController implements InputProcessor {

        private Hexagon<HexagonData> firstHexID = null;
        private Hexagon<HexagonData> beforeLastHexID = null;
        private Hexagon<HexagonData> lastHexID = null;

        private boolean createMode = true;

        // Game coordinates
        private int gameX;
        private int gameY;

        private boolean isDragged = false;
        private Vector2 startResetDragPoint;
        private Vector2 stopResetDragPoint;

        // Grid dimensions
        private float gridWidth;
        private float gridHeight;

        private final int screenWidth = Gdx.graphics.getWidth();
        private final int screenHeight = Gdx.graphics.getHeight();

        private boolean isNextLevelLaunched = false;

        private ArrayList<Hexagon<HexagonData>> currentLevelFixedHexagons;

        GameController() {
            currentLevelFixedHexagons = levelController.getCurrentLevelFixedHexagons();
            initHexagonArrays();
        }

        /**
         * Clear the arrays holding selected and next possible selection Hexagons
         * <p>
         * Bring possible selection array in initial phase
         * <p>
         * Calculate grid boundaries and update gridWidth and gridHeight
         */
        private void initHexagonArrays() {
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
                    if (isAvailable(data)) {
                        nextPossibleSelection.add(hexagon);
                    }
                });

                // Find the grid boundaries
                float rectX = (float) hexagon.getExternalBoundingBox().getX();
                float rectY = (float) hexagon.getExternalBoundingBox().getY();
                float rectWidth = (float) hexagon.getExternalBoundingBox().getWidth();
                float rectHeight = (float) hexagon.getExternalBoundingBox().getHeight();

                if (xMin > rectX) xMin = rectX;
                if (xMax < rectX + rectWidth) xMax = rectX + rectWidth;
                if (yMin > rectY + rectHeight) yMin = rectY + rectHeight;
                if (yMax < rectY) yMax = rectY;
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
            System.out.println("TOUCH UP EVENT");
            if(isDragged) {
                stopResetDragPoint = new Vector2(screenX, screenY);
                isDragged = false;
                float dist = stopResetDragPoint.dst(startResetDragPoint);
                System.out.println("Drag distance:" + dist);
                if ( dist > 2800 ) {
                    launchNextLevel();
                }
            }
            createMode = true;
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            System.out.println("TOUCH DOWN EVENT");
            updateMousePos(screenX, screenY);
            Vector3 gameWorldMouse = camera.unproject(new Vector3(screenX, screenY, 0));

            // Find the hovered hexagon
            Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(hexagonalGrid, (int)gameWorldMouse.x, (int)gameWorldMouse.y);
            if (hex != null) {
                System.out.println("ID:" + hex.getId() + " X:" + hex.getGridX() + " Y:" + hex.getGridY());
                // If a selected hexagon is clicked, backtrack the path to the respective step
                if (selectedHexagons.contains(hex)) {
                    createMode = false;
                    backtrackPath(hex);
                } else {
                    handleInput(hex);
                    updateFixedHexagons();
                    checkWinCondition();
                }
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            // System.out.println("TOUCH DRAGGED EVENT");
            if(!isDragged){
                startResetDragPoint = new Vector2(screenX, screenY);
                isDragged = true;
            }
            updateMousePos(screenX, screenY);
            // Find the hovered hexagon
            Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(hexagonalGrid, gameX, gameY);
            if (hex != null && createMode) {
                handleInput(hex);
                updateFixedHexagons();
                checkWinCondition();
            }
            return false;
        }

        @Override
        public boolean keyDown(int keycode) {
            if (Input.Keys.R == keycode) {
                initHexagonArrays();

                // Clear each hexagon
                // Mark the unfixed ones as not selected
                // Change nr of neighbours of fixed ones
                for (Hexagon<HexagonData> hexagon : hexagonalGrid.getHexagons()) {
                    HexagonData data = hexagon.getSatelliteData().get();
                    // Init fixed hexes
                    if (data.isFixed()) {
                        data.setNrSelectedNeighbours(0);
                    } else if (!data.isVisible()) {
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
        private void handleInput(Hexagon<HexagonData> hexagon) {

            if (hexagon != null && isAvailable(hexagon.getSatelliteData().get())) {
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

        private void handleFirstHexagon(Hexagon<HexagonData> hexagon) {
            // Only proceed if the hexagon can be found in the next possible selection array
            if (nextPossibleSelection.contains(hexagon)) {
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

        private void handleSecondHexagon(Hexagon<HexagonData> hex) {

            // Only proceed if the hexagon can be found in the next possible selection array
            if (nextPossibleSelection.contains(hex)) {
                // Set the clicked hexagon isSelected property as "true"
                hex.getSatelliteData().get().setSelected(true);

                // Update lastHexID and beforeLastHexID to keep the head of the path and the hexagon before the head
                updateHexIDs(hex);

                // Add the clicked hex to the selected hexagons array
                selectedHexagons.add(hex);

                updatePossibleMoves(hex);
            }
        }

        private void handleMultipleHexagons(Hexagon<HexagonData> hex) {
            // Only proceed if the hexagon can be found in the next possible selection array
            if (nextPossibleSelection.contains(hex) && !selectedHexagons.contains(hex)) {
                // Set the clicked hexagon isSelected property as "true"
                hex.getSatelliteData().get().setSelected(true);

                // Update lastHexID and beforeLastHexID to keep the head of the path and the hexagon before the head
                updateHexIDs(hex);

                // Add the clicked hex to the selected hexagons array
                selectedHexagons.add(hex);

                updatePossibleMoves(hex);
            }
        }

        private void updateHexIDs(@NotNull Hexagon<HexagonData> hex) {
            // Update lastHexID to keep the head of the path
            beforeLastHexID = lastHexID;
            lastHexID = hex;
        }

        private void updatePossibleMoves(@NotNull Hexagon<HexagonData> hex) {
            // Update the next possible selection array with the neighbours of the current selection minus the neighbours of the last selection
            nextPossibleSelection.clear();
            Collection<Hexagon<HexagonData>> neighbours = hexagonalGrid.getNeighborsOf(hex);

            // Filter out impossible moves
            neighbours.removeAll(hexagonalGrid.getNeighborsOf(selectedHexagons.get(selectedHexagons.size() - 2)));

            // Remove all the selected hexagons
            neighbours.removeAll(selectedHexagons);

            // Filter out non-null hexagons
            for (Hexagon<HexagonData> h : neighbours) {
                h.getSatelliteData().ifPresent(hexData -> {
                    if (isAvailable(hexData)) {
                        nextPossibleSelection.add(h);
                    }
                });
            }
        }

        private void updatePossibleMovesFirstHex(@NotNull Hexagon<HexagonData> hexagon) {
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

        private void updatePossibleMovesInitialState() {
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

        float getGridWidth() {
            return gridWidth;
        }

        float getGridHeight() {
            return gridHeight;
        }

        /**
         * Counts the selected neighbours of each fixed hexagon and updates the hexagon internal count
         */
        private void updateFixedHexagons() {
            for (Hexagon<HexagonData> hex : currentLevelFixedHexagons) {
                int nrSelected = 0;
                for (Hexagon<HexagonData> nHex : hexagonalGrid.getNeighborsOf(hex)) {
                    if (nHex.getSatelliteData().get().isSelected()) nrSelected++;
                }
                hex.getSatelliteData().get().setNrSelectedNeighbours(nrSelected);
            }
        }

        /**
         * Verifies the state of the fixed hexagons
         *
         * @return true if all the fixed hexagons are satisfied
         */
        private boolean checkGameState() {
            for (Hexagon<HexagonData> hex : currentLevelFixedHexagons) {
                HexagonData data = hex.getSatelliteData().get();
                if (data.getValue() != data.getNrSelectedNeighbours()) return false;
            }
            return true;
        }

        private void checkWinCondition() {
            // Check for win condition (all fixed hexagons are satisfied)
            if (checkGameState()) {
                // Find the hovered hexagon
                Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(hexagonalGrid, gameX, gameY);
                if (hex != null) {
                    // Check if among the hovered hexagon's neighbours is the first selected hexagon
                    for (Hexagon<HexagonData> hexagon : hexagonalGrid.getNeighborsOf(hex)) {
                        if (hexagon.getId().equals(firstHexID.getId())) {
                            isWon = true;
                            if (!isNextLevelLaunched) {
                                isNextLevelLaunched = true;
                                launchNextLevel();
                            }
                        }
                    }
                }
            }
        }

        private void launchNextLevel() {
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    levelController.levelFinished();
                    hexagonalGrid = levelController.getCurrentLevelHexagonalGrid();
                    gridCalculator = levelController.getCurrentLevelHexagonalCalculator();
                    currentLevelFixedHexagons = levelController.getCurrentLevelFixedHexagons();
                    initHexagonArrays();
                    isWon = false;
                    isNextLevelLaunched = false;
                }
            }, 1);
        }

        private void updateMousePos(int screenX, int screenY) {
            gameX = screenX - (int) ((screenWidth - gridWidth) / 2);
            gameY = screenY - (int) ((screenHeight - gridHeight) / 2);
        }

        private void backtrackPath(Hexagon<HexagonData> hexagon) {
            // If a selected hexagon is clicked, cut the path and remove every following selected hexagon
            int hexIndex = selectedHexagons.indexOf(hexagon);
            int size = selectedHexagons.size();

            List<Hexagon<HexagonData>> removedHexagons = selectedHexagons.subList(hexIndex, size);

            // Mark hexagon internal variable isSelected as false
            for (Hexagon<HexagonData> h : removedHexagons) {
                h.getSatelliteData().get().setSelected(false);
            }

            // Remove the hexagons from the selected hexagons list
            selectedHexagons.subList(hexIndex, size).clear();
            if (hexIndex == 0) {
                updatePossibleMovesInitialState();
                firstHexID = null;
                beforeLastHexID = null;
                lastHexID = null;
            } else if (hexIndex == 1) {
                Hexagon<HexagonData> firstHexagon = selectedHexagons.get(0);
                updatePossibleMovesFirstHex(firstHexagon);
                firstHexID = firstHexagon;
                lastHexID = firstHexagon;
                beforeLastHexID = firstHexagon;
            } else if (hexIndex >= 2) {
                Hexagon<HexagonData> newHeadHexagon = selectedHexagons.get(hexIndex - 1);
                updatePossibleMoves(newHeadHexagon);
                lastHexID = newHeadHexagon;
                beforeLastHexID = selectedHexagons.get(hexIndex - 2);
            }
            updateFixedHexagons();
            checkWinCondition();
        }

        // Verifies if the hexagon can be taken into consideration for the game logic algorithms
        private boolean isAvailable(HexagonData data) {
            return !data.isFixed() && data.isVisible();
        }
    }

    @Override
    public void resize (int width, int height) {
        camera.viewportHeight = MINIMUM_VIEWPORT_WIDTH/VIEWPORT_ASPECT_RATIO;
        camera.viewportWidth = MINIMUM_VIEWPORT_WIDTH;
        camera.update();
    }

    private static void setPosition(Sprite s, float x, float y) {
        s.setPosition(x - 0.5f * s.getWidth(), y - 0.5f * s.getHeight());
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
