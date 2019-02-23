package com.bogdan.puzzle.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LevelController {

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

        levelReader.loadLevel(currentLevelId);

        currentLevel = new Level(
                levelReader.getGridWidth(),
                levelReader.getGridHeight(),
                levelReader.getRadius(),
                levelReader.getOrientation(),
                levelReader.getLayout(),
                levelReader.getFixedHexes());
    }

    private void updatePreferences(){
        prefs.putInteger("currentLevel", currentLevelId);
        prefs.flush();
    }

    public void levelFinished(){

        currentLevelId++;
        System.out.println(currentLevelId);
        loadNextLevel();
        updatePreferences();
    }

    private void loadNextLevel(){
        levelReader.loadLevel(currentLevelId);

        currentLevel = new Level(
                levelReader.getGridWidth(),
                levelReader.getGridHeight(),
                levelReader.getRadius(),
                levelReader.getOrientation(),
                levelReader.getLayout(),
                levelReader.getFixedHexes());
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}