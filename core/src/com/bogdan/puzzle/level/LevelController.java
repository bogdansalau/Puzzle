package com.bogdan.puzzle.level;

public class LevelController {

    private Level currentLevel;

    public LevelController(){
        LevelReader levelReader = new LevelReader();
        levelReader.loadLevel(2);
        currentLevel = new Level(
                levelReader.getGridWidth(),
                levelReader.getGridHeight(),
                levelReader.getRadius(),
                levelReader.getOrientation(),
                levelReader.getLayout(),
                levelReader.getFixedHexes()
        );
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
