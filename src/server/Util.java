package server;

import java.util.HashMap;
import java.util.Map;

public class Util {
	private Map<String, String> poruka;

	public Map<String, String> getPoruka() {
		return poruka;
	}

	public Util() {
		poruka = new HashMap<>();
		
//		poruke pri registraciji
		poruka.put("registracija.username", "Unesite vaše korisničko ime: \n");
		poruka.put("registracija.password", "Unesite vašu lozinku: \n");
		poruka.put("registracija.ime", "Unesite vaše ime: \n");
		poruka.put("registracija.prezime", "Unesite vaše prezime: \n");
		poruka.put("registracija.pol", "Unesite vaš pol: \n");
		poruka.put("registracija.email", "Unesite vašu email adresu: \n");
		poruka.put("registracija.uspesna", "Uspešno ste se registrovali!\n");
		
//		upozorenje vezano za unos
		poruka.put("upozorenje.neprazanUnos", "(NE SME BITI PRAZAN UNOS)");
		
//		opcije ako je prijava neuspesna
		poruka.put("prijava.neuspesanUnosMeni",
				"Korisnicko ime i lozinka koje ste uneli ne pripadaju ni jednom registrovanom korisniku\n"
						+ "Unesite redni broj vaseg izbora\n" + "1. Unos novih kredencijala\n"
						+ "2. Povratak na glavni meni kako biste se registrovali ");
		poruka.put("prijava.uvod",
				"Molimo vas da se prijavite na platformu za samotestiranje\n");
		poruka.put("prijava.username", "Unesite vaše korisničko ime: \n");
		poruka.put("prijava.password", "Unesite vašu lozinku: \n");
		
//		obavestenja o rezultatima testa i upozorenja za maksimalan broj testova
		poruka.put("poruka.pozitivanBrziTest",
				"Nazalost rezultat vaseg brzog testa je da ste pozitivni na COVID-19... \n");
		poruka.put("poruka.negativanBrziTest", "Rezultat vaseg brzog testa je negativan. \n");

		poruka.put("poruka.pozitivanPCRTest",
				"Nazalost rezultat vaseg PCR testa je da ste pozitivni na COVID-19...\n");
		poruka.put("poruka.negativanPCRTest", "Rezultat vaseg PCR testa je negativan. \n");
		poruka.put("upozorenje.jedanTestDnevno",
				"Ne mozete se testirati dva puta u jednom danu!\n");

//		cekanje na rezultat pcrTesta
		poruka.put("pcr.cekanje", "Vaše PCR testiranje će uskoro biti poslato na obradu. \n");
		poruka.put("pcr.poslato", "Vaše PCR testiranje je poslato na obradu. \n");
		poruka.put("pcr.obrada", "Vaše PCR testiranje se obrađuje. Očekivano vreme obrade: 2min \n");

//		meni za test samoprocene
		poruka.put("testiranje.meni",
				"Izaberite sledeću akciju:\n1. Zapocnite testiranje\n2. Prikaz poslednjeg testiranja\n3. Povratak u glavni meni\n");
		
//		test samoprocene, pitanja i desavanja nakon pitanja
		poruka.put("samoprocena.uvod",
				"Molimo vas da na sledeća pitanja odgovarate sa 'da' ili 'ne' \n");
		poruka.put("samoprocena.pitanje1", "Da li ste putovali van Srbije u okviru 14 dana pre početka simptoma?\n");
		poruka.put("samoprocena.pitanje2", "Da li ste bili u kontaku sa zaraženim osobama?\n");
		poruka.put("samoprocena.pitanje3", "Da li imate povišenu temperaturu?\n");
		poruka.put("samoprocena.pitanje4", "Da li imate kašalj?\n");
		poruka.put("samoprocena.pitanje5", "Da li osećate opštu slabost?\n");
		poruka.put("samoprocena.pitanje6", "Da li imate gubitak čula mirisa?\n");
		poruka.put("samoprocena.pitanje7", "Da li imate gubitak/promenu čula ukusa?\n");
		poruka.put("samoprocena.podNadzorom", "Ne možete raditi brzi/PCR test jer nemate dovoljno potvrdnih odgovora. Ubuduće ćete biti pod nadzorom, potrebno je da se unarednih 2 dana ponovo testirate...\n");
		poruka.put("samoprocena.meniNakonPitanja",
				"Izaberite sledeću akciju unosom odgovarajućeg rednog broja:\n" + "1. Započnite brzi test\n"
						+ "2. Započnite PCR testiranje\n" + "3. Odradite oba testiranja\n"
						+ "4. Vratite se u prethodni meni");

//		admin mode
		poruka.put("admin.prijava", "Molimo vas da unesete šifru administratora:");
		poruka.put("admin.pogresnaLozinka", "Uneli ste pogrešnu šifru, povratak u glavni meni...");
		poruka.put("admin.uvod",
				"-----------------Dobrodosli u administratorski rezim -----------------");
		poruka.put("admin.meni",
				"Izaberite jednu od opcija unosom odgovarajuceg broja:\n1. izlistaj sve"
						+ " korisnike; \n2. izlistaj  korisnike pozitivne na brzom testu; \n3. izlistaj korisnike negativne na brzom testu; "
						+ "\n4. izlistaj korisnike pozitivne na PCR testu.\n5. Izlistaj korisnike negarivne na PCR testu\n6. Izlistak korisnike pod nadzorom\n7. Prikaz trenutne statistike;\n0. Povratak u prethodni meni");
		poruka.put("admin.izvestajSviKorisnici", "Lista svih korisnika:");
		poruka.put("admin.izvestajPCRPozitivni", "Lista korisnika pozitivnih na PCR testiranju:");
		poruka.put("admin.izvestajPCRNegativni", "Lista korisnika negativnih na PCR testiranju:");
		poruka.put("admin.izvestajBrziPozitivni", "Lista korisnika pozitivnih na brzom testiranju:");
		poruka.put("admin.izvestajBrziNegativni", "korisnika negativnih na brzom testiranju:");
		poruka.put("admin.izvestajPodNadzorom", "Lista korisnika pod nadzorom:");
		poruka.put("admin.izvestajStatistika", "Statistički podaci koji oslikavaju trenutno stanje:");
		
//		glavni meni na poscetku
		poruka.put("glavniMeni",
				"----------------------------------------------------------------------------------------\n"
						+ "Izaberite sledecu aktivnost tako sto cete uneti odgovarajuci redni broj(primer: '2'):\n"
						+ "1. Registrujte se\n" + "2. Prijavite se\n" + "3. Prijavite se kao administrator\n"
						+ " Ukoliko u bilo kom trenutku želite da prekinete rad aplikacije, unesite '*quit*'\n");

		// greske
		poruka.put("greska.tokPodataka", "Doslo je do problema pri uspostavljanju toka podataka ka klijentu\n");
		poruka.put("klijent.prekid", "KRAJ_RADA");


	}
}
