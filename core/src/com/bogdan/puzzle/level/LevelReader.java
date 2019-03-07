package com.bogdan.puzzle.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.hexworks.mixite.core.api.HexagonOrientation;
import org.hexworks.mixite.core.api.HexagonalGridLayout;

import java.util.ArrayList;

class LevelReader {

    private int gridWidth;
    private int gridHeight;
    private int radius;
    private HexagonOrientation orientation;
    private HexagonalGridLayout layout;
    private ArrayList<FixedHex> fixedHexes;
    private ArrayList<HiddenHex> hiddenHexes;

    LevelReader(){

    }

    void loadLevel(int levelId){

        fixedHexes = new ArrayList<>();

        if(Gdx.files.internal("levels/level_" + levelId + ".txt").exists()){
            // Open level with the ID = levelId
            FileHandle handle = Gdx.files.internal("levels/level_" + levelId + ".txt");

            // Read all the file in a string
            String levelDescriptionStr = handle.readString();

            // Split the description in lines
            String[] linesArray = levelDescriptionStr.split("\n");

            String lineIdentifier;
            for(String line : linesArray) {
                if(line.charAt(0) != '%'){
                    String[] splitLine = line.split(" ");
                    lineIdentifier = splitLine[0];
                    handleInputLine(lineIdentifier, splitLine);
                }
            }
        } else {
            System.out.println("Level not found!");
        }
    }

    private void handleInputLine(String identifier, String[] splitLine){
        switch (identifier){
            case "dimension":
                gridWidth = Integer.parseInt(splitLine[1].trim());
                gridHeight = Integer.parseInt(splitLine[2].trim());
                break;
            case "radius":
                radius = Integer.parseInt(splitLine[1].trim());
                break;
            case "orientation":
                if(Integer.parseInt(splitLine[1].trim()) == 1) orientation = HexagonOrientation.FLAT_TOP;
                else orientation = HexagonOrientation.POINTY_TOP;
                break;
            case "layout":
                switch (Integer.parseInt(splitLine[1].trim())){
                    case 1: layout = HexagonalGridLayout.RECTANGULAR; break;
                    case 2: layout = HexagonalGridLayout.HEXAGONAL; break;
                    case 3: layout = HexagonalGridLayout.TRIANGULAR; break;
                    case 4: layout = HexagonalGridLayout.TRAPEZOID; break;
                    default: layout = HexagonalGridLayout.TRAPEZOID; break;
                }
                break;
            case "fixed":
                fixedHexes.add(new FixedHex(
                        Integer.parseInt(splitLine[1].trim()),
                        Integer.parseInt(splitLine[2].trim()),
                        Integer.parseInt(splitLine[3].trim())
                ));
                break;
            case "hidden":
                hiddenHexes.add(new HiddenHex(
                        Integer.parseInt(splitLine[1].trim()),
                        Integer.parseInt(splitLine[2].trim()),
                        Integer.parseInt(splitLine[3].trim())
                ));
        }

    }

    class FixedHex{

        private String id;
        private int idX;
        private int idY;
        private int value;

        FixedHex(int idX, int idY, int value){
            this.idX = idX;
            this.idY = idY;
            this.value = value;
            id = idX+","+idY;
        }

        public String getId() {
            return id;
        }
        public int getIdX() {
            return idX;
        }
        public int getIdY() {
            return idY;
        }
        public int getValue() {
            return value;
        }
    }

    class HiddenHex{

        private String id;
        private int idX;
        private int idY;
        private int value;

        HiddenHex(int idX, int idY, int value){
            this.idX = idX;
            this.idY = idY;
            this.value = value;
            id = idX+","+idY;
        }

        public String getId() {
            return id;
        }
        public int getIdX() {
            return idX;
        }
        public int getIdY() {
            return idY;
        }
        public int getValue() {
            return value;
        }
    }

    public int getGridWidth() {
        return gridWidth;
    }
    public int getGridHeight() {
        return gridHeight;
    }
    public int getRadius() {
        return radius;
    }
    public HexagonOrientation getOrientation() {
        return orientation;
    }
    public HexagonalGridLayout getLayout() {
        return layout;
    }
    public ArrayList<FixedHex> getFixedHexes() {
        return fixedHexes;
    }
    public ArrayList<HiddenHex> getHiddenHexes() {
        return hiddenHexes;
    }
}
