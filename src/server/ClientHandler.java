package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

	Socket komunikacioniSoket;
	BufferedReader unosKlijenta;
	PrintWriter tokKaKlijentu;
	String username, password, ime, prezime, pol, email;
	boolean isSignedUp = false;

	public ClientHandler(Socket s) {
		komunikacioniSoket = s;
	}

	public Korisnik registrujKorisnika(PrintWriter tokKaKlijentu, BufferedReader unosKlijenta) throws IOException {

		tokKaKlijentu.println("----------------------------------\nForma za registraciju\n--------------------------");
		tokKaKlijentu.println("Unesite vase korisnicko ime: ");
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

	public Korisnik prijaviKorisnika(PrintWriter tokKaKlijentu, BufferedReader unosKlijenta) throws IOException {
		tokKaKlijentu
				.println("----------------------------------\nForma za prijavljivanje\n--------------------------");
		while (true) {

			tokKaKlijentu.println("Unesite vase korisnicko ime: ");
			username = unosKlijenta.readLine();

			tokKaKlijentu.println("----------------------------------");
			tokKaKlijentu.println("Unesite vasu lozinku: ");
			password = unosKlijenta.readLine();

			for (Korisnik korisnik : Evidencija.registrovaniKorisnici) {
				if (korisnik.getUsername().equals(username) && korisnik.getPassword().equals(password)) {
					return korisnik;
				}
			}
		}
	}

	public int postaviPitanje(PrintWriter tokKaKlijentu, BufferedReader unosKlijenta, String pitanje)
			throws IOException {
		String odgovor;
		while (true) {
			tokKaKlijentu.println("----------------------------------");
			tokKaKlijentu.println(pitanje);
			odgovor = unosKlijenta.readLine();
			if (odgovor.equals("da")) {
				return 1;
			}
			if (odgovor.equals("ne"))
				return 0;
		}
	}

	public void testirajKorisnika(Korisnik korisnik, PrintWriter tokKaKlijentu, BufferedReader unosKlijenta)
			throws IOException {
		int brojacPotvrdnihOdgovora = 0;
		tokKaKlijentu.println("----------------------------------");
		tokKaKlijentu.println("Molimo vas da na slede'a pitanja odgovarate sa 'da' ili 'ne' ");

		brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
				"Da li ste putovali van Srbije u okviru 14 dana pre početka simptoma?");
		brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
				"Da li ste bili u kontaku sa zaraženim osobama");
		brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta, "Da li imate povišenu temperaturu?");
		brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta, "Da li imate kašalj?");
		brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta, "Da li osećate opštu slabost?");
		brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta, "Da li imate gubitak čula mirisa?");
		brojacPotvrdnihOdgovora += postaviPitanje(tokKaKlijentu, unosKlijenta,
				"Da li imate gubitak/promenu čula ukusa?");

		if (brojacPotvrdnihOdgovora >= 2) {
			int razultatTesta = (int) Math.round(Math.random());
			if (razultatTesta == 1) {
				tokKaKlijentu.println("Nazalost reyultat vaseg brzog testa je da ste pozitivni na COVID-19...");
				korisnik.setStatus(Status.POZITIVAN);
//				azuriranje evidencije
				Server.evidencija.setBrojPozitivnihTestova(Server.evidencija.getBrojPozitivnihTestova() + 1);
			} else {
				tokKaKlijentu.println("Rezultat vaseg brzog testa je negativan.");
				korisnik.setStatus(Status.NEGATIVAN);
				Server.evidencija.setBrojNegativnihTestova(Server.evidencija.getBrojNegativnihTestova() + 1);
			}
		} else {
			korisnik.setStatus(Status.POD_NADZOROM);
			Server.evidencija.setBrojPacijenataodNadzorom(Server.evidencija.getBrojPacijenataodNadzorom() + 1);
		}

	}

	public void obradiAdministratora(PrintWriter tokKaKlijentu, BufferedReader unosKlijenta)
			throws NumberFormatException, IOException {
		tokKaKlijentu.println("-------Dobrodosli u administratorski rezim -----------------");
		tokKaKlijentu.println("------------------------------------------------------------");
		int izbor;
//		Korisnik moze da prekine komunikaciju nasilno ili unosom '*quit*'
		while (true) {
			tokKaKlijentu
					.println("Izaberite jednu od opcija unosom odgovarajuceg broja (1, 2, 3 ili 4): 1. izlistaj sve"
							+ " korisnike; 2. izlistaj pozitivne korisnike; 3. izlistaj negativne korisnike; "
							+ "4. izlistaj korisnike pod nadzorom.\n Unesite '*quit*' za prekid rada");
			String unos = unosKlijenta.readLine().trim();
			if (unos.equals("*quit*")) {
				komunikacioniSoket.close();
				return;
//				kako da resim da se prekine ceo hendler, mozda bi mogo da se napravi quit exception
			}
//			na osnovu izbora iylistavamo sta korisnik zeli
			izbor = Integer.parseInt(unos);
			switch (izbor) {
			case 1:
				tokKaKlijentu.println("Lista svih korisnika:");
				Server.evidencija.izlistajKorisnike(tokKaKlijentu);
				break;
			case 2:
				tokKaKlijentu.println("Lista svih korisnika:");
				Server.evidencija.izlistajPozitivne(tokKaKlijentu);
				break;
			case 3:
				tokKaKlijentu.println("Lista svih korisnika:");
				Server.evidencija.izlistajNegativne(tokKaKlijentu);
				break;
			case 4:
				tokKaKlijentu.println("Lista svih korisnika:");
				Server.evidencija.izlistajPodNadzorom(tokKaKlijentu);
				break;
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
			tokKaKlijentu = new PrintWriter(komunikacioniSoket.getOutputStream());

			tokKaKlijentu.println(
					"Za registraciju unesite 'r',za logovanje kao administrator unesite 'admin', za prekid unesite '*quit*', za prijavu pritisnite bilo koji drugi karakter");
			String izbor = unosKlijenta.readLine();
//			e sad ako korisnik unese 'admin' to je skroz drugacije nego kod svih ostalih pa za to ide jedan if, a za ostale else
			if (izbor.equals("admin")) {
				obradiAdministratora(tokKaKlijentu, unosKlijenta);
			} else {
				if (izbor.equals("*quit*")) {
					komunikacioniSoket.close();
					return;
				}
				if (izbor.equals("r")) {
					Korisnik noviKorisnik = registrujKorisnika(tokKaKlijentu, unosKlijenta);
					Evidencija.registrovaniKorisnici.add(noviKorisnik);
					tokKaKlijentu.println("Cestitamo, uspesno ste se registrovali!");
				}
				System.out.println("-----------------------------");
				System.out.println("Molimo vas da se prijavite na platformu za samotestiranje");
				Korisnik prijavljeniKorisnik = prijaviKorisnika(tokKaKlijentu, unosKlijenta);
//		korisnik je prijavljen dakle ima ga u bazi podataka, sad se pristupa pitanjima i testiranju
				testirajKorisnika(prijavljeniKorisnik, tokKaKlijentu, unosKlijenta);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
