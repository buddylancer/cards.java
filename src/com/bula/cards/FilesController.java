/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bula.cards;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Administrator
 */
public class FilesController {
    private String folderPath;
    private ArrayList<String> fileNames;
    private Hashtable<String, String> fileResults;
    private int totalFiles = 0, notIdentified = 0;
    
    private int nominalHash[], suitHash[]; //DEBUG
    
    public FilesController(String folder) {
        fileNames = new ArrayList<>();
        fileResults = new Hashtable<>();
        nominalHash = new int[5]; suitHash = new int[5]; //DEBUG
        initializeFiles(folder);
    }
    
    private void initializeFiles(String folder) {
        File fileFolder = new File(folder);
        folderPath = fileFolder.getPath();
        File[] filesInFolder = fileFolder.listFiles();
        totalFiles = 0;
        for (File file : filesInFolder) {
            String fileName = file.getName();
            if (!fileName.endsWith(".png"))
                continue;
            fileNames.add(fileName);
            fileResults.put(fileName, ""); // Initialize emptyresults
            totalFiles++;
        }        
    }
    
    public ArrayList<String> getFileNames() {
        return fileNames;
    }
    
    public int getTotalFiles() {
        return totalFiles;
    }
    
    public int[] getNominalHash() {
        return nominalHash;
    }
    
    public int[] getSuitHash() {
        return suitHash;
    }

    public int getNotIdentified() {
        return notIdentified;
    }
    
    public String processFile(String fileName, CardsController cardsController) {
        /*[*/
        for (int n = 0; n < 5; n++)
            nominalHash[n] = suitHash[n] = -1;
        /*]*/

        String result = cardsController.identifyCards(folderPath + "/" + fileName /*[*/, nominalHash, suitHash /*]*/);

        while (result.contains("?")) {
            notIdentified++;
            result = result.replace("?", "");
        }
        
        return result;
    }
}
