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
    private final ScheduledExecutorService mainThread = new ScheduledThreadPoolExecutor(1, r -> new Thread("main thread"));
    /**
     * 4,096 kb
     */
    private final byte[] memory = new byte[0x1000];
    /**
     * 16 Registers
     * v0,v1,v2...vf
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
        Commons.assertCondition(rom.length < (0x1000 - 0x200),
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
        switch (op >> 12) {
            case 0x0:
                switch (op) {
                    case 0x00E0:
                        /*
                           00E0 - CLS
                           Clear the display.
                         */
                        this.screen.clear();
                        break;

                    case 0x00EE:
                        /*
                        00EE - RET
                        Return from a subroutine.

                        The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
                         */
                        this.pc = this.stack[pc--];
                        break;
                    default:
                        // unknown operator
                        this.unknownOp(op);
                        break;
                }
                break;
            case 0x1:
                /*
                1nnn - JP addr
                Jump to location nnn.
                 */
                this.pc = op & 0x0FFF;
                break;
            case 0x2:
                /*
                2nnn - CALL addr
                Call subroutine at nnn.

                The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
                 */
                this.stack[++sp] = this.pc;
                this.pc = op & 0x0FFF;
                break;
            case 0x3:
                /*
                3xkk - SE Vx, byte
                Skip next instruction if Vx = kk.

                The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
                 */
                if (v[op & 0x0F00] == (op & 0xFF)) {
                    pc += 2;
                }
                break;
            case 0x4:
                /*
                4xkk - SNE Vx, byte
                Skip next instruction if Vx != kk.

                The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
                 */
                if (v[op & 0x0F00] != (op & 0xFF)) {
                    pc += 2;
                }
                break;
            case 0x5:
                switch (op & 0xF) {
                    case 0:
                        /*
                        5xy0 - SE Vx, Vy
                        Skip next instruction if Vx = Vy.

                        The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
                         */
                        if (v[op & 0x0F00] == v[op & 0x00F0]) {
                            pc += 2;
                        }
                        break;
                    default:
                        this.unknownOp(op);
                        break;
                }
                break;
            case 0x6:
                /*
                6xkk - LD Vx, byte
                Set Vx = kk.

                The interpreter puts the value kk into register Vx.
                 */
                v[op & 0x0F00] = (byte) (op & 0x00FF);
                break;
            case 0x7:
                /*
                7xkk - ADD Vx, byte
                Set Vx = Vx + kk.

                Adds the value kk to the value of register Vx, then stores the result in Vx.
                 */
                v[op & 0x0F00] += (byte) (op & 0x00FF);
                break;
            case 0x8:
                switch (op & 0xF) {
                    case 0x0:
                        /*
                        8xy0 - LD Vx, Vy
                        Set Vx = Vy.

                        Stores the value of register Vy in register Vx.
                         */
                        v[op & 0x0F00] = v[op & 0x00F0];
                        break;
// TODO: 2020/4/13 18:13 
                    default:
                        unknownOp(op);
                        break;
                }
                break;
            default:
                // unknown operator
                this.unknownOp(op);
                break;

        }
    }

    private void unknownOp(int op) {

    }


}
