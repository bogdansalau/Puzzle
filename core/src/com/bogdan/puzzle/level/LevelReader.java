package com.bogdan.puzzle.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import org.hexworks.mixite.core.api.HexagonOrientation;
import org.hexworks.mixite.core.api.HexagonalGridLayout;

import java.util.ArrayList;

import static com.bogdan.puzzle.level.LevelController.LEVELS_PATH;

class LevelReader {

    private Gson gson;

    LevelReader(){
        gson = new Gson();
    }

    LevelModel loadLevel(int levelId){
        LevelModel levelModel = null;

        if(Gdx.files.internal(LEVELS_PATH + "level_" + levelId + ".json").exists()){
            FileHandle handle = Gdx.files.internal(LEVELS_PATH + "level_" + levelId + ".json");
            // Read all the file in a string
            String levelJson = handle.readString();
            levelModel = gson.fromJson(levelJson, LevelModel.class);
        } else {
            System.out.println("Level not found!");
        }

        return levelModel;
    }

}
