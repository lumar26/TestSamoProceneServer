package server;

import java.io.*;
import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Evidencija {
    //	Singleton pattern
    private static Evidencija instance;
    private File korisnici;
    private File testiranja;

    public static Evidencija getInstance() {
        if (instance == null) {
            return new Evidencija();
        }
        return instance;
    }

    private Evidencija() {
        korisnici = new File("registrovaniKorisnici.txt");
        testiranja = new File("testiranja.txt");
    }


//  1. da vidimo prvo za fajl koji ima korisnike sadrzane


    //	prvo nam treba za upisivanje u fajl
    public boolean postojiKorisnikUBazi(Korisnik k) {
        if (!korisnici.exists()) return false;
        try {
            Scanner citac = new Scanner(korisnici);
            while (citac.hasNextLine()) {
                String sledecaLinija = citac.nextLine();
                if (sledecaLinija != null && !sledecaLinija.equals("")) {
                    System.out.println("Jedna linija procitan iz fajla" + sledecaLinija);
                    Korisnik rez = linijaUKorisnika(sledecaLinija);

                    if (rez.equals(k)) return true;
                }

            }
            citac.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.err.println("[ERROR EVIDENCIJA]: Scanner klasa nije mogla da nadje fajl trazeni ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dodajKorisnika(Korisnik k) {
        try {
            FileWriter upis = new FileWriter(korisnici, true);
            upis.write(korisnikULiniju(k));
            upis.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR EVIDENCIJA]: Prilikom registracije i upisa u fajl doslo je do problema");
            return false;
        }
    }

    //    onda nam treba za prelistavanje fajla
    public Korisnik nadjiKorisnikaUBazi(String username, String password) {
        try {
            Scanner citac = new Scanner(korisnici);
            while (citac.hasNextLine()) {
                Korisnik rez = linijaUKorisnika(citac.nextLine());
                System.out.println("Jedan korisnik iz fajla" + rez.toString());
                if (rez.getUsername().equals(username) && rez.getPassword().equals(password)) return rez;
            }
            citac.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Korisnik nadjiKorisnikaUBazi(String username) {
        try {
            Scanner citac = new Scanner(korisnici);
            while (citac.hasNextLine()) {
                Korisnik rez = linijaUKorisnika(citac.nextLine());
                System.out.println("Jedan korisnik iz fajla" + rez.toString());
                if (rez.getUsername().equals(username)) return rez;
            }
            citac.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //    2. sad da odradimo testiranja
    public boolean dodajTestiranje(Test t) {
        try {
            FileWriter upis = new FileWriter(testiranja);
            upis.write(testULiniju(t));
            upis.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR EVIDENCIJA]: rilikom upoisa novog testiranja u fajl doslo je do greske doslo je do problema");
            return false;
        }
    }

    public String izlistajTestoveTipa(Status status) {
        String rez = "";
        List<Test> testovi = vratiSveTestoveTipa(status);
        for (Test t :
                testovi) {
            rez += testULiniju(t);
        }
        return rez;
    }

    private List<Test> vratiSveTestoveTipa(Status status) {
        List<Test> testovi = new ArrayList<>();

        try (Scanner citac = new Scanner(testiranja)) {
            while (citac.hasNextLine()) {
                Test t = linijuUTest(citac.nextLine());
                if (t.getRezultatTesta().equals(status)) testovi.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERROR EVIDENCIJA]: ]Greska prilikom vracanja trazenih testova iz fajla 'testiranje.txt'...");
        }
        return testovi;
    }

    private Korisnik linijaUKorisnika(String linija) {
//		nama je linija oblika: username:luka password:1234 ime:Luka itd...
        String[] kredencijali = linija.split(" ");
//		imamo 6 kredeencijala, ne bi trebalo \n na kraju sto je da nam pravi problem
        Status status = Status.valueOf(izdvojiKredencijal(kredencijali[6]));
        return new Korisnik(izdvojiKredencijal(kredencijali[0]), izdvojiKredencijal(kredencijali[1]), izdvojiKredencijal(kredencijali[2]),
                izdvojiKredencijal(kredencijali[3]), izdvojiKredencijal(kredencijali[4]), izdvojiKredencijal(kredencijali[5]), status);
    }

    private String korisnikULiniju(Korisnik k) {
        return k.toString();
    }

    private String izdvojiKredencijal(String zapis) {
//		zapis je oblika username:lumar
        return zapis.split(":")[1];
    }

    private String testULiniju(Test t) {
        return t.toString();
    }

    private Test linijuUTest(String linija) throws ParseException {
//        dobijamo liniju tipa Korisni:luka tip_test:PCR itd..., jedini je problem dal pretvara odmah string u enum
        String[] kredencijali = linija.split(" ");

//ovde je veoma veliko pitanje dal ce da radi 'linijaUKOrisnika()', al trebalo bi
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d = df.parse(izdvojiKredencijal(kredencijali[3]));
        GregorianCalendar datum = new GregorianCalendar();
        datum.setTime(d);
        return new Test(TipTesta.valueOf(izdvojiKredencijal(kredencijali[1])), Status.valueOf(izdvojiKredencijal(kredencijali[2])),
                datum, nadjiKorisnikaUBazi(izdvojiKredencijal(kredencijali[0])));
    }

    // ovde proveravamo dal je vec testiran korisnik
    public boolean jeVecTestiran(Korisnik k, TipTesta test) {
        try {
            Scanner citac = new Scanner(testiranja);
            while (citac.hasNextLine()) {
                String linija = citac.nextLine();
//                if (linija == null || linija.equals("")) return false;
                if (linijuUTest(linija).getKorisnik().equals(k) && linijuUTest(linija).getTip() == test) return true;
            }
            citac.close();
        } catch (FileNotFoundException | ParseException e) {
            System.out.println("[ERROR je vec testiran]Ili je lose parsovano ili nije nadjen fajl ");
            e.printStackTrace();
        }
        return false;
    }

    public boolean dodajTestiranje(Korisnik k, TipTesta tip, Status rezultat) {
        Test t = new Test(tip, rezultat, new GregorianCalendar(), k);
        try {
            FileWriter upis = new FileWriter(testiranja, true);
            upis.write(testULiniju(t));
            upis.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR EVIDENCIJA]: Prilikom registracije i upisa u fajl doslo je do problema");
            return false;
        }
    }

    public String poslednjeTestiranje(Korisnik k) {
        Test poslednji = null;
        try {
            Scanner citac = new Scanner(testiranja);
            while (citac.hasNextLine()) {
                String linija = citac.nextLine();
                Test trenutni = linijuUTest(linija);
//                ovde bi trebalo da ova metoda vraca broj milisekundi od epoch-a
                if (poslednji == null || trenutni.getDatumTestiranja().getTimeInMillis() > poslednji.getDatumTestiranja().getTimeInMillis()) {
                    poslednji = trenutni;
                }
            }
            citac.close();
        } catch (FileNotFoundException | ParseException e) {
            System.out.println("[ERROR je vec testiran]Ili je lose parsovano ili nije nadjen fajl ");
            e.printStackTrace();
        }
        if (poslednji != null){
            return poslednji.toString();
        }
        return "Korisnik se nije ni jednom testirao";
    }


    private GregorianCalendar vratiDatum(String pattern) {

        // this actually works, got rid of the original code idea
        String[] splitDate = pattern.split("/");
        int days = Integer.parseInt(splitDate[0]);
        int month = (Integer.parseInt(splitDate[1]) - 1);
        int year = Integer.parseInt(splitDate[2]);

        // dates go in properly
        return new GregorianCalendar(year, month, days);

    }
}