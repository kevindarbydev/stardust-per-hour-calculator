package com.stardust;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("stardust")
public interface StardustConfig extends Config
{
	@ConfigItem(
			keyName = "resetButton",
			name = "Reset Stardust Per Hour",
			description = "Click to reset stardust per hour calculation",
			position = 1
	)
	default Button resetButton() {
		return new Button();
	}
	@ConfigItem(
			keyName = "textColor",
			name = "Text Color",
			description = "Choose the color of the text",
			position = 2
	)
	default Color textColor() {
		return Color.GREEN; // Default color
	}
}
