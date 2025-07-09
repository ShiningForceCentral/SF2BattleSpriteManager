/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.layout;

import com.sfc.sf2.battlesprite.BattleSprite;
import com.sfc.sf2.graphics.Tile;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author wiz
 */
public class BattleSpriteLayout extends JPanel {
    
    private BattleSprite battleSprite;
    
    private int displaySize = 1;
    private boolean showGrid = false;
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(buildImage(), 0, 0, this);       
    }
    
    public BufferedImage buildImage(){
        BufferedImage image = buildImage(battleSprite);
        image = resize(image);
        setSize(image.getWidth(), image.getHeight());
        if (showGrid) { drawGrid(image); }
        return image;
    }
    
    public static BufferedImage buildImage(BattleSprite battleSprite){
        
        int tilesPerRow = battleSprite.getTilesPerRow();
        int frames = battleSprite.getFrames().length;
        int imageWidth = tilesPerRow*8;
        int imageHeight = frames*12*8;
        BufferedImage image;
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        for(int f = 0; f < frames; f++) {
            Tile[] frameTiles = battleSprite.getFrames()[f];
            for(int t = 0; t < frameTiles.length; t++) {
                int x = (t%tilesPerRow)*8;
                int y = (f*12 + t/tilesPerRow)*8;
                graphics.drawImage(frameTiles[t].getIndexedColorImage(), x, y, null);
            }
        }
        graphics.dispose();
        return image;
    }
    
    private void drawGrid(BufferedImage image) {
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(1));
        int x = 0;
        int y = 0;
        while (x < image.getWidth()) {
            graphics.drawLine(x, 0, x, image.getHeight());
            x += 8*displaySize;
        }
        graphics.drawLine(x-1, 0, x-1, image.getHeight());
        while (y < image.getHeight()) {
            graphics.setStroke(new BasicStroke((y % (96*displaySize) == 0) ? 3 : 1));
            graphics.drawLine(0, y, image.getWidth(), y);
            y += 8*displaySize;
        }
        graphics.setStroke(new BasicStroke(3));
        graphics.drawLine(0, y-1, image.getWidth(), y-1);
        graphics.dispose();
    }
    
    private BufferedImage resize(BufferedImage image) {
        if (displaySize == 1)
            return image;
        BufferedImage newImage = new BufferedImage(image.getWidth()*displaySize, image.getHeight()*displaySize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(image, 0, 0, image.getWidth()*displaySize, image.getHeight()*displaySize, null);
        g.dispose();
        return newImage;
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
    
    public int getDisplaySize() {
        return displaySize;
    }

    public void setDisplaySize(int displaySize) {
        this.displaySize = displaySize;
        this.revalidate();
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        this.revalidate();
    }
}
