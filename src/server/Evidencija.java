package server;

import java.io.*;
import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Evidencija {
    //	Singleton pattern
    private static Evidencija instance;

    public static Evidencija getInstance() {
        if (instance == null) {
            return new Evidencija();
        }
        return instance;
    }

    private Evidencija() {
    }

    private File korisnici = new File("registrovaniKorisnici.txt");
    private File testiranja = new File("testiranja.txt");

//  1. da vidimo prvo za fajl koji ima korisnike sadrzane


    //	prvo nam treba za upisivanje u fajl
    public boolean postojiKorisnikUBazi(Korisnik k){
        try  {
            Scanner citac = new Scanner(korisnici);
            while (citac.hasNextLine()) {
                Korisnik rez = linijaUKorisnika(citac.nextLine());
                if (rez.equals(k)) return true;
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
            FileWriter upis = new FileWriter(korisnici);
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
        try  {
            Scanner citac = new Scanner(korisnici);
            while (citac.hasNextLine()) {
                Korisnik rez = linijaUKorisnika(citac.nextLine());
                if (rez.getUsername().equals(username) && rez.getPassword().equals(password)) return rez;
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
            System.err.println("P[ERROR EVIDENCIJA]: rilikom upoisa novog testiranja u fajl doslo je do greske doslo je do problema");
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
        return new Korisnik(izdvojiKredencijal(kredencijali[0]), izdvojiKredencijal(kredencijali[1]), izdvojiKredencijal(kredencijali[2]), izdvojiKredencijal(kredencijali[3]), izdvojiKredencijal(kredencijali[4]), izdvojiKredencijal(kredencijali[5]));
    }

    private String korisnikULiniju(Korisnik k) {
        return k.toString();
    }

    private String izdvojiKredencijal(String zapis) {
//		zapis je oblika username:lumar
        System.out.println(zapis);
        return zapis.split(":")[1];
    }

    private String testULiniju(Test t) {
        return t.toString();
    }

    private Test linijuUTest(String linija) throws ParseException {
//        dobijamo liniju tipa Korisni:luka tip_test:PCR itd..., jedini je problem dal pretvara odmah string u enum
        String[] kredencijali = linija.split(" ");

//        _????????????????????????????????????????????????????????????????????
//ovde je veoma veliko pitanje dal ce da radi 'linijaUKOrisnika()', al trebalo bi
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d = df.parse(izdvojiKredencijal(kredencijali[3]));
        GregorianCalendar datum = new GregorianCalendar();
        datum.setTime(d);
        return new Test(TipTesta.valueOf(izdvojiKredencijal(kredencijali[1])), Status.valueOf(izdvojiKredencijal(kredencijali[2])), datum, linijaUKorisnika(izdvojiKredencijal(kredencijali[0])));
    }
}