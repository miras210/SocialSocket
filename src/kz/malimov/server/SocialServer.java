package kz.malimov.server;

import kz.malimov.db.PostsDB;
import kz.malimov.db.UserDB;
import kz.malimov.models.Post;
import kz.malimov.models.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class SocialServer {
    private ServerSocket serverSocket;
    private static int id = 1;

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
        private String username;

        public ClientHandler (Socket socket) {
            this.clientSocket = socket;
            this.username = "GUEST"+id;
            id++;
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
                    sendMessage("This server provides following commands:\n" +
                            "1) Type 'UPDATE' if you want to get latest posts\n" +
                            "2) Type 'POST' if you want to create a new post\n" +
                            "3) Type 'QUIT' if you want to finish your session\n" +
                            "-----------------------------------------------");
                    inputLine = in.readLine();
                    switch (inputLine) {
                        case "UPDATE" -> update();
                        case "POST" -> post();
                        case "QUIT" -> sendMessage("Quiting the system");
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
            sendMessage("Here are the list of all posts:");
            List<Post> posts = PostsDB.getInstance().getPostsList(client.getId(), true);
            for (Post post: posts) {
                sendMessage(post.toString());
            }
            sendMessage("End of the list");
        }

        private void post() {
            sendMessage("Enter the title of the new post\n" +
                    "-----------------------------------------------");
            String title = getInput();
            sendMessage("Enter the content of the new post\n" +
                    "-----------------------------------------------");
            String content = getInput();
            int visibility = -1;
            do {
                sendMessage("Enter the access level to the post, where\n" +
                        "'0' - access to the whole internet\n" +
                        "'1' - access to the authorised users\n" +
                        "'2' - access to the friends only\n" +
                        "-----------------------------------------------");
                visibility = Integer.parseInt(Objects.requireNonNull(getInput()));
                if (visibility < 0 || visibility > 2) {
                    sendMessage("Incorrect input");
                }
            } while (visibility < 0 || visibility > 2);
            PostsDB.getInstance().addPost(title, content, visibility, client.getId());
            sendMessage("The new post has been added to the database");
        }

        private boolean isAuthorised() {
            try {
                sendMessage("Enter your username\n" +
                        "-----------------------------------------------");
                String inputLine = getInput();
                List<User> userList = UserDB.getInstance().getAllUsers();
                for (User u :
                        userList) {
                    if (u.getUsername().equals(inputLine)) {
                        sendMessage("Enter your password\n" +
                                "-----------------------------------------------");
                        inputLine = getInput();
                        if (inputLine.equals(u.getPassword())) {
                            sendMessage("You have been successfully logged in");
                            this.username = u.getUsername();
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
                    System.out.println(this.username+"<<"+inputLine);
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
