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
					isCounting = true; // start tracking stardust count and time
					initialStardustCount = count;
					startTimeMillis = System.currentTimeMillis();
				}
				else
				{
					stardustPerHour = stardustPerHour(startTimeMillis, System.currentTimeMillis(), initialStardustCount, count);

					eventBus.post(new StardustPerHourUpdate(stardustPerHour));
				}
			} else {
				isCounting = false; //inv no longer contains stardust, reset
			}
		}
	}

	public double stardustPerHour(long startTimeMillis, long currentTimeMillis, int startCount, int newCount){
		long elapsedTimeMillis = currentTimeMillis - startTimeMillis;
		double elapsedTimeHours = elapsedTimeMillis / (1000.0 * 60 * 60);
		int stardustReceived = newCount - startCount;

		return stardustReceived / elapsedTimeHours;
	}
	private void resetStardustCount() {
		isCounting = false;
		stardustPerHour = 0;
		overlay.resetStardustPerHour(); // Notify overlay to reset display
	}

	@Override
	protected void startUp() throws Exception
	{
		eventBus.register(this);
		eventBus.register(overlay);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		eventBus.unregister(this);
		eventBus.unregister(overlay);
		resetStardustCount();
		overlayManager.remove(overlay);
	}


	@Provides
	StardustConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StardustConfig.class);
	}
}
