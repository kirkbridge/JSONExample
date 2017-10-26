package com.example.bit.jsondemo;

/**
 * Created by bit on 25/10/17.
 */

public class ListItem {
    private String homeTeam;
    private String awayTeam ;
    private String gameDate;
    private String arena;

    public ListItem()
    {

    }

    public ListItem(String homeTeam, String awayTeam, String gameDate, String arena)
    {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameDate = gameDate;
        this.arena = arena;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public void setArena(String arena) {
        this.arena = arena;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getGameDate() {
        return gameDate;
    }

    public String getArena() {
        return arena;
    }

    @Override
    public String toString()
    {
        return String.format("%s vs %s @%s\n%s",homeTeam, awayTeam, arena, gameDate);
    }
}
