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
            int lineNr = 0;
            for(String line : linesArray) {
//                System.out.println(line);
                if(line.charAt(0) != '%'){
                    switch (lineNr){
                        case 0:
                            String[] widthAndHeight = line.split(" ");
                            //System.out.println(widthAndHeight[0] + " " + widthAndHeight[1]+"|");
                            gridWidth = Integer.parseInt(widthAndHeight[0].trim());
                            gridHeight = Integer.parseInt(widthAndHeight[1].trim());
                            lineNr++;
                            break;
                        case 1:
                            try{
                                radius = Integer.parseInt(line.trim());
                            } catch (Exception e){
                                System.out.println(line);
                            }

                            lineNr++;
                            break;
                        case 2:
                            if(Integer.parseInt(line.trim()) == 1) orientation = HexagonOrientation.FLAT_TOP;
                            else orientation = HexagonOrientation.POINTY_TOP;
                            lineNr++;
                            break;
                        case 3:
                            switch (Integer.parseInt(line.trim())){
                                case 1: layout = HexagonalGridLayout.RECTANGULAR; break;
                                case 2: layout = HexagonalGridLayout.HEXAGONAL; break;
                                case 3: layout = HexagonalGridLayout.TRIANGULAR; break;
                                case 4: layout = HexagonalGridLayout.TRAPEZOID; break;
                                default: layout = HexagonalGridLayout.TRAPEZOID; break;
                            }

                            lineNr++;
                            break;
                        default:
                            String[] fixedHexagonData = line.split(" ");
                            fixedHexes.add(new FixedHex(
                                    Integer.parseInt(fixedHexagonData[0].trim()),
                                    Integer.parseInt(fixedHexagonData[1].trim()),
                                    Integer.parseInt(fixedHexagonData[2].trim())
                            ));
                            lineNr++;
                            break;
                    }
                }
            }
        } else {
            System.out.println("Level not found!");
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
}
