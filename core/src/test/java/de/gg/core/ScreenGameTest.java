package de.gg.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import de.gg.ProjektGGUnitTest;
import de.gg.screens.LoadableScreen;
import de.gg.screens.exception.ScreenNotFoundException;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.jodah.concurrentunit.Waiter;

public class ScreenGameTest extends ProjektGGUnitTest {

	private int i = 0;
	private final Waiter waiter = new Waiter();
	private LoadableScreen testScreen;

	@Test
	public void test() throws TimeoutException {
		ScreenGame<LoadableScreen> app = new ScreenGame<>() {
			@Override
			protected void onScreenInitialization(LoadableScreen screen) {
				i++;

				if (screen == testScreen)
					assertEquals(1, i);
				else
					assertEquals(5, i);
			};
		};
		app.create();

		// Asset Manager
		waiter.assertNotNull(app.getAssetManager());

		testScreen = new LoadableScreen() {
			@Override
			public void show() {
				i++;
				waiter.assertEquals(4, i);
				waiter.resume();
			}

			@Override
			protected void onInit(AnnotationAssetManager assetManager) {
				if (assetManager != null) { // Erster Durchlauf
					i++;
					waiter.assertEquals(3, i);
				} else { // Zweiter (manueller) Durchlauf
					i++;
					waiter.assertEquals(12, i);
				}
			}

			@Override
			public void render(float delta) {
			}

			@Override
			public void hide() {
			}

			@Override
			public void resume() {
			}

			@Override
			public void pause() {
				i++;
				waiter.assertEquals(13, i);
			}

			@Override
			public void dispose() {
				i++;
				waiter.assertEquals(14, i);
			}

			@Override
			public void resize(int width, int height) {
			}

			@Override
			public boolean equals(Object obj) {
				return this == obj;
			}
		};

		LoadableScreen test2Screen = new LoadableScreen() {
			@Override
			public void show() {
				i++;
				waiter.assertEquals(7, i);
				waiter.resume();
			}

			@Override
			protected void onInit(AnnotationAssetManager assetManager) {
				if (assetManager != null) { // Zweiter Durchlauf
					i++;
				} else { // Erster (manueller) Durchlauf
					i++;
					waiter.assertEquals(6, i);
				}
			}

			@Override
			public void render(float delta) {
				i++;
			}

			@Override
			public void hide() {
			}

			@Override
			public void resume() {
			}

			@Override
			public void pause() {
				i++;
				waiter.assertEquals(15, i);
			}

			@Override
			public void dispose() {
				i++;
				waiter.assertEquals(16, i);
			}

			@Override
			public void resize(int width, int height) {
			}

			@Override
			public boolean equals(Object obj) {
				return this == obj;
			}
		};

		String screenName = "Test";
		String screen2Name = "Test2";

		// Screen hinzufügen
		app.addScreen(screenName, testScreen);
		i++;
		waiter.assertEquals(2, i);
		waiter.assertEquals(testScreen, app.getScreen(new String(screenName)));
		waiter.assertEquals(null, app.getScreen());

		// Screen pushen
		waiter.assertEquals(testScreen.isLoaded(), false);
		app.pushScreen(new String(screenName));
		waiter.await(500);
		waiter.assertEquals(testScreen.isLoaded(), true);
		waiter.assertEquals(testScreen, app.getScreen());

		// Schon geloadeten Screen pushen
		app.addScreen(screen2Name, test2Screen);
		test2Screen.finishLoading(null);
		app.pushScreen(new String(screen2Name));
		waiter.await(500);
		waiter.assertEquals(7, i);
		waiter.assertEquals(test2Screen.isLoaded(), true);
		waiter.assertEquals(test2Screen, app.getScreen());

		// Screen rendern
		app.render();
		app.render();
		app.render();
		app.render();
		waiter.assertEquals(11, i);

		// Screen manuell laden
		testScreen.finishLoading(null);

		// Exceptions
		assertThrows(IllegalArgumentException.class, () -> {
			app.addScreen("", testScreen);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			app.pushScreen("");
		});

		assertThrows(IllegalArgumentException.class, () -> {
			app.getScreen("");
		});

		assertThrows(ScreenNotFoundException.class, () -> {
			app.getScreen("123");
		});

		assertThrows(ScreenNotFoundException.class, () -> {
			app.pushScreen("123");
		});

		// Dispose
		app.dispose();
	}

}
