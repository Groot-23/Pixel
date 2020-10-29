package me.groot_23.pixel.gui;

import org.bukkit.entity.Player;

public class PixelGuiRunnables {
	/**
	 * Selects Kit when item runnable is executed. <br>
	 * Requires 2 nbt parameters: <br>
	 * 	- ming_kit		(name of kit) <br>
	 * 	- ming_kit_group (name of kit group) <br>
	 * Those parameters are automatically filled in by {@link me.groot_23.pixel.kits.Kit#getDisplayItem(Player) Kit.getDisplayItem(Player)}
	 */
	public static final String KIT_SELECTOR = "ming_kit_selector";
	
	/**
	 * Closes the gui when used / clicked.
	 */
	public static final String GUI_CLOSE = "ming_gui_close";
	
	/**
	 * Selects a team for the player who used it and updates the gui (places player head in gui) <br>
	 * Requires 1 nbt parameter: <br>
	 * - ming_team (The team which should be selected on click) <br>
	 * See {@link me.groot_23.pixel.player.team.TeamHandler#getTeamSelectorInv() TemaHandler} as reference.
	 */
	public static final String TEAM_SELECTOR = "ming_team_selector";
	
	/**
	 * Can only be used on valid skulls. It will teleport you to the skull owner.
	 */
	public static final String TP_TO_PLAYER = "ming_tp_to_player";
	
	/**
	 * Opens the spectator gui to teleport to other players. Uses TP_TO_PLAYER
	 */
	public static final String SPECTATOR_TP = "ming_spectator_tp";
}
