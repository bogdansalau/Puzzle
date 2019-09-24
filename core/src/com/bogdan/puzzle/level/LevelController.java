package com.bogdan.puzzle.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bogdan.puzzle.hexagon.HexagonData;
import org.hexworks.mixite.core.api.Hexagon;
import org.hexworks.mixite.core.api.HexagonalGrid;
import org.hexworks.mixite.core.api.HexagonalGridCalculator;

import java.util.ArrayList;

public class LevelController {

    static final String LEVELS_PATH = "levels/";
    private static final int FIRST_LEVEL_ID = 1;

    private int currentLevelId;
    private Level currentLevel;
    private Preferences prefs;
    private LevelReader levelReader = new LevelReader();

    public LevelController(){
        prefs = Gdx.app.getPreferences("Puzzle_level_preferences");
        if(prefs.contains("currentLevel")){
            currentLevelId = prefs.getInteger("currentLevel");
        } else {
            currentLevelId = FIRST_LEVEL_ID;
            prefs.putInteger("currentLevel", currentLevelId);
            prefs.flush();
        }

        LevelModel levelModel = levelReader.loadLevel(1);

        currentLevel = new Level(levelModel);
    }

    private void updatePreferences(){
        prefs.putInteger("currentLevel", currentLevelId);
        prefs.flush();
    }

    public void levelFinished(int level){
        currentLevelId = level;
        System.out.println(currentLevelId);
        loadNextLevel();
        updatePreferences();
    }

    public void levelFinished(){
        currentLevelId++;
        System.out.println(currentLevelId);
        loadNextLevel();
        updatePreferences();
    }

    public void loadNextLevel(){
        LevelModel levelModel = levelReader.loadLevel(currentLevelId);

        currentLevel = new Level(levelModel);
    }

    public void resetLevels() {
        prefs.putInteger("currentLevel", FIRST_LEVEL_ID);
        prefs.flush();
    }

    public HexagonalGrid<HexagonData> getCurrentLevelHexagonalGrid() {
        return currentLevel.getHexagonalGrid();
    }
    public HexagonalGridCalculator getCurrentLevelHexagonalCalculator() {
        return currentLevel.getGridCalculator();
    }
    public ArrayList<Hexagon<HexagonData>> getCurrentLevelFixedHexagons(){
        return currentLevel.getFixedHexagons();
    }
}