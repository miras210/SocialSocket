package kz.malimov.server;

import kz.malimov.db.UserDB;
import kz.malimov.models.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SocialServer {
    private ServerSocket serverSocket;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true)
                new ClientHandler(serverSocket.accept()).start();
        } catch (IOException ioe) {
            stop();
            ioe.printStackTrace();
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private User client;

        public ClientHandler (Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                sendMessage("Connection has been established");
                while (!isAuthorised()) {
                    sendMessage("Please try again");
                }
                do {
                    sendMessage("This server provides following commands:" +
                            "1) Type 'UPDATE' if you want to get latest posts" +
                            "2) Type 'POST' if you want to create a new post" +
                            "3) Type 'QUIT' if you want to finish your session" +
                            "-----------------------------------------------");
                    inputLine = in.readLine();
                    switch (inputLine) {
                        case "UPDATE" -> update();
                        case "POST" -> post();
                        default -> sendMessage("Incorrect command, please try again");
                    }
                } while (!inputLine.equals("QUIT"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void update() {

        }

        private void post() {

        }

        private boolean isAuthorised() {
            try {
                sendMessage("Enter your username" +
                        "-----------------------------------------------");
                String inputLine = getInput();
                List<User> userList = UserDB.getInstance().getAllUsers();
                for (User u :
                        userList) {
                    if (u.getUsername().equals(inputLine)) {
                        sendMessage("Enter your password" +
                                "-----------------------------------------------");
                        inputLine = getInput();
                        if (inputLine.equals(u.getPassword())) {
                            sendMessage("You have been successfully logged in");
                            client = u;
                            return true;
                        } else {
                            sendMessage("Incorrect password");
                            return false;
                        }
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private String getInput() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("CLIENT<<"+inputLine);
                    return inputLine;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void sendMessage(String msg) {
            out.println(msg);
            System.out.println("SERVER>>"+msg);
        }
    }
}
