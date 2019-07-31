package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bogdan.puzzle.hexagon.HexagonData;
import com.bogdan.puzzle.hexagon.HexagonDataBuilder;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.vendor.Maybe;

public class TestScreen implements Screen {

    private Hexagon<HexagonData> animationSubject;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ShapeRenderer fixedShapeRenderer = new ShapeRenderer();

    private float angle = 0;
    private float size = 0;
    private float time = 1;

    private int height;
    private int width;

    private float t = 0;

    private float x1 = 0;
    private float y1 = 0;

    private float xCenter;
    private float yCenter;

    // Distance from center of a hex to the upper left corner of the hex(the shape renderer uses that corner as a drawing reference)
    private double xOffset;
    private double yOffset;

    private boolean pause = true;
    private boolean animate = false;

    private HexagonalGrid<HexagonData> grid;

    @Override
    public void show() {

        InputController inputController = new InputController();
        Gdx.input.setInputProcessor(inputController);

        height = Gdx.graphics.getHeight();
        width = Gdx.graphics.getWidth();

        OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.position.set(width / 2.0f, height / 2.0f, 0);
        camera.update();
//        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        grid = new HexagonalGridBuilder<HexagonData>()
                .setGridWidth(1)
                .setGridHeight(1)
                .setRadius(50)
                .setOrientation(HexagonOrientation.FLAT_TOP)
                .setGridLayout(HexagonalGridLayout.HEXAGONAL)
                .build();
        for (Hexagon<HexagonData> hexagon : grid.getHexagons()){
            xOffset = hexagon.getCenterX() - hexagon.getExternalBoundingBox().getX();
            yOffset = hexagon.getCenterY() - hexagon.getExternalBoundingBox().getY();
            hexagon.setSatelliteData(new HexagonDataBuilder()
                    .setFixed(false)
                    .setNrSelectedNeighbours(0)
                    .setSelected(false)
                    .setVisible(true).build());
        }

        xCenter = (float)(width/2.0);
        yCenter = (float)(height/2.0);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.updateMatrices();


        shapeRenderer.setProjectionMatrix(camera.combined);
        fixedShapeRenderer.updateMatrices();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clearScreen(0,0,0,1);

//        ScreenUtils.drawCircle(fixedShapeRenderer, x1, y1, 3, Color.BLUE);
        ScreenUtils.drawCircle(fixedShapeRenderer, xCenter, yCenter, 3, Color.BLUE);
        Matrix4 translationMatrix = new Matrix4()
                .translate(100, 100, 0);
        fixedShapeRenderer.setTransformMatrix(translationMatrix);
        fixedShapeRenderer.updateMatrices();

//        fixedShapeRenderer.getProjectionMatrix()

        if(animate){
            ScreenUtils.drawCircle(shapeRenderer,
                    (float)(animationSubject.getCenterX()-grid.getGridData().getRadius()),
                    (float)animationSubject.getCenterY()+(float)animationSubject.getExternalBoundingBox().getHeight()/2,
                    4,
                    Color.BLUE);

            float x = xCenter*t + (1-t)*x1;
            float y = yCenter*t + (1-t)*y1;
            if(t > 1) pause = true;
            if(!pause){
                angle += 1 * time;
                size += 0.2 * time;
                t += 0.01 * time;
            }



            Matrix4 rotationMatrix = new Matrix4()
                    .translate(x - (float)xOffset, y + (float)yOffset, 0);
//                    .scale(size, size, 0);
//                    .rotate(new Vector3(0, 0, 1), angle);
            shapeRenderer.setTransformMatrix(rotationMatrix);
            shapeRenderer.updateMatrices();
            ScreenUtils.drawEmptyHexagon(shapeRenderer, animationSubject);
        }

//         Draw Hexagonal Grid
        for (Hexagon<HexagonData> hexagon : grid.getHexagons()) {

            // Draw hexagons from the hexagon grid
            Maybe<HexagonData> currHexDataMaybe = hexagon.getSatelliteData();
            if (currHexDataMaybe.isPresent()) {
                HexagonData currHexData = currHexDataMaybe.get();

                // Draw the hexagon only if it is visible
                if (currHexData.isVisible()) {
                    ScreenUtils.drawEmptyHexagon(fixedShapeRenderer, hexagon);
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
                    time += 0.01;
                    break;
                case Input.Keys.DOWN:
                    time -= 0.01;
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

            // Transform touch coordinates to screen (or image) coordinates
            screenY = height - 1 - screenY;

            pause = !pause;

            Hexagon<HexagonData> hex = ScreenUtils.getHoveredHex(grid, screenX-100, screenY-100);

            if(hex!=null){
                animationSubject = hex;
                x1 = (float)hex.getCenterX();
                y1 = (float)hex.getCenterY();
                animate = true;
                System.out.println("X: " + x1 + " Y: " + y1);
            }
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

    private class MyActor extends Actor {

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
