package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.GregorianCalendar;

public class ClientHandler extends Thread {

    Socket komunikacioniSoket;
    BufferedReader unosKlijenta;
    PrintStream tokKaKlijentu;
    String username, password, ime, prezime, pol, email;
    boolean isSignedUp = false;
    private Util util = new Util();

    public ClientHandler(Socket s) {
        komunikacioniSoket = s;
    }

    private void posaljiPoruku(String string) {
        String poruka = util.getPoruka().get(string);
        tokKaKlijentu.println(poruka);
    }

    private void posaljiIzvestaj(String izvestaj) {
        tokKaKlijentu.println(izvestaj);
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
        return odgovor.trim();
    }

    public Korisnik registrujKorisnika() throws IOException {

//		posaljiPoruku("----------------------------------\nForma za registraciju\n--------------------------");
        username = unesiKredencijal("registracija.username");
        password = unesiKredencijal("registracija.password");
        ime = unesiKredencijal("registracija.ime");
        prezime = unesiKredencijal("registracija.prezime");
        pol = unesiKredencijal("registracija.pol");
        email = unesiKredencijal("registracija.email");
        Korisnik novi = new Korisnik(username, password, ime, prezime, pol, email, StatusKorisnika.POCETAN);
        if (Evidencija.getInstance().postojiKorisnikUBazi(novi)) return null;
//        ako je vec registrovan vracamo null, a ako moze da se registruje ondak cemo da probamo da ga ubacimo u fajl i da ga vratimo
        if (Evidencija.getInstance().dodajKorisnikaUFajl(novi)) {
            System.out.println("Uspesno je dodat novi korisnik u fajl");
            return novi;
        }
        System.out.println("[ERROR EVIDENCIJA + CLIENTHENDLER] Korisnik novi nije dodat u fajl");
        return null;

    }

    public Korisnik prijaviKorisnika() throws IOException {
        tokKaKlijentu
                .println("----------------------------------\nForma za prijavljivanje\n--------------------------\n"
                        + "---- unesite '*meni*' za povratak u glavni meni");
        while (true) {
            boolean korisnikHoceIzlaz = false;
            username = unesiKredencijal("prijava.username");
            password = unesiKredencijal("prijava.password");
            Korisnik rez = Evidencija.getInstance().nadjiKorisnikaUBazi(username, password);
            if (rez != null) return rez;
//            ukoliko ne postoji u bazi, otvara se novi dojalog

//			ovde mi se javlja neka greska, nece da me vrati u glavni meni
            posaljiPoruku("prijava.neuspesanUnosMeni");
            String izbor = primiPoruku();
            switch (izbor) {
                case "1":
                    break;
                case "2":
//				ovaj kejs me zeza jer mi izbacuje korisnika iz aplikacije skroz, umesto da ga vrati u glavni meni
//                    privremeno resenje
                    korisnikHoceIzlaz = true;
                    break;
                default:
                    break;
            }
            if (korisnikHoceIzlaz) {
                System.out.println("Korisni je izabrao da izadje iz dela za prijavljivanje");
                break;
            }
        }
        return null;
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

    public void brzoTestiranje(Korisnik korisnik) {
        int razultatTesta = (int) Math.round(Math.random());
        if (razultatTesta == 1) {
            posaljiPoruku("poruka.pozitivanBrziTest");
//			ovde se setuje trenutno stanje
            Evidencija.getInstance().zameniStatusKorisnika(korisnik, StatusKorisnika.POZITIVAN);
//            korisnik.setTrenutniStatus(StatusKorisnika.POZITIVAN);
//			azuriranje evidencije
            Evidencija.getInstance().dodajTestiranje(korisnik, TestTip.BRZI, StatusKorisnika.POZITIVAN);

        } else {
            tokKaKlijentu.println("Rezultat vaseg brzog testa je negativan.");
            korisnik.setTrenutniStatus(StatusKorisnika.NEGATIVAN);
            Evidencija.getInstance().dodajTestiranje(korisnik, TestTip.BRZI, StatusKorisnika.NEGATIVAN);
        }
    }

    private void testirajPCR(Korisnik korisnik) {
        int razultatTesta = (int) Math.round(Math.random());
        if (razultatTesta == 1) {
            posaljiPoruku("poruka.pozitivanPCRTest");
            Evidencija.getInstance().zameniStatusKorisnika(korisnik, StatusKorisnika.PCR_POZITIVAN);
//            korisnik.setTrenutniStatus(StatusKorisnika.PCR_POZITIVAN);
//			azuriranje evidencije
            Evidencija.getInstance().dodajTestiranje(korisnik, TestTip.PCR, StatusKorisnika.PCR_POZITIVAN);
        } else {
            posaljiPoruku("poruka.negativanPCRTest");
//            korisnik.setTrenutniStatus(StatusKorisnika.PCR_NEGATIVAN);
            Evidencija.getInstance().zameniStatusKorisnika(korisnik, StatusKorisnika.PCR_NEGATIVAN);
            Evidencija.getInstance().dodajTestiranje(korisnik, TestTip.PCR, StatusKorisnika.PCR_NEGATIVAN);
        }
    }

//    private boolean jeVecTestiran(Korisnik korisnik, TipTesta tip) {
//        if (korisnik == null) {
//            System.err.println("Korisnik " + korisnik.getIme() + " zza koga se gleda dal je testiran ne postoji");
//            return false;
//        }
//        if (korisnik.getTestiranja() == null) {
//            System.err.println("Lista testiranja kod korisnika" + korisnik.getIme() + " nije inicijalizovana");
//            return false;
//        }
//        if (korisnik.getTestiranja().getLast() == null) {
//            System.err.println("nema testiranja u listi testiranja kod korisnika " + korisnik.getIme());
//            return false;
//        }
////		prvo provera da li se korisnik vec testirao danas (za bilo koji test)
//        if ((korisnik.getTestiranja().getLast().getDatumTestiranja().get(Calendar.YEAR) == new GregorianCalendar()
//                .get(Calendar.YEAR)
//                && korisnik.getTestiranja().getLast().getDatumTestiranja()
//                .get(Calendar.MONTH) == new GregorianCalendar().get(Calendar.MONTH)
//                && korisnik.getTestiranja().getLast().getDatumTestiranja()
//                .get(Calendar.DAY_OF_MONTH) == new GregorianCalendar().get(Calendar.DAY_OF_MONTH)
//                && korisnik.getTestiranja().getLast().getTip() == tip)) {
//            posaljiPoruku("upozorenje.jedanTestDnevno");
//            return true;
//        }
//        return false;
//    }

    public void testirajKorisnika(Korisnik korisnik) throws IOException, NullPointerException {

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
                                if (!Evidencija.getInstance().jeVecTestiran(korisnik, TestTip.BRZI))
                                    brzoTestiranje(korisnik);
                                break;
                            case "2":
                                if (!Evidencija.getInstance().jeVecTestiran(korisnik, TestTip.PCR))
                                    testirajPCR(korisnik);
                                break;
                            case "3":
                                if (!Evidencija.getInstance().jeVecTestiran(korisnik, TestTip.BRZI) && !Evidencija.getInstance().jeVecTestiran(korisnik, TestTip.PCR)) {
                                    testirajPCR(korisnik);
                                    brzoTestiranje(korisnik);
                                }
                                break;
                            default:
                                break;
                        }
                    else {
//					korisnik je imao negativan test
                        korisnik.setTrenutniStatus(StatusKorisnika.POD_NADZOROM); // menja mu se trenutni status
                        // dodajemo jedno testiranje u listu testiranja tog korisnika
                        korisnik.getTestiranja().add(new Test(TestTip.BRZI, StatusKorisnika.POD_NADZOROM, new GregorianCalendar(), korisnik));
                    }
                    break;
                case "2":
//				prikaz korisniku njegovog poslednje testiranja
//                    tokKaKlijentu.println("Poslednje testiranje:\n" + korisnik.getTestiranja().getLast().toString());
                    posaljiIzvestaj(Evidencija.getInstance().poslednjeTestiranje(korisnik));

                    break;
                case "3":
                    return;
                default:
                    break;
            }
        }
    }

    public void obradiAdministratora(PrintStream tokKaKlijentu, BufferedReader unosKlijenta)
            throws NumberFormatException, IOException, NullPointerException {
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
                    posaljiPoruku("admin.izvestajSviKorisnici");
                    posaljiIzvestaj(Evidencija.getInstance().izlistajKorisnikeNaOsnovuStatusa(StatusKorisnika.SVI));
                    break;
                case "2":
                    posaljiPoruku("admin.izvestajBrziPozitivni");
                    posaljiIzvestaj(Evidencija.getInstance().izlistajKorisnikeNaOsnovuStatusa(StatusKorisnika.POZITIVAN));
                    break;
                case "3":
                    posaljiPoruku("admin.izvestajBrziNegativni");
                    posaljiIzvestaj(Evidencija.getInstance().izlistajKorisnikeNaOsnovuStatusa(StatusKorisnika.NEGATIVAN));
                    break;
                case "4":
                    posaljiPoruku("admin.izvestajPCRPozitivni");
                    posaljiIzvestaj(Evidencija.getInstance().izlistajKorisnikeNaOsnovuStatusa(StatusKorisnika.PCR_POZITIVAN));
                    break;
                case "5":
                    posaljiPoruku("admin.izvestajPCRNegativni");
                    posaljiIzvestaj(Evidencija.getInstance().izlistajKorisnikeNaOsnovuStatusa(StatusKorisnika.PCR_NEGATIVAN));
                    break;
                case "6":
                    posaljiPoruku("admin.izvestajPodNadzorom");
                    posaljiIzvestaj(Evidencija.getInstance().izlistajKorisnikeNaOsnovuStatusa(StatusKorisnika.POD_NADZOROM));
                    break;
                case "0":
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
//					evidencija.registrovaniKorisnici.add(noviKorisnik);
//					System.out.println(evidencija.registrovaniKorisnici.getLast().toString());
                        posaljiPoruku("registracija.uspesna");

                        break;
                    case "2":
                        posaljiPoruku("prijava.uvod");

                        Korisnik prijavljeniKorisnik = prijaviKorisnika();
//	korisnik je prijavljen dakle ima ga u bazi podataka, sad se pristupa pitanjima i testiranju
//					ako je hteo da se prijavi a nije registrovan vratice se null za prijavljeniKorisnik, pa mu se ponovo otvara glavni meni
                        if (prijavljeniKorisnik != null)
                            testirajKorisnika(prijavljeniKorisnik);

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
            System.err.println("Doslo je do problema pri uspostavljanju toka podataka ka klijentu");
            return;
        }
//         catch (NullPointerException e) {
//            // TODO Auto-generated catch block
//            System.err.println("Korisnik je nasilno napustio aplikaciju");
//            return;
//        }
    }

}
