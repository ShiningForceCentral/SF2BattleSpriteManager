/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.battlesprite.BattleSprite;
import com.sfc.sf2.graphics.compressed.StackGraphicsDecoder;
import com.sfc.sf2.graphics.compressed.StackGraphicsEncoder;
import com.sfc.sf2.palette.graphics.PaletteDecoder;
import com.sfc.sf2.palette.graphics.PaletteEncoder;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
public class DisassemblyManager {
    
    public static BattleSprite importDisassembly(String filepath){
        System.out.println("com.sfc.sf2.battlesprite.io.DisassemblyManager.importDisassembly() - Importing disassembly file ...");
        
        BattleSprite battlesprite = new BattleSprite();
        try{
            Path path = Paths.get(filepath);
            if(path.toFile().exists()){
                byte[] data = Files.readAllBytes(path);
                if(data.length>42){
                    battlesprite.setAnimSpeed(getNextWord(data,0));
                    battlesprite.setStatusOffsetX(getNextByte(data,2));
                    battlesprite.setStatusOffsetY(getNextByte(data,3));
                    int palettesOffset = 4 + getNextWord(data,4);
                    int firstFrameOffset = 6 + getNextWord(data,6);
                    List<Color[]> paletteList = new ArrayList<Color[]>();
                    for(int i=0;(palettesOffset+32*i)<firstFrameOffset;i++){
                        byte[] paletteData = new byte[32];
                        System.arraycopy(data, palettesOffset+i*32, paletteData, 0, paletteData.length);
                        Color[] palette = PaletteDecoder.parsePalette(paletteData);
                        //palette[0] = new Color(0, 255, 255, 0);
                        paletteList.add(palette);
                    }
                    battlesprite.setPalettes(paletteList.toArray(new Color[paletteList.size()][]));
                    List<Tile[]> frameList = new ArrayList<Tile[]>();
                    for(int i=0;(6+i*2)<palettesOffset;i++){
                        int frameOffset = 6+i*2 + getNextWord(data,6+i*2);
                        int dataLength = 0;
                        if((6+(i+1)*2)<palettesOffset){
                            dataLength = 6+i*2 + getNextWord(data,6+(i+1)*2)+2 - frameOffset;
                        }else{
                            dataLength = data.length - frameOffset;
                        }
                        byte[] tileData = new byte[dataLength];
                        System.arraycopy(data, frameOffset, tileData, 0, dataLength);
                        Tile[] frame = new StackGraphicsDecoder().decodeStackGraphics(tileData, paletteList.get(0));
                        frame = reorderTilesSequentially(frame, frame.length == 144);
                        frameList.add(frame);
                        System.out.println("Frame "+i+" length="+dataLength+", offset="+frameOffset+", tiles="+frame.length);
                    }
                    battlesprite.setFrames(frameList.toArray(new Tile[frameList.size()][]));
                    if(battlesprite.getFrames()[0].length>144){
                        battlesprite.setType(BattleSprite.TYPE_ENEMY);
                    }
                }else{
                    System.out.println("com.sfc.sf2.battlesprite.io.DisassemblyManager.parseGraphics() - File ignored because of too small length (must be a dummy file) " + data.length + " : " + filepath);
                }
            }            
        }catch(Exception e){
             System.err.println("com.sfc.sf2.battlesprite.io.DisassemblyManager.parseGraphics() - Error while parsing graphics data : "+e);
             e.printStackTrace();
        }    
        System.out.println("com.sfc.sf2.battlesprite.io.DisassemblyManager.importDisassembly() - Disassembly imported.");
        return battlesprite;
    }
    
    public static void exportDisassembly(BattleSprite battlesprite, String filepath){
        System.out.println("com.sfc.sf2.battlesprite.io.DisassemblyManager.exportDisassembly() - Exporting disassembly ...");
        try{
                short animSpeed = (short)(battlesprite.getAnimSpeed()&0xFFFF);
                byte statusOffsetX = battlesprite.getStatusOffsetX();
                byte statusOffsetY = battlesprite.getStatusOffsetY();
                
                Color[][] palettes = battlesprite.getPalettes();
                byte[][] paletteBytes = new byte[palettes.length][];
            
                Tile[][] frames = battlesprite.getFrames();
            
                byte[][] frameBytes = new byte[frames.length][];
                short[] frameOffsets = new short[frames.length];
                        
                short palettesOffset = (short) (frames.length * 2 + 2);
                
                for(int i=0;i<palettes.length;i++){
                    PaletteEncoder.producePalette(palettes[i]);
                    paletteBytes[i] = PaletteEncoder.getNewPaletteFileBytes();
                }
                
                int framesSize = 0;
                int totalSize = 6 + frames.length * 2 + palettes.length * 32;
                for(int i=0;i<frames.length;i++){
                    Tile[] frameTiles = reorderTilesForDisasssembly(frames[i], battlesprite.getType() == BattleSprite.TYPE_ALLY);
                    StackGraphicsEncoder.produceGraphics(frameTiles);
                    frameBytes[i] = StackGraphicsEncoder.getNewGraphicsFileBytes();
                    if(i==0){
                        frameOffsets[i] = (short)(frames.length * 2 + palettes.length * 32);
                        System.out.println("Frame "+i+" length="+frameBytes[i].length+", offset="+frameOffsets[i]);
                    }else{
                        int target = frameOffsets[i-1] + 6 + (i-1)*2 + frameBytes[i-1].length;
                        int offsetLocation = 6 + i*2;
                        frameOffsets[i] = (short)((target - offsetLocation)&0xFFFF);
                        System.out.println("Frame "+i+" length="+frameBytes[i].length+", offset="+frameOffsets[i]);
                    }
                    framesSize += frameBytes[i].length;
                    totalSize += frameBytes[i].length;
                }

                byte[] newBattleSpriteFileBytes = new byte[totalSize];
                        
                newBattleSpriteFileBytes[0] = (byte)((animSpeed&0xFF00) >> 8);
                newBattleSpriteFileBytes[1] = (byte)(animSpeed&0xFF); 
                newBattleSpriteFileBytes[2] = statusOffsetX;
                newBattleSpriteFileBytes[3] = statusOffsetY; 
                newBattleSpriteFileBytes[4] = (byte)((palettesOffset&0xFF00) >> 8);
                newBattleSpriteFileBytes[5] = (byte)(palettesOffset&0xFF); 
                for(int i=0;i<frameOffsets.length;i++){
                    newBattleSpriteFileBytes[6+i*2] =  (byte) ((frameOffsets[i]&0xFF00) >> 8);
                    newBattleSpriteFileBytes[6+i*2+1] = (byte) (frameOffsets[i]&0xFF); 
                }
                for(int i=0;i<paletteBytes.length;i++){
                    System.arraycopy(paletteBytes[i], 0, newBattleSpriteFileBytes, 6+frameOffsets.length*2+i*32, 32);
                }
                for(int i=0;i<frameBytes.length;i++){
                    System.out.println("Writing frame "+i+" with length="+frameBytes[i].length+" at offset="+(int)(frameOffsets[i]+6+i*2));
                    System.arraycopy(frameBytes[i], 0, newBattleSpriteFileBytes, frameOffsets[i]+6+i*2, frameBytes[i].length);
                }
                Path graphicsFilePath = Paths.get(filepath);
                Files.write(graphicsFilePath,newBattleSpriteFileBytes);
                System.out.println(newBattleSpriteFileBytes.length + " bytes into " + graphicsFilePath);                
        } catch (Exception ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }  
        System.out.println("com.sfc.sf2.battlesprite.io.DisassemblyManager.exportDisassembly() - Disassembly exported.");        
    }
    
    private static Tile[] reorderTilesSequentially(Tile[] tiles, boolean isAllyType) {
        /* Disassembly tiles are stored in 4x4 chunks (top-bottom, left-right)
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
        // \/ Edit these variables \/
        int blockColumnCount = isAllyType ? 3 : 4;
        int blockRowCount = 3;
        int tilesPerBlock = 4;
        // /\ Edit these variables /\
        int blockTotalTiles = tilesPerBlock*tilesPerBlock;
        Tile[] newTiles = new Tile[tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            int bc = (i/tilesPerBlock) % blockColumnCount;
            int br = i/(blockColumnCount*blockTotalTiles);
            int tc = i%tilesPerBlock;
            int tr = (i/(tilesPerBlock*blockColumnCount)) % tilesPerBlock;
            newTiles[i] = tiles[bc*(blockTotalTiles*blockRowCount) + br*blockTotalTiles + tc*tilesPerBlock + tr];
        }
        return newTiles;
    }
    
    private static Tile[] reorderTilesForDisasssembly(Tile[] tiles, boolean isAllyType) {
        // \/ Edit these variables \/
        int blockColumnCount = isAllyType ? 3 : 4;
        int blockRowCount = 3;
        int tilesPerBlock = 4;
        // /\ Edit these variables /\
        int blockTotalTiles = tilesPerBlock*tilesPerBlock;
        Tile[] newTiles = new Tile[tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            int bc = (i/tilesPerBlock) % blockColumnCount;
            int br = i/(blockColumnCount*blockTotalTiles);
            int tc = i%tilesPerBlock;
            int tr = (i/(tilesPerBlock*blockColumnCount)) % tilesPerBlock;
            newTiles[bc*(blockTotalTiles*blockRowCount) + br*blockTotalTiles + tc*tilesPerBlock + tr] = tiles[i];
        }
        return newTiles;
    }
    
    private static short getNextWord(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor+1]);
        bb.put(data[cursor]);
        short s = bb.getShort(0);
        return s;
    }
    
    private static byte getNextByte(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor]);
        byte b = bb.get(0);
        return b;
    }
}
