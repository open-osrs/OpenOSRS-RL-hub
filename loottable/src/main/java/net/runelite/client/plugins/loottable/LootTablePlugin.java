package net.runelite.client.plugins.loottable;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.loottable.controllers.LootTableController;
import net.runelite.client.ui.ClientToolbar;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "loottable",
	description = "Shows loot table for monsters",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class LootTablePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	private LootTableController lootTableController;

	@Override
	protected void startUp() throws Exception
	{
		lootTableController = new LootTableController(clientToolbar);
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		lootTableController.onMenuOpened(event, client);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		lootTableController.onMenuOptionClicked(event);
	}
}
