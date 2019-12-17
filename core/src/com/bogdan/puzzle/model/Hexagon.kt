package com.bogdan.puzzle.model

/**
 * @param id - "x,y" as a string
 * @param x - x value in cube coordinates
 * @param y - y value in cube coordinates
 * @param value - 0 <= value <= 6
 * */
data class Hexagon(val id: String, val x: Int, val y: Int, val value: Int)