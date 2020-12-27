package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        String rez = unosKlijenta.readLine();
        if(rez.equals("*quit*")) throw new PrekidRadaException("Korisnik je odlucio da izađe iy aplikacije");
        return rez;
    }

    private String unesiKredencijal(String upit) throws IOException, PrekidRadaException {
        String odgovor;
        do {
            posaljiPoruku(upit);
//			mozda bi za ovo trebalo odmah ovde da se odradi try catch
            odgovor = primiPoruku();
        } while (odgovor == null || odgovor.equals(""));
        return odgovor.trim();
    }

    public Korisnik registrujKorisnika() throws IOException, PrekidRadaException {

//		posaljiPoruku("----------------------------------\nForma za registraciju\n--------------------------");
        username = unesiKredencijal("registracija.username");
        password = unesiKredencijal("registracija.password");
        ime = unesiKredencijal("registracija.ime");
        prezime = unesiKredencijal("registracija.prezime");
        pol = unesiKredencijal("registracija.pol");
        email = unesiKredencijal("registracija.email");
        Korisnik novi = new Korisnik(username, password, ime, prezime, pol, email, StatusKorisnika.POCETAN);
        if (Evidencija.getInstance().postojiKorisnikUFajlu(novi)) return null;
//        ako je vec registrovan vracamo null, a ako moze da se registruje ondak cemo da probamo da ga ubacimo u fajl i da ga vratimo
        if (Evidencija.getInstance().dodajKorisnikaUFajl(novi)) {
            System.out.println("Uspesno je dodat novi korisnik u fajl");
            return novi;
        }
        System.out.println("[ERROR EVIDENCIJA + CLIENTHENDLER] Korisnik novi nije dodat u fajl");
        return null;

    }

    public Korisnik prijaviKorisnika() throws IOException, PrekidRadaException {
        while (true) {
            boolean korisnikHoceIzlaz = false;
            username = unesiKredencijal("prijava.username");
            password = unesiKredencijal("prijava.password");
            Korisnik rez = Evidencija.getInstance().nadjiKorisnikaUFajlu(username, password);
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

    public int samoproceni(String upit) throws IOException, PrekidRadaException {

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

    public void brzoTestiranje(Korisnik korisnik) throws PrekidRadaException {
        int razultatTesta = (int) Math.round(Math.random());
        if (razultatTesta == 1) {
            posaljiPoruku("poruka.pozitivanBrziTest");
//			ovde se setuje trenutno stanje
            Evidencija.getInstance().zameniStatusKorisnika(korisnik, StatusKorisnika.POZITIVAN);
//            korisnik.setTrenutniStatus(StatusKorisnika.POZITIVAN);
//			azuriranje evidencije
            Evidencija.getInstance().dodajTestiranjeUFajl(korisnik, TestTip.BRZI, StatusKorisnika.POZITIVAN);

        } else {
            tokKaKlijentu.println("Rezultat vaseg brzog testa je negativan.");
            korisnik.setTrenutniStatus(StatusKorisnika.NEGATIVAN);
            Evidencija.getInstance().dodajTestiranjeUFajl(korisnik, TestTip.BRZI, StatusKorisnika.NEGATIVAN);
        }
    }

    private void testirajPCR(Korisnik korisnik) throws PrekidRadaException {
        int razultatTesta = (int) Math.round(Math.random());
        ExecutorService executorService = Executors.newCachedThreadPool();

        Runnable runnable = () -> {
            try {

                posaljiPoruku("pcr.cekanje");
                Thread.sleep(1000 * 5);
                posaljiPoruku("pcr.poslato");
                Thread.sleep(1000 * 5);
                posaljiPoruku("pcr.obrada");
//                posle da se promeni ovde u vise minuta
                Thread.sleep(1000 * 5);

                if (razultatTesta == 1) {
                    posaljiPoruku("poruka.pozitivanPCRTest");
                    Evidencija.getInstance().zameniStatusKorisnika(korisnik, StatusKorisnika.PCR_POZITIVAN);
//			azuriranje evidencije
                    Evidencija.getInstance().dodajTestiranjeUFajl(korisnik, TestTip.PCR, StatusKorisnika.PCR_POZITIVAN);
                } else {
                    posaljiPoruku("poruka.negativanPCRTest");
                    Evidencija.getInstance().zameniStatusKorisnika(korisnik, StatusKorisnika.PCR_NEGATIVAN);
                    Evidencija.getInstance().dodajTestiranjeUFajl(korisnik, TestTip.PCR, StatusKorisnika.PCR_NEGATIVAN);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Karanje");
        };

        executorService.submit(runnable);

    }

    public void testirajKorisnika(Korisnik korisnik) throws IOException, NullPointerException, PrekidRadaException {



        while (true) {
            posaljiPoruku("testiranje.meni");

            String akcija = primiPoruku();

            switch (akcija) {
                case "1":
                    if(Evidencija.getInstance().jeTestiranDanas(korisnik)){
                        posaljiPoruku("upozorenje.jedanTestDnevno");
                        break;
                    }
                    int brojacPotvrdnihOdgovora = 0;
                    posaljiPoruku("samoprocena.uvod");

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
//					korisnik nije imao dovoljno potvrdnih odgovora
                        Evidencija.getInstance().zameniStatusKorisnika(korisnik, StatusKorisnika.POD_NADZOROM);
                    }
                    break;
                case "2":
//				prikaz korisniku njegovog poslednje testiranja
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
            throws NumberFormatException, IOException, NullPointerException, PrekidRadaException {
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
                case "7":
                    posaljiPoruku("admin.izvestajStatistika");
                    posaljiIzvestaj(Evidencija.getInstance().prikaziStatistiku());
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
                String izbor = primiPoruku();

                switch (izbor) {
                    case "1":
                        Korisnik noviKorisnik = registrujKorisnika();
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
        catch (NullPointerException npe) {
            System.err.println("Korisnik je nasilno prekinuo aplikaciju");
        }
        catch (PrekidRadaException e){
            System.out.println(e.getMessage());
            posaljiPoruku("klijent.prekid");
        }
    }

}
