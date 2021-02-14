// Buddy Cards: sample playing cards recognizer.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license

package com.bula.cards;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

public class Main {
    private static final boolean debugPrint = true; //DEBUG

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: Program Folder");
            System.exit(1);
        }
        
        long startTime = System.currentTimeMillis(); //DEBUG
        
        FilesController filesController = new FilesController(args[0]);
        CardsController cardsController = new CardsController();
        
        int fileNo = 0;
        for (String fileName : filesController.getFileNames()) {
            String result = filesController.processFile(fileName, cardsController);
            
            System.out.print(fileName+ " - " + result);
            /*[*/
            if (debugPrint) {
                System.out.print("; FileNo: " + fileNo + ";");

                int[] nominalHash = filesController.getNominalHash();
                System.out.print(" Nominal Hash:[");
                for (int n = 0; n < 5; n++) {
                    if (nominalHash[n] != -1)
                        System.out.print((n != 0 ? ", " : "") + nominalHash[n]);
                }
                int[] suitHash = filesController.getSuitHash();
                System.out.print("]; Suit Hash:[");
                for (int n = 0; n < 5; n++) {
                    if (suitHash[n] != -1)
                        System.out.print((n != 0 ? ", " : "") + suitHash[n]);
                }
                System.out.print("]");
            }
            /*]*/

            System.out.println();
            fileNo++;
        }
        
        /*[*/
        if (debugPrint) {
            long totalTime = System.currentTimeMillis() - startTime;
            int totalFiles = filesController.getTotalFiles();
            int notIdentified = filesController.getNotIdentified();
            System.out.println(
                    "Files:" + totalFiles + ";" +
                            " Not Identified:" + notIdentified + " (" + ((float) notIdentified * 100 / totalFiles) + "%);" +
                            " Time:" + totalTime + " ms; Per file:" + totalTime / totalFiles + " ms.");
        }
        /*]*/
    }
}
