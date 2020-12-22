package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientHandler extends Thread {

	Socket komunikacioniSoket;
	BufferedReader unosKlijenta;
	PrintStream tokKaKlijentu;
	String username, password, ime, prezime, pol, email;
	boolean isSignedUp = false;
	Evidencija evidencija = new Evidencija();
	private Util util = new Util();

	public ClientHandler(Socket s) {
		komunikacioniSoket = s;
	}

	private void posaljiPoruku(String string) {
		String poruka = util.getPoruka().get(string);
		tokKaKlijentu.println(poruka);
	}

	private String primiPoruku() throws IOException {
		return unosKlijenta.readLine();
	}

	private String unesiKredencijal(String upit) throws IOException {
		String odgovor;
		do {
			posaljiPoruku(upit);
//			mozda bi za ovo trebalo odmah ovde da se odradi try catch
			odgovor = primiPoruku();
		} while (odgovor == null || odgovor.equals(""));
		return odgovor;
	}

	public Korisnik registrujKorisnika() throws IOException {

//		posaljiPoruku("----------------------------------\nForma za registraciju\n--------------------------");
		username = unesiKredencijal("registracija.username");
		password = unesiKredencijal("registracija.password");
		ime = unesiKredencijal("registracija.ime");
		prezime = unesiKredencijal("registracija.prezime");
		pol = unesiKredencijal("registracija.pol");
		email = unesiKredencijal("registracija.email");
		return new Korisnik(username, password, ime, prezime, pol, email);
	}

	public Korisnik prijaviKorisnika() throws IOException {
		tokKaKlijentu
				.println("----------------------------------\nForma za prijavljivanje\n--------------------------\n"
						+ "---- unesite '*meni*' za povratak u glavni meni");
		while (true) {

			username = unesiKredencijal("prijava.username");
			password = unesiKredencijal("prijava.password");

			System.out.println("Broj elemenata u listi je jednak:  " + evidencija.getRegistrovaniKorisnici().size());
			for (Korisnik korisnik : evidencija.registrovaniKorisnici) {
				if (korisnik.getUsername().equals(username) && korisnik.getPassword().equals(password)) {
//					ako smo nasli korisnika u 'bazi' onda ova funkcija prekida sa raadom i vraca tog korisnika
					return korisnik;
				}
			}
//			ovde mi se javlja neka greska, nece da me vrati u glavni meni
			posaljiPoruku("prijava.neuspesanUnosMeni");
			String izbor = primiPoruku();
			switch (izbor) {
			case "1":
				break;
			case "2":
//				ovaj kejs me zeza jer mi izbacuje korisnika iz aplikacije skroz, umesto da ga vrati u glavni meni 
				return null;
			default:
				break;
			}
		}
	}

	public int samoproceni(String upit) throws IOException {

		String odgovor;
		while (true) {
			posaljiPoruku(upit);
			odgovor = unosKlijenta.readLine();
			if (odgovor.equals("da")) {
				return 1;
			}
			if (odgovor.equals("ne"))
				return 0;
		}
	}

	public void evidentirajTestiranje(Korisnik korisnik, TipTesta tipTesta, Status status) {
		switch (status) {
//		ovde su ovi statusi visak jer svakako prosledjujem status i rezultat testa
		case POZITIVAN:
			korisnik.getTestiranja().add(new Test(tipTesta, Status.POZITIVAN, new GregorianCalendar()));
			break;
		case NEGATIVAN:
			korisnik.getTestiranja().add(new Test(TipTesta.BRZI, Status.NEGATIVAN, new GregorianCalendar()));
			break;
		case PCR_POZITIVAN:
			korisnik.getTestiranja().add(new Test(TipTesta.PCR, Status.PCR_POZITIVAN, new GregorianCalendar()));
			break;
		case PCR_NEGATIVAN:
			korisnik.getTestiranja().add(new Test(TipTesta.PCR, Status.PCR_NEGATIVAN, new GregorianCalendar()));
			break;
		default:
			break;
		}
	}

	public void brzoTestiranje(Korisnik korisnik) {
		int razultatTesta = (int) Math.round(Math.random());
		if (razultatTesta == 1) {
			posaljiPoruku("poruka.pozitivanBrziTest");
//			ovde se setuje trenutno stanje
			korisnik.setTrenutniStatus(Status.POZITIVAN);
//			azuriranje evidencije
			evidentirajTestiranje(korisnik, TipTesta.BRZI, Status.POZITIVAN);

		} else {
			tokKaKlijentu.println("Rezultat vaseg brzog testa je negativan.");
			korisnik.setTrenutniStatus(Status.NEGATIVAN);
			evidentirajTestiranje(korisnik, TipTesta.BRZI, Status.NEGATIVAN);
		}
	}

	private void testirajPCR(Korisnik korisnik) {
		int razultatTesta = (int) Math.round(Math.random());
		if (razultatTesta == 1) {
			posaljiPoruku("poruka.pozitivanPCRTest");
			korisnik.setTrenutniStatus(Status.PCR_POZITIVAN);
//			azuriranje evidencije
			evidentirajTestiranje(korisnik, TipTesta.PCR, Status.PCR_POZITIVAN);
		} else {
			posaljiPoruku("poruka.negativanPCRTest");
			korisnik.setTrenutniStatus(Status.PCR_NEGATIVAN);
			evidentirajTestiranje(korisnik, TipTesta.PCR, Status.PCR_NEGATIVAN);
		}
	}

	private boolean jeVecTestiran(Korisnik korisnik, TipTesta tip) {
		if (korisnik == null) {
			System.err.println("Korisnik " + korisnik.getIme() + " zza koga se gleda dal je testiran ne postoji");
			return false;
		}
		if (korisnik.getTestiranja() == null) {
			System.err.println("Lista testiranja kod korisnika" + korisnik.getIme() + " nije inicijalizovana");
			return false;
		}
		if (korisnik.getTestiranja().getLast() == null) {
			System.err.println("nema testiranja u listi testiranja kod korisnika " + korisnik.getIme());
			return false;
		}
//		prvo provera da li se korisnik vec testirao danas (za bilo koji test)
		if ((korisnik.getTestiranja().getLast().getDatumTestiranja().get(Calendar.YEAR) == new GregorianCalendar()
				.get(Calendar.YEAR)
				&& korisnik.getTestiranja().getLast().getDatumTestiranja()
						.get(Calendar.MONTH) == new GregorianCalendar().get(Calendar.MONTH)
				&& korisnik.getTestiranja().getLast().getDatumTestiranja()
						.get(Calendar.DAY_OF_MONTH) == new GregorianCalendar().get(Calendar.DAY_OF_MONTH)
				&& korisnik.getTestiranja().getLast().getTip() == tip)) {
			posaljiPoruku("upozorenje.jedanTestDnevno");
			return true;
		}
		return false;
	}

	public void testirajKorisnika(Korisnik korisnik) throws IOException {

		while (true) {
			posaljiPoruku("testiranje.meni");

			String akcija = primiPoruku();

			switch (akcija) {
			case "1":

				int brojacPotvrdnihOdgovora = 0;
				posaljiPoruku("samoprocena.uvod");
//				ovde postavljamo sedam pitanja vezanih za samoprocenu
//				for (int i = 0; i < 7; i++) {
//					brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje" + i);
//				}

				brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje1");
				brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje2");
				brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje3");
				brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje4");
				brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje5");
				brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje6");
				brojacPotvrdnihOdgovora += samoproceni("samoprocena.pitanje7");

				posaljiPoruku("samoprocena.meniNakonPitanja");
				String izbor = primiPoruku().trim();
				if (brojacPotvrdnihOdgovora >= 2)
					switch (izbor) {
					case "1":
						if (!jeVecTestiran(korisnik, TipTesta.BRZI))
							brzoTestiranje(korisnik);
						break;
					case "2":
						if (!jeVecTestiran(korisnik, TipTesta.PCR))
							testirajPCR(korisnik);
						break;
					case "3":
						if (!jeVecTestiran(korisnik, TipTesta.BRZI) && !jeVecTestiran(korisnik, TipTesta.PCR)) {
							testirajPCR(korisnik);
							brzoTestiranje(korisnik);
						}

						break;
					case "4":
						break;
					default:
						break;
					}
				else {
//					korisnik je imao negativan test
					korisnik.setTrenutniStatus(Status.POD_NADZOROM); // menja mu se trenutni status
					// dodajemo jedno testiranje u listu testiranja tog korisnika
					korisnik.getTestiranja().add(new Test(TipTesta.BRZI, Status.POD_NADZOROM, new GregorianCalendar()));
					// u prostoj evidenciji koja postoji azuriraj
					evidencija.setBrojPacijenataodNadzorom(evidencija.getBrojPacijenataodNadzorom() + 1);
				}
				break;
			case "2":
//				prikaz korisniku njegovog poslednje testiranja
				tokKaKlijentu.println("Poslednje testiranje:\n" + korisnik.getTestiranja().getLast().toString());

				break;
			case "3":
				return;
			default:
				break;
			}
		}

	}

	public void obradiAdministratora(PrintStream tokKaKlijentu, BufferedReader unosKlijenta)
			throws NumberFormatException, IOException {
		posaljiPoruku("admin.uvod");
		int izbor;
//		Korisnik moze da prekine komunikaciju nasilno ili unosom '*quit*'
		while (true) {
			posaljiPoruku("admin.meni");

//			treba oni korisnici koji su pod nadzorom pa treba da rade ponovo
			String unos = unosKlijenta.readLine().trim();
//			na osnovu izbora izlistavamo sta korisnik zeli
			switch (unos) {
			case "1":
				posaljiPoruku("admin.izlistavanjeSvi");
				evidencija.izlistajKorisnike(tokKaKlijentu);
				break;
			case "2":
				posaljiPoruku("admin.izlistavanjePozitivni");
				evidencija.izlistajPozitivne(tokKaKlijentu);
				break;
			case "3":
				posaljiPoruku("admin.izlistavanjeNegativni");
				evidencija.izlistajNegativne(tokKaKlijentu);
				break;
			case "4":
				posaljiPoruku("admin.izlistavanjeNadzor");
				evidencija.izlistajPodNadzorom(tokKaKlijentu);
				break;
			case "5":
				return;
			default:
				break;
			}
		}

	}

	@Override
	public void run() {
		try {
			unosKlijenta = new BufferedReader(new InputStreamReader(komunikacioniSoket.getInputStream()));
			tokKaKlijentu = new PrintStream(komunikacioniSoket.getOutputStream());
			System.out.println("Inicijalizovani su tokovi ka korisniku!");
			while (true) {
				posaljiPoruku("glavniMeni");
//				tokKaKlijentu.println("Glavni meni treba ovde da se pojavi");

				String izbor = unosKlijenta.readLine();

				switch (izbor) {
				case "1":
					Korisnik noviKorisnik = registrujKorisnika();
					evidencija.registrovaniKorisnici.add(noviKorisnik);
					System.out.println(evidencija.registrovaniKorisnici.getLast().toString());
					posaljiPoruku("registracija.uspesna");

					break;
				case "2":
					posaljiPoruku("prijava.uvod");
					Korisnik prijavljeniKorisnik = prijaviKorisnika();
					System.err.println("Ovde treba da se pojavi korisnik koji je prijavljen");
					System.out.println(prijavljeniKorisnik.toString());
//	korisnik je prijavljen dakle ima ga u bazi podataka, sad se pristupa pitanjima i testiranju
//					ako je hteo da se prijavi a nije registrovan vratice se null za prijavljeniKorisnik, pa mu se ponovo otvara glavni meni
					if (prijavljeniKorisnik != null)
						testirajKorisnika(prijavljeniKorisnik);
					continue;
				case "3":
					obradiAdministratora(tokKaKlijentu, unosKlijenta);
					break;
				case "*quit*":
					komunikacioniSoket.close();
					return;

				default:
					break;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Doslo je do problema pri uspostavljanju toka podataka ka klijentu");
			return;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			System.err.println("Korisnik je nasilno napustio aplikaciju");
			return;
		}
	}

}
