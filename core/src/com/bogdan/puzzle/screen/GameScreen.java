package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bogdan.puzzle.GlobalConstants;
import com.bogdan.puzzle.Puzzle;

import com.bogdan.puzzle.actors.HexTileActor;
import com.bogdan.puzzle.assets.Assets;
import com.bogdan.puzzle.level.LevelController;
import com.bogdan.puzzle.model.Hexagon;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.sqrt;


public class GameScreen implements Screen, GlobalConstants {

    // Rendering
    private SpriteBatch batch = new SpriteBatch();
    private ResolutionFileResolver fileResolver;
    private ObjectSet<Sprite> redDots = new ObjectSet<>();
    private AssetManager assetManager = new AssetManager();
    private Grid grid;
    private Hex hex1;
    private Hex hex2;
    private Hex hex3;
    private Map<String, String> loadedFiles = new HashMap<>();

    // Camera
    private OrthographicCamera camera;

    // Game logic
    private LevelController levelController;
    private Timer timer = new Timer();
    private final float MINIMUM_VIEWPORT_WIDTH = 11.0f;
    private final float VIEWPORT_ASPECT_RATIO = Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
    static final float GRID_WIDTH = 9.0f;
    static final float GRID_HEIGHT = GRID_WIDTH*GRID_3_RATIO;
    HexTileActor hexTileActor1;
    HexTileActor hexTileActor2;
    HexTileActor hexTileActor3;
    Stage stage;

    private Puzzle game;
    public GameScreen(Puzzle game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        stage = new Stage(new ExtendViewport(camera.viewportWidth, camera.viewportHeight), batch);


        // Texture
        batch = new SpriteBatch();
        createRedDots();



        Assets assets = new Assets();
        assets.load();
        TextureRegion hexTextureRegion = new TextureRegion(assets.getHex7());

        hexTileActor1 = new HexTileActor(hexTextureRegion, new Hexagon("1,1", 1, 1, 1), 3);
//        hexTileActor2 = new HexTileActor(new Sprite(assets.getHex3()), new Hexagon("1,2", 1, 2, 2), 3);
//        hexTileActor3 = new HexTileActor(new Sprite(assets.getHex3()), new Hexagon("1,3", 1, 3, 3), 3);
        hexTileActor1.setPosition(0f, 0f);




        stage.addActor(hexTileActor1);

        hex1 = new Hex(new Sprite(assets.getHex3()));
        hex2 = new Hex(new Sprite(assets.getHex3()));
        hex3 = new Hex(new Sprite(assets.getHex3()));
        hex1.setPosition(0,0);
        hex2.setPosition((3.0f/4.0f)*2*HEX_RADIUS_3,(float)sqrt(3)*HEX_RADIUS_3/2);
        hex3.setPosition(0,(float)sqrt(3)*HEX_RADIUS_3);


        // Input processor
//        Gdx.input.setInputProcessor(stage);
    }

    private void createRedDots() {
        for(float i = -5; i < 5; i++) {
            for(float j = -5; j < 5; j++) {
                Sprite dot = new Sprite(new Texture(Gdx.files.internal("red_dot.png")));
                dot.setSize(3f, 3f);
                if(i == 0 || j == 0) dot.setSize(7f, 7f);
                dot.setPosition(i - dot.getWidth()/2, j - dot.getHeight()/2);
                redDots.add(dot);
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clearScreen(0.2f, 0, 0.6f, 1);
//        stage.act(Gdx.graphics.getDeltaTime());
//        stage.draw();
        camera.update();
        batch.setProjectionMatrix(camera.combined); //set the spriteBatch to draw what our stageViewport sees
//
        batch.begin();
//        stage.act();
//        stage.draw();
        hex1.draw(batch);
        hex2.draw(batch);
        hex3.draw(batch);
//        hexTileActor1.draw(batch, 1f);
//        for(Sprite s: redDots) {
//            s.draw(batch);
//        }
        batch.end();
    }

    public static class Hex {

        private final Sprite hexSprite;

        public Hex(Sprite hexSprite) {
            hexSprite.setSize(2*HEX_RADIUS_3, (float)sqrt(3)*HEX_RADIUS_3);
            this.hexSprite = hexSprite;
        }

        public void setPosition(float x, float y) {
            hexSprite.setPosition(x - 0.5f * hexSprite.getWidth(), y - 0.5f * hexSprite.getHeight());
        }

        public void draw(Batch batch) {
            hexSprite.draw(batch);
        }
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
//        stage.setViewport(new ExtendViewport(camera.viewportWidth, camera.viewportHeight));
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
        stage.dispose();
    }
}
