package com.bogdan.puzzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.bogdan.puzzle.hexagon.HexagonData;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.Point;
import org.hexworks.mixite.core.vendor.Maybe;

import java.awt.*;
import java.util.Collection;

import static java.lang.StrictMath.sqrt;

class ScreenUtils {

    static void clearScreen(float red, float green, float blue, float alpha){
        // Clear Screen
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
    }

    static void drawCenteredHexagon(ShapeRenderer shapeRenderer, Hexagon<HexagonData> hexagon, HexagonalGrid<HexagonData> grid){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.polygon(ScreenUtils.convertToPointsArrWithOffset(hexagon,
                (float)grid.getGridData().getRadius(),
                (float)-hexagon.getExternalBoundingBox().getHeight()/2));
//        shapeRenderer.polygon(ScreenUtils.convertToPointsArr(hexagon));
        shapeRenderer.end();
    }
//(float)2*(9/5)*(float)(sqrt(3)/(float)2))
    static void drawEmptyHexagon(ShapeRenderer shapeRenderer, Hexagon<HexagonData> hexagon) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.polygon(ScreenUtils.convertToPointsArrWithOffset(hexagon, 0, 0));
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

    private static float[] convertToPointsArr(Hexagon<HexagonData> hexagon) {
        Collection<Point> points = hexagon.getPoints();
        float[] pointsArr = new float[12];
        int idx = 0;
        for (Point point : points) {
            pointsArr[idx] = (float) point.getCoordinateX();
            pointsArr[idx + 1] = (float) point.getCoordinateY();
            idx += 2;
        }
        return pointsArr;
    }

    private static float[] convertToPointsArrWithOffset(Hexagon<HexagonData> hexagon, float x, float y) {
        Collection<Point> points = hexagon.getPoints();
        float[] pointsArr = new float[12];
        int idx = 0;
        for (Point point : points) {
            pointsArr[idx] = (float) point.getCoordinateX() - x;
            pointsArr[idx + 1] = (float) point.getCoordinateY() - y;
            idx += 2;
        }
        return pointsArr;
    }

    static Hexagon<HexagonData> getHoveredHex(HexagonalGrid<HexagonData> hexagonalGrid, int screenX, int screenY){
        Maybe<Hexagon<HexagonData>> currMouseOverMaybe = hexagonalGrid.getByPixelCoordinate(screenX, screenY);
        Hexagon<HexagonData> currentHex = null;
        if (currMouseOverMaybe.isPresent()) {
            currentHex = currMouseOverMaybe.get();
        }
        return currentHex;
    }

    static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}
