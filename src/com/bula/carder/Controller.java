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

    private final short cardX[] = { 143, 215, 286, 358, 429 }, cardY = 585, cardSizeX = 64, cardSizeY = 85; // Hardcoded for now
    private final short nominalShiftX = 15, nominalShiftY = 18, suitShiftX = 24, suitShiftY = 20; // Hardcoded for now

    private int nominalSize[] = {0, 0}, suitSize[] = {0, 0}; // Calculated maximum size for reference symbols (nominals & suits)

    private final int deltaX = 7, deltaY = 7; // Extend rectangle a little for proper identification of nominal or suit
    private final int xorHashThreshold = 50; // How much non-white pixels are allowed during XOR operation
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
        arrayDeltaX = createArrayWithDeltas(deltaX);
        arrayDeltaY = createArrayWithDeltas(deltaY);
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

    public void preloadImages(String[] names, ArrayList<BufferedImage> images, int[] size) throws Exception {
        for (String name : names) {
            BufferedImage image = ImageIO.read(classLoader.getResourceAsStream(name + ".png"));
            images.add(getBlackAndWhiteImage(image, false)); // Store black & white image
            size[0] = Math.max(image.getWidth(), size[0]);
            size[1] = Math.max(image.getHeight(), size[1]);
        }
    }

    public String identifyCards(int counter, File file) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        }
        catch (Exception ex) {
            return("Can not load image '" + file.getName() + "'! Skipped for now!");
        }
        if (image.getWidth() < 636 || image.getHeight() < 1166) // Very simple testing for required image type
            return "This is not required image!";
        String cards = "";
        fileNo = counter; //DEBUG
        for (int n = 0; n < 5; n++) {
            cardNo = n + 1; //DEBUG
            cards += identifyCard(image, cardX[n], cardY);
        }
        return cards;
    }

    private String identifyCard(BufferedImage image, int x, int y) {
        Color middle = new Color(image.getRGB(x + half(cardSizeX), y + half(cardSizeY)));
        if (middle.getRed() < 100 && middle.getGreen() < 100 && middle.getBlue() < 100) // Card not found at this position - skip position.
            return "";

        checkNo = 1; //DEBUG
        String nominal = identifyNominalOrSuit(image, (x + nominalShiftX), (y + nominalShiftY), nominalImages, nominalNames, nominalSize);
        checkNo = 2; //DEBUG
        String suit = identifyNominalOrSuit(image, (x + cardSizeX) - suitShiftX, (y + cardSizeY) - suitShiftY, suitImages, suitNames, suitSize);
        return nominal + suit.charAt(0);
    }

    private String identifyNominalOrSuit(BufferedImage image, int x, int y, ArrayList<BufferedImage> images, String[] names, int[] size) {
        String result = "?";
        for (int n = 0; n < names.length; n++) {
            int hash = calcualateXorHash(image, x - half(size[0]), y - half(size[1]), images.get(n), n, size);
            if (hash < xorHashThreshold) {
                result = names[n];
                break;
            }
        }
        return result;
    }

    private int calcualateXorHash(BufferedImage image, int shiftX, int shiftY, BufferedImage image2, int n, int[] referenceSize) {
        BufferedImage image1 =
                getBlackAndWhiteImage(image.getSubimage(shiftX - half(deltaX), shiftY - half(deltaY), referenceSize[0] + deltaX + (n==9?5:0), referenceSize[1] + deltaY), true);
        //DEBUG START
        if (fileNo == 194 && cardNo == 2 && checkNo == 1 && n == 9) {
            int x=1;
        }
        //DEBUG END
        int shiftXa = half(image1.getWidth() - image2.getWidth()) - 1 + half(deltaX);
        int shiftYa = half(image1.getHeight() - image2.getHeight()) - 1 + half(deltaY);
        int hash = xorHashThreshold + 1; // Set as "Not identified" initially
    outer:
        for (int xd : arrayDeltaX) {
            for (int yd : arrayDeltaY) {
                if ((hash = calculateXorHash(image1, shiftXa + xd, shiftYa + yd, image2)) < xorHashThreshold)
                    break outer;
            }
        }
        return hash;
    }

    private int calculateXorHash(BufferedImage image1, int shiftX, int shiftY, BufferedImage image2) {
        int hash = 0;
    outer:
        for (int y = 0; y < image2.getHeight() - 1; y++) {
            for (int x = 0; x < image2.getWidth() - 1; x++) {
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
                Color color = new Color(image.getRGB(x, y));
                if (color.getRed() == 120 && color.getGreen() == 120 && color.getBlue() == 120)
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
