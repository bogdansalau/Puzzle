package com.bogdan.puzzle.model

import com.bogdan.puzzle.model.Hexagon

/**
 * @param id - level unique identifier
 * @param size - the width of the hexagonal grid
 * @param layout - 1-hex; 2-rectangular; 3-triangle; 4-trapezoid;
 * @param fixedHexes - x-idx; y-idy; z-value, 0 < z < 6
 * */
data class LevelModel(val id: Int, val size: Int, val layout: Int, val fixedHexes: List<Hexagon>)