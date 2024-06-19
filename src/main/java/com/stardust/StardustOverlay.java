package com.stardust;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class StardustOverlay extends Overlay {

    private final Client client;
    private final int STARDUST_ID = 25527;
    private double stardustPerHour;
    private final PanelComponent panelComponent = new PanelComponent();
    DecimalFormat decimalFormat = new DecimalFormat("#");

    @Inject
    private StardustConfig config;

    @Inject
    private StardustOverlay(Client client) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
    }

    @Subscribe
    public void onStardustPerHourUpdate(StardustPerHourUpdate event) {
        stardustPerHour = event.getStardustPerHour();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        String overlayTitle = "Stardust per hour:";

        if (client.getGameState() == GameState.LOGGED_IN) {
            if (client.getItemContainer(InventoryID.INVENTORY).contains(STARDUST_ID)) {

                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(overlayTitle)
                        .color(config.colorConfig()) // update to use color from config
                        .build());

                panelComponent.setPreferredSize(new Dimension(
                        graphics.getFontMetrics().stringWidth(overlayTitle) + 30,
                        0));

                int displayRate = (int) Double.parseDouble(decimalFormat.format(stardustPerHour));

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Dust/Hr:")
                        .right(String.valueOf(displayRate))
                        .build());
            }
        }
        return panelComponent.render(graphics);
    }

    // Complimentary method to reset stardust per hour in overlay
    // fired from resetConfiguration() in StardustPlugin
    public void resetStardustPerHour() {
        stardustPerHour = 0;
    }

}
