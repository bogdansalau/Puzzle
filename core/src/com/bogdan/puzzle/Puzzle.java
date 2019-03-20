package com.bogdan.puzzle;

import com.badlogic.gdx.Game;
import com.bogdan.puzzle.screen.GameScreen;
import com.bogdan.puzzle.screen.TestScreen;

public class Puzzle extends Game {

	@Override
	public void create () {
//        this.setScreen(new GameScreen(this));
		this.setScreen(new TestScreen());
	}
}
