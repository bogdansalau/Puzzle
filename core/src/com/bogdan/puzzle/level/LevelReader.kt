package com.bogdan.puzzle.level

import com.badlogic.gdx.Gdx
import com.bogdan.puzzle.exceptions.LevelNotFoundException
import com.bogdan.puzzle.level.LevelConstants.LEVELS_PATH
import com.bogdan.puzzle.model.LevelModel
import com.google.gson.Gson

class LevelReader {
    private var gson: Gson = Gson()

    fun loadLevel(levelId: Int): LevelModel {
        val levelModel: LevelModel
        if (Gdx.files.internal(LEVELS_PATH + "level_" + levelId + ".json").exists()) {

            val handle = Gdx.files.internal(LEVELS_PATH + "level_" + levelId + ".json")
            // Read all the file in a string

            val levelString = handle.readString()
            levelModel = gson.fromJson(levelString, LevelModel::class.java)
        } else {
            throw LevelNotFoundException("Level $levelId not found at ${LEVELS_PATH}level_${levelId}.json")
        }
        return levelModel
    }
}