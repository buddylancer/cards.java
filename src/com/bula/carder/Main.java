// Buddy Carder: sample playing cards recognizer.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license

package com.bula.carder;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: Program Folder");
            System.exit(1);
        }
        Controller controller = new Controller();

        int fileNo = 0, notIdentified = 0;
        File[] filesInFolder = new File(args[0]).listFiles();
        long startTime = System.currentTimeMillis(); //DEBUG
        for (File file : filesInFolder) {
            if (!file.getName().endsWith(".png"))
                continue;
            fileNo++; /*[*/ int[] nominalHash = new int[] { -1, -1, -1, -1, -1}, suitHash = new int[] { -1, -1, -1, -1, -1}; /*]*/
            String result = controller.identifyCards(file /*[*/, fileNo, nominalHash, suitHash /*]*/);
            System.out.print(file.getName() + " - " + result);
            /*[*/
            System.out.print("; FileNo: " + fileNo + ";");

            System.out.print(" Nominal Hash:[");
            for (int n = 0; n < 5; n++) {
                if (nominalHash[n] != -1)
                    System.out.print((n != 0 ? ", " : "") + nominalHash[n]);
            }
            System.out.print("]; Suit Hash:[");
            for (int n = 0; n < 5; n++) {
                if (suitHash[n] != -1)
                    System.out.print((n != 0 ? ", " : "") + suitHash[n]);
            }
            System.out.print("]");
            /*]*/

            System.out.println();

            while (result.contains("?")) {
                notIdentified++;
                result = result.replace("?", "");
            }
        }
        /*[*/
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println(
                "Files:" + fileNo + ";" +
                " Not Identified:" + notIdentified + " (" + ((float)notIdentified * 100 / fileNo) + "%);" +
                " Time:" + totalTime + " ms; Per file:" + totalTime / fileNo + " ms.");
        /*]*/
    }
}
