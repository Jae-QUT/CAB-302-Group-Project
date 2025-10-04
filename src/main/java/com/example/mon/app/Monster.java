package com.example.mon.app;

public class Monster {
    private String name;
    private String spritePath;
    private int maxHp;
    private int currentHp;

    public Monster(String name, String spritePath, int maxHp) {
        this.name = name;
        this.spritePath = spritePath;
        this.maxHp = maxHp;
        this.currentHp = maxHp; // start full HP
    }

    public String getName() { return name; }
    public String getSpritePath() { return spritePath; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int hp) { this.currentHp = Math.max(0, Math.min(maxHp, hp)); }
}
