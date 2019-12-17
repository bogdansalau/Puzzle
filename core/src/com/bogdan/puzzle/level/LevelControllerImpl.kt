package com.bogdan.puzzle.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.bogdan.puzzle.level.LevelConstants.CURRENT_LEVEL
import com.bogdan.puzzle.level.LevelConstants.FIRST_LEVEL_ID
import com.bogdan.puzzle.level.LevelConstants.GAME_PREFERENCES_FILENAME
import com.bogdan.puzzle.model.LevelModel

class LevelControllerImpl: LevelController {

    private val prefs: Preferences = Gdx.app.getPreferences(GAME_PREFERENCES_FILENAME)
    private val levelReader: LevelReader = LevelReader()
    private var currentLevel: LevelModel

    init {
//        if(prefs.contains("currentLevel")){
//            currentLevelId = prefs.getInteger("currentLevel")
//        } else {
//            currentLevelId = FIRST_LEVEL_ID
//            prefs.putInteger("currentLevel", currentLevelId)
//            prefs.flush();
//        }
        currentLevel = levelReader.loadLevel(FIRST_LEVEL_ID)
    }

    override fun loadNextLevel(): LevelModel {
        updatePreferences(currentLevel.id + 1)
        currentLevel = levelReader.loadLevel(currentLevel.id + 1)
        return currentLevel
    }

    override fun loadLevel(id: Int): LevelModel {
        updatePreferences(id)
        currentLevel = levelReader.loadLevel(id)
        return currentLevel
    }

    override fun getCurrentLevel(): LevelModel {
        return currentLevel
    }

    override fun resetLevels() {
        updatePreferences(FIRST_LEVEL_ID)
        currentLevel = levelReader.loadLevel(FIRST_LEVEL_ID)
    }

    private fun updatePreferences(newLevelId: Int){
        prefs.putInteger(CURRENT_LEVEL, newLevelId)
        prefs.flush()
    }
}