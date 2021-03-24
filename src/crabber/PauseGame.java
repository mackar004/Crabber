package crabber;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class PauseGame extends Canvas implements Runnable{

	public void run() {	
	}

	protected void paint(Graphics graf) {
            graf.setColor(255, 255, 255);
            graf.fillRect(0, 0, getWidth(), getHeight());
            graf.setColor(220, 68, 37); 
            graf.drawString("Proszę czekać Panie Krabio!", getWidth()/2, getHeight()/2, Graphics.HCENTER | Graphics.BASELINE);
	}
}


