package com.stardust;

public class StardustPerHourUpdate
{
    private double stardustPerHour;

    public StardustPerHourUpdate(double stardustPerHour)
    {
        this.stardustPerHour = stardustPerHour;
    }

    public double getStardustPerHour()
    {
        return stardustPerHour;
    }

    public void resetStardustPerHour() { stardustPerHour = 0; }
}

