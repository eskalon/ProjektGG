package de.gg.screen.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import de.gg.core.ProjektGG;
import de.gg.event.PlayerChangedEvent;
import de.gg.game.type.PlayerIcon;
import de.gg.game.type.ProfessionTypes;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.game.type.Religion;
import de.gg.input.ButtonClickListener;
import de.gg.network.LobbyPlayer;
import de.gg.ui.AnimationlessDialog;
import de.gg.ui.OffsetableTextField;
import de.gg.util.PlayerUtils;

public class PlayerLobbyConfigDialog extends AnimationlessDialog {

	private boolean selectedSex;
	private Religion selectedReligion;
	private PlayerIcon selectedIcon;
	private int selectedProfessionIndex;

	private TextField surnameTextField;
	private TextField nameTextField;
	private ImageTextButton sexButton;
	private ImageTextButton religionButton;

	public PlayerLobbyConfigDialog(ProjektGG game, AssetManager assetManager,
			Skin skin, HashMap<Short, LobbyPlayer> players,
			short localPlayerId) {
		super("Spielerkonfiguration", skin, "window");

		// Create UI elements for player configuration dialog
		AnimationlessDialog iconDialog = new AnimationlessDialog("Wappen", skin,
				"window");
		AnimationlessDialog professionDialog = new AnimationlessDialog(
				"Profession", skin, "window");

		surnameTextField = new OffsetableTextField("StandardSurname", skin, 8);
		nameTextField = new OffsetableTextField("StandardName", skin, 8);

		Label surnameLabel = new Label("Name: ", skin);
		Label nameLabel = new Label("Vorname: ", skin);

		sexButton = new ImageTextButton("StandardSex", skin, "small");
		Label sexLabel = new Label("Geschlecht: ", skin);

		religionButton = new ImageTextButton("StandardReligion", skin, "small");
		Label religionLabel = new Label("Religion: ", skin);

		ImageTextButton iconButton = new ImageTextButton("Anpassen...", skin,
				"small");
		Label iconLabel = new Label("Wappen: ", skin);

		ImageTextButton professionButton = new ImageTextButton("Anpassen...",
				skin, "small");
		Label professionLabel = new Label("Profession: ", skin);

		// ImageTextButton skillButton = new ImageTextButton("Anpassen...",
		// skin, "small");
		// Label skillLabel = new Label("Fähigkeiten: ", skin);

		ImageTextButton applyButton = new ImageTextButton("Übernehmen", skin,
				"small");
		ImageTextButton discardButton = new ImageTextButton("Abbrechen", skin,
				"small");

		// Listener for configuration buttons
		sexButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						if (selectedSex) {
							selectedSex = false;
							sexButton.setText("Weiblich");
						} else {
							selectedSex = true;
							sexButton.setText("Männlich");
						}
					}
				});

		religionButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						if (selectedReligion.equals(Religion.values()[1])) {
							selectedReligion = Religion.values()[0];
							religionButton.setText("Katholisch");
						} else {
							religionButton.setText("Orthodox");
							selectedReligion = Religion.values()[1];
						}
					}
				});

		Table iconTable = new Table();
		iconButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						iconTable.clear();

						// Only currently unused icons can be selected
						List<LobbyPlayer> tmpPlayers = new ArrayList<>(
								players.values());
						tmpPlayers.remove(players.get(localPlayerId));

						List<PlayerIcon> availableIcons = PlayerUtils
								.getAvailableIcons(tmpPlayers);

						for (int i = 0; i < availableIcons.size(); i++) {
							ImageButton iconIButton = new ImageButton(
									skin.getDrawable(availableIcons.get(i)
											.getIconFileName()));
							final int index = i;
							iconIButton.addListener(new ButtonClickListener(
									assetManager, game.getSettings()) {
								@Override
								protected void onClick() {
									selectedIcon = availableIcons.get(index);
									iconDialog.hide();
								}
							});
							iconTable.add(iconIButton).pad(25);
						}

						iconDialog
								.show(PlayerLobbyConfigDialog.this.getStage());
					}
				});

		Table professionTable = new Table();
		professionButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						professionTable.clear();

						// Only currently unused professions can be selected
						List<LobbyPlayer> tmpPlayers = new ArrayList<>(
								players.values());
						tmpPlayers.remove(players.get(localPlayerId));

						List<Integer> availableProfessions = PlayerUtils
								.getAvailableProfessionIndices(tmpPlayers);

						for (int i = 0; i < availableProfessions.size(); i++) {
							ProfessionType prof = ProfessionTypes
									.getByIndex(availableProfessions.get(i));

							ImageButton profButton = new ImageButton(
									skin.getDrawable(prof.getIconFileName()));
							final int index = i;
							profButton.addListener(new ButtonClickListener(
									assetManager, game.getSettings()) {
								@Override
								protected void onClick() {
									selectedProfessionIndex = index;
									professionDialog.hide();
								}
							});
							professionTable.add(profButton).pad(25);
						}

						professionDialog
								.show(PlayerLobbyConfigDialog.this.getStage());
					}
				});

		// Apply button
		applyButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						players.get(localPlayerId)
								.setName(nameTextField.getText());
						players.get(localPlayerId)
								.setSurname(surnameTextField.getText());
						players.get(localPlayerId).setMale(selectedSex);
						players.get(localPlayerId)
								.setReligion(selectedReligion);
						players.get(localPlayerId).setIcon(selectedIcon);
						players.get(localPlayerId).setProfessionTypeIndex(
								selectedProfessionIndex);

						// To update the ui
						game.getEventBus().post(new PlayerChangedEvent(
								localPlayerId, players.get(localPlayerId)));

						// Inform the server
						game.getClient().onLocalPlayerChange(
								players.get(localPlayerId));

						PlayerLobbyConfigDialog.this.hide();
					}
				});

		// Discard button
		discardButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						PlayerLobbyConfigDialog.this.hide();
					}
				});

		// Adding all UI elements
		Table playerConfigurationTable = new Table();
		playerConfigurationTable.add(nameLabel).padBottom(14);
		playerConfigurationTable.add(nameTextField).padBottom(14).row();
		playerConfigurationTable.add(surnameLabel).padBottom(40);
		playerConfigurationTable.add(surnameTextField).padBottom(40).row();
		playerConfigurationTable.add(sexLabel).padBottom(14);
		playerConfigurationTable.add(sexButton).padBottom(14).row();
		playerConfigurationTable.add(religionLabel).padBottom(14);
		playerConfigurationTable.add(religionButton).padBottom(14).row();
		playerConfigurationTable.add(iconLabel).padBottom(14);
		playerConfigurationTable.add(iconButton).padBottom(14).row();
		playerConfigurationTable.add(professionLabel).padBottom(55);
		playerConfigurationTable.add(professionButton).padBottom(55).row();
		// playerConfigurationTable.add(skillLabel).padBottom(55);
		// playerConfigurationTable.add(skillButton).padBottom(55).row();
		playerConfigurationTable.add(applyButton);
		playerConfigurationTable.add(discardButton);

		this.add(playerConfigurationTable).pad(80);

		iconDialog.add(iconTable).pad(20);

		professionDialog.add(professionTable).pad(20);
	}

	/**
	 * Sets up the text for all buttons and textfields in the player
	 * configuration dialog.
	 * 
	 * @param localPlayer
	 */
	public void setLocalPlayerValues(LobbyPlayer localPlayer) {
		selectedSex = localPlayer.isMale();
		selectedReligion = localPlayer.getReligion();
		selectedIcon = localPlayer.getIcon();
		selectedProfessionIndex = localPlayer.getProfessionTypeIndex();

		String sexString = selectedSex ? "Männlich" : "Weiblich";
		String religionString = localPlayer.getReligion()
				.equals(Religion.values()[0]) ? "Katholisch" : "Orthodox";

		sexButton.setText(sexString);
		surnameTextField.setText(localPlayer.getSurname());
		nameTextField.setText(localPlayer.getName());
		religionButton.setText(religionString);
	}

}
