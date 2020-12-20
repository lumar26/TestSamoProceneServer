package server;

import java.util.LinkedList;

public class Proba {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 LinkedList<Korisnik> registrovaniKorisnici = new LinkedList<Korisnik>();
		 registrovaniKorisnici.add(new Korisnik("luka", "luka", "luka", "luka", "luka", "luka"));
		 registrovaniKorisnici.add(new Korisnik("l", "a", "z", "a", "r", "m"));
		 System.out.println(registrovaniKorisnici.getLast().toString());
	}

}
