package com.bogdan.puzzle;

import com.badlogic.gdx.Game;
import com.bogdan.puzzle.screen.GameScreen;

public class Puzzle extends Game {

	@Override
	public void create () {
        this.setScreen(new GameScreen(this));
	}
}
