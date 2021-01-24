package kz.malimov.client;

public class Main {
    public static void main(String[] args) {
        SocialClient client1 = new SocialClient();
        client1.startConnection("127.0.0.1", 5555);

    }
}
