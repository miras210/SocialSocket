package kz.malimov.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocialClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Scanner sc = new Scanner(System.in);
            while (true) {
                String msg = getMessage();
                if (msg.charAt(msg.length()-1) == '-') {
                    sendMessage(sc.nextLine());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopConnection();
        }
    }

    public void sendMessage(String msg) {
        try {
            out.println(msg);
            System.out.println("CLIENT>>"+msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        String msg = "";
        try {
            msg = in.readLine();
            System.out.println("SERVER<<"+msg);
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
