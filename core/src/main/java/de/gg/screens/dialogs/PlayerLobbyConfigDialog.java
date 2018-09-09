package de.gg.screens.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import de.gg.core.ProjektGG;
import de.gg.events.PlayerChangedEvent;
import de.gg.game.types.PlayerIcon;
import de.gg.game.types.ProfessionType;
import de.gg.game.types.Religion;
import de.gg.input.ButtonClickListener;
import de.gg.lang.Lang;
import de.gg.network.LobbyPlayer;
import de.gg.ui.components.AnimationlessDialog;
import de.gg.ui.components.OffsetableTextField;
import de.gg.utils.PlayerUtils;

public class PlayerLobbyConfigDialog extends AnimationlessDialog {

	private boolean selectedSex;
	private Religion selectedReligion;
	private PlayerIcon selectedIcon;
	private int selectedProfessionIndex;

	private TextField surnameTextField;
	private TextField nameTextField;
	private ImageTextButton sexButton;
	private ImageTextButton religionButton;

	public PlayerLobbyConfigDialog(ProjektGG game, Sound buttonClickSound,
			Skin skin, HashMap<Short, LobbyPlayer> players,
			short localPlayerId) {
		super("Spielerkonfiguration", skin, "window");

		// Create UI elements for player configuration dialog
		AnimationlessDialog iconDialog = new AnimationlessDialog("Wappen", skin,
				"window");
		AnimationlessDialog professionDialog = new AnimationlessDialog(
				"Profession", skin, "window");

		surnameTextField = new OffsetableTextField(" - ", skin, 8);
		nameTextField = new OffsetableTextField(" - ", skin, 8);

		Label surnameLabel = new Label(Lang.get("dialog.player_config.surname"),
				skin);
		Label nameLabel = new Label(Lang.get("dialog.player_config.name"),
				skin);

		sexButton = new ImageTextButton(" - ", skin, "small");
		Label sexLabel = new Label(Lang.get("dialog.player_config.gender"),
				skin);

		religionButton = new ImageTextButton(" - ", skin, "small");
		Label religionLabel = new Label(
				Lang.get("dialog.player_config.religion"), skin);

		ImageTextButton iconButton = new ImageTextButton("Anpassen...", skin,
				"small");
		Label iconLabel = new Label(Lang.get("dialog.player_config.icon"),
				skin);

		ImageTextButton professionButton = new ImageTextButton("Anpassen...",
				skin, "small");
		Label professionLabel = new Label(
				Lang.get("dialog.player_config.profession"), skin);

		// ImageTextButton skillButton = new ImageTextButton("Anpassen...",
		// skin, "small");
		// Label skillLabel = new Label("Fähigkeiten: ", skin);

		ImageTextButton applyButton = new ImageTextButton(
				Lang.get("ui.generic.apply"), skin, "small");
		ImageTextButton discardButton = new ImageTextButton(
				Lang.get("ui.generic.cancel"), skin, "small");

		// Listener for configuration buttons
		sexButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						if (selectedSex) {
							selectedSex = false;
							sexButton.setText(Lang
									.get("dialog.player_config.gender.female"));
						} else {
							selectedSex = true;
							sexButton.setText(Lang
									.get("dialog.player_config.gender.male"));
						}
					}
				});

		religionButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						if (selectedReligion.equals(Religion.values()[1])) {
							selectedReligion = Religion.values()[0];
							religionButton
									.setText(Lang.get(Religion.values()[0]));
						} else {
							religionButton
									.setText(Lang.get(Religion.values()[1]));
							selectedReligion = Religion.values()[1];
						}
					}
				});

		Table iconTable = new Table();
		iconButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
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
									buttonClickSound, game.getSettings()) {
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
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
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
							ProfessionType prof = ProfessionType
									.values()[availableProfessions.get(i)];

							ImageButton profButton = new ImageButton(
									skin.getDrawable(
											prof.getData().getIconFileName()));
							final int index = i;
							profButton.addListener(new ButtonClickListener(
									buttonClickSound, game.getSettings()) {
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
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
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
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
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

		String sexString = selectedSex
				? Lang.get("dialog.player_config.gender.male")
				: Lang.get("dialog.player_config.gender.female");
		String religionString = Lang.get(localPlayer.getReligion());

		sexButton.setText(sexString);
		surnameTextField.setText(localPlayer.getSurname());
		nameTextField.setText(localPlayer.getName());
		religionButton.setText(religionString);
	}

}
