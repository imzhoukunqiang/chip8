package com.github.zkq.emulator.chip8;

import java.util.Observable;

/**
 * Date:2020/3/31 11:03
 *
 * @author zhoukq
 */
public class Screen extends Observable {
    public static final int HIGH = 32;
    public static final int WIDTH = 64;
    private boolean[][] pixels;

    public Screen() {
        this.pixels = new boolean[WIDTH][HIGH];
    }

    Screen setPixel(int x, int y, boolean value) {
        this.pixels[x][y] = value;
        return this;
    }

    public boolean[][] getPixels() {
        return pixels;
    }

    void setPixels(boolean[][] pixels) {
        this.pixels = pixels;
    }

    void clear() {
        boolean[][] booleans = new boolean[WIDTH][HIGH];
        this.setPixels(booleans);
    }



}
