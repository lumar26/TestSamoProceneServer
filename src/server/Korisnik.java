package server;

import java.util.LinkedList;
import java.util.Objects;

public class Korisnik {
    private String username;
    private String password;
    private String ime;
    private String prezime;
    private String pol;
    private String email;
    StatusKorisnika trenutniStatusKorisnika;


    // ovde mi je public pravilo problem
    public Korisnik(String username, String password, String ime, String prezime, String pol, String email, StatusKorisnika statusKorisnika) {
        this.username = username;
        this.password = password;
        this.ime = ime;
        this.prezime = prezime;
        this.pol = pol;
        this.email = email;
        this.trenutniStatusKorisnika = statusKorisnika;
    }

    @Override
    public String toString() {
//        vrlo su bitni ovi razmaci ovde i dvotacke
        return "username:" +this.username+ " password:" +this.password+ " ime:" + ime + " prezime:" + prezime + " pol:" + this.pol+ " email:" + email + " status:" + trenutniStatusKorisnika + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Korisnik)) return false;
        Korisnik korisnik = (Korisnik) o;
        return Objects.equals(getUsername(),
                korisnik.getUsername()) && Objects.equals(getPassword(),
                korisnik.getPassword()) && Objects.equals(getIme(),
                korisnik.getIme()) && Objects.equals(getPrezime(),
                korisnik.getPrezime()) && Objects.equals(getPol(),
                korisnik.getPol()) && Objects.equals(getEmail(),
                korisnik.getEmail()) && getTrenutniStatus() == korisnik.getTrenutniStatus();
    }

    public Korisnik zameniStatus(StatusKorisnika noviStatus) {
        this.trenutniStatusKorisnika = noviStatus;
        return this;
    }

    //	geteri i seteri za atribute korisnika
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StatusKorisnika getTrenutniStatus() {
        return trenutniStatusKorisnika;
    }

    public void setTrenutniStatus(StatusKorisnika statusKorisnika) {
        this.trenutniStatusKorisnika = statusKorisnika;
    }

}

