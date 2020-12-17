package server;

import java.util.Date;
import java.util.GregorianCalendar;

public class Test {
	private TipTesta tip;
	private Status rezultatTesta;
	private GregorianCalendar datumTestiranja;
	
	public Test(TipTesta tip, Status rezultatTesta, GregorianCalendar datumTestiranja) {
		this.tip = tip;
		this.rezultatTesta = rezultatTesta;
		this.datumTestiranja = datumTestiranja;
	}
	
	@Override
	public String toString() {
		return "Tip testa: " + this.tip + "\t rezultat testa: " + this.rezultatTesta + "\t datum testiranja: " + this.datumTestiranja;
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
