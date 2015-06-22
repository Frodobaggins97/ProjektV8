package de.schmitz.fabian.bienen;

import android.graphics.Bitmap;

public class BienenBitmap {
	private Bitmap blackAndWhite;
	private int bienenAnzahl;
	public BienenBitmap(Bitmap blackAndWhite, int bienenAnzahl) {
		super();
		this.blackAndWhite = blackAndWhite;
		this.bienenAnzahl = bienenAnzahl;
	}
	public Bitmap getBlackAndWhite() {
		return blackAndWhite;
	}
	public int getBienenAnzahl() {
		return bienenAnzahl;
	}
	
	

}
