package de.gg.game.ui.screens;

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.gg.game.core.ProjektGGApplication;

public abstract class AbstractGGUIScreen extends AbstractEskalonUIScreen {

	protected ProjektGGApplication application;

	public AbstractGGUIScreen(ProjektGGApplication application) {
		super(application.getWidth(), application.getHeight());
		this.application = application;
	}

	@Override
	protected EskalonApplication getApplication() {
		return application;
	}

	@Override
	public void show() {
		super.show();
		application.getEventBus().register(this);
	}

	@Override
	public void hide() {
		super.hide();
		application.getEventBus().unregister(this);
	}

}
