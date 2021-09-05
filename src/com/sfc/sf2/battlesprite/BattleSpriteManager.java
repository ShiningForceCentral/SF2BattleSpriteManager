/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite;

import com.sfc.sf2.graphics.GraphicsManager;
import com.sfc.sf2.graphics.Tile;
import com.sfc.sf2.battlesprite.io.DisassemblyManager;
import com.sfc.sf2.battlesprite.io.PngManager;
import com.sfc.sf2.battlesprite.io.GifManager;
import com.sfc.sf2.battlesprite.io.SFCDBankManager;
import com.sfc.sf2.palette.PaletteManager;
import java.awt.Color;

/**
 *
 * @author wiz
 */
public class BattleSpriteManager {
       
    private PaletteManager paletteManager = new PaletteManager();
    private GraphicsManager graphicsManager = new GraphicsManager();
    private Tile[] tiles;
    private BattleSprite battlesprite = new BattleSprite();
       
    public void importDisassembly(String filePath){
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importDisassembly() - Importing disassembly ...");
        battlesprite = DisassemblyManager.importDisassembly(filePath);
        if(battlesprite.getFrames() != null && battlesprite.getFrames().length > 0){
            int blockColumnsNumber = (battlesprite.getType()==BattleSprite.TYPE_ALLY)?3:4;
            tiles = new Tile[battlesprite.getFrames().length*blockColumnsNumber*4*12];
            for(int i=0;i<battlesprite.getFrames().length;i++){
                System.arraycopy(battlesprite.getFrames()[i], 0, tiles, i*blockColumnsNumber*4*12, blockColumnsNumber*4*12);
            }
            graphicsManager.setTiles(tiles);
        }
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importDisassembly() - Disassembly imported.");
    }
    
    public void exportDisassembly(String filepath, String animSpeed, String unknown){
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importDisassembly() - Exporting disassembly ...");
        battlesprite.setAnimSpeed(Integer.parseInt(animSpeed));
        battlesprite.setUnknown((short)(Integer.parseInt(unknown)&0xFFFF));
        DisassemblyManager.exportDisassembly(battlesprite, filepath);
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importDisassembly() - Disassembly exported.");        
    }   
    
    
    public void importPng(String filepath, boolean usePngPalette){
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importPng() - Importing PNG ...");
        battlesprite = PngManager.importPng(filepath, battlesprite, usePngPalette);
        if(battlesprite.getFrames() != null && battlesprite.getFrames().length > 0){
            int blockColumnsNumber = (battlesprite.getType()==BattleSprite.TYPE_ALLY)?3:4;
            tiles = new Tile[battlesprite.getFrames().length*blockColumnsNumber*4*12];
            for(int i=0;i<battlesprite.getFrames().length;i++){
                System.arraycopy(battlesprite.getFrames()[i], 0, tiles, i*blockColumnsNumber*4*12, blockColumnsNumber*4*12);
            }
            graphicsManager.setTiles(tiles);
        }
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importPng() - PNG imported.");
    }
    
    public void exportPng(String filepath, int selectedPalette){
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.exportPng() - Exporting PNG ...");
        PngManager.exportPng(battlesprite, filepath, selectedPalette);
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.exportPng() - PNG exported.");       
    }
    
    
    public void importGif(String filepath, boolean useGifPalette){
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importGif() - Importing GIF ...");
        battlesprite = GifManager.importGif(filepath, battlesprite, useGifPalette);
        if(battlesprite.getFrames() != null && battlesprite.getFrames().length > 0){
            int blockColumnsNumber = (battlesprite.getType()==BattleSprite.TYPE_ALLY)?3:4;
            tiles = new Tile[battlesprite.getFrames().length*blockColumnsNumber*4*12];
            for(int i=0;i<battlesprite.getFrames().length;i++){
                System.arraycopy(battlesprite.getFrames()[i], 0, tiles, i*blockColumnsNumber*4*12, blockColumnsNumber*4*12);
            }
            graphicsManager.setTiles(tiles);
        }
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importGif() - GIF imported.");
    }
    
    public void exportGif(String filepath, int selectedPalette){
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.exportGif() - Exporting GIF ...");
        GifManager.exportGif(battlesprite, filepath, selectedPalette);
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.exportGif() - GIF exported.");       
    }
       
    public void importSFCDBank(String filePath, String loadingOffset, String pointerTableOffset, int battleSpriteIndex){
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importDisassembly() - Importing disassembly ...");
        int ldOffset = Integer.parseInt(loadingOffset, 16);
        int ptOffset = 0;
        if(!pointerTableOffset.isBlank()){
            ptOffset = Integer.parseInt(pointerTableOffset, 16);
        }
        try{
            battlesprite = SFCDBankManager.importSFCDBank(filePath, ldOffset, ptOffset, battleSpriteIndex);
        }catch(Exception e){
            
        }
        if(battlesprite.getFrames() != null && battlesprite.getFrames().length > 0){
            int blockColumnsNumber = (battlesprite.getType()==BattleSprite.TYPE_ALLY)?3:4;
            tiles = new Tile[battlesprite.getFrames().length*blockColumnsNumber*4*12];
            for(int i=0;i<battlesprite.getFrames().length;i++){
                System.arraycopy(battlesprite.getFrames()[i], 0, tiles, i*blockColumnsNumber*4*12, blockColumnsNumber*4*12);
            }
            graphicsManager.setTiles(tiles);
        }
        System.out.println("com.sfc.sf2.battlesprite.BattleSpriteManager.importDisassembly() - Disassembly imported.");
    }

    public BattleSprite getBattleSprite() {
        return battlesprite;
    }

    public void setBattleSprite(BattleSprite battlesprite) {
        this.battlesprite = battlesprite;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
}
