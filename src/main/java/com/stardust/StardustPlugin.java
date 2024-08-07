package com.stardust;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
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
        name = "Stardust Per Hour"
)
public class StardustPlugin extends Plugin {
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
    private boolean isCounting = false;
    private int initialStardustCount = 0;
    private long startTimeMillis = 0;
    public double stardustPerHour = 0;
    private final int STARDUST_ID = 25527;

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY)) {
            if (event.getItemContainer().contains(STARDUST_ID)) {
                int currentCount = event.getItemContainer().count(STARDUST_ID);
                if (!isCounting) {
                    isCounting = true; // start tracking stardust count and time
                    initialStardustCount = currentCount;
                    startTimeMillis = System.currentTimeMillis();
                } else {
                    if (currentCount < initialStardustCount) {
                        return; //return if count goes down, happens if dust is spent at vendor
                    }
                    stardustPerHour = stardustPerHour(startTimeMillis, System.currentTimeMillis(), initialStardustCount, currentCount);

                    eventBus.post(new StardustPerHourUpdate(stardustPerHour));
                }
            } else {
                isCounting = false; // inv no longer contains stardust, reset
            }
        }
    }

    public double stardustPerHour(long startTimeMillis, long currentTimeMillis, int startCount, int newCount) {
        long elapsedTimeMillis = currentTimeMillis - startTimeMillis;
        double elapsedTimeHours = elapsedTimeMillis / (1000.0 * 60 * 60);
        int stardustReceived = newCount - startCount;

        return stardustReceived / elapsedTimeHours;
    }

    private void resetStardustCount() {
        isCounting = false;
        stardustPerHour = 0;
        // Notify overlay to reset to 0 right away
        overlay.resetStardustPerHour(); //rather than wait for next update
    }

    @Override
    protected void startUp() throws Exception {
        eventBus.register(this);
        eventBus.register(overlay);
        overlayManager.add(overlay);
    }

    @Override
    public void resetConfiguration() {
        resetStardustCount();
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(this);
        eventBus.unregister(overlay);
        resetStardustCount();
        overlayManager.remove(overlay);
    }

    @Provides
    StardustConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(StardustConfig.class);
    }
}
