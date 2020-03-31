package com.github.zkq.emulator.chip8;

/**
 * Date:2020/3/31 9:23
 *
 * @author zhoukq
 */
public class Chip8 {
    /**
     * 4,096 kb
     */
    private byte[] memory = new byte[0x1000];

    /**
     * 16 Registers
     * v0,v1,v2...vf
     */
    private byte[] v = new byte[0x10];

    /*
     * Register I. This register is generally used to store memory addresses.
     */
    private short i = 0;


    //delay timer 8-bit
    private byte dt = 0;

    // Sound timer 8-bit
    private byte st = 0;

    //program counter 16-bit
    private short pc = 0;

    //stack pointer (SP)  8-bit
    private byte sp = 0;

    //stack  16 16-bit
    private short[] stack = new short[16];

}
