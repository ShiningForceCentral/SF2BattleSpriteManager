/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.io;

import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.graphics.compressed.BasicGraphicsDecoder;
import com.sfc.sf2.graphics.compressed.BasicGraphicsEncoder;
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
import java.util.Arrays;
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
                    short animSpeed = getNextWord(data,0);
                    short unknown = getNextWord(data,2);
                    battlesprite.setAnimSpeed(animSpeed);
                    battlesprite.setUnknown(unknown);
                    int palettesOffset = 4 + getNextWord(data,4);
                    int firstFrameOffset = 6 + getNextWord(data,6);
                    List<Color[]> paletteList = new ArrayList<Color[]>();
                    for(int i=0;(palettesOffset+32*i)<firstFrameOffset;i++){
                        byte[] paletteData = new byte[32];
                        System.arraycopy(data, palettesOffset+i*32, paletteData, 0, paletteData.length);
                        Color[] palette = PaletteDecoder.parsePalette(paletteData);
                        palette[0] = new Color(255, 255, 255, 0);
                        paletteList.add(palette);
                    }
                    battlesprite.setPalettes(paletteList.toArray(new Color[paletteList.size()][]));
                    List<Tile[]> frameList = new ArrayList<Tile[]>();
                    for(int i=0;(6+i*2)<palettesOffset;i++){
                        int frameOffset = 6+i*2 + getNextWord(data,6+i*2);
                        int dataLength = 0;
                        if((6+(i+1)*2)<palettesOffset){
                            dataLength = 6+i*2 + getNextWord(data,6+(i+1)*2) - frameOffset;
                        }else{
                            dataLength = data.length - frameOffset;
                        }
                        byte[] tileData = new byte[dataLength];
                        System.arraycopy(data, frameOffset, tileData, 0, dataLength);
                        Tile[] frame = new StackGraphicsDecoder().decodeStackGraphics(tileData, paletteList.get(0));
                        frameList.add(frame);
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
            /*    byte[] eyeTiles = new byte[2+battlesprite.getEyeTiles().length*4];
                eyeTiles[0] = 0;
                eyeTiles[1] = (byte)(battlesprite.getEyeTiles().length & 0xFF);
                for(int i=0;i<battlesprite.getEyeTiles().length;i++){
                    eyeTiles[2+i*4+0] = (byte)(battlesprite.getEyeTiles()[i][0] & 0xFF);
                    eyeTiles[2+i*4+1] = (byte)(battlesprite.getEyeTiles()[i][1] & 0xFF);
                    eyeTiles[2+i*4+2] = (byte)(battlesprite.getEyeTiles()[i][2] & 0xFF);
                    eyeTiles[2+i*4+3] = (byte)(battlesprite.getEyeTiles()[i][3] & 0xFF);
                }
                byte[] mouthTiles = new byte[2+battlesprite.getMouthTiles().length*4];
                mouthTiles[0] = 0;
                mouthTiles[1] = (byte)(battlesprite.getMouthTiles().length & 0xFF);
                for(int i=0;i<battlesprite.getMouthTiles().length;i++){
                    mouthTiles[2+i*4+0] = (byte)(battlesprite.getMouthTiles()[i][0] & 0xFF);
                    mouthTiles[2+i*4+1] = (byte)(battlesprite.getMouthTiles()[i][1] & 0xFF);
                    mouthTiles[2+i*4+2] = (byte)(battlesprite.getMouthTiles()[i][2] & 0xFF);
                    mouthTiles[2+i*4+3] = (byte)(battlesprite.getMouthTiles()[i][3] & 0xFF);
                }
                PaletteEncoder.producePalette(battlesprite.getTiles()[0].getPalette());
                byte[] palette = PaletteEncoder.getNewPaletteFileBytes();
                StackGraphicsEncoder.produceGraphics(battlesprite.getTiles());
                byte[] tileset = StackGraphicsEncoder.getNewGraphicsFileBytes();
                byte[] newBattleSpriteFileBytes = new byte[eyeTiles.length+mouthTiles.length+palette.length+tileset.length];
                System.arraycopy(eyeTiles, 0, newBattleSpriteFileBytes, 0, eyeTiles.length);
                System.arraycopy(mouthTiles, 0, newBattleSpriteFileBytes, eyeTiles.length, mouthTiles.length);
                System.arraycopy(palette, 0, newBattleSpriteFileBytes, eyeTiles.length+mouthTiles.length, palette.length);
                System.arraycopy(tileset, 0, newBattleSpriteFileBytes, eyeTiles.length+mouthTiles.length+palette.length, tileset.length);
                Path graphicsFilePath = Paths.get(filepath);
                Files.write(graphicsFilePath,newBattleSpriteFileBytes);
                System.out.println(newBattleSpriteFileBytes.length + " bytes into " + graphicsFilePath);  */              
        } catch (Exception ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }  
        System.out.println("com.sfc.sf2.battlesprite.io.DisassemblyManager.exportDisassembly() - Disassembly exported.");        
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
