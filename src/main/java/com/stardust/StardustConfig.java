package com.stardust;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;

@ConfigGroup("stardust")
public interface StardustConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "textColorChoice",
			name= "Color Selector",
			description = "Choose the color of the text box"
	)
	default Color colorConfig() { return Color.GREEN; }
}
