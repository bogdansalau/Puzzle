package com.bogdan.puzzle.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class AssetMan {
    private AssetManager manager;

    public AssetMan() {
        manager = new AssetManager();
    }

    public void loadAllAssets() {
        manager.load(Gdx.files.internal("font.fnt").path(), BitmapFont.class);
        manager.finishLoading();
    }

    public BitmapFont getFont(){
        return manager.get(Gdx.files.internal("font.fnt").path());
    }
}