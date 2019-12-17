package com.bogdan.puzzle.level

import com.bogdan.puzzle.model.LevelModel

interface LevelController {
    fun loadNextLevel(): LevelModel
    fun loadLevel(id: Int): LevelModel
    fun getCurrentLevel(): LevelModel
    fun resetLevels()
}