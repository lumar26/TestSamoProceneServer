package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

	static int port = 8000;
	static LinkedList<ClientHandler> handlers = new LinkedList<>();
//	static LinkedList<Korisnik> registrovaniKorisnici = new LinkedList<Korisnik>();
	static String adminPassword = "admin123";
	static Evidencija evidencija;

	
	public static void main(String[] args) {

		ServerSocket prijemniSoket;
		Socket komunikacioniSoket;
		
		try {
			prijemniSoket = new ServerSocket(port);
			evidencija = new Evidencija();

			while (true) {
				System.out.println("Server je pokrenut i ceka na zahteve za konekcijem");
				
				komunikacioniSoket = prijemniSoket.accept();
				System.out.println("Doslo je do konekcije sa klijentom");
				ClientHandler clientHandler = new ClientHandler(komunikacioniSoket);
				handlers.add(clientHandler);
				clientHandler.start();
				System.out.println("trenutno je aktivna " + handlers.size() + " konekcija");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

