package net.runelite.client.plugins.crowdsourcing.cooking;

import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;

public class CrowdsourcingCooking
{
	private static final int HOSIDIUS_KITCHEN_REGION = 6712;

	@Inject
	private CrowdsourcingManager manager;

	@Inject
	private Client client;

	private int lastGameObjectClicked;

	private boolean hasCookingGauntlets()
	{
		ItemContainer equipmentContainer = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipmentContainer == null)
		{
			return false;
		}

		Item[] items = equipmentContainer.getItems();
		int idx = EquipmentInventorySlot.GLOVES.getSlotIdx();

		if (idx >= items.length)
		{
			return false;
		}

		Item glove = items[idx];
		return glove != null && glove.getId() == ItemID.COOKING_GAUNTLETS;
	}

	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		final String message = event.getMessage();
		// Message prefixes taken from CookingPlugin
		if (message.startsWith("You successfully cook")
			|| message.startsWith("You successfully bake")
			|| message.startsWith("You manage to cook")
			|| message.startsWith("You roast a")
			|| message.startsWith("You cook")
			|| message.startsWith("You accidentally burn")
			|| message.startsWith("You accidentally spoil"))
		{
			boolean inHosidiusKitchen = false;
			Player local = client.getLocalPlayer();
			if (local != null && local.getWorldLocation().getRegionID() == HOSIDIUS_KITCHEN_REGION)
			{
				inHosidiusKitchen = true;
			}

			int cookingLevel = client.getBoostedSkillLevel(Skill.COOKING);
			boolean hasCookingGauntlets = hasCookingGauntlets();
			boolean kourendElite = client.getVar(Varbits.DIARY_KOUREND_ELITE) == 1;
			CookingData data = new CookingData(message, hasCookingGauntlets, inHosidiusKitchen, kourendElite, lastGameObjectClicked, cookingLevel);
			manager.storeEvent(data);
		}
	}

	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		MenuOpcode action = menuOptionClicked.getMenuOpcode();
		if (action == MenuOpcode.ITEM_USE_ON_GAME_OBJECT
			|| action == MenuOpcode.GAME_OBJECT_FIRST_OPTION
			|| action == MenuOpcode.GAME_OBJECT_SECOND_OPTION
			|| action == MenuOpcode.GAME_OBJECT_THIRD_OPTION
			|| action == MenuOpcode.GAME_OBJECT_FOURTH_OPTION
			|| action == MenuOpcode.GAME_OBJECT_FIFTH_OPTION)
		{
			lastGameObjectClicked = menuOptionClicked.getIdentifier();
		}
	}
}