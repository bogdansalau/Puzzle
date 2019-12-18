package com.bogdan.puzzle.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver
import com.badlogic.gdx.graphics.Texture
import com.bogdan.puzzle.assets.AssetsConstants.GRIDS_PATH
import com.bogdan.puzzle.assets.AssetsConstants.HEXAGONS_PATH

class Assets {
    private val fileResolver = ResolutionFileResolver(InternalFileHandleResolver(), ResolutionFileResolver.Resolution(1440, 2960, "1440x2960"))
    private val loadedFiles = mutableMapOf<String, String>()
    private val assetManager = AssetManager()



    fun load() {
        // Texture
        loadedFiles[GRID_3] = fileResolver.resolve(GRIDS_PATH + GRID_3).path()
        loadedFiles[GRID_5] = fileResolver.resolve(GRIDS_PATH + GRID_5).path()
        loadedFiles[GRID_7] = fileResolver.resolve(GRIDS_PATH + GRID_7).path()
        loadedFiles[HEXAGON_3] = fileResolver.resolve(HEXAGONS_PATH + HEXAGON_3).path()
        loadedFiles[HEXAGON_5] = fileResolver.resolve(HEXAGONS_PATH + HEXAGON_5).path()
        loadedFiles[HEXAGON_7] = fileResolver.resolve(HEXAGONS_PATH + HEXAGON_7).path()

        assetManager.load(loadedFiles[GRID_3], Texture::class.java)
        assetManager.load(loadedFiles[GRID_5], Texture::class.java)
        assetManager.load(loadedFiles[GRID_7], Texture::class.java)
        assetManager.load(loadedFiles[HEXAGON_3], Texture::class.java)
        assetManager.load(loadedFiles[HEXAGON_5], Texture::class.java)
        assetManager.load(loadedFiles[HEXAGON_7], Texture::class.java)
        assetManager.finishLoading();

    }

    fun getHex3(): Texture {
        return assetManager.get(loadedFiles[HEXAGON_3], Texture::class.java)
    }

    fun getHex5(): Texture {
        return assetManager.get(loadedFiles[HEXAGON_5], Texture::class.java)
    }

    fun getHex7(): Texture {
        return assetManager.get(loadedFiles[HEXAGON_7], Texture::class.java)
    }

    fun getGrid3(): Texture {
        return assetManager.get(loadedFiles[GRID_3], Texture::class.java)
    }

    fun dispose()     {
        assetManager.dispose();
    }

    fun update(): Boolean {
        return assetManager.update();
    }

    companion object{
        private const val HEXAGON_3 = "hex_3.png"
        private const val HEXAGON_5 = "hex_5.png"
        private const val HEXAGON_7 = "hex_7.png"
        private const val GRID_3 = "grid_3.png"
        private const val GRID_5 = "grid_5.png"
        private const val GRID_7 = "grid_7.png"
    }
}