/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.battlesprite.BattleSprite;
import com.sfc.sf2.palette.graphics.PaletteDecoder;
import com.sfc.sf2.palette.graphics.PaletteEncoder;
import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class GifManager {
    
    public static BattleSprite importGif(String filepath, BattleSprite battleSprite, boolean useGifPalette){
        System.out.println("com.sfc.sf2.battlesprite.io.GifManager.importGif() - Importing GIF files ...");
        BattleSprite battlesprite = new BattleSprite();
        try{
            List<Tile[]> frames = new ArrayList<Tile[]>();
            List<Color[]> palettes = new ArrayList<Color[]>();
            String dir = filepath.substring(0, filepath.lastIndexOf(System.getProperty("file.separator")));
            String pattern = filepath.substring(filepath.lastIndexOf(System.getProperty("file.separator"))+1);
            File directory = new File(dir);
            File[] files = directory.listFiles();
            for(File f : files){
                if(f.getName().startsWith(pattern + "-frame") && f.getName().endsWith(".gif")){
                    Tile[] frame = com.sfc.sf2.graphics.io.RawImageManager.importImage(f.getAbsolutePath());
                    frames.add(frame);
                }else if(f.getName().startsWith(pattern + "-palette")){
                    byte[] data = Files.readAllBytes(f.toPath());
                    Color[] palette = PaletteDecoder.parsePalette(data);
                    palettes.add(palette);
                }
            }
            if(frames.isEmpty()){
                System.err.println("com.sfc.sf2.battlesprite.io.GifManager.importGif() - ERROR : no frame imported. GIF files missing for this pattern ?");
            } else{
                System.err.println("com.sfc.sf2.battlesprite.io.GifManager.importGif() - " + frames.size() + " : " + frames);
                if(frames.get(0).length>144){
                    battlesprite.setType(BattleSprite.TYPE_ENEMY);
                }
                if(useGifPalette || palettes.isEmpty()){
                    palettes.add(0, frames.get(0)[0].getPalette());
                }
                battlesprite.setFrames(frames.toArray(new Tile[frames.size()][]));
                battlesprite.setPalettes(palettes.toArray(new Color[palettes.size()][]));
            }
        }catch(Exception e){
             System.err.println("com.sfc.sf2.battlesprite.io.GifManager.importGif() - Error while parsing graphics data : "+e);
             e.printStackTrace();
        }        
        System.out.println("com.sfc.sf2.battlesprite.io.GifManager.importGif() - GIF files imported.");
        return battlesprite;                
    }
    public static void exportGif(BattleSprite battlesprite, String filepath, int selectedPalette){
        try {
            System.out.println("com.sfc.sf2.battlesprite.io.GifManager.exportGif() - Exporting GIF files and palettes ...");
            Tile[][] frames = battlesprite.getFrames();
            for(int i=0;i<frames.length;i++){
                String framePath = filepath + "-frame-" + String.valueOf(i) + ".gif";
                com.sfc.sf2.graphics.io.RawImageManager.exportImage(frames[i], framePath, battlesprite.getType() == BattleSprite.TYPE_ALLY ? 12 : 16, com.sfc.sf2.graphics.io.RawImageManager.FILE_FORMAT_GIF);
            }
            Color[][] palettes = battlesprite.getPalettes();
            for(int i=0;i<palettes.length;i++){
                String palettePath = filepath + "-palette-" + String.valueOf(i) + ".bin";
                PaletteEncoder.producePalette(palettes[i]);
                byte[] palette = PaletteEncoder.getNewPaletteFileBytes();
                Path graphicsFilePath = Paths.get(palettePath);
                Files.write(graphicsFilePath,palette);
            }
                           
            System.out.println("com.sfc.sf2.battlesprite.io.GifManager.exportGif() - GIF files and palettes exported.");
        } catch (Exception ex) {
            Logger.getLogger(GifManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
