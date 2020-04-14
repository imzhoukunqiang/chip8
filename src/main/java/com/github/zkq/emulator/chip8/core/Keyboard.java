package com.github.zkq.emulator.chip8.core;

import java.awt.event.KeyEvent;

/**
 * Date:2020/4/14 10:23
 *
 * @author zhoukq
 */
public class Keyboard {

    private static final int[] DEFAULT_KEY_MAP = {
            KeyEvent.VK_4, // Key 1
            KeyEvent.VK_5, // Key 2
            KeyEvent.VK_6, // Key 3
            KeyEvent.VK_7, // Key 4
            KeyEvent.VK_R, // Key 5
            KeyEvent.VK_Y, // Key 6
            KeyEvent.VK_U, // Key 7
            KeyEvent.VK_F, // Key 8
            KeyEvent.VK_G, // Key 9
            KeyEvent.VK_H, // Key A
            KeyEvent.VK_J, // Key B
            KeyEvent.VK_V, // Key C
            KeyEvent.VK_B, // Key D
            KeyEvent.VK_N, // Key E
            KeyEvent.VK_M, // Key F
    };

    private int[] keyMap = DEFAULT_KEY_MAP;

    private int currentKeyPressed = 0;

    public int getCurrentKeyPressed() {
        return currentKeyPressed;
    }

    public void pressKey(int keyCode) {
        for (int i = 0; i < keyMap.length; i++) {
            if (keyMap[i] == keyCode) {
                currentKeyPressed = i + 1;
                break;
            }
        }
    }

    // should block waiting
    public int waitForKey() {
        // TODO: 2020/4/14 10:43
        return 0;
    }
}
