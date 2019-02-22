package com.bogdan.puzzle.hexagon;

import com.badlogic.gdx.graphics.Color;
import org.hexworks.mixite.core.api.defaults.DefaultSatelliteData;

public class HexagonData extends DefaultSatelliteData {

    // Variables
    private boolean isSelected;
    private int nrSelectedNeighbours;

    // Constants
    private boolean isFixed;
    private int value;


    HexagonData(boolean isSelected, boolean isFixed, int value, int nrSelectedNeighbours){
        super();
        this.isSelected = isSelected;
        this.isFixed = isFixed;
        this.value = value;
        this.nrSelectedNeighbours = nrSelectedNeighbours;
    }

    public boolean isSelected(){
        return isSelected;
    }
    public boolean isFixed(){
        return isFixed;
    }
    public int getValue(){
        return value;
    }
    public int getNrSelectedNeighbours(){
        return nrSelectedNeighbours;
    }
}
