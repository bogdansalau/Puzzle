package com.bogdan.puzzle.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.bogdan.puzzle.GlobalConstants.HEX_RADIUS_3
import com.bogdan.puzzle.model.Hexagon
import kotlin.math.sqrt


class HexTileActor(private val texture: TextureRegion, val hexagon: Hexagon, val gridSize: Int): Actor() {

    init {
        setBounds(x, y, texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
//        texture.setSize(HEX_RADIUS_3*2, sqrt(3.0f)*(HEX_RADIUS_3))
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, buttons: Int): Boolean {
                println("Touched$name")
                isVisible = false
                return true
            }
        })
    }

    // Implement the full form of draw() so we can handle rotation and scaling.
    override fun draw(batch: Batch, alpha: Float) {
        batch.draw(texture, x, y, originX, originY, width, height,
                scaleX, scaleY, rotation);
    }

    override fun setPosition(x: Float, y: Float) {
        this.x = x - 0.5f * texture.regionWidth
        this.y = y - 0.5f * texture.regionHeight
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (!this.isVisible || this.touchable == Touchable.disabled) return null
        val circle = Circle(width / 2, height / 2, HEX_RADIUS_3)
        return if (circle.contains(x, y)) this else null
    }
}