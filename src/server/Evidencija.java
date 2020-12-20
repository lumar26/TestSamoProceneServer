package server;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;

//Singulton
public class Evidencija {
	
//	private static Evidencija instance;
//	
//	public static Evidencija getInstance() {
//		if (instance == null) {
//			return new Evidencija();
//		}
//		return instance;
//	}
//	
//	private Evidencija() {
//	}
	
	private int brojTestiranih;
	private int brojPozitivnihTestova;
	private int brojNegativnihTestova;
	private int brojPacijenataodNadzorom;
	public  LinkedList<Korisnik> registrovaniKorisnici = new LinkedList<Korisnik>();

	public LinkedList<Korisnik> getRegistrovaniKorisnici() {
		return registrovaniKorisnici;
	}

	public void setRegistrovaniKorisnici(LinkedList<Korisnik> registrovaniKorisnici) {
		this.registrovaniKorisnici = registrovaniKorisnici;
	}

	@Override
	public String toString() {
		return "Ukupan broj testiranih korisnika je: " + brojTestiranih + "\nBroj pozitivnih testova je jednak: "
				+ brojPozitivnihTestova + "\nBroj negativnih testova je jednak: " + brojNegativnihTestova
				+ "\nBroj pacijenata pod nadzorom je: " + brojPacijenataodNadzorom;
	}

	public void izlistajKorisnike(PrintStream tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			tokKaKorisniku.println(korisnik.toString());
		}
	}

	public void izlistajPozitivne(PrintStream tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			if (korisnik.getTrenutniStatus() == Status.POZITIVAN)
				tokKaKorisniku.println(korisnik.toString());
		}
	}
	
	public void izlistajNegativne(PrintStream tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			if (korisnik.getTrenutniStatus() == Status.NEGATIVAN)
				tokKaKorisniku.println(korisnik.toString());
		}
	}
	
	public void izlistajPodNadzorom(PrintStream tokKaKorisniku) {
		for (Korisnik korisnik : registrovaniKorisnici) {
			if (korisnik.getTrenutniStatus() == Status.POD_NADZOROM)
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