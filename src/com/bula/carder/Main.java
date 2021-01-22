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
        ControllerMinimal controller = new ControllerMinimal();

        int fileNo = 0, notIdentified = 0;
        File[] filesInFolder = new File(args[0]).listFiles();
        long startTime = System.currentTimeMillis();
        for (File file : filesInFolder) {
            if (!file.getName().endsWith(".png"))
                continue;
            String result = controller.identifyCards(file);
            fileNo++;
            System.out.println(file.getName() + " - " + result);
            while (result.contains("?")) {
                notIdentified++;
                result = result.replace("?", "");
            }
        }
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println(
                "Files:" + fileNo + ";" +
                " Not Identified:" + notIdentified + " (" + ((float)notIdentified * 100 / fileNo) + "%);" +
                " Time:" + totalTime + " ms; Per file:" + totalTime / fileNo + " ms.");
    }
}
