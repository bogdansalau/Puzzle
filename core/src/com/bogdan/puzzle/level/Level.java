package com.bogdan.puzzle.level;

import com.bogdan.puzzle.hexagon.HexagonData;
import com.bogdan.puzzle.hexagon.HexagonDataBuilder;
import org.hexworks.mixite.core.api.*;

import java.util.ArrayList;

public class Level {
    public String levelId;

    private HexagonalGrid<HexagonData> hexagonalGrid;
    private HexagonalGridCalculator gridCalculator;
    private ArrayList<Hexagon<HexagonData>> fixedHexagons;

    Level(int width, int height, int radius, HexagonOrientation orientation, HexagonalGridLayout layout, ArrayList<LevelReader.FixedHex> fixedHexes){
        HexagonalGridBuilder<HexagonData> builder = new HexagonalGridBuilder<HexagonData>()
                .setGridWidth(width)
                .setGridHeight(height)
                .setRadius(radius)
                .setOrientation(orientation)
                .setGridLayout(layout);
        hexagonalGrid = builder.build();
        gridCalculator = builder.buildCalculatorFor(hexagonalGrid);

        // Set SatelliteData for each hex
        fixedHexagons = new ArrayList<>();
        for(Hexagon<HexagonData> hexagon: hexagonalGrid.getHexagons()){
            boolean isFixed = false;

            // Compare each hexagon from the hexagonal grid with the fixed hexagons from the file
            for(LevelReader.FixedHex fixedHex : fixedHexes){

                // If the hexagon is fixed, update satellite data accordingly
                if(hexagon.getId().equals(fixedHex.getId())){
                    hexagon.setSatelliteData(new HexagonDataBuilder().setValue(fixedHex.getValue()).setFixed(true).build());
                    isFixed = true;
                    fixedHexagons.add(hexagon);
                }
            }

            // If the hexagon is not fixed, update with default satellite data
            if(!isFixed){
                hexagon.setSatelliteData(new HexagonDataBuilder().build());
            }
        }
    }

    public HexagonalGrid<HexagonData> getHexagonalGrid() {
        return hexagonalGrid;
    }
    public HexagonalGridCalculator getGridCalculator() {
        return gridCalculator;
    }
    public ArrayList<Hexagon<HexagonData>> getFixedHexagons() {
        return fixedHexagons;
    }
}
