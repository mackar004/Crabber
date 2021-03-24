/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crabber;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

/**
 *
 * @author m
 */

public class Gra extends Canvas implements Runnable {

    private Image level, player, rcar, bcar;
    private final Sprite crabSprite;

    private final Sprite[] rcarS = new Sprite[5];
    private final Sprite[] bcarS = new Sprite[5];
    private int nrAutaR = 0;
    private int nrAutaB = 0;

    private final Sprite[] crabSpriteU = new Sprite[3];

    private int klawisz, wynik;

    private boolean fail = false;

    private static boolean PAUZA;

    //zapełnienie pasów pojazdami wraz z typem
    int[] pas1 = {0, 1};
    int[] pas2 = {0, 1};
    int[] pas3 = {0};
    int[] pas4 = {0, 1};
    int[] pas5 = {1};
    int[] pas6 = {0, 1};
    
    private int crabI = 0;
    
    //szybkość w ms odświeżania ekranu
    private int poziom = 500;

    //dane gracza
    private int xPlayer;
    private int yPlayer;
    private int zycia = 3;

    //współżędne odniesienia pojazdów na pasach
    private int xPas1, xPas2, xPas3, xPas4, xPas5, xPas6;
    private final int yPas1, yPas2, yPas3, yPas4, yPas5, yPas6;
    private int aktX;

    public Gra() {
        wynik = 0;
        PAUZA = false;

        try {
            player = Image.createImage("/crab.png");
            rcar = Image.createImage("/rcar.png");
            bcar = Image.createImage("/bcar.png");
        } catch (IOException e) {
            //rób nic
        }

        //Zapełnienie 2 tablic samochodów - czerwonej i niebieskiej
        for (int i = 0; i < (rcarS.length + bcarS.length); i++) {
            if ((double) i % 2 == 0) {
                rcarS[nrAutaR] = new Sprite(rcar);
                nrAutaR++;
            } else {
                bcarS[nrAutaB] = new Sprite(bcar);
                nrAutaB++;
            }
        }
        
        //zerowanie nrAuta
        nrAutaR = 0;
        nrAutaB = 0;

        //wątek - start
        (new Thread(this)).start();

        //ustawienie pozycji kraba
        xPlayer = (getWidth() / 2);
        yPlayer = 225;

        //pozycje początkowe na pasach
        xPas1 = 22;
        yPas1 = 225 - 30;

        xPas2 = 22;
        yPas2 = 225 - 60;

        xPas3 = 76;
        yPas3 = 225 - 90;

        xPas4 = 200;
        yPas4 = 225 - 120;

        xPas5 = 200;
        yPas5 = 225 - 150;

        xPas6 = 200;
        yPas6 = 225 - 180;

        crabSprite = new Sprite(player);
        crabSprite.defineReferencePixel(13, 14);
            
        for (int n = 0; n < rcarS.length; n++){
            rcarS[n].defineReferencePixel(22, 13);
        }
        
        for (int n = 0; n < bcarS.length; n++){
            bcarS[n].defineReferencePixel(22, 13);
        }

        for (int n = 0; n < 3; n++) {
            crabSpriteU[n] = new Sprite(player);
            crabSpriteU[n].defineReferencePixel(13, 14);
            crabSpriteU[n].setRefPixelPosition(-30, -30);
        }

        //pierwsze rysowanie planyszy
        repaint();
    }

    void pause() {
        PAUZA = true;
    }

    protected void paint(Graphics g) {
        try {
            level = Image.createImage("/level.png");
        } catch (IOException e) {
            g.setColor(0, 0, 0);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(0xffffff);
            g.drawString("Nie wczytano tła", 0, 0, Graphics.TOP | Graphics.LEFT);
        }
        //rysowanie tła
        g.drawImage(level, 0, 0, Graphics.TOP | Graphics.LEFT);
        
        //informacje
        g.setColor(0xffffff);
        g.drawString("Życia: " + zycia, 10, 250, Graphics.TOP | Graphics.LEFT);
        g.drawString("Wynik: " + wynik, 150, 250, Graphics.TOP | Graphics.LEFT);

        for (int n = 0; n < 3; n++) {
            crabSpriteU[n].paint(g);
        }

        //jeśli nie porażka to:
        if (!fail) {
            
            //gracz
            crabSprite.setRefPixelPosition(xPlayer, yPlayer);
            crabSprite.paint(g);
            
            //zerowanie licznika aut w tablicach
            nrAutaR = 0;
            nrAutaB = 0;

        //POJAZDY NA PASACH    
            //pojazdy na pasie 1
            for (int i = 0; i < pas1.length; i++) {
                //kontrola prędkości
                aktX = xPas1;
                //samochód czerwony
                if (pas1[i] == 0) {
                    //przewinięcie gdy poza ekranem
                    if (xPas1 >= getWidth() + 22) {
                        xPas1 = xPas1 - getWidth() - 45;
                    }
                    //rysowanie samochodu
                    rcarS[nrAutaR].setRefPixelPosition(xPas1, yPas1);
                    rcarS[nrAutaR].setTransform(Sprite.TRANS_NONE);
                    rcarS[nrAutaR].paint(g);
                    nrAutaR++;
                }
                //nastpęny samochód na pasie
                xPas1 += 150;

                //samochód niebieski
                if (pas1[i] == 1) {
                    //przewinięcie gdy poza ekranem
                    if (xPas1 >= getWidth() + 22) {
                        xPas1 = xPas1 - getWidth() - 45;
                    }
                    //rysowanie samochodu
                    bcarS[nrAutaB].setRefPixelPosition(xPas1, yPas1);
                    bcarS[nrAutaB].setTransform(Sprite.TRANS_NONE);
                    bcarS[nrAutaB].paint(g);
                    nrAutaB++;
                }
                //nastpęny samochód na pasie
                xPas1 += 150;

                //kontrola prędkości - część 2
                xPas1 = aktX;
            }
            //przewinięcie gdy poza ekranem
            if (xPas1 >= getWidth() + 22) {
                xPas1 = xPas1 - getWidth() - 45;
            }

            //pojazdy na pasie 2
            for (int i = 0; i < pas2.length; i++) {
                //kontrola prędkości
                aktX = xPas2;
                
                //czerwony
                if (pas2[i] == 0) {
                    //przewinięcie gdy poza ekranem
                    if (xPas2 >= getWidth() + 22) {
                        xPas2 = xPas2 - getWidth() - 45;
                    }
                    //generujAuto(int typ, int x, int y)
                    rcarS[nrAutaR].setRefPixelPosition(xPas2, yPas2);
                    rcarS[nrAutaR].setTransform(Sprite.TRANS_NONE);
                    rcarS[nrAutaR].paint(g);
                    nrAutaR++;
                }
                
                //nastpęny samochód na pasie
                xPas2 += 100;

                //niebieski    
                if (pas2[i] == 1) {
                    //przewinięcie gdy poza ekranem
                    if (xPas2 >= getWidth() + 22) {
                        xPas2 = xPas2 - getWidth() - 45;
                    }
                    //generujAuto(int typ, int x, int y)
                    bcarS[nrAutaB].setRefPixelPosition(xPas2, yPas2);
                    bcarS[nrAutaB].setTransform(Sprite.TRANS_NONE);
                    bcarS[nrAutaB].paint(g);
                    nrAutaB++;      
                }
                //nastpęny samochód na pasie
                xPas2 += 100; 
                
                //kontrola prędkości - część 2
                xPas2 = aktX;
            }
            
            //przewinięcie gdy poza ekranem
            if (xPas2 >= getWidth() + 22) {
                xPas2 = xPas2 - getWidth() - 45;
            }
                       
            //pojazdy na pasie 3
            for (int i = 0; i < pas3.length; i++) {
                //kontrola prędkości
                aktX = xPas3;

                //samochód czerwony
                if (pas3[i] == 0) {
                    //przewinięcie gdy poza ekranem
                    if (xPas3 >= getWidth() + 22) {
                        xPas3 = xPas3 - getWidth() - 45;
                    }
                    rcarS[nrAutaR].setRefPixelPosition(xPas3, yPas3);
                    rcarS[nrAutaR].setTransform(Sprite.TRANS_NONE);
                    rcarS[nrAutaR].paint(g);
                    nrAutaR++;
                }
                //nastpęny samochód na pasie
                xPas3 += 110;

                //samochód niebieski    
                if (pas3[i] == 1) {
                    //przewinięcie gdy poza ekranem
                    if (xPas3 >= getWidth() + 22) {
                        xPas3 = xPas3 - getWidth() - 45;
                    }
                    bcarS[nrAutaB].setRefPixelPosition(xPas3, yPas3);
                    bcarS[nrAutaB].setTransform(Sprite.TRANS_NONE);
                    bcarS[nrAutaB].paint(g);
                    nrAutaB++;
                }
                xPas3 += 110;

                //kontrola prędkości - część 2
                xPas3 = aktX;
            }
            //przewinięcie gdy poza ekranem
            if (xPas3 >= getWidth() + 22) {
                xPas3 = xPas3 - getWidth() - 45;
            }

            //pojazdy na pasie 4
            for (int i = 0; i < pas4.length; i++) {
                //kontrola prędkości
                aktX = xPas4;
                //samochód czerwony
                if (pas4[i] == 0) {
                    //przewinięcie gdy poza ekranem
                    if (xPas4 <= -22) {
                        xPas4 = xPas4 + getWidth() + 45;
                    }
                    //rysowanie samochodu
                    rcarS[nrAutaR].setRefPixelPosition(xPas4, yPas4);
                    rcarS[nrAutaR].setTransform(Sprite.TRANS_MIRROR);
                    rcarS[nrAutaR].paint(g);
                    nrAutaR++;
                }
                //nastpęny samochód na pasie
                xPas4 -= 150;

                //samochód niebieski
                if (pas4[i] == 1) {
                    //przewinięcie gdy poza ekranem
                    if (xPas4 <= -22) {
                        xPas4 = xPas4 + getWidth() + 45;
                    }
                    //rysowanie samochodu
                    bcarS[nrAutaB].setRefPixelPosition(xPas4, yPas4);
                    bcarS[nrAutaB].setTransform(Sprite.TRANS_MIRROR);
                    bcarS[nrAutaB].paint(g);
                    nrAutaB++;
                }
                //nastpęny samochód na pasie
                xPas4 -= 150;

                //kontrola prędkości - część 2
                xPas4 = aktX;
            }
            //przewinięcie gdy poza ekranem
            if (xPas4 <= -22) {
                xPas4 = xPas4 + getWidth() + 45;
            }

            //pojazdy na pasie 5
            for (int i = 0; i < pas5.length; i++) {
                //kontrola prędkości
                aktX = xPas5;
                
                //czerwony
                if (pas5[i] == 0) {
                    //przewinięcie gdy poza ekranem
                    if (xPas5 <= -22) {
                        xPas5 = xPas5 + getWidth() + 45;
                    }
                    //generujAuto(int typ, int x, int y)
                    rcarS[nrAutaR].setRefPixelPosition(xPas5, yPas5);
                    rcarS[nrAutaR].setTransform(Sprite.TRANS_MIRROR);
                    rcarS[nrAutaR].paint(g);
                    nrAutaR++;   
                }
                
                //nastpęny samochód na pasie
                xPas5 -= 100;

                //niebieski    
                if (pas5[i] == 1) {
                    //przewinięcie gdy poza ekranem
                    if (xPas5 <= -22) {
                        xPas5 = xPas5 + getWidth() + 45;
                    }
                    bcarS[nrAutaB].setRefPixelPosition(xPas5, yPas5);
                    bcarS[nrAutaB].setTransform(Sprite.TRANS_MIRROR);
                    bcarS[nrAutaB].paint(g);
                    nrAutaB++;      
                }
                
                //nastpęny samochód na pasie
                xPas5 -= 100; 
                
                //kontrola prędkości - część 2
                xPas5 = aktX;
            }
            
            //przewinięcie gdy poza ekranem
            if (xPas5 <= -22) {
                xPas5 = xPas5 + getWidth() + 45;
            }
            
            //pojazdy na pasie 6
            for (int i = 0; i < pas6.length; i++) {
                //kontrola prędkości
                aktX = xPas6;
                
                //czerwony
                if (pas6[i] == 0) {
                    //przewinięcie gdy poza ekranem
                    if (xPas6 <= -22) {
                        xPas6 = xPas6 + getWidth() + 45;
                    }   
                    rcarS[nrAutaR].setRefPixelPosition(xPas6, yPas6);
                    rcarS[nrAutaR].setTransform(Sprite.TRANS_MIRROR);
                    rcarS[nrAutaR].paint(g);
                    nrAutaR++;
                }
                
                //nastpęny samochód na pasie
                xPas6 -= 100;
                
                //niebieski    
                if (pas6[i] == 1) {
                    //przewinięcie gdy poza ekranem
                    if (xPas6 <= -22) {
                        xPas6 = xPas6 + getWidth() + 45;
                    }
                    bcarS[nrAutaB].setRefPixelPosition(xPas6, yPas6);
                    bcarS[nrAutaB].setTransform(Sprite.TRANS_MIRROR);
                    bcarS[nrAutaB].paint(g);
                    nrAutaB++;
                }
                
                //nastpęny samochód na pasie
                xPas6 -= 100;    
                
                //kontrola prędkości - część 2
                xPas6 = aktX;
            }
            
            //przewinięcie gdy poza ekranem
            if (xPas6 <= -22) {
                xPas6 = xPas6 + getWidth() + 45;
            }
            
            //sprawdzenie czy krab wpadł pod samochód
            czyKolizja();

        //ekran porażki
        } else {
            g.setColor(0, 0, 0);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(0xffffff);
            g.drawString("Krabastrofa!", getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("Koniec gry", getWidth() / 2, getHeight() / 2 + 15, Graphics.HCENTER | Graphics.BASELINE);
            g.drawString("Uratowanych krabów: " + wynik, getWidth() / 2, getHeight() / 2 + 35, Graphics.HCENTER | Graphics.BASELINE);
        }
    }

    //kontrola wciśniętego klawisza
    protected void keyPressed(int keyCode) {
        this.klawisz = keyCode;
        if (!fail){
            switch (this.klawisz) {
                //góra
                case -1:
                    if (yPlayer > 15) {
                        crabSprite.setTransform(0);
                        yPlayer -= 30;
                    }
                    break;

                case KEY_NUM2:
                    if (yPlayer > 15) {
                        crabSprite.setTransform(0);
                        yPlayer -= 30;
                    }
                    break;

                //dół
                case -2:
                    if (yPlayer < 225) {
                        crabSprite.setTransform(0);
                        yPlayer += 30;
                    }
                    break;

                case KEY_NUM8:
                    if (yPlayer < 225) {
                        crabSprite.setTransform(0);
                        yPlayer += 30;
                    }
                    break;

                //lewo
                case -3:
                    if (xPlayer > 15) {
                        crabSprite.setTransform(6);
                        xPlayer -= 15;
                    }
                    break;

                case KEY_NUM4:
                    if (xPlayer > 15) {
                        crabSprite.setTransform(6);
                        xPlayer -= 15;
                    }
                    break;

                //prawo        	 	
                case -4:
                    if (xPlayer < (getWidth() - 15)) {
                        crabSprite.setTransform(6);
                        xPlayer += 15;
                    }
                    break;

                case KEY_NUM6:
                    if (xPlayer < (getWidth() - 15)) {
                        crabSprite.setTransform(6);
                        xPlayer += 15;
                    }
                    break;
            }

            //spawdzenie czy udało się kraba uratować
            czyUratowany();

            //sprawdzenie kolizji z pojazdami w wyniku ruchu kraba
            czyKolizja();
            repaint();
        }
    }
    
    //przesunięcie pojazdów na pasie z daną prędkośćią (piksele/tick)
    private void jedz() {
        xPas1 += 7;
        xPas2 += 10;
        xPas3 += 14;
        xPas4 -= 12;
        xPas5 -= 9;
        xPas6 -= 6;
    }
    
    //player dźwięków dzwiek(nazwa_pliku.wav);
    private void dzwiek(String soundName) {
        try {
            InputStream is = getClass().getResourceAsStream(soundName);
            Player player = Manager.createPlayer(is, "audio/x-wav");
            player.start();
        } catch (IOException ex) {
            System.err.println("Unable to locate or read .wav file");
        } catch (MediaException ex) {
            //ex.printStackTrace();
        }
    }

    //wykrywanie kolizji z pojzadami
    private void czyKolizja() {
        for (int i = 0; i < rcarS.length; i++) {
            if (crabSprite.collidesWith(rcarS[i], false)) {
            trafienie();
            }
        }
        for (int i = 0; i < bcarS.length; i++) {
            if (crabSprite.collidesWith(bcarS[i], false)) {
            trafienie();
            }
        }
    }

    //kolizja z samochodem
    private void trafienie() {
        dzwiek("/traf.wav");
        zycia -= 1;
        if (zycia <=0) {
            fail = true;
        }
        resetCrab();
    }

    private void czyUratowany() {
        //sprawdzenie ile krabów udało się uratować na danym poziomie
        if (yPlayer <= 29) {
            wynik += 1;
            crabSpriteU[crabI].setRefPixelPosition(xPlayer, yPlayer);
            if (crabI < 3) {
                crabI++;
            }
            resetCrab();
        }
    }

    //główny wątek gry
    public void run() {
        while (true) {
            try {
                Thread.sleep(poziom);
            } catch (InterruptedException ex) {
                //ex.printStackTrace();
            }

            if (!PAUZA) {
                jedz();
            }

            if (crabI == 3) {
                nastLevel();
            }

            //odswiez plansze
            repaint();
        }
    }

    //następny poziom po uratowaniu 3 krabów - prędkość pojazdó wzrasta o zadaną wartość
    public void nastLevel() {
        for (int n = 0; n < 3; n++) {
            crabSpriteU[n] = new Sprite(player);
            crabSpriteU[n].defineReferencePixel(13, 14);
            crabSpriteU[n].setRefPixelPosition(-30, -30);
        }
        poziom *= 0.8;
        crabI = 0;
        resetCrab();
    }

    //powrót z pauzy
    static void resume() {
        PAUZA = false;
    }

    //krab wraca na pozycję początkową
    public void resetCrab() {
        xPlayer = (getWidth() / 2);
        yPlayer = 225;
        repaint();
    }
}
