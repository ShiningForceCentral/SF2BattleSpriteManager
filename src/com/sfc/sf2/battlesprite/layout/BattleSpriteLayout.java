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
import java.awt.image.IndexColorModel;
import javax.swing.JPanel;

/**
 *
 * @author wiz
 */
public class BattleSpriteLayout extends JPanel {
    
    private static final int ALLY_TILES_PER_ROW = 12;
    private static final int ENEMY_TILES_PER_ROW = 16;    
    private static final int DEFAULT_TILES_PER_ROW = ALLY_TILES_PER_ROW;    
    private int tilesPerRow = DEFAULT_TILES_PER_ROW;
    
    private int displaySize = 1;
    private boolean showGrid = false;
    
    private int battlespriteType;
    private Tile[] tiles;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(buildImage(), 0, 0, this);       
    }
    
    public BufferedImage buildImage(){
        BufferedImage image = buildImage(this.tiles,this.tilesPerRow, battlespriteType, false);
        image = resize(image);
        setSize(image.getWidth(), image.getHeight());
        if (showGrid) { drawGrid(image); }
        return image;
    }
    
    public static BufferedImage buildImage(Tile[] tiles, int tilesPerRow, int battlespriteType, boolean pngExport){
        
        int blockColumnsNumber = 3;
        if(battlespriteType==BattleSprite.TYPE_ENEMY){
            tilesPerRow = ENEMY_TILES_PER_ROW;
            blockColumnsNumber = 4;
        }
        
        int imageHeight = (tiles.length/tilesPerRow)*8;
        if(tiles.length%tilesPerRow!=0){
            imageHeight+=8;
        }
        BufferedImage image;
        IndexColorModel icm = buildIndexColorModel(tiles[0].getPalette());
        image = new BufferedImage(tilesPerRow*8, imageHeight , BufferedImage.TYPE_BYTE_BINARY, icm);
        Graphics graphics = image.getGraphics();     
        for(int frame=0;frame<(tiles.length/(blockColumnsNumber*4*12));frame++){         
            /*
                1  5  9 13 49 53                  
                2  6 10 14 50  .                  
                3  7 11 15 51  .                  
                4  8 12 16 52  .                  
               17 21 25 29  
               18 22 26 30
               19 23 27 31
               20 24 28 32
               33 37 41 45                  . 141
               34 38 42 46                  . 142
               35 39 43 47                  . 143
               36 40 44 48                140 144
            */
            
                for(int blockColumn=0;blockColumn<blockColumnsNumber;blockColumn++){
                    for(int blockLine=0;blockLine<3;blockLine++){
                        for(int tileColumn=0;tileColumn<4;tileColumn++){
                            for(int tileLine=0;tileLine<4;tileLine++){
                                graphics.drawImage(tiles[(frame*blockColumnsNumber*4*12)+(blockColumn*16*3)+(blockLine*16)+(tileColumn*4)+tileLine].getImage(), (blockColumn*4+tileColumn)*8, (frame*12+blockLine*4+tileLine)*8, null);
                            }
                        }
                    }
                }
        }            
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
    
    private static IndexColorModel buildIndexColorModel(Color[] colors){
        byte[] reds = new byte[16];
        byte[] greens = new byte[16];
        byte[] blues = new byte[16];
        byte[] alphas = new byte[16];
        /*reds[0] = (byte)0xFF;
        greens[0] = (byte)0xFF;
        blues[0] = (byte)0xFF;
        alphas[0] = 0;*/
        for(int i=0;i<16;i++){
            reds[i] = (byte)colors[i].getRed();
            greens[i] = (byte)colors[i].getGreen();
            blues[i] = (byte)colors[i].getBlue();
            alphas[i] = (byte)0xFF;
        }
        alphas[0] = (byte)0;
        IndexColorModel icm = new IndexColorModel(4,16,reds,greens,blues,alphas);
        return icm;
    }     
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }
    
    public int getBattlespriteType() {
        return battlespriteType;
    }

    public void setBattlespriteType(int battlespriteType) {
        this.battlespriteType = battlespriteType;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
    
    public int getTilesPerRow() {
        return tilesPerRow;
    }

    public void setTilesPerRow(int tilesPerRow) {
        this.tilesPerRow = tilesPerRow;
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
