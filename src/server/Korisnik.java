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
    Status trenutniStatus;
    private LinkedList<Test> testiranja = new LinkedList<>();

    public LinkedList<Test> getTestiranja() {
        return testiranja;
    }

    public void setTestiranja(LinkedList<Test> testiranja) {
        this.testiranja = testiranja;
    }

    // ovde mi je public pravilo problem
    public Korisnik(String username, String password, String ime, String prezime, String pol, String email) {
        this.username = username;
        this.password = password;
        this.ime = ime;
        this.prezime = prezime;
        this.pol = pol;
        this.email = email;
    }

    @Override
    public String toString() {
        return "ime: " + ime + "\t prezime; " + prezime + "\t status: " + trenutniStatus + "\t email: " + email;
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
                korisnik.getEmail()) && getTrenutniStatus() == korisnik.getTrenutniStatus() && Objects.equals(getTestiranja(),
                korisnik.getTestiranja());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getIme(), getPrezime(), getPol(), getEmail(), getTrenutniStatus(), getTestiranja());
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

    public Status getTrenutniStatus() {
        return trenutniStatus;
    }

    public void setTrenutniStatus(Status status) {
        this.trenutniStatus = status;
    }

}

