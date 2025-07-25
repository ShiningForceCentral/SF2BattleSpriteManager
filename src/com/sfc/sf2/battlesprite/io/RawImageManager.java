/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.battlesprite.BattleSprite;
import com.sfc.sf2.palette.Palette;
import com.sfc.sf2.palette.graphics.PaletteDecoder;
import com.sfc.sf2.palette.graphics.PaletteEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
public class RawImageManager {
    
    public static BattleSprite importImage(String filepath, BattleSprite battleSprite, boolean usePngPalette){
        System.out.println("com.sfc.sf2.battlesprite.io.RawImageManager.importImage() - Importing Image files ...");
        BattleSprite battlesprite = new BattleSprite();
        try{
            List<Tile[]> frames = new ArrayList<>();
            List<Palette> palettes = new ArrayList<>();
            String dir = filepath.substring(0, filepath.lastIndexOf(System.getProperty("file.separator")));
            String pattern = filepath.substring(filepath.lastIndexOf(System.getProperty("file.separator"))+1);
            File directory = new File(dir);
            File[] files = directory.listFiles();
            for(File f : files){
                if(f.getName().startsWith(pattern + "-frame")){
                    Tile[] frame = com.sfc.sf2.graphics.io.RawImageManager.importImage(f.getAbsolutePath());
                    frames.add(frame);
                }else if(f.getName().startsWith(pattern + "-palette")){
                    byte[] data = Files.readAllBytes(f.toPath());
                    Palette palette = new Palette(f.getName(), PaletteDecoder.parsePalette(data));
                    palettes.add(palette);
                }
            }
            if(frames.isEmpty()){
                System.err.println("com.sfc.sf2.battlesprite.io.RawImageManager.importImage() - ERROR : no frame imported. Image files missing for this pattern ?");
            } else{
                System.err.println("com.sfc.sf2.battlesprite.io.RawImageManager.importImage() - " + frames.size() + " : " + frames);
                if(frames.get(0).length>144){
                    battlesprite.setType(BattleSprite.TYPE_ENEMY);
                }
                if(usePngPalette || palettes.isEmpty()){
                    palettes.add(0, frames.get(0)[0].getPalette());
                }
                battlesprite.setFrames(frames.toArray(new Tile[frames.size()][]));
                battlesprite.setPalettes(palettes.toArray(new Palette[palettes.size()]));
            }
            String metaPath = filepath + ".meta";
            File inputFile = new File(metaPath);
            if (inputFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                String data = reader.readLine();
                data = data.substring(data.indexOf(":")+1).trim();
                battlesprite.setAnimSpeed(Short.parseShort(data));
                data = reader.readLine();
                data = data.substring(data.indexOf(":")+1).trim();
                String[] statusOffset = data.split(",");
                battlesprite.setStatusOffsetX(Byte.parseByte(statusOffset[0].trim()));
                battlesprite.setStatusOffsetY(Byte.parseByte(statusOffset[1].trim()));
                reader.close();
            }
        }catch(Exception e){
             System.err.println("com.sfc.sf2.battlesprite.io.RawImageManager.importImage() - Error while parsing graphics data : "+e);
             e.printStackTrace();
        }        
        System.out.println("com.sfc.sf2.battlesprite.io.RawImageManager.importImage() - PNG files imported.");
        return battlesprite;                
    }
    
    public static void exportImage(BattleSprite battlesprite, String filepath, int selectedPalette, int fileFormat){
        try {
            System.out.println("com.sfc.sf2.battlesprite.io.RawImageManager.exportImage() - Exporting Image files and palettes ...");
            Tile[][] frames = battlesprite.getFrames();
            for(int i=0;i<frames.length;i++){
                String framePath = filepath + "-frame-" + String.valueOf(i) + "." + com.sfc.sf2.graphics.io.RawImageManager.GetFileExtensionString(fileFormat);
                com.sfc.sf2.graphics.io.RawImageManager.exportImage(frames[i], framePath, battlesprite.getType() == BattleSprite.TYPE_ALLY ? 12 : 16, fileFormat);
            }
            Palette[] palettes = battlesprite.getPalettes();
            for(int i=0;i<palettes.length;i++){
                String palettePath = filepath + "-palette-" + String.valueOf(i) + ".bin";
                //palettes[i][0] = new Color(0, 255, 255, 0);
                PaletteEncoder.producePalette(palettes[i].getColors());
                byte[] palette = PaletteEncoder.getNewPaletteFileBytes();
                Path graphicsFilePath = Paths.get(palettePath);
                Files.write(graphicsFilePath,palette);
            }
            String metaPath = filepath + ".meta";
            StringBuilder sb = new StringBuilder();
            sb.append("Anim Speed: " + battlesprite.getAnimSpeed() + "\n");
            sb.append("Status Offset: " + battlesprite.getStatusOffsetX() + ", " + battlesprite.getStatusOffsetY() + "\n");
            File outputfile = new File(metaPath);
            FileWriter writer = new FileWriter(outputfile, false);
            writer.write(sb.toString());
            writer.close();
                           
            System.out.println("com.sfc.sf2.battlesprite.io.RawImageManager.exportImage() - Image files and palettes exported.");
        } catch (Exception ex) {
            Logger.getLogger(RawImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
