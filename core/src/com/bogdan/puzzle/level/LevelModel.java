package com.bogdan.puzzle.level;

import com.badlogic.gdx.scenes.scene2d.ui.Value;

import java.util.List;

public class LevelModel{
    private int width;
    private int height;
    private int orientation;
    private int layout;
    private List<FixedHex> fixedHexes;
    private List<HiddenHex> hiddenHexes;

    /**
     * @param width - the width of the hexagonal grid
     * @param height - the height of the hexagonal grid
     * @param orientation - 1-flat top; 2-pointy top;
     * @param layout - 1-rectangular; 2-hex; 3-triangle; 4-trapezoid;
     * @param fixedHexes - x-idx; y-idy; z-value, 0 < z < 6
     * @param hiddenHexes - x-idx; y-idy; z-value, 0 < z < 6
     * */
    public LevelModel(int width, int height, int orientation, int layout, List<FixedHex> fixedHexes, List<HiddenHex> hiddenHexes) {
        this.width = width;
        this.height = height;
        this.orientation = orientation;
        this.layout = layout;
        this.fixedHexes = fixedHexes;
        this.hiddenHexes = hiddenHexes;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    List<FixedHex> getFixedHexes() {
        return fixedHexes;
    }

    public void setFixedHexes(List<FixedHex> fixedHexes) {
        this.fixedHexes = fixedHexes;
    }

    List<HiddenHex> getHiddenHexes() {
        return hiddenHexes;
    }

    public void setHiddenHexes(List<HiddenHex> hiddenHexes) {
        this.hiddenHexes = hiddenHexes;
    }
}