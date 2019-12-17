package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Timer;
import com.bogdan.puzzle.Constants;
import com.bogdan.puzzle.Puzzle;

import com.bogdan.puzzle.level.LevelController;


public class GameScreen implements Screen, Constants {

    // Rendering
    private SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;
    private ResolutionFileResolver fileResolver;
    private Grid grid;
    private ObjectSet<Sprite> redDot = new ObjectSet<>();

    // Game logic
    private LevelController levelController;
    private Timer timer = new Timer();
    private final float MINIMUM_VIEWPORT_WIDTH = 11.0f;
    private final float VIEWPORT_ASPECT_RATIO = Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
    static final float GRID_WIDTH = 9.0f;
    static final float GRID_HEIGHT = GRID_WIDTH*GRID_3X3X3_RATIO;

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


        // Create a new game controller to solve the game logic and the input


        // Input processor
//        Gdx.input.setInputProcessor(gameController);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clearScreen(0.2f, 0, 0.6f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined); //set the spriteBatch to draw what our stageViewport sees

        batch.begin();
        grid.draw(batch);
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
