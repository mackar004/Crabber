/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crabber;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.*;

/**
 * 
 * @author m&m
 */

public class Midlet extends MIDlet implements CommandListener {

    private Display display;
    StartGame startGame;
    Gra gra;
    PauseGame pauseGame;

    private final Command newGame, pause, resume, endGame;

    Midlet() {
        startGame = new StartGame();
        pauseGame = new PauseGame();
        gra = new Gra();

        newGame = new Command("Nowa gra", Command.ITEM, 1);
        pause = new Command("Pauza", Command.ITEM, 2);
        resume = new Command("Graj dalej", Command.ITEM, 3);
        endGame = new Command("Koniec", Command.ITEM, 4);
    }

    // new game
    public void newGame() {
        gra = new Gra();
        gra.addCommand(pause);
        gra.addCommand(endGame);
        gra.setCommandListener(this);
        display.setCurrent(gra);
    }

    //==================
    public void startApp() {
        // gets the Display object that is unique to this MIDlet
        display = Display.getDisplay(this);
        startGame.addCommand(newGame);
        gra.addCommand(pause);
        gra.addCommand(endGame);
        pauseGame.addCommand(resume);
        pauseGame.addCommand(endGame);

        startGame.setCommandListener(this);
        gra.setCommandListener(this);
        pauseGame.setCommandListener(this);

        // display start window when game start 
        display.setCurrent(startGame);
    }

    public void pauseApp() {
        display.setCurrent(pauseGame);
        gra.pause();
    }

    public void end() {
        // reset frog (used for end game when game paused)
        display.setCurrent(startGame);
        gra.resetCrab();
    }

    public void resumeApp() {
        display.setCurrent(gra);
        Gra.resume();
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    public void commandAction(Command c, Displayable d) {
        String label = c.getLabel();
        if (label.equals("Nowa gra")) {
            newGame();
        }
        if (label.equals("Pauza")) {
            pauseApp();
        }
        if (label.equals("Graj dalej")) {
            resumeApp();
        }

        if (label.equals("Koniec")) {
            end();
        }
        if (label.equals("Exit")) {
            destroyApp(true);
        }
    }
}
