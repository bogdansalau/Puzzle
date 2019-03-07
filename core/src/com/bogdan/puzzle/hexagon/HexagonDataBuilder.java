package com.bogdan.puzzle.hexagon;

import com.badlogic.gdx.graphics.Color;

public class HexagonDataBuilder {
    private boolean isSelected = false;
    private boolean isFixed = false;
    private boolean isVisible = true;
    private int value = 0;
    private int nrSelectedNeighbours = 0;

    public HexagonDataBuilder(){}

    public HexagonDataBuilder(HexagonData data){
        isSelected = data.isSelected();
        isFixed = data.isFixed();
        value = data.getValue();
        nrSelectedNeighbours = data.getNrSelectedNeighbours();
    }

    public HexagonDataBuilder setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }

    public HexagonDataBuilder setFixed(boolean fixed) {
        isFixed = fixed;
        return this;
    }

    public HexagonDataBuilder setVisible(boolean visible){
        isVisible = visible;
        return this;
    }

    public HexagonDataBuilder setValue(int value) {
        this.value = value;
        return this;
    }

    public HexagonDataBuilder setNrSelectedNeighbours(int nrSelectedNeighbours) {
        this.nrSelectedNeighbours = nrSelectedNeighbours;
        return this;
    }

    public HexagonData build(){
        return new HexagonData(isSelected, isFixed, isVisible, value, nrSelectedNeighbours);
    }

}
