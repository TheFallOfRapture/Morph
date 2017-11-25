package com.morph.engine.core.gui;

import com.morph.engine.core.Game;
import com.morph.engine.debug.Console;
import com.morph.engine.events.ConsoleUpdateEvent;
import com.morph.engine.events.EventDispatcher;
import com.morph.engine.events.EventListener;
import com.morph.engine.events.KeyEvent;
import com.morph.engine.graphics.Color;
import com.morph.engine.graphics.Texture;
import com.morph.engine.math.Vector2f;
import com.morph.engine.newgui.ConsoleTextField;
import com.morph.engine.newgui.GUI;
import com.morph.engine.newgui.Panel;
import com.morph.engine.newgui.TextField;

/**
 * Created on 11/24/2017.
 */
public class ConsoleGUI extends GUI {
    private Console console;
    private int width;
    private int height;

    private ConsoleTextField consoleInput;
    private TextField consoleOutput;

    public ConsoleGUI(Game game, Console console, int width, int height) {
        super(game);
        this.console = console;
        this.width = width;
        this.height = height;

        EventDispatcher.INSTANCE.addEventHandler(this);
    }

    @Override
    public void load() {
        Panel consoleBG = new Panel(new Vector2f(0, height - 500), new Vector2f(width, 500), new Color(0, 1, 0, 0.3f), new Texture("textures/solid.png"));
        Panel consoleInputBG = new Panel(new Vector2f(0, height - 520), new Vector2f(width, 20), new Color(0, 0, 1, 0.3f), new Texture("textures/solid.png"));
        consoleInput = new ConsoleTextField(console, "", "C:/Windows/Fonts/Roboto-Regular.ttf", 16, new Color(1, 1, 1), new Vector2f(0, height - 515), -1200);
        consoleOutput = new TextField("Morph 0.5.15 - Console Output\n", "C:/Windows/Fonts/Roboto-Regular.ttf", 16, new Color(1, 1, 1, 0.7f), new Vector2f(10, height - 20), -1200);
        consoleBG.setDepth(-1000);
        consoleInputBG.setDepth(-1000);
        addElement(consoleBG);
        addElement(consoleInputBG);
        addElement(consoleInput);
        addElement(consoleOutput);
    }

    @EventListener(KeyEvent.class)
    public void onKeyEvent(KeyEvent e) {
        consoleInput.handleGUIKeyEvent(e);
    }

    @EventListener(ConsoleUpdateEvent.class)
    public void onConsoleUpdateEvent(ConsoleUpdateEvent e) {
        System.out.println("Reading to console: " + console.getLastLine());
        consoleOutput.addString(console.getLastLine() + "\n");
    }

    @Override
    public void unload() {

    }
}