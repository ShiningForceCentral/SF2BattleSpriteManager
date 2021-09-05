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
public class SFCDBankManager {
    
    private static List<Integer> processedPointers = new ArrayList();
    
    public static BattleSprite importSFCDBank(String filepath, int loadingOffset, int pointerTableOffset, int battleSpriteIndex) throws Exception{
        System.out.println("com.sfc.sf2.battlesprite.io.SFCDBankManager.importSFCDBank() - Importing SFCD Bank file ...");
        
        BattleSprite battlesprite = new BattleSprite();
        try{
            Path path = Paths.get(filepath);
            if(path.toFile().exists()){
                byte[] fileData = Files.readAllBytes(path);
                int ptOffset = 0;
                if(pointerTableOffset!=0){
                    ptOffset = pointerTableOffset;
                }else{
                    ptOffset = ((fileData[0x28+0]&0xFF)<<24) + ((fileData[0x28+1]&0xFF)<<16) + ((fileData[0x28+2]&0xFF)<<8) + ((fileData[0x28+3]&0xFF)) - loadingOffset;
                }
                int portraitPointerByte1 = fileData[0x10+0]&0xFF;
                int portraitPointerByte2 = fileData[0x10+1]&0xFF;
                int portraitPointerByte3 = fileData[0x10+2]&0xFF;
                int portraitPointerByte4 = fileData[0x10+3]&0xFF;
                long portraitPointer = (portraitPointerByte1<<24) + (portraitPointerByte2<<16) + (portraitPointerByte3<<8) + (portraitPointerByte4) - loadingOffset;
                if(ptOffset==portraitPointer){
                    System.out.println("No enemy battle sprites");
                    throw new Exception("No enemy battle sprites");
                }
                int pointerByte1 = fileData[ptOffset+battleSpriteIndex*4+0]&0xFF;
                int pointerByte2 = fileData[ptOffset+battleSpriteIndex*4+1]&0xFF;
                int pointerByte3 = fileData[ptOffset+battleSpriteIndex*4+2]&0xFF;
                int pointerByte4 = fileData[ptOffset+battleSpriteIndex*4+3]&0xFF;
                int pointer = (pointerByte1<<24) + (pointerByte2<<16) + (pointerByte3<<8) + (pointerByte4) - loadingOffset;
                if(!processedPointers.contains(pointer)){
                    processedPointers.add(pointer);
                }else{
                    return null;
                }
                byte[] data = new byte[0x4000];
                System.arraycopy(fileData, (int)pointer, data, 0, 0x4000);
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
                        frameList.add(frame);
                        System.out.println("Frame "+i+" length="+dataLength+", offset="+frameOffset+", tiles="+frame.length);
                    }
                    battlesprite.setFrames(frameList.toArray(new Tile[frameList.size()][]));
                    if(battlesprite.getFrames()[0].length>144){
                        battlesprite.setType(BattleSprite.TYPE_ENEMY);
                    }
                }else{
                    System.out.println("com.sfc.sf2.battlesprite.io.SFCDBankManager.importSFCDBank() - File ignored because of too small length (must be a dummy file) " + data.length + " : " + filepath);
                }
            }            
        }catch(Exception e){
             System.err.println("com.sfc.sf2.battlesprite.io.SFCDBankManager.importSFCDBank() - Error while parsing graphics data : "+e);
             //e.printStackTrace();
             throw e;
        }    
        System.out.println("com.sfc.sf2.battlesprite.io.SFCDBankManager.importSFCDBank() - SFCD Bank imported.");
        return battlesprite;
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
    
    public static void clearProcessedPointersList(){
        processedPointers.clear();
    }

    
}
