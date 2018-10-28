package de.gg.engine.asset;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

import de.gg.engine.asset.AnnotationAssetManager.AssetLoaderParametersFactory;

public class BitmapFontAssetLoaderParametersFactory
		implements AssetLoaderParametersFactory<BitmapFont> {

	@Override
	public AssetLoaderParameters<BitmapFont> newInstance(String path,
			String params) {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = path;
		font.fontParameters.size = Integer.valueOf(params);
		return font;
	}

}
