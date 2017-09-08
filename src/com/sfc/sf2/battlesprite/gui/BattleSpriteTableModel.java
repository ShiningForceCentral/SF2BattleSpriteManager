/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battlesprite.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author wiz
 */
public class BattleSpriteTableModel extends AbstractTableModel {
    
    private final String[][] tableData;
    private final String[] columns = {"X", "Y", "X'", "Y'"};
 
    public BattleSpriteTableModel(int[][] altTiles) {
        super();
        tableData = new String[64][];
        int i = 0;
        if(altTiles!=null){
            while(i<altTiles.length){
                tableData[i] = new String[4];
                tableData[i][0] = Integer.toString(altTiles[i][0]);
                tableData[i][1] = Integer.toString(altTiles[i][1]);
                tableData[i][2] = Integer.toString(altTiles[i][2]);
                tableData[i][3] = Integer.toString(altTiles[i][3]);
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new String[4];
            tableData[i][0] = "null";
            tableData[i][1] = "null";
            tableData[i][2] = "null";
            tableData[i][3] = "null";
            i++;
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        return tableData[row][col];
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = (String)value;
    }    
 
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }    
    
    @Override
    public int getRowCount() {
        return tableData.length;
    }
 
    @Override
    public int getColumnCount() {
        return columns.length;
    }
 
    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }
 
    public int[][] produceTiles(){
        List<int[]> entries = new ArrayList<>();
        for(String[] stringEntry : tableData){
            try{
                int[] intEntry = new int[4];
                intEntry[0] = Integer.parseInt(stringEntry[0]);
                intEntry[1] = Integer.parseInt(stringEntry[1]);
                intEntry[2] = Integer.parseInt(stringEntry[2]);
                intEntry[3] = Integer.parseInt(stringEntry[3]);
                entries.add(intEntry);
            }catch(Exception e){
                break;
            }
        }
        int[][] returnedEntries = new int[entries.size()][4];
        return entries.toArray(returnedEntries);
    }
    
}
