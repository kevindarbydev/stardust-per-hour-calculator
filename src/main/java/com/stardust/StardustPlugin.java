package com.stardust;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Stardust Per Hour Calculator"
)
public class StardustPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private StardustOverlay overlay;
	@Inject
	private StardustConfig config;

	private boolean isCounting;
	private int initialStardustCount;
	private long startTimeMillis;
	public double stardustPerHour = 0;
	private final int STARDUST_ID = 25527;

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
		{
			if (event.getItemContainer().contains(STARDUST_ID))
			{
				int count = event.getItemContainer().count(STARDUST_ID);
				if (!isCounting)
				{
					// Start tracking stardust count and time
					isCounting = true;
					initialStardustCount = count;
					startTimeMillis = System.currentTimeMillis();
				}
				else
				{
					stardustPerHour = stardustPerHour(startTimeMillis, System.currentTimeMillis(), initialStardustCount, count);

					eventBus.post(new StardustPerHourUpdate(stardustPerHour));
				}
			}
		}
	}

	public double stardustPerHour(long startTimeMillis, long currentTimeMillis, int startCount, int newCount){
		long elapsedTimeMillis = currentTimeMillis - startTimeMillis;
		double elapsedTimeHours = elapsedTimeMillis / (1000.0 * 60 * 60);
		int stardustReceived = newCount - startCount;

		return stardustReceived / elapsedTimeHours;
	}

	@Override
	protected void startUp() throws Exception
	{
		// Register the plugin class with the event bus
		eventBus.register(this);

		// Register the overlay class with the event bus
		eventBus.register(overlay);

		// Add the overlay to the overlay manager
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		// Unregister the plugin class from the event bus
		eventBus.unregister(this);

		// Unregister the overlay class from the event bus
		eventBus.unregister(overlay);

		// Remove the overlay from the overlay manager
		overlayManager.remove(overlay);
	}


	@Provides
	StardustConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StardustConfig.class);
	}
}
