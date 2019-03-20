package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.bogdan.puzzle.hexagon.HexagonData;
import org.hexworks.mixite.core.api.*;
import org.hexworks.mixite.core.internal.impl.HexagonImpl;

public class TestScreen implements Screen {

    Hexagon<HexagonData> animationSubject;

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    float angle = 0;
    float size = 0;
    float time = 5;

    boolean pause = true;

    @Override
    public void show() {

        InputController inputController = new InputController();
        Gdx.input.setInputProcessor(inputController);

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        HexagonalGrid<HexagonData> grid = new HexagonalGridBuilder<HexagonData>()
                .setGridWidth(1)
                .setGridHeight(1)
                .setRadius(20)
                .setOrientation(HexagonOrientation.FLAT_TOP)
                .setGridLayout(HexagonalGridLayout.RECTANGULAR)
                .build();

        animationSubject = grid.getHexagons().iterator().next();


        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.updateMatrices();

//        Matrix4 centerTranslationMatrix = new Matrix4().translate((Gdx.graphics.getWidth() - gameController.getGridWidth()) / 2,
//                (Gdx.graphics.getHeight() - gameController.getGridHeight()) / 2, 0);
//
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.setTransformMatrix(centerTranslationMatrix);
//        shapeRenderer.updateMatrices();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clearScreen(0,0,0,1);


        if(!pause){
            angle += 0.1 * time*delta;
            size += 0.01 * time*delta;
        }

        Matrix4 rotationMatrix = new Matrix4().translate(100, 100, 0).scale(size, size, 0).rotate(new Vector3(0, 0, 1), angle);
        shapeRenderer.setTransformMatrix(rotationMatrix);
        shapeRenderer.updateMatrices();
        ScreenUtils.drawEmptyHexagon(shapeRenderer, animationSubject);


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
