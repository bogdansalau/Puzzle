package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

class ScreenUtils {

    static void clearScreen(float red, float green, float blue, float alpha){
        // Clear Screen
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
    }

    static void drawEmptyHexagon(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.polygon(null);
//                (float)hexagon.getExternalBoundingBox().getHeight()/2));
//        shapeRenderer.polygon(ScreenUtils.convertToPointsArr(hexagon));
        shapeRenderer.end();
    }

    static void drawCircle(ShapeRenderer shapeRenderer, float x, float y, float radius, Color color){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, y, radius, 100);
        shapeRenderer.end();
    }

    static void drawLine(ShapeRenderer shapeRenderer, Vector2 start, Vector2 end, int lineWidth, Color color) {
        Gdx.gl.glLineWidth(lineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(start, end);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    static void drawRectangle(ShapeRenderer shapeRenderer,float x, float y, float width, float height,Color color){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width,height);
        shapeRenderer.end();
    }

    static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}
