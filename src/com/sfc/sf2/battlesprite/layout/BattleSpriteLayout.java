/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.layout;

import com.sfc.sf2.battlesprite.BattleSprite;
import com.sfc.sf2.graphics.Tile;
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
    private Tile[] tiles;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   
        g.drawImage(buildImage(), 0, 0, this);       
    }
    
    public BufferedImage buildImage(){
        BufferedImage image = buildImage(battleSprite, false);
        setSize(image.getWidth(), image.getHeight());
        return image;
    }
    
    public static BufferedImage buildImage(BattleSprite battleSprite, boolean pngExport){
        
        int tilesPerRow = battleSprite.getTilesPerRow();
        int frames = battleSprite.getFrames().length;
        int imageWidth = 16*8;
        int imageHeight = frames*16*8;
        BufferedImage image;
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        for(int f = 0; f < frames; f++) {
            Tile[] frameTiles = battleSprite.getFrames()[f];
            for(int t = 0; t < frameTiles.length; t++) {
                int x = (t%tilesPerRow)*8;
                int y = (f*16 + t/tilesPerRow)*8;
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
}
