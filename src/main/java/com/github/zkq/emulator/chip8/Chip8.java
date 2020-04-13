package com.github.zkq.emulator.chip8;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Date:2020/3/31 9:23
 *
 * @author zhoukq
 */
public class Chip8 {

    private static final Logger LOGGER = Logger.getLogger(Chip8.class.getName());

    // cpu frequency 500Hz
    private final int frequency = 500;
    private final ScheduledExecutorService mainThread = new ScheduledThreadPoolExecutor(1,
                                                                                        r -> new Thread("main thread"));
    /**
     * 4,096 b
     */
    private final byte[] memory = new byte[0x1000];
    /**
     * 16 Registers
     * v0,v1,v2...vF
     */
    private final byte[] v = new byte[0x10];
    /**
     * stack  16 16-bit
     */
    private final int[] stack = new int[16];
    private final Screen screen = new Screen();
    /*
     * Register I. This register is generally used to store memory addresses.
     */
    private int i = 0;
    /**
     * delay timer 8-bit
     */
    private int dt = 0;
    /**
     * Sound timer 8-bit
     */
    private byte st = 0;
    /**
     * program counter 16-bit
     */
    private int pc = 0;
    /**
     * stack pointer (SP)  8-bit
     */
    private byte sp = 0;
    private ScheduledFuture<?> mainLoop;

    public Chip8(byte[] rom) {
        Objects.requireNonNull(rom, "Empty Rom!");
        Commons.assertCondition(rom.length > (0x1000 - 0x200),
                                (Supplier<IllegalArgumentException>) () -> new IllegalArgumentException("rom is too big"));
        System.arraycopy(rom, 0, memory, 0x200, rom.length);
        this.pc = 0x200;
    }

    public synchronized void start() {

        if (mainLoop == null || mainLoop.isDone() || mainLoop.isCancelled()) {
            mainLoop = mainThread.scheduleAtFixedRate(this::loop, 0, 1000 * 1000 / frequency, TimeUnit.MILLISECONDS);
        }

    }

    public synchronized void pause() {
        if (Commons.isNull(mainLoop)) {
            mainLoop.cancel(false);
        }
    }

    public synchronized void resume() {
        this.start();
    }

    private void loop() {
        int op = memory[pc++] << 8 | memory[pc++];
        this.execute(op);
    }

    private void execute(int op) {
        int x = op & 0x0F00 >> 8;
        int y = op & 0x00F0 >> 4;
        switch (op >> 12) {
            case 0x0:
                switch (op) {
                    /*
                       00E0 - CLS
                       Clear the display.
                     */
                    case 0x00E0:
                        this.screen.clear();
                        break;

                    /*
                    00EE - RET
                    Return from a subroutine.

                    The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
                     */
                    case 0x00EE:
                        this.pc = this.stack[sp--];
                        break;
                    default:
                        // unknown operator
                        this.unknownOp(op);
                        break;
                }
                break;

            /*
            1nnn - JP addr
            Jump to location nnn.
             */
            case 0x1:
                this.pc = op & 0x0FFF;
                break;

            /*
            2nnn - CALL addr
            Call subroutine at nnn.

            The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
             */
            case 0x2:
                this.stack[++sp] = this.pc;
                this.pc = op & 0x0FFF;
                break;

            /*
            3xkk - SE Vx, byte
            Skip next instruction if Vx = kk.

            The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
             */
            case 0x3:
                if (v[x] == (op & 0xFF)) {
                    pc += 2;
                }
                break;

            /*
            4xkk - SNE Vx, byte
            Skip next instruction if Vx != kk.

            The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
             */
            case 0x4:
                if (v[x] != (op & 0xFF)) {
                    pc += 2;
                }
                break;

            /*
            5xy0 - SE Vx, Vy
            Skip next instruction if Vx = Vy.

            The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
             */
            case 0x5:
                switch (op & 0xF) {
                    case 0:
                        if (v[x] == v[y]) {
                            pc += 2;
                        }
                        break;
                    default:
                        this.unknownOp(op);
                        break;
                }
                break;

            /*
            6xkk - LD Vx, byte
            Set Vx = kk.

            The interpreter puts the value kk into register Vx.
             */
            case 0x6:
                v[x] = (byte) (op & 0x00FF);
                break;

            /*
            7xkk - ADD Vx, byte
            Set Vx = Vx + kk.

            Adds the value kk to the value of register Vx, then stores the result in Vx.
             */
            case 0x7:
                v[x] += (byte) (op & 0x00FF);
                break;

            case 0x8:
                switch (op & 0xF) {
                    /*
                    8xy0 - LD Vx, Vy
                    Set Vx = Vy.

                    Stores the value of register Vy in register Vx.
                     */
                    case 0x0:
                        v[x] = v[y];
                        break;
                    /*
                    8xy1 - OR Vx, Vy
                    Set Vx = Vx OR Vy.

                    Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the corrseponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0.
                    */
                    case 0x1:
                        v[x] |= v[y];
                        break;

                    /*

                    8xy2 - AND Vx, Vy
                    Set Vx = Vx AND Vy.

                    Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the corrseponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
                     */
                    case 0x2:
                        v[x] &= v[y];
                        break;
                     /*
                    8xy3 - XOR Vx, Vy
                    Set Vx = Vx XOR Vy.

                    Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx. An exclusive OR compares the corrseponding bits from two values, and if the bits are not both the same, then the corresponding bit in the result is set to 1. Otherwise, it is 0.
                    */
                    case 0x3:
                        v[x] ^= v[y];
                        break;

                     /*

                    8xy4 - ADD Vx, Vy
                    Set Vx = Vx + Vy, set VF = carry.

                    The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
                    */
                    case 0x4:
                        int sum = (int) v[x] + v[y];
                        v[0xf] = (byte) (sum > 255 ? 1 : 0);
                        v[x] = (byte) (sum & 0xFF);
                        break;

                    /*
                    8xy5 - SUB Vx, Vy
                    Set Vx = Vx - Vy, set VF = NOT borrow.

                    If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
                    */
                    case 0x5:
                        v[0xf] = (byte) (v[x] > v[y] ? 1 : 0);
                        v[x] -= v[y];
                        break;
                    /*
                    8xy6 - SHR Vx {, Vy}
                    Set Vx = Vx SHR 1.

                    If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
                    */
                    case 0x6:
                        v[0xf] = (byte) (v[x] & 1);
                        v[x] >>>= 1;
                        break;
                    /*
                    8xy7 - SUBN Vx, Vy
                    Set Vx = Vy - Vx, set VF = NOT borrow.

                    If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.

                    */
                    case 0x7:
                        v[0xf] = (byte) (v[y] > v[x] ? 1 : 0);
                        v[y] -= v[x];
                        break;

                    /*

                    8xyE - SHL Vx {, Vy}
                    Set Vx = Vx SHL 1.

                    If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.

                     */
                    case 0xE:
                        v[0xf] = (byte) (v[x] & 0x80);
                        v[x] <<= 1;
                        break;

                    default:
                        unknownOp(op);
                        break;
                }
                break;
            /*
            9xy0 - SNE Vx, Vy
            Skip next instruction if Vx != Vy.

            The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
             */
            case 0x9:
                pc += (v[x] != v[y]) ? 2 : 0;
                break;
            /*
            Annn - LD I, addr
            Set I = nnn.

            The value of register I is set to nnn.
             */
            case 0xA:
                i = op & 0x0FFF;
                break;
            /*
            Bnnn - JP V0, addr
            Jump to location nnn + V0.

            The program counter is set to nnn plus the value of V0.
            */
            case 0xB:
                pc = op & 0x0FFF + v[0];
                break;
            /*
            Cxkk - RND Vx, byte
            Set Vx = random byte AND kk.

            The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk. The results are stored in Vx. See instruction 8xy2 for more information on AND.
             */
            case 0xC:
                v[x] = (byte) (random(0xFF) & 0x00FF);
                break;
             /*
             Dxyn - DRW Vx, Vy, nibble
             Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.

             The interpreter reads n bytes from memory, starting at the address stored in I. These bytes are then displayed as sprites on screen at coordinates (Vx, Vy). Sprites are XORed onto the existing screen. If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0. If the sprite is positioned so part of it is outside the coordinates of the display, it wraps around to the opposite side of the screen. See instruction 8xy3 for more information on XOR, and section 2.4, Display, for more information on the Chip-8 screen and sprites.
              */
            // TODO: 2020/4/13 21:44
            case 0xD:

            default:
                // unknown operator
                this.unknownOp(op);
                break;

        }
    }

    private byte random(int i) {
        return (byte) (Math.random() * i);
    }

    private void unknownOp(int op) {

    }


}
