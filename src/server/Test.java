package server;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Test {
	private TestTip tip;
	private StatusKorisnika rezultatTesta;
	private GregorianCalendar datumTestiranja;
	private Korisnik korisnik;
	
	public Test(TestTip tip, StatusKorisnika rezultatTesta, GregorianCalendar datumTestiranja, Korisnik korisnik) {
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
	
	public TestTip getTip() {
		return tip;
	}
	public void setTip(TestTip tip) {
		this.tip = tip;
	}
	public Korisnik getKorisnik() {
		return korisnik;
	}
	public void setkorinsik(Korisnik k) {
		this.korisnik = k;
	}
	public StatusKorisnika getRezultatTesta() {
		return rezultatTesta;
	}
	public void setRezultatTesta(StatusKorisnika rezultatTesta) {
		this.rezultatTesta = rezultatTesta;
	}
	public GregorianCalendar getDatumTestiranja() {
		return datumTestiranja;
	}
	public void setDatumTestiranja(GregorianCalendar datumTestiranja) {
		this.datumTestiranja = datumTestiranja;
	} 
	
	
}
