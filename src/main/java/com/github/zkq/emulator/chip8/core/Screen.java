package com.github.zkq.emulator.chip8.core;

import java.util.Arrays;
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

    boolean xorPixel(int x, int y, boolean value) {
        boolean b = this.pixels[x][y];
        this.pixels[x][y] ^= value;
        return b & value;
    }

    public boolean[][] getPixels() {
        return pixels;
    }

    void clear() {
        for (boolean[] pixel : this.pixels) {
            Arrays.fill(pixel, false);
        }
    }

    public boolean xorPixels(int x, int y, byte pixels) {
        boolean r = false;
        for (int size = Byte.SIZE - 1; size >= 0; size--) {
            if (xorPixel(x + size, y, (pixels & 0x1) == 1)) {
                r = true;
            }
            pixels >>= 1;
        }
        super.notifyObservers();
        return r;
    }


}
