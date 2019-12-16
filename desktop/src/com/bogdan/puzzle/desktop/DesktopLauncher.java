package com.bogdan.puzzle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bogdan.puzzle.Puzzle;
import com.bogdan.puzzle.tests.TiledMapBench;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = false;
		config.width = 1440/5;
		config.height = 2960/5;
		config.samples = 3;
		//config.foregroundFPS = 30;
		new LwjglApplication(new Puzzle(), config);
//	    new LwjglApplication(new TiledMapBench(), config);
	}
}
