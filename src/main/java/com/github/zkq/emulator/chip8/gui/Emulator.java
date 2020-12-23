package com.github.zkq.emulator.chip8.gui;

import com.github.zkq.emulator.chip8.core.Chip8;
import com.github.zkq.emulator.chip8.core.Keyboard;
import com.github.zkq.emulator.chip8.core.Screen;

import javax.swing.*;
import java.awt.*;

/**
 * Date:2020/4/14 11:26
 *
 * @author zhoukq
 */
public class Emulator extends JFrame {

    private static final int E_HEIGHT = Screen.HIGH * 5;
    private static final int E_WIDTH = Screen.WIDTH * 5;

    public Emulator() {
        super("Chip 8 ");
        Keyboard keyboard = new Keyboard();
        Screen screen = new Screen();

        Canvas canvas = new Canvas();

        Container panel = this.getContentPane();


        this.pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }

    public static void main(String[] args) {
        JFrame jf = new JFrame("用户登录");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 第 1 个 JPanel, 使用默认的浮动布局
        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("用户名"));
        panel01.add(new JTextField(10));

        // 第 2 个 JPanel, 使用默认的浮动布局
        JPanel panel02 = new JPanel();
        panel02.add(new JLabel("密   码"));
        panel02.add(new JPasswordField(10));

        // 第 3 个 JPanel, 使用浮动布局, 并且容器内组件居中显示
        JPanel panel03 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel03.add(new JButton("登录"));
        panel03.add(new JButton("注册"));

        // 创建一个垂直盒子容器, 把上面 3 个 JPanel 串起来作为内容面板添加到窗口
        Box vBox = Box.createVerticalBox();
        vBox.add(panel01);
        vBox.add(panel02);
        vBox.add(panel03);

        jf.setContentPane(vBox);

        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }


}
