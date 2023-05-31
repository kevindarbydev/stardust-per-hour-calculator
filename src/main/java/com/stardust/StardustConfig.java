package com.stardust;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("stardust")
public interface StardustConfig extends Config
{

	@ConfigItem(
			keyName = "stardustPerHour",
			name = "Stardust per Hour",
			description = "Calculates the stardust per hour",
			position = 1
	)
	default int stardustPerHour(long startTimeMillis, long currentTimeMillis, int startCount, int newCount){
		long elapsedTimeMillis = currentTimeMillis - startTimeMillis;
		double elapsedTimeHours = elapsedTimeMillis / (1000.0 * 60 * 60);
		int stardustReceived = newCount - startCount;

		double stardustPerHour = stardustReceived / elapsedTimeHours;
		return (int) stardustPerHour;
	}
}
