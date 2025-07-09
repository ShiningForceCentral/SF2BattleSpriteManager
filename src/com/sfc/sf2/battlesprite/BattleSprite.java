/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite;

import com.sfc.sf2.graphics.Tile;
import java.awt.Color;

/**
 *
 * @author wiz
 */
public class BattleSprite {
    
    public static final int TYPE_ALLY = 0;
    public static final int TYPE_ENEMY = 1;
    
    private int type;
    
    private Tile[][] frames;    
    private Color[][] palettes;
    
    private int animSpeed;    
    private byte statusOffsetX;
    private byte statusOffsetY;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTilesPerRow() {
        return type == TYPE_ALLY ? 12 : 16;
    }

    public Tile[][] getFrames() {
        return frames;
    }

    public void setFrames(Tile[][] frames) {
        this.frames = frames;
    }

    public Color[][] getPalettes() {
        return palettes;
    }

    public void setPalettes(Color[][] palettes) {
        this.palettes = palettes;
    }

    public int getAnimSpeed() {
        return animSpeed;
    }

    public void setAnimSpeed(int animSpeed) {
        this.animSpeed = animSpeed;
    }

    public byte getStatusOffsetX() {
        return statusOffsetX;
    }

    public void setStatusOffsetX(byte statusOffsetX) {
        this.statusOffsetX = statusOffsetX;
    }

    public byte getStatusOffsetY() {
        return statusOffsetY;
    }

    public void setStatusOffsetY(byte statusOffsetY) {
        this.statusOffsetY = statusOffsetY;
    }
}
