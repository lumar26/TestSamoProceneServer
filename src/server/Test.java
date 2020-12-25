package server;

import java.text.SimpleDateFormat;
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
//		da prebaacimo datum u citljiv oblik
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formatiraniDatum = df.format(this.datumTestiranja.getTime());
		System.out.println(formatiraniDatum);
		return "Korisnik:"+ this.korisnik.getUsername() +
				" Tip_testa:" + this.tip + " rezultat_testa:" + this.rezultatTesta + " datum_testiranja:"
				+ formatiraniDatum +"\n";
	}
	
	public TipTesta getTip() {
		return tip;
	}
	public void setTip(TipTesta tip) {
		this.tip = tip;
	}
	public Korisnik getKorisnik() {
		return korisnik;
	}
	public void setkorinsik(Korisnik k) {
		this.korisnik = k;
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
