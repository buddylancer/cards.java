// Buddy Carder: sample playing cards recognizer.
// Copyright (c) 2021 Buddy Lancer. All rights reserved.
// Author - Buddy Lancer <http://www.buddylancer.com>.
// Licensed under the MIT license

package com.bula.carder;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class Controller {
    private ClassLoader classLoader;

    private final String nominalNames[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private final String suitNames[] = {"spades", "clubs", "diamonds", "hearts"};

    private ArrayList<BufferedImage> nominalImages = new ArrayList<>(), suitImages = new ArrayList<>(); // Stored images

    // Hardcoded for now
    private final Dimension imageSize = new Dimension(636, 1166), cardSize = new Dimension(64, 85);  // Area and card size
    private final Point[] cards = { new Point(143, 585), new Point(215, 585), new Point(286, 585), new Point(358, 585), new Point(429, 585) };
    private final Dimension nominalShift = new Dimension(15, 18), suitShift = new Dimension(24, 20);
    private final Dimension extra10Shift = new Dimension(5, 0); // Extra shift for "10" as it is more wide than others
    private final int grayRGB = new Color(120, 120, 120).getRGB(); // For identification of "gray" cards
    private final int colorThreshold = 100; // For identification whether a card is on place or not
    private final Dimension delta = new Dimension(7, 7); // Extend rectangle a little for proper identification of nominal or suit
    private final int xorHashThreshold = 60; // How much non-white pixels are allowed during XOR operation

    private Dimension nominalSize = new Dimension(0, 0), suitSize = new Dimension(0, 0); // Calculated maximum size for reference symbols (nominal & suit)

    private Integer[] arrayDeltaX, arrayDeltaY; // Store the list of deltas in form of (0, 1, -1, 2, -2, ..., deltaN/2, -deltaN/2)

    private final int whiteRGB = Color.WHITE.getRGB(), blackRGB = Color.BLACK.getRGB();

    private int fileNo = -1, cardNo = -1, checkNo = -1; //DEBUG

    public Controller() {
        classLoader = getClass().getClassLoader();
        try {
            preloadImages(nominalNames, nominalImages, nominalSize);
            preloadImages(suitNames, suitImages, suitSize);
        }
        catch (Exception ex) {
            System.out.println("Can not preload Nominal or Suit images!");
            System.exit(1);
        }
        arrayDeltaX = createArrayWithDeltas(delta.width);
        arrayDeltaY = createArrayWithDeltas(delta.height);
    }

    private Integer[] createArrayWithDeltas(int delta) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int n = 0; n <= half(delta); n++) {
            list.add(n);
            if (n != 0)
                list.add(-n);
        }
        return list.toArray(new Integer[] {});
    }

    private void preloadImages(String[] names, ArrayList<BufferedImage> images, Dimension size) throws Exception {
        for (String name : names) {
            BufferedImage image = ImageIO.read(classLoader.getResourceAsStream("com/bula/carder/resources/" + name + ".png"));
            images.add(getBlackAndWhiteImage(image, false)); // Store black & white image
            size.width = Math.max(image.getWidth(), size.width);
            size.height = Math.max(image.getHeight(), size.height);
        }
    }

    public String identifyCards(File file /*[*/, int counter, int[] nominalHash, int[] suitHash /*]*/) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        }
        catch (Exception ex) {
            return("Can not load image '" + file.getName() + "'! Skipped for now!");
        }
        if (image.getWidth() < imageSize.width || image.getHeight() < imageSize.height) // Very simple testing for required image type
            return "This is not required image!";
        String cardsFound = "";
        fileNo = counter; //DEBUG
        for (int n = 0; n < 5; n++) {
            cardNo = n + 1; //DEBUG
            cardsFound += identifyCard(image, cards[n] /*[*/, n, nominalHash, suitHash /*]*/);
        }
        return cardsFound;
    }

    private String identifyCard(BufferedImage image, Point card /*[*/, int cardNo, int[] nominalHash, int[] suitHash /*]*/) {
        Color middle = new Color(image.getRGB(card.x + half(cardSize.width), card.y + half(cardSize.height)));
        if (middle.getRed() < colorThreshold && middle.getGreen() < colorThreshold && middle.getBlue() < colorThreshold) // Card not found at this position - skip position.
            return "";

        checkNo = 1; //DEBUG
        String nominal = identifyNominalOrSuit(image, (card.x + nominalShift.width), (card.y + nominalShift.height), nominalImages, nominalNames, nominalSize /*[*/, cardNo, nominalHash /*]*/);
        checkNo = 2; //DEBUG
        String suit = identifyNominalOrSuit(image, (card.x + cardSize.width) - suitShift.width, (card.y + cardSize.height) - suitShift.height, suitImages, suitNames, suitSize /*[*/, cardNo, suitHash/*]*/);
        return nominal + suit.charAt(0);
    }

    private String identifyNominalOrSuit(BufferedImage image, int x, int y, ArrayList<BufferedImage> images, String[] names, Dimension size, /*[*/ int cardNo, int[] cardsHash /*]*/) {
        String result = "?";
        int minHash = Integer.MAX_VALUE, minHashAt = -1, hashArray[] = new int[names.length];
        for (int nameNo = 0; nameNo < names.length; nameNo++) {
            int hash = calcualateXorHash(image, x - half(size.width), y - half(size.height), images.get(nameNo), size, nameNo);
            if (hash < minHash) {
                minHash = hash;
                minHashAt = nameNo;
            }
        }
        cardsHash[cardNo] = minHash; //DEBUG
        if (minHash < xorHashThreshold)
            result = names[minHashAt];
        return result;
    }

    private int calcualateXorHash(BufferedImage image, int shiftX, int shiftY, BufferedImage image2, Dimension referenceSize, int nominalOrSuitNo) {
        int extraShiftX = nominalNames[nominalOrSuitNo] == "10" ? extra10Shift.width : 0; // Workaround -- shift "10" a little more as it is more wide than others
        BufferedImage image1 =
                getBlackAndWhiteImage(image.getSubimage(shiftX - half(delta.width), shiftY - half(delta.height), referenceSize.width + delta.width + extraShiftX, referenceSize.height + delta.height), true);
        /*[*/
        if (fileNo == 1 && cardNo == 3 && ((checkNo == 1 && nominalNames[nominalOrSuitNo] == "10") || (checkNo == 2 && suitNames[nominalOrSuitNo] == "?????"))) {
            int x=1;
        }
        /*]*/
        int shiftXa = half(image1.getWidth() - image2.getWidth()) - 1 + half(delta.width);
        int shiftYa = half(image1.getHeight() - image2.getHeight()) - 1 + half(delta.height);
        int hash = -1, minHash = Integer.MAX_VALUE;
        for (int deltaX : arrayDeltaX) {
            for (int deltaY : arrayDeltaY) {
                if ((hash = calculateXorHash(image1, shiftXa + deltaX, shiftYa + deltaY, image2)) < minHash)
                    minHash = hash;
            }
        }
        return minHash;
    }

    private int calculateXorHash(BufferedImage image1, int shiftX, int shiftY, BufferedImage image2) {
        int hash = 0;
    outer:
        for (int y = 0; y < image2.getHeight(); y++) {
            for (int x = 0; x < image2.getWidth(); x++) {
                if (x + shiftX >= image1.getWidth() || y + shiftY >= image1.getHeight())
                    continue;
                if ((image1.getRGB(x + shiftX, y + shiftY) ^ image2.getRGB(x, y)) != 0)
                    hash++;
                if (hash > xorHashThreshold) // Break as hash too large already, don't check further
                    break outer;
            }
        }
        return hash;
    }

    private void fixGrayAndOrangeColor(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) == grayRGB)
                    image.setRGB(x, y, whiteRGB);
            }
        }
    }

    private BufferedImage getBlackAndWhiteImage(BufferedImage image, boolean fixColors) {
        if (fixColors)
            fixGrayAndOrangeColor(image);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                result.setRGB(x, y, color.getRGB() == whiteRGB ? whiteRGB : blackRGB);
            }
        }
        return result;
    }

    private int half(int value) {
        return value >> 1;
    }
}
