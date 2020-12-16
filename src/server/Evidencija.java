package server;

import java.io.PrintWriter;
import java.util.LinkedList;

public class Evidencija {
	private int brojTestiranih;
	private int brojPozitivnihTestova;
	private int brojNegativnihTestova;
	private int brojPacijenataodNadzorom;
	static LinkedList<Korisnik> registrovaniKorisnici = new LinkedList<Korisnik>();

	@Override
	public String toString() {
		return "Ukupan broj testiranih korisnika je: " + brojTestiranih + "\nBroj pozitivnih testova je jednak: "
				+ brojPozitivnihTestova + "\nBroj negativnih testova je jednak: " + brojNegativnihTestova
				+ "\nBroj pacijenata pod nadzorom je: " + brojPacijenataodNadzorom;
	}

	public void izlistajKorisnike(PrintWriter tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			tokKaKorisniku.println(korisnik.toString());
		}
	}

	public void izlistajPozitivne(PrintWriter tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			if (korisnik.status == Status.POZITIVAN)
				tokKaKorisniku.println(korisnik.toString());
		}
	}
	
	public void izlistajNegativne(PrintWriter tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			if (korisnik.status == Status.NEGATIVAN)
				tokKaKorisniku.println(korisnik.toString());
		}
	}
	
	public void izlistajPodNadzorom(PrintWriter tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			if (korisnik.status == Status.POD_NADZOROM)
				tokKaKorisniku.println(korisnik.toString());
		}
	}

	public int getBrojTestiranih() {
		return brojTestiranih;
	}

	public void setBrojTestiranih(int brojTestiranih) {
		this.brojTestiranih = brojTestiranih;
	}

	public int getBrojPozitivnihTestova() {
		return brojPozitivnihTestova;
	}

	public void setBrojPozitivnihTestova(int brojPozitivnihTestova) {
		this.brojPozitivnihTestova = brojPozitivnihTestova;
	}

	public int getBrojNegativnihTestova() {
		return brojNegativnihTestova;
	}

	public void setBrojNegativnihTestova(int brojNegativnihTestova) {
		this.brojNegativnihTestova = brojNegativnihTestova;
	}

	public int getBrojPacijenataodNadzorom() {
		return brojPacijenataodNadzorom;
	}

	public void setBrojPacijenataodNadzorom(int brojPacijenataodNadzorom) {
		this.brojPacijenataodNadzorom = brojPacijenataodNadzorom;
	}

}