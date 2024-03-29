package com.stardust;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.client.eventbus.Subscribe;
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
        String overlayTitle = "Stardust/HR:";

        if (client.getGameState() == GameState.LOGGED_IN) {
            if (client.getItemContainer(InventoryID.INVENTORY).contains(STARDUST_ID)) {

                // Build overlay title
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(overlayTitle)
                        .color(Color.GREEN)
                        .build());

                // Set the size of the overlay (width)
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
}
