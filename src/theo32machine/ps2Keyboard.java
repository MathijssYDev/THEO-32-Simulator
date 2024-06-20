package theo32machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

public class ps2Keyboard extends JFrame  {
    byte register1 = 0;
    byte register2 = 0;
    byte register3 = 0;

    public ps2Keyboard() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Keyboard");
        setSize(100, 50);
        setLocationRelativeTo(null);

        Keys keys = new Keys(this);
        addKeyListener(keys);

        JLabel text = new JLabel();
        text.setText("Type...");

        add(text);
        pack();

        setVisible(true);
    }
    public int getRegister1() {
        return register1;
    }
    public int getRegister2() {
        return register2;
    }
    public int getRegister3() {
        return register3;
    }
}
class Keys implements KeyListener {
    HashMap<Integer, String> extendedKeycodeMap = new HashMap<>();
    ps2Keyboard parent;
    public Keys (ps2Keyboard parent){
        this.parent = parent;

        extendedKeycodeMap.put(KeyEvent.VK_ESCAPE, "F076");
        extendedKeycodeMap.put(KeyEvent.VK_K, "F042");
        extendedKeycodeMap.put(KeyEvent.VK_F1, "F005");
        extendedKeycodeMap.put(KeyEvent.VK_L, "F04B");
        extendedKeycodeMap.put(KeyEvent.VK_F2, "F006");
        extendedKeycodeMap.put(KeyEvent.VK_SEMICOLON, "F04C");
        extendedKeycodeMap.put(KeyEvent.VK_F3, "F004");
        extendedKeycodeMap.put(KeyEvent.VK_QUOTE, "F052");
        extendedKeycodeMap.put(KeyEvent.VK_F4, "F00C");
        extendedKeycodeMap.put(KeyEvent.VK_ENTER, "F05A");
        extendedKeycodeMap.put(KeyEvent.VK_F5, "F003");
        extendedKeycodeMap.put(KeyEvent.VK_SHIFT, "F012");
        extendedKeycodeMap.put(KeyEvent.VK_F6, "F00B");
        extendedKeycodeMap.put(KeyEvent.VK_Z, "F01A");
        extendedKeycodeMap.put(KeyEvent.VK_F7, "F083");
        extendedKeycodeMap.put(KeyEvent.VK_X, "F022");
        extendedKeycodeMap.put(KeyEvent.VK_F8, "F00A");
        extendedKeycodeMap.put(KeyEvent.VK_C, "F021");
        extendedKeycodeMap.put(KeyEvent.VK_F9, "F001");
        extendedKeycodeMap.put(KeyEvent.VK_V, "F02A");
        extendedKeycodeMap.put(KeyEvent.VK_F10, "F009");
        extendedKeycodeMap.put(KeyEvent.VK_B, "F032");
        extendedKeycodeMap.put(KeyEvent.VK_F11, "F078");
        extendedKeycodeMap.put(KeyEvent.VK_N, "F031");
        extendedKeycodeMap.put(KeyEvent.VK_F12, "F007");
        extendedKeycodeMap.put(KeyEvent.VK_M, "F03A");
        extendedKeycodeMap.put(KeyEvent.VK_PRINTSCREEN, "E012E07C E0F07CE0F012");
        extendedKeycodeMap.put(KeyEvent.VK_COMMA, "F041");
        extendedKeycodeMap.put(KeyEvent.VK_SCROLL_LOCK, "F07E");
        extendedKeycodeMap.put(KeyEvent.VK_PERIOD, "F049");
        extendedKeycodeMap.put(KeyEvent.VK_PAUSE, "E11477E1F014E077");
        extendedKeycodeMap.put(KeyEvent.VK_SLASH, "F04A");
        extendedKeycodeMap.put(KeyEvent.VK_BACK_QUOTE, "F00E");
        extendedKeycodeMap.put(KeyEvent.VK_1, "F016");
        extendedKeycodeMap.put(KeyEvent.VK_CONTROL, "F014"); // Left Ctrl
        extendedKeycodeMap.put(KeyEvent.VK_2, "F01E");
        extendedKeycodeMap.put(KeyEvent.VK_WINDOWS, "E01F"); // Left Windows
        extendedKeycodeMap.put(KeyEvent.VK_3, "F026");
        extendedKeycodeMap.put(KeyEvent.VK_ALT, "F011"); // Left Alt
        extendedKeycodeMap.put(KeyEvent.VK_4, "F025");
        extendedKeycodeMap.put(KeyEvent.VK_SPACE, "F029");
        extendedKeycodeMap.put(KeyEvent.VK_5, "F02E");
        extendedKeycodeMap.put(KeyEvent.VK_6, "F036");
        extendedKeycodeMap.put(KeyEvent.VK_7, "F03D");
        extendedKeycodeMap.put(KeyEvent.VK_CONTEXT_MENU, "E02F"); // Menu
        extendedKeycodeMap.put(KeyEvent.VK_8, "F03E");
        extendedKeycodeMap.put(KeyEvent.VK_9, "F046");
        extendedKeycodeMap.put(KeyEvent.VK_INSERT, "E070");
        extendedKeycodeMap.put(KeyEvent.VK_0, "F045");
        extendedKeycodeMap.put(KeyEvent.VK_HOME, "E06C");
        extendedKeycodeMap.put(KeyEvent.VK_MINUS, "F04E");
        extendedKeycodeMap.put(KeyEvent.VK_PAGE_UP, "E07D");
        extendedKeycodeMap.put(KeyEvent.VK_EQUALS, "F055");
        extendedKeycodeMap.put(KeyEvent.VK_DELETE, "E071");
        extendedKeycodeMap.put(KeyEvent.VK_BACK_SPACE, "F066");
        extendedKeycodeMap.put(KeyEvent.VK_END, "E069");
        extendedKeycodeMap.put(KeyEvent.VK_TAB, "F00D");
        extendedKeycodeMap.put(KeyEvent.VK_PAGE_DOWN, "E07A");
        extendedKeycodeMap.put(KeyEvent.VK_Q, "F015");
        extendedKeycodeMap.put(KeyEvent.VK_UP, "E075");
        extendedKeycodeMap.put(KeyEvent.VK_W, "F01D");
        extendedKeycodeMap.put(KeyEvent.VK_LEFT, "E06B");
        extendedKeycodeMap.put(KeyEvent.VK_E, "F024");
        extendedKeycodeMap.put(KeyEvent.VK_DOWN, "E072");
        extendedKeycodeMap.put(KeyEvent.VK_R, "F02D");
        extendedKeycodeMap.put(KeyEvent.VK_RIGHT, "E074");
        extendedKeycodeMap.put(KeyEvent.VK_T, "F02C");
        extendedKeycodeMap.put(KeyEvent.VK_NUM_LOCK, "F077");
        extendedKeycodeMap.put(KeyEvent.VK_Y, "F035");
        extendedKeycodeMap.put(KeyEvent.VK_DIVIDE, "E04A"); // Numpad /
        extendedKeycodeMap.put(KeyEvent.VK_U, "F03C");
        extendedKeycodeMap.put(KeyEvent.VK_MULTIPLY, "F07C"); // Numpad *
        extendedKeycodeMap.put(KeyEvent.VK_I, "F043");
        extendedKeycodeMap.put(KeyEvent.VK_SUBTRACT, "F07B"); // Numpad -
        extendedKeycodeMap.put(KeyEvent.VK_O, "F044");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD7, "F06C"); // Numpad 7
        extendedKeycodeMap.put(KeyEvent.VK_P, "F04D");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD8, "F075"); // Numpad 8
        extendedKeycodeMap.put(KeyEvent.VK_OPEN_BRACKET, "F054");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD9, "F07D"); // Numpad 9
        extendedKeycodeMap.put(KeyEvent.VK_CLOSE_BRACKET, "F05B");
        extendedKeycodeMap.put(KeyEvent.VK_ADD, "F079"); // Numpad +
        extendedKeycodeMap.put(KeyEvent.VK_BACK_SLASH, "F05D");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD4, "F06B"); // Numpad 4
        extendedKeycodeMap.put(KeyEvent.VK_CAPS_LOCK, "F058");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD5, "F073"); // Numpad 5
        extendedKeycodeMap.put(KeyEvent.VK_A, "F01C");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD6, "F074"); // Numpad 6
        extendedKeycodeMap.put(KeyEvent.VK_S, "F01B");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD1, "F069"); // Numpad 1
        extendedKeycodeMap.put(KeyEvent.VK_D, "F023");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD2, "F072"); // Numpad 2
        extendedKeycodeMap.put(KeyEvent.VK_F, "F02B");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD3, "F07A"); // Numpad 3
        extendedKeycodeMap.put(KeyEvent.VK_G, "F034");
        extendedKeycodeMap.put(KeyEvent.VK_NUMPAD0, "F070"); // Numpad 0
        extendedKeycodeMap.put(KeyEvent.VK_H, "F033");
        extendedKeycodeMap.put(KeyEvent.VK_DECIMAL, "F071"); // Numpad .
        extendedKeycodeMap.put(KeyEvent.VK_J, "F03B");
        extendedKeycodeMap.put(KeyEvent.VK_ENTER, "E05A"); // Numpad Enter
    }
    @Override
    public void keyPressed(KeyEvent e) {
        String key = getKey(e);
        if (key.startsWith("F0")) key = key.replace("F0","");
        handleKey(key);
    }
    @Override
    public void keyReleased(KeyEvent e) {
        String code = getKey(e);
        if (code.startsWith("E0")) handleKey("E0F0"+getKey(e).replace("E0",""));
        if (code.startsWith("F0")) handleKey(getKey(e));
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public void handleKey(String key) {
        String[] bytes = key.split("(?<=\\G.{"+2+"})");
        for (int b = 0; b < bytes.length; b++) {
            byte by = (byte) Integer.parseInt(bytes[b],16);
            parent.register3 = parent.register2;
            parent.register2 = parent.register1;
            parent.register1 = by;
            //System.out.println(bytes[b]);
        }
    }
    public String getKey(KeyEvent e) {
        return extendedKeycodeMap.get(e.getKeyCode());
    }
}