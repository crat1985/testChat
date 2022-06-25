import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	
	public Client(Socket socket, String username) {
		try {
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.username = username;
			
		} catch(IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void sendMsg() {
		try {
			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			Scanner scanner = new Scanner(System.in);
			while(socket.isConnected()) {
				String msgToSend = scanner.nextLine();
				bufferedWriter.write(username+": "+msgToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} catch(IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void listenForMsg() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String msgForGroupChat = bufferedReader.readLine();
					System.out.println(msgForGroupChat);
				} catch(IOException e) {
					closeEverything(socket, bufferedReader, bufferedWriter);
				}
			}
		}).start();
	}
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		try {
			if(bufferedReader!=null) {
				bufferedReader.close();
			}
			if(bufferedWriter!=null) {
				bufferedWriter.close();
			}
			if(socket!=null) {
				socket.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Entrez votre pseudo : ");
		String username = scanner.nextLine();
		Socket socket = new Socket("90.62.158.27",8888);
		Client client = new Client(socket, username);
		client.listenForMsg();
		client.sendMsg();
	}
}
