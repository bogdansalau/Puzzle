package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.bogdan.puzzle.hexagon.HexagonData;
import com.bogdan.puzzle.hexagon.HexagonDataBuilder;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.internal.impl.HexagonImpl;
import org.hexworks.mixite.core.vendor.Maybe;

public class TestScreen implements Screen {

    private Hexagon<HexagonData> animationSubject;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ShapeRenderer fixedShapeRenderer = new ShapeRenderer();

    private float angle = 0;
    private float size = 0;
    private float time = 0.02f;

    private int height;
    private int width;

    private float t = 0;

    private float x1 = 100;
    private float y1 = 100;

    private float x2;
    private float y2;

    private boolean pause = true;
    private boolean animate = false;

    private HexagonalGrid<HexagonData> grid;

    @Override
    public void show() {

        InputController inputController = new InputController();
        Gdx.input.setInputProcessor(inputController);

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        grid = new HexagonalGridBuilder<HexagonData>()
                .setGridWidth(3)
                .setGridHeight(3)
                .setRadius(20)
                .setOrientation(HexagonOrientation.FLAT_TOP)
                .setGridLayout(HexagonalGridLayout.HEXAGONAL)
                .build();
        for (Hexagon<HexagonData> hexagon : grid.getHexagons()){
            hexagon.setSatelliteData(new HexagonDataBuilder()
                    .setFixed(false)
                    .setNrSelectedNeighbours(0)
                    .setSelected(false)
                    .setVisible(true).build());
        }


        height = Gdx.graphics.getHeight();
        width = Gdx.graphics.getWidth();

        x2 = (float)(width/2.0);
        y2 = (float)(height/2.0);




        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.updateMatrices();
        fixedShapeRenderer.setProjectionMatrix(camera.combined);
        fixedShapeRenderer.updateMatrices();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clearScreen(0,0,0,1);

//        ScreenUtils.drawCircle(fixedShapeRenderer, x1, y1, 3, Color.BLUE);
//        ScreenUtils.drawCircle(fixedShapeRenderer, x2, y2, 3, Color.BLUE);


        if(animate){
            ScreenUtils.drawCircle(shapeRenderer,
                    (float)(animationSubject.getCenterX()-grid.getGridData().getRadius()),
                    (float)animationSubject.getCenterY()+(float)animationSubject.getExternalBoundingBox().getHeight()/2,
                    4,
                    Color.BLUE);

            float x = x2*t + (1-t)*x1;
            float y = y2*t + (1-t)*y1;

            if(!pause){
                angle += 1 * time;
                size += 0.2 * time;
                if(t > 1) pause = true;
                t += 0.0001;
            }

            Matrix4 rotationMatrix = new Matrix4()
                    .translate(x, y, 0)
                    .scale(size, size, 0)
                    .rotate(new Vector3(0, 0, 1), angle);
            shapeRenderer.setTransformMatrix(rotationMatrix);
            shapeRenderer.updateMatrices();
            ScreenUtils.drawCenteredHexagon(shapeRenderer, animationSubject, grid);
        }




        // Draw Hexagonal Grid
        for (Hexagon<HexagonData> hexagon : grid.getHexagons()) {

            // Draw hexagons from the hexagon grid
            Maybe<HexagonData> currHexDataMaybe = hexagon.getSatelliteData();
            if (currHexDataMaybe.isPresent()) {
                HexagonData currHexData = currHexDataMaybe.get();

                // Draw the hexagon only if it is visible
                if (currHexData.isVisible()) {
                    ScreenUtils.drawEmptyHexagon(shapeRenderer, hexagon);
                }
            }
        }
    }

    private class InputController implements InputProcessor{

        @Override
        public boolean keyDown(int keycode) {
            switch(keycode){
                case Input.Keys.SPACE:
                    pause = !pause;
                    break;
                case Input.Keys.UP:
                    time++;
                    break;
                case Input.Keys.DOWN:
                    time--;
                    break;
            }
            return false;
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
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {


            Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(grid, screenX, screenY);
            animationSubject = hex;
            x1 = (float)hex.getCenterX();
            y1 = (float)hex.getCenterY();
            animate = true;
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
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
