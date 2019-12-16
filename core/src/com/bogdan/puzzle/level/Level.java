package com.bogdan.puzzle.level;

import com.bogdan.puzzle.hexagon.HexagonData;
import com.bogdan.puzzle.hexagon.HexagonDataBuilder;
import com.bogdan.puzzle.model.FixedHex;
import com.bogdan.puzzle.model.HiddenHex;
import org.hexworks.mixite.core.api.*;

import java.util.ArrayList;

public class Level {
    public String levelId;

    private HexagonalGrid<HexagonData> hexagonalGrid;
    private HexagonalGridCalculator gridCalculator;
    private ArrayList<Hexagon<HexagonData>> fixedHexagons;

    Level(LevelModel levelModel){
        HexagonalGridBuilder<HexagonData> builder = new HexagonalGridBuilder<HexagonData>()
                .setGridWidth(levelModel.getWidth())
                .setGridHeight(levelModel.getHeight())
                .setRadius(levelModel.getHeight() == 3 ? (float)9/5 : (float) 9/8)
                .setOrientation(intToHexOrientation(levelModel.getOrientation()))
                .setGridLayout(intToHexGridLayout(levelModel.getLayout()));
        hexagonalGrid = builder.build();
        gridCalculator = builder.buildCalculatorFor(hexagonalGrid);

        // Set SatelliteData for each hex
        fixedHexagons = new ArrayList<>();

        for(Hexagon<HexagonData> hexagon: hexagonalGrid.getHexagons()){
            boolean isFixed = false;
            boolean isVisible = true;

            // Compare each hexagon from the hexagonal grid with the fixed hexagons from the file
            for(FixedHex fixedHex : levelModel.getFixedHexes()){
                  // If the hexagon is fixed, update satellite data accordingly
                if(hexagon.getId().equals(fixedHex.getId())){
                    hexagon.setSatelliteData(new HexagonDataBuilder()
                            .setValue(fixedHex.getValue())
                            .setFixed(true)
                            .setVisible(false)
                            .build());
                    isFixed = true;
                    fixedHexagons.add(hexagon);
                }
            }

            // Only check if it is hidden if it is not fixed
            if(!isFixed && !levelModel.getHiddenHexes().isEmpty()){
                // Compare each hexagon from the hexagonal grid with the hidden hexagons from the file
                for(HiddenHex hiddenHex : levelModel.getHiddenHexes()){
                    // If the hexagon is hidden, update satellite data accordingly
                    if(hexagon.getId().equals(hiddenHex.getId())){
                        hexagon.setSatelliteData(new HexagonDataBuilder()
                                .setVisible(false)
                                .build());
                        isVisible = false;
                    }
                }
            }
            // If the hexagon is not fixed, update with default satellite data
            if(!isFixed && isVisible){
                hexagon.setSatelliteData(new HexagonDataBuilder().build());
            }
        }
    }

    private HexagonOrientation intToHexOrientation(int hexOrientation){
        if (hexOrientation == 1) return HexagonOrientation.FLAT_TOP;
        else return HexagonOrientation.POINTY_TOP;
    }

    private HexagonalGridLayout intToHexGridLayout(int hexLayout){
        switch(hexLayout){
            case 1: return HexagonalGridLayout.RECTANGULAR;
            case 2: return HexagonalGridLayout.HEXAGONAL;
            case 3: return HexagonalGridLayout.TRIANGULAR;
            case 4: return HexagonalGridLayout.TRAPEZOID;
            default: return HexagonalGridLayout.HEXAGONAL;
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
