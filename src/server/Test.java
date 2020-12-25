package server;

import java.util.Date;
import java.util.GregorianCalendar;

public class Test {
	private TipTesta tip;
	private Status rezultatTesta;
	private GregorianCalendar datumTestiranja;
	private Korisnik korisnik;
	
	public Test(TipTesta tip, Status rezultatTesta, GregorianCalendar datumTestiranja, Korisnik korisnik) {
		this.tip = tip;
		this.rezultatTesta = rezultatTesta;
		this.datumTestiranja = datumTestiranja;
		this.korisnik = korisnik;
	}
	
	@Override
	public String toString() {
		return "Korisnik:"+ this.korisnik.getIme() + "_" + this.korisnik.getPrezime() +  "Tip_testa: " + this.tip + " rezultat_testa: " + this.rezultatTesta + " datum_testiranja: " + this.datumTestiranja;
	}
	
	public TipTesta getTip() {
		return tip;
	}
	public void setTip(TipTesta tip) {
		this.tip = tip;
	}
	public Status getRezultatTesta() {
		return rezultatTesta;
	}
	public void setRezultatTesta(Status rezultatTesta) {
		this.rezultatTesta = rezultatTesta;
	}
	public GregorianCalendar getDatumTestiranja() {
		return datumTestiranja;
	}
	public void setDatumTestiranja(GregorianCalendar datumTestiranja) {
		this.datumTestiranja = datumTestiranja;
	} 
	
	
}
