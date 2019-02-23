package com.bogdan.puzzle.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bogdan.puzzle.hexagon.HexagonData;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.Point;
import org.hexworks.mixite.core.vendor.Maybe;

import java.util.Collection;

class ScreenUtils {

    static void drawEmptyHexagon(ShapeRenderer shapeRenderer, Hexagon<HexagonData> hexagon) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.polygon(ScreenUtils.convertToPointsArr(hexagon));
        shapeRenderer.end();
    }

    static void drawCircle(ShapeRenderer shapeRenderer, float x, float y, int radius, Color color){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.end();
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
