package client;

import server.Server;

import java.util.Scanner;


public class ConsoleClient {

    private Client client;

    public ConsoleClient(Client client) {
        this.client = client;
    }

    public void startWork(String[] args){
        Client client = new Client();

        if (!client.isLoggedIn()) {
            try {

                client.setServer(args[0]);
                int port = Integer.parseInt(args[1]);
                checkPortNumber(port);
                client.setPort(port);
                client.setUsername(args[2]);
                client.setClientConnectedHandler(isConnected -> {
                    if (isConnected) {
                        System.out.println("try");
                    }
                });
                client.startWork();


//                addClientConnectedHandler();
            } catch (NumberFormatException e) {
                System.out.println("Try again, enter host, port and username");
            }
        }

      try (Scanner in = new Scanner(System.in)) {
          while (client.isLoggedIn()) {
                String text = in.nextLine();
                if(text.equals("/logout")) {
//                    System.out.println("logout!!!");
                    if(client.isLoggedIn()) {
                    client.sendLogoutMessage();
                    }
                }
                else {
                    if (text.equals("/others")) {
//                        System.out.println(text + "<-is it the line");
//                        System.out.println("othres!!!");
                        if (client.isLoggedIn()) {
                            client.sendUserListMessage();
                        }
                    } else {
                        client.sendTextMessage(text);
                    }
                }
            }
      }
    }

    private void checkPortNumber(int portNumber) throws NumberFormatException {
        if ((portNumber < Server.MIN_PORT_NUMBER) || (portNumber > Server.MAX_PORT_NUMBER)) {
            throw new NumberFormatException("Incorrect value");
        }
        client.setPort(portNumber);
    }

    public class MessageUpdater implements ValueChangedHandler {
        @Override
        public void handle(Object value) {
            if (value != null) {
                System.out.println(value.toString() + "/n");
            }

        }
    }
}
