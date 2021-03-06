package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();
        ServerSocket server = null;
        Socket socket = null;
        final int PORT = 8189;

        try {
            server = new ServerSocket( PORT );
            System.out.println( "Server started" );

            while (true) {
                socket = server.accept();
                new ClientHandler( this, socket );
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format( "Сообщение всем от [ %s ]:\n%s", sender.getNickname(), msg );
        for (ClientHandler c : clients) {
            c.sendMsg( message );
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add( clientHandler );
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove( clientHandler );
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void sendPrivate(ClientHandler client, String nickname, String msg) {
        for (ClientHandler c : clients) {

            if (c.getNickname().equals( nickname )) {
                c.sendMsg( "Сообщение от [ " + client.getNickname() + " ]:\n" + msg );
                client.sendMsg( "Сообщение пользователю [ " + c.getNickname() + " ]:\n" + msg );
                return;
            }

        }
        client.sendMsg( "Ошибка:\nПользователь [ " + nickname + " ] отсутствует в чате" );
    }

    public boolean repeatNickname(String nick) {
        for (ClientHandler c : clients) {
            if (c.getNickname().equals( nick )) {
                return true;
            }
        }
        return false;
    }
}
