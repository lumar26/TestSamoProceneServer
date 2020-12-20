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
	private Util u = new Util();
	Map<String, String> poruke = u.getPoruka();

	public ClientHandler(Socket s) {
		komunikacioniSoket = s;
	}
	
	private void posaljiPoruku(String poruka) {
		tokKaKlijentu.println(poruka);
	}
	private String primiPoruku() throws IOException {
		return unosKlijenta.readLine();
	}

	public Korisnik registrujKorisnika() throws IOException {
//		poruke.forEach((key, value) -> {
//			if(key.startsWith("registracija")) {
//				posaljiPoruku(value);
//				
//			}
//		});
		

		tokKaKlijentu.println("----------------------------------\nForma za registraciju\n--------------------------");
		tokKaKlijentu.println(u.getPoruka().get("registracija.username"));
//		tokKaKlijentu.println("Unesite vase korisnicko ime: ");
		username = unosKlijenta.readLine();

		tokKaKlijentu.println("----------------------------------");
		tokKaKlijentu.println("Unesite vasu lozinku: ");
		password = unosKlijenta.readLine();

		tokKaKlijentu.println("----------------------------------");
		tokKaKlijentu.println("Unesite vase ime: ");
		ime = unosKlijenta.readLine();

		tokKaKlijentu.println("----------------------------------");
		tokKaKlijentu.println("Unesite vase prezime: ");
		prezime = unosKlijenta.readLine();

		tokKaKlijentu.println("----------------------------------");
		tokKaKlijentu.println("Unesite vas pol: ");
		pol = unosKlijenta.readLine();

		tokKaKlijentu.println("----------------------------------");
		tokKaKlijentu.println("Unesite vas email: ");
		email = unosKlijenta.readLine();
		return new Korisnik(username, password, ime, prezime, pol, email);
	}

	public Korisnik prijaviKorisnika(PrintStream tokKaKlijentu, BufferedReader unosKlijenta) throws IOException {
		tokKaKlijentu
				.println("----------------------------------\nForma za prijavljivanje\n--------------------------\n"
						+ "---- unesite '*meni*' za povratak u glavni meni");
		while (true) {

			tokKaKlijentu.println(
					"----------------------------------\n" + "Unesite vase korisnicko ime (ne sme biti prazan unos): ");
			username = unosKlijenta.readLine();
			if (username.equals(""))
				continue;

			tokKaKlijentu.println();
			tokKaKlijentu.println(
					"----------------------------------\n" + "Unesite vasu lozinku (ne sme biti prazan unos): ");
			password = unosKlijenta.readLine();
			if (password.equals(""))
				continue;

			System.out.println("Broj elemenata u listi je jednak:  " + evidencija.getRegistrovaniKorisnici().size());
			for (Korisnik korisnik : evidencija.registrovaniKorisnici) {
				if (korisnik.getUsername().equals(username) && korisnik.getPassword().equals(password)) {
//					ako smo nasli korisnika u 'bazi' onda ova funkcija prekida sa raadom i vraca tog korisnika
					
					return korisnik;
				}
			}
			tokKaKlijentu
					.println("Korisnicko ime i lozinka koje ste uneli ne pripadaju ni jednom registrovanom korisniku\n"
							+ "Unesite redni broj vaseg izbora" + "1. Unos novih kredencijala\n"
							+ "2. Povratak na glavni meni kako biste se registrovali ");
			switch (unosKlijenta.readLine()) {
			case "1":
				break;
			case "2":
				return null;
			default:
				break;
			}
		}
	}

	public int postaviPitanje(PrintStream tokKaKlijentu, BufferedReader unosKlijenta, String pitanje)
			throws IOException {
		String odgovor;
		while (true) {
//			tokKaKlijentu.println("----------------------------------");
//			tokKaKlijentu.println(pitanje);
			posaljiPoruku(pitanje);
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

	public void brzoTestiranje(Korisnik korisnik, PrintStream tokKaKlijentu, BufferedReader unosKlijenta) {
		int razultatTesta = (int) Math.round(Math.random());
		if (razultatTesta == 1) {
			tokKaKlijentu.println("Nazalost rezultat vaseg brzog testa je da ste pozitivni na COVID-19...");
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

	private void testirajPCR(Korisnik korisnik, PrintStream tokKaKlijentu2, BufferedReader unosKlijenta2) {
		int razultatTesta = (int) Math.round(Math.random());
		if (razultatTesta == 1) {
			tokKaKlijentu.println("Nazalost rezultat vaseg PCR testa je da ste pozitivni na COVID-19...");
			korisnik.setTrenutniStatus(Status.PCR_POZITIVAN);
//			azuriranje evidencije
			evidentirajTestiranje(korisnik, TipTesta.PCR, Status.PCR_POZITIVAN);
		} else {
			tokKaKlijentu.println("Rezultat vaseg PCR testa je negativan.");
			korisnik.setTrenutniStatus(Status.PCR_NEGATIVAN);
			evidentirajTestiranje(korisnik, TipTesta.PCR, Status.PCR_NEGATIVAN);
		}
	}

	private boolean jeVecTestiran(Korisnik korisnik, TipTesta tip) {
		if (korisnik != null) {
			System.err.println("Korisnik "+ korisnik.getIme() +" zza koga se gleda dal je testiran ne postoji");
			return false;
		}
		if(korisnik.getTestiranja() != null  ) {
			System.err.println("Lista testiranja kod korisnika"+ korisnik.getIme() +" nije inicijalizovana");
			return false;
		}
		if(korisnik.getTestiranja().getLast() == null) {
			System.err.println("nema testiranja u listi testiranja kod korisnika "+ korisnik.getIme());
			return false;
		}
//		prvo provera da li se korisnik vec testirao danas (za bilo koji test)
		if ((korisnik.getTestiranja().getLast().getDatumTestiranja()
						.get(Calendar.YEAR) == new GregorianCalendar().get(Calendar.YEAR)
						&& korisnik.getTestiranja().getLast().getDatumTestiranja()
								.get(Calendar.MONTH) == new GregorianCalendar().get(Calendar.MONTH)
						&& korisnik.getTestiranja().getLast().getDatumTestiranja()
								.get(Calendar.DAY_OF_MONTH) == new GregorianCalendar().get(Calendar.DAY_OF_MONTH)
						&& korisnik.getTestiranja().getLast().getTip() == tip)) {
			tokKaKlijentu.println("Ne mozete se testirati dva puta u jednom danu!");
			tokKaKlijentu.println("-----------------------------------------------");
			return true;
		}
		return false;
	}

	public void testirajKorisnika(Korisnik korisnik, PrintStream tokKaKlijentu, BufferedReader unosKlijenta)
			throws IOException {

		while (true) {
			tokKaKlijentu.println("Izaberite sledecu akciju (unesite preko tastature redni broj izabrane akcije): \n"
					+ "1. Zapocnite testiranje\n" + "2. Prikaz poslednjeg testiranja\n" + "3. Povratak u glavni meni ");

			String akcija = unosKlijenta.readLine();

			switch (akcija) {
			case "1":

				int brojacPotvrdnihOdgovora = 0;
				tokKaKlijentu.println("----------------------------------");
				tokKaKlijentu.println("Molimo vas da na slede'a pitanja odgovarate sa 'da' ili 'ne' ");

//				Map<String, String> map = new HashMap<>();
//				map.put("header", "Postavi pitanje:");
//
//				tokKaKlijentu.println(map.get("header"));

				brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
						"Da li ste putovali van Srbije u okviru 14 dana pre početka simptoma?");
				brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
						"Da li ste bili u kontaku sa zaraženim osobama");
				brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
						"Da li imate povišenu temperaturu?");
				brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta, "Da li imate kašalj?");
				brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta, "Da li osećate opštu slabost?");
				brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
						"Da li imate gubitak čula mirisa?");
				brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
						"Da li imate gubitak/promenu čula ukusa?");

				tokKaKlijentu.println("Izaberite sledecu akciju unosom odgovarajuceg rednog broja:\n"
						+ "1. Zapocnite brzi test\n" + "2. Zapocnite PCR testiranje\n" + "3. Odradite oba testiranja\n"
						+ "4. Vrati se u prethodni meni");
				int izbor = Integer.parseInt(unosKlijenta.readLine().trim());
				if (brojacPotvrdnihOdgovora >= 2)
					switch (izbor) {
					case 1:
						if (!jeVecTestiran(korisnik, TipTesta.BRZI))
							brzoTestiranje(korisnik, tokKaKlijentu, unosKlijenta);
						break;
					case 2:
						if (!jeVecTestiran(korisnik, TipTesta.PCR))
							testirajPCR(korisnik, tokKaKlijentu, unosKlijenta);
						break;
					case 3:
						if (!jeVecTestiran(korisnik, TipTesta.BRZI) && !jeVecTestiran(korisnik, TipTesta.PCR)) {
							testirajPCR(korisnik, tokKaKlijentu, unosKlijenta);
							brzoTestiranje(korisnik, tokKaKlijentu, unosKlijenta);
						}

						break;
					case 4:
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
					evidencija
							.setBrojPacijenataodNadzorom(evidencija.getBrojPacijenataodNadzorom() + 1);
				}
				break;
			case "2":
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
		tokKaKlijentu.println("-------Dobrodosli u administratorski rezim -----------------");
		tokKaKlijentu.println("------------------------------------------------------------");
		int izbor;
//		Korisnik moze da prekine komunikaciju nasilno ili unosom '*quit*'
		while (true) {
			tokKaKlijentu
					.println("Izaberite jednu od opcija unosom odgovarajuceg broja (1, 2, 3 ili 4): 1. izlistaj sve"
							+ " korisnike; 2. izlistaj pozitivne korisnike; 3. izlistaj negativne korisnike; "
							+ "4. izlistaj korisnike pod nadzorom.\n 5. Povratak u glavni meni");
//			treba oni korisnici koji su pod nadzorom pa treba da rade ponovo
			String unos = unosKlijenta.readLine().trim();
//			na osnovu izbora izlistavamo sta korisnik zeli
			izbor = Integer.parseInt(unos);
			switch (izbor) {
			case 1:
				tokKaKlijentu.println("Lista svih korisnika:");
				evidencija.izlistajKorisnike(tokKaKlijentu);
				break;
			case 2:
				tokKaKlijentu.println("Lista svih korisnika:");
				evidencija.izlistajPozitivne(tokKaKlijentu);
				break;
			case 3:
				tokKaKlijentu.println("Lista svih korisnika:");
				evidencija.izlistajNegativne(tokKaKlijentu);
				break;
			case 4:
				tokKaKlijentu.println("Lista svih korisnika:");
				evidencija.izlistajPodNadzorom(tokKaKlijentu);
				break;
			case 5:
				return;
			default:
				break;
			}
			System.out.println("-----------------------------------------");
		}

	}

	@Override
	public void run() {
		try {
			unosKlijenta = new BufferedReader(new InputStreamReader(komunikacioniSoket.getInputStream()));
			tokKaKlijentu = new PrintStream(komunikacioniSoket.getOutputStream());
			System.out.println("Inicijalizovani su tokovi ka korisniku!");
			while (true) {
				tokKaKlijentu.println(
						"----------------------------------------------------------------------------------------\n"
								+ "Izaberite sledecu aktivnost tako sto cete uneti odgovarajuci redni broj(primer: '2'):\n"
								+ "1. Registrujte se\n" + "2. Prijavite se\n" + "3. Prijavite se kao administrator\n"
								+ "*quit*. Za izlaz iz aplikacije unesite '*quit*'");

				String izbor = unosKlijenta.readLine();

				switch (izbor) {
				case "1":
					Korisnik noviKorisnik = registrujKorisnika();
					evidencija.registrovaniKorisnici.add(noviKorisnik);
					System.out.println(evidencija.registrovaniKorisnici.getLast().toString());
					tokKaKlijentu.println("Uspesno ste se registrovali!");

					break;
				case "2":
					tokKaKlijentu.println("-----------------------------");
					tokKaKlijentu.println("Molimo vas da se prijavite na platformu za samotestiranje");
					Korisnik prijavljeniKorisnik = prijaviKorisnika(tokKaKlijentu, unosKlijenta);
					System.err.println("Ovde treba da se pojavi korisnik koji je prijavljen");
					System.out.println(prijavljeniKorisnik.toString());
//	korisnik je prijavljen dakle ima ga u bazi podataka, sad se pristupa pitanjima i testiranju
//					ako je hteo da se prijavi a nije registrovan vratice se null za prijavljeniKorisnik, pa mu se ponovo otvara glavni meni
					if (prijavljeniKorisnik != null)
						testirajKorisnika(prijavljeniKorisnik, tokKaKlijentu, unosKlijenta);
					break;
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
