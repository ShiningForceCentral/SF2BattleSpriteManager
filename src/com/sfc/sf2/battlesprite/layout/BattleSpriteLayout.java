/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.layout;

import com.sfc.sf2.battlesprite.BattleSprite;
import com.sfc.sf2.graphics.Tile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author wiz
 */
public class BattleSpriteLayout extends JPanel {
        
    private BattleSprite battleSprite;
    private int currentPalette;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   
        g.drawImage(buildImage(), 0, 0, this);       
    }
    
    public BufferedImage buildImage(){
        BufferedImage image = buildImage(battleSprite, currentPalette);
        setSize(image.getWidth(), image.getHeight());
        return image;
    }
    
    public static BufferedImage buildImage(BattleSprite battleSprite, int paletteIndex) {
        
        int tilesPerRow = battleSprite.getTilesPerRow();
        int frames = battleSprite.getFrames().length;
        int imageWidth = tilesPerRow*8;
        int imageHeight = frames*12*8;
        BufferedImage image;
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        Color[] palette = battleSprite.getPalettes()[paletteIndex];
        for(int f = 0; f < frames; f++) {
            Tile[] frameTiles = battleSprite.getFrames()[f];
            for(int t = 0; t < frameTiles.length; t++) {
                int x = (t%tilesPerRow)*8;
                int y = (f*12 + t/tilesPerRow)*8;
                frameTiles[t].setPalette(palette);
                frameTiles[t].clearIndexedColorImage();
                graphics.drawImage(frameTiles[t].getIndexedColorImage(), x, y, null);
            }
        }
        graphics.dispose();
        return image;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }
    
    public BattleSprite getBattleSprite() {
        return battleSprite;
    }

    public void setBattleSprite(BattleSprite battleSprite) {
        this.battleSprite = battleSprite;
    }
    
    public int getCurrentPalette() {
        return currentPalette;
    }

    public void setCurrentPalette(int currentPalette) {
        this.currentPalette = currentPalette;
    }
}
