package de.gg.game.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import de.eskalon.commons.lang.Lang;
import de.gg.engine.ui.components.OffsettableTextField;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.UIRefreshEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.misc.PlayerUtils;
import de.gg.game.model.types.PlayerIcon;
import de.gg.game.model.types.ProfessionType;
import de.gg.game.model.types.Religion;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.ui.components.BasicDialog;

public class PlayerLobbyConfigDialog extends BasicDialog {

	private HashMap<Short, LobbyPlayer> players;
	private LobbyPlayer localPlayer;

	private boolean selectedSex;
	private Religion selectedReligion;
	private PlayerIcon selectedIcon;
	private int selectedProfessionIndex;

	private TextField surnameTextField;
	private TextField nameTextField;
	private ImageTextButton sexButton;
	private ImageTextButton religionButton;

	public PlayerLobbyConfigDialog(ProjektGGApplication game, Skin skin) {
		super("Spielerkonfiguration", skin, "big");

		// Create UI elements for player configuration dialog
		BasicDialog iconDialog = new BasicDialog("Wappen", skin, "big");
		BasicDialog professionDialog = new BasicDialog("Profession", skin,
				"big");

		surnameTextField = new OffsettableTextField(" - ", skin, 8);
		nameTextField = new OffsettableTextField(" - ", skin, 8);

		Label surnameLabel = new Label(Lang.get("dialog.player_config.surname"),
				skin);
		Label nameLabel = new Label(Lang.get("dialog.player_config.name"),
				skin);

		sexButton = new ImageTextButton(" - ", skin);
		Label sexLabel = new Label(Lang.get("dialog.player_config.gender"),
				skin);

		religionButton = new ImageTextButton(" - ", skin);
		Label religionLabel = new Label(
				Lang.get("dialog.player_config.religion"), skin);

		ImageTextButton iconButton = new ImageTextButton(
				Lang.get("dialog.player_config.configure"), skin);
		Label iconLabel = new Label(Lang.get("dialog.player_config.icon"),
				skin);

		ImageTextButton professionButton = new ImageTextButton(
				Lang.get("dialog.player_config.configure"), skin);
		Label professionLabel = new Label(
				Lang.get("dialog.player_config.profession"), skin);

		// ImageTextButton skillButton = new
		// ImageTextButton(Lang.get("dialog.player_config.configure"), skin);
		// Label skillLabel = new Label("Fähigkeiten: ", skin);

		ImageTextButton applyButton = new ImageTextButton(
				Lang.get("ui.generic.apply"), skin);
		ImageTextButton discardButton = new ImageTextButton(
				Lang.get("ui.generic.cancel"), skin);

		// Listener for configuration buttons
		sexButton.addListener(new ButtonClickListener(game.getSoundManager()) {
			@Override
			protected void onClick() {
				if (selectedSex) {
					selectedSex = false;
					sexButton.setText(
							Lang.get("dialog.player_config.gender.female"));
				} else {
					selectedSex = true;
					sexButton.setText(
							Lang.get("dialog.player_config.gender.male"));
				}
			}
		});

		religionButton
				.addListener(new ButtonClickListener(game.getSoundManager()) {
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
		iconButton.addListener(new ButtonClickListener(game.getSoundManager()) {
			@Override
			protected void onClick() {
				iconTable.clear();

				// Only currently unused icons can be selected
				List<LobbyPlayer> tmpPlayers = new ArrayList<>(
						players.values());
				tmpPlayers.remove(localPlayer);

				List<PlayerIcon> availableIcons = PlayerUtils
						.getAvailableIcons(tmpPlayers);

				for (int i = 0; i < availableIcons.size(); i++) {
					ImageButton iconIButton = new ImageButton(skin.getDrawable(
							availableIcons.get(i).getIconDrawableName()));
					final int index = i;
					iconIButton.addListener(
							new ButtonClickListener(game.getSoundManager()) {
								@Override
								protected void onClick() {
									selectedIcon = availableIcons.get(index);
									iconDialog.hide();
								}
							});
					iconTable.add(iconIButton).pad(25);
				}

				iconDialog.show(PlayerLobbyConfigDialog.this.getStage());
			}
		});

		Table professionTable = new Table();
		professionButton
				.addListener(new ButtonClickListener(game.getSoundManager()) {
					@Override
					protected void onClick() {
						professionTable.clear();

						// Only currently unused professions can be selected
						List<LobbyPlayer> tmpPlayers = new ArrayList<>(
								players.values());
						tmpPlayers.remove(localPlayer);

						List<Integer> availableProfessions = PlayerUtils
								.getAvailableProfessionIndices(tmpPlayers);

						for (int i = 0; i < availableProfessions.size(); i++) {
							ProfessionType prof = ProfessionType
									.values()[availableProfessions.get(i)];

							ImageButton profButton = new ImageButton(
									skin.getDrawable(prof.getIconFileName()));
							final int index = i;
							profButton.addListener(new ButtonClickListener(
									game.getSoundManager()) {
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
		applyButton
				.addListener(new ButtonClickListener(game.getSoundManager()) {
					@Override
					protected void onClick() {
						localPlayer.setName(nameTextField.getText());
						localPlayer.setSurname(surnameTextField.getText());
						localPlayer.setMale(selectedSex);
						localPlayer.setReligion(selectedReligion);
						localPlayer.setIcon(selectedIcon);
						localPlayer.setProfessionTypeIndex(
								selectedProfessionIndex);

						// To update the ui
						game.getEventBus().post(new UIRefreshEvent());

						// Inform the server
						game.getClient().getActionHandler()
								.changeLocalPlayer(localPlayer);

						PlayerLobbyConfigDialog.this.hide();
					}
				});

		// Discard button
		discardButton
				.addListener(new ButtonClickListener(game.getSoundManager()) {
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
	 * @param players
	 * @param localPlayer
	 */
	public void initUIValues(HashMap<Short, LobbyPlayer> players,
			LobbyPlayer localPlayer) {
		this.players = players;
		this.localPlayer = localPlayer;

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
