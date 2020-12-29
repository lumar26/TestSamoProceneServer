package server;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


public class Evidencija {
    //	Singleton pattern
    private static Evidencija instance;
    private File korisnici;
    private File testiranja;

    public static Evidencija getInstance() {
        if (instance == null) {
            instance = new Evidencija();
        }
        return instance;
    }

    private Evidencija() {
            korisnici = new File("registrovaniKorisnici.txt");
            testiranja = new File("testiranja.txt");
    }


//  1. da vidimo prvo za fajl koji ima korisnike sadrzane


    //	prvo nam treba za upisivanje u fajl
    public boolean postojiKorisnikUFajlu(Korisnik k) {
        if (!korisnici.exists()) return false;
        List<Korisnik> listaKorisnika = prebaciFajlUListuKorisnika();
        if(listaKorisnika.contains(k)) return true;
//        try {
//            Scanner citac = new Scanner(korisnici);
//            while (citac.hasNextLine()) {
//                String sledecaLinija = citac.nextLine();
//                if (sledecaLinija != null && !sledecaLinija.equals("")) {
//                    Korisnik rez = linijaUKorisnika(sledecaLinija);
//
//                    if (rez.equals(k)) return true;
//                }
//
//            }
//            citac.close();
//        } catch (FileNotFoundException fnfe) {
//            fnfe.printStackTrace();
//            System.err.println("[ERROR EVIDENCIJA]: Scanner klasa nije mogla da nadje fajl trazeni ");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return false;
    }

    public boolean dodajKorisnikaUFajl(Korisnik k) {
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
    public Korisnik nadjiKorisnikaUFajlu(String username, String password) {
        try {
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

    public Korisnik nadjiKorisnikaUFajlu(String username) {
        try {
            Scanner citac = new Scanner(korisnici);
            while (citac.hasNextLine()) {
                Korisnik rez = linijaUKorisnika(citac.nextLine());
                if (rez.getUsername().equals(username)) return rez;
            }
            citac.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //    2. sad da odradimo testiranja

    public String izlistajTestoveTipa(StatusKorisnika statusKorisnika) {
        String rez = "";
        List<Test> testovi = prebaciFajlUListuTestova();
        for (Test t :
                testovi) {
            if(t.getRezultatTesta()==statusKorisnika)
            rez += testULiniju(t);
        }
        return rez;
    }

    private List<Test> vratiSveTestoveTipa(StatusKorisnika statusKorisnika) {
        List<Test> testovi = new ArrayList<>();

        try (Scanner citac = new Scanner(testiranja)) {
            while (citac.hasNextLine()) {
                Test t = linijuUTest(citac.nextLine());
                if (t.getRezultatTesta().equals(statusKorisnika)) testovi.add(t);
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
        StatusKorisnika statusKorisnika = StatusKorisnika.valueOf(izdvojiKredencijal(kredencijali[6]));
        return new Korisnik(izdvojiKredencijal(kredencijali[0]), izdvojiKredencijal(kredencijali[1]), izdvojiKredencijal(kredencijali[2]),
                izdvojiKredencijal(kredencijali[3]), izdvojiKredencijal(kredencijali[4]), izdvojiKredencijal(kredencijali[5]), statusKorisnika);
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
        return new Test(TestTip.valueOf(izdvojiKredencijal(kredencijali[1])), StatusKorisnika.valueOf(izdvojiKredencijal(kredencijali[2])),
                datum, nadjiKorisnikaUFajlu(izdvojiKredencijal(kredencijali[0])));
    }

    // ovde proveravamo dal je vec testiran korisnik
    public boolean jeVecTestiran(Korisnik k, TestTip test) {
        if (!testiranja.exists()) {
            return false;
        }
        List<Test> testovi = prebaciFajlUListuTestova();
        for (Test t :
                testovi) {
            if (t.getKorisnik().equals(k) && t.getTip() == test) return true;
        }
        return false;
    }

    public boolean dodajTestiranjeUFajl(Korisnik k, TestTip tip, StatusKorisnika rezultat) {
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
        List<Test> testovi = prebaciFajlUListuTestova();
        for (Test t :
                testovi) {
            if ((t.getKorisnik().equals(k) && poslednji == null) ||
                        (t.getKorisnik().equals(k) && t.getDatumTestiranja().getTimeInMillis() > poslednji.getDatumTestiranja().getTimeInMillis())) {
                    poslednji = t;
                }
        }
        if (poslednji != null){
            return poslednji.toString();
        }
        return "Korisnik se nije ni jednom testirao";
    }

    private List<Korisnik> prebaciFajlUListuKorisnika(){
        Scanner citac;
        List<Korisnik> rez = new LinkedList<>();
        try  {
            citac = new Scanner(korisnici);

            while (citac.hasNextLine()){
                rez.add(linijaUKorisnika(citac.nextLine()));
            }
            citac.close();
        } catch (FileNotFoundException e) {
            System.out.println("[EVIDENCIJA, zameniString]: nije mogao da se inicijalizuje citac");
            e.printStackTrace();
        }
        return rez;
    }
    private List<Test> prebaciFajlUListuTestova() {
        Scanner citac;
        List<Test> rez = new LinkedList<>();
        try  {
            citac = new Scanner(testiranja);

            while (citac.hasNextLine()){
                rez.add(linijuUTest(citac.nextLine()));
            }
            citac.close();
        } catch (FileNotFoundException | ParseException e) {
            System.out.println("[EVIDENCIJA, zameniString]: nije mogao da se inicijalizuje citac");
            e.printStackTrace();
        }
        return rez;
    }

    public void zameniStatusKorisnika(Korisnik korisnik,StatusKorisnika noviStatus) {
//        planiram da ubacim svaku liniju u listu, onda iz liste da izbacim datog korisnika pa da ga posle ubacim sa novim statusom
        FileWriter brisac, upis;
        List<Korisnik> listaKorisnika;
            try  {
                listaKorisnika = prebaciFajlUListuKorisnika();

                if(postojiKorisnikUFajlu(korisnik)){

                    listaKorisnika.remove(korisnik);
                    listaKorisnika.add(korisnik.zameniStatus(noviStatus));
//                    ovde sam korisnika sa zamenjenim statusom dodao u listu
                }else{
                    System.out.println("korisnik koji je prosledjen se uopste nije nalazio u listi");
                }
//                ovim sam obezbedio da ovaj upis prvo izbrise sav sadrzaj pa da tek onda upisuje
                brisac = new FileWriter(korisnici, false);
                brisac.write("");
                brisac.close();
//                sad krecemo da ubacujemo iz liste
                upis = new FileWriter(korisnici, true);
                for (Korisnik k :
                        listaKorisnika) {
                    upis.write(korisnikULiniju(k));
                }


//                zatvaramo tokove
                upis.close();
            } catch (FileNotFoundException e) {
                System.out.println("[EVIDENCIJA, zameniString]: nije mogao da se inicijalizuje citac");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("[EVIDENCIJA, zameniString]: nije mogao da se inicijalizuje upis");
                e.printStackTrace();
            }
    }

    public String izlistajKorisnikeNaOsnovuStatusa(StatusKorisnika s) {
        String rez = "";
        List<Korisnik> lista = prebaciFajlUListuKorisnika();
        if (StatusKorisnika.SVI == s) {
            for (Korisnik k :
                    lista) {
                rez += k.toString();
            }
        }else{
            for (Korisnik k :
                    lista) {
                if(k.getTrenutniStatus() == s) rez += k.toString();
            }
        }
        return rez;
    }

    public String prikaziStatistiku() {
        int brziNegativni = 0, brziPozitivini = 0, pcrPozitivni = 0, pcrNegativni = 0, podNadzorom = 0, pocetni = 0;
        List<Korisnik> listaKOrisnika = prebaciFajlUListuKorisnika();
        for (Korisnik k :
                listaKOrisnika) {
            switch (k.getTrenutniStatus()){
                case NEGATIVAN:
                    brziNegativni++;
                    break;
                case POZITIVAN:
                    brziPozitivini++;
                    break;
                case PCR_NEGATIVAN:
                    pcrNegativni++;
                    break;
                case PCR_POZITIVAN:
                    pcrPozitivni++;
                    break;
                case POD_NADZOROM:
                    podNadzorom++;
                    break;
                case POCETAN:
                    pocetni++;
                    break;

            }
        }
        return "Broj pozitivnih na brzom testu: " + brziPozitivini +"\nBroj negativnih na brzom testu: " + brziNegativni + "\nBroj pozitivnih na PCR testu: " + pcrPozitivni +
                "\nBroj negativnih na PCR testu: " + pcrNegativni + "\nBroj korisnika koji su pod nadzorom: " + podNadzorom + "\nBroj prijavljenih korisnika koji se nisu još uvek testirali: "+pocetni;
    }

    public boolean jeTestiranDanas(Korisnik korisnik) {
        List<Test> testovi = prebaciFajlUListuTestova();
        for (Test t :
                testovi) {
            LocalDateTime danas = LocalDateTime.now();
            LocalDateTime prosledjenoVreme = LocalDateTime.ofInstant(t.getDatumTestiranja().toInstant(), ZoneId.systemDefault());
            if(t.getKorisnik().equals(korisnik) && prosledjenoVreme.getDayOfYear() == danas.getDayOfYear()
            && prosledjenoVreme.getYear() == danas.getYear()) return true;
        }
        return false;
    }

    public String podNadzoromTajmaut() {
        String rez = ">>>----<<<\nLista korisnika pod nadzorom kojima je isteklo vreme za ponovno testiranje:\n";
        List<Test> listaTestiranja = prebaciFajlUListuTestova();
        for (Test t :
                listaTestiranja) {
            LocalDateTime danas = LocalDateTime.now();
            LocalDateTime vremeTestiranja = LocalDateTime.ofInstant(t.getDatumTestiranja().toInstant(), ZoneId.systemDefault());
            if (danas.getDayOfYear() - vremeTestiranja.getDayOfYear() > 2 && t.getRezultatTesta() == StatusKorisnika.POD_NADZOROM) rez += t.getKorisnik().toString();
        }
        return rez + ">>>----<<<";
    }
}
