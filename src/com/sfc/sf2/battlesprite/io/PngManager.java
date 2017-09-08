/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.graphics.layout.DefaultLayout;
import com.sfc.sf2.battlesprite.BattleSprite;
import com.sfc.sf2.battlesprite.layout.BattleSpriteLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author wiz
 */
public class PngManager {
    
    public static BattleSprite importPng(String filepath, BattleSprite battleSprite){
        System.out.println("com.sfc.sf2.battlesprite.io.PngManager.importPng() - Importing PNG files ...");
        BattleSprite battlesprite = new BattleSprite();
        try{
/*            Tile[] tiles = loadPngFile(filepath);
            if(tiles!=null){
                if(tiles.length==64){                   
                   battlesprite.setTiles(tiles);
                   System.out.println("Created BattleSprite with " + tiles.length + " tiles.");                       
                }else{
                    System.out.println("Could not create BattleSprite because of wrong length : tiles=" + tiles.length);
                }
            }*/
        }catch(Exception e){
             System.err.println("com.sfc.sf2.battlesprite.io.PngManager.importPng() - Error while parsing graphics data : "+e);
        }        
        System.out.println("com.sfc.sf2.battlesprite.io.PngManager.importPng() - PNG files imported.");
        return battlesprite;                
    }
    
    
    
    public static Tile[] loadPngFile(String filepath) throws IOException{
        Tile[] tiles = null;
        try{
            Path path = Paths.get(filepath);
            if(path.toFile().exists()){
                BufferedImage img = ImageIO.read(path.toFile());
                ColorModel cm = img.getColorModel();
                if(!(cm instanceof IndexColorModel)){
                    System.out.println("PNG FORMAT ERROR : COLORS ARE NOT INDEXED AS EXPECTED.");
                }else{
                    IndexColorModel icm = (IndexColorModel) cm;
                    Color[] palette = buildColors(icm);

                    int imageWidth = img.getWidth();
                    int imageHeight = img.getHeight();
                    if(imageWidth%8!=0 || imageHeight%8!=0){
                        System.out.println("PNG FORMAT WARNING : DIMENSIONS ARE NOT MULTIPLES OF 8. (8 pixels per tile)");
                    }else{
                        tiles = new Tile[(imageWidth/8)*(imageHeight/8)];
                        int tileId = 0;
                                    for(int tileLine=0;tileLine<8;tileLine++){
                                        for(int tileColumn=0;tileColumn<8;tileColumn++){
                                            int x = (tileColumn)*8;
                                            int y = (tileLine)*8;
                                            //System.out.println("Building tile from coordinates "+x+":"+y);
                                            Tile tile = new Tile();
                                            tile.setId(tileId);
                                            tile.setPalette(palette);
                                            for(int j=0;j<8;j++){
                                                for(int i=0;i<8;i++){
                                                    Color color = new Color(img.getRGB(x+i,y+j));
                                                    for(int c=0;c<16;c++){
                                                        if(color.equals(palette[c])){
                                                            tile.setPixel(i, j, c);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            //System.out.println(tile);
                                            tiles[tileId] = tile;   
                                            tileId++;
                                        }
                                    }        

                    }
                }                
            }
        }catch(Exception e){
             System.err.println("com.sfc.sf2.battlesprite.io.PngManager.importPng() - Error while parsing PNG data : "+e);
             throw e;
        }                
        return tiles;                
    }
    
    private static Color[] buildColors(IndexColorModel icm){
        Color[] colors = new Color[16];
        if(icm.getMapSize()>16){
            System.out.println("com.sfc.sf2.battlesprite.io.PngManager.buildColors() - PNG FORMAT HAS MORE THAN 16 INDEXED COLORS : "+icm.getMapSize());
        }
        byte[] reds = new byte[icm.getMapSize()];
        byte[] greens = new byte[icm.getMapSize()];
        byte[] blues = new byte[icm.getMapSize()];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);
        for(int i=0;i<16;i++){
            colors[i] = new Color((int)(reds[i]&0xff),(int)(greens[i]&0xff),(int)(blues[i]&0xff));
        }
        return colors;
    }
    
    
    public static void exportPng(BattleSprite battlesprite, String filepath){
        try {
            System.out.println("com.sfc.sf2.battlesprite.io.PngManager.exportPng() - Exporting PNG file ...");
            //writePngFile(battlesprite.getTiles(),filepath);                
            System.out.println("com.sfc.sf2.battlesprite.io.PngManager.exportPng() - PNG file exported.");
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
    }    
    
    public static void writePngFile(Tile[] tiles, String filepath){
        try {
        /*    System.out.println("com.sfc.sf2.battlesprite.io.PngManager.exportPng() - Exporting PNG file ...");
            BufferedImage image = BattleSpriteLayout.buildImage(tiles, 8, true);
            File outputfile = new File(filepath);
            System.out.println("File path : "+outputfile.getAbsolutePath());
            ImageIO.write(image, "png", outputfile);
            System.out.println("PNG file exported : " + outputfile.getAbsolutePath());*/
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
       
    
}
