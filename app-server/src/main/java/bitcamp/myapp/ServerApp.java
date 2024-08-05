package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.listener.InitApplicationListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerApp {
    // 애플리케이션의 상태 변화를 알리기 위한 리스너 객체들을 저장하는 리스트
    List<ApplicationListener> listeners = new ArrayList<>();
    // 애플리케이션의 상태 전체를 관리하는 애플리케이션 컨텍스트 객체를 생성
    ApplicationContext appCtx = new ApplicationContext();
    // 연결된 클라이언트 핸들러를 리스트에 저장
    List<ClientHandler> clients = new ArrayList<>();

    // 현재 턴인 플레이어 (0 또는 1을 가짐)
    int currentPlayer = 0;

    // 현재 숫자
    int currentNumber = 0;

    String nickname;

    public static void main(String[] args) {
        ServerApp app = new ServerApp();
        app.addApplicationListener(new InitApplicationListener());
        app.execute();
    }
    // 애플리케이션 리스너를 리스트에 추가하는 메서드
    private void addApplicationListener(ApplicationListener listener) {
        listeners.add(listener);
    }

    // 애플리케이션 리스너를 리스트에서 제거하는 메서드
    private void removeApplicationListener(ApplicationListener listener) {
        listeners.remove(listener);
    }

    void execute() {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("서버가 시작되었습니다.");

            for (ApplicationListener listener : listeners) {
                listener.onStart(appCtx);
            }

            // 두 명의 클라이언트가 연결될 때까지 대기
            while (clients.size() < 2) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this, clients.size() + 1);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 모든 클라이언트가 접속한 후 게임 시작
        startGame();
    }

    // 게임을 시작할때 메세지를 각 클라이언트에게 전송
    synchronized void startGame() {
        for (ClientHandler client : clients) {
            client.sendMessage("게임이 시작되었습니다. 닉네임은 " + client.nickname + " 입니다.");
        }
        sendNextTurnMessage();
    }

    synchronized void sendNextTurnMessage() {
        // 숫자가 31보다 큰지 확인하고 31이상이면 게임 종료 판단
        if (currentNumber >= 31) {
            endGame();
        } else {
            // 게임이 종료되면 승패 결과를 각 클라이언트에게 전송
            clients.get(currentPlayer).sendMessage(clients.get(currentPlayer).nickname + "님 턴, 현재 숫자: " + currentNumber);
        }
    }

    synchronized void handlePlayerInput(int playerNumber, int count) {
        if (playerNumber != currentPlayer + 1) {
            clients.get(playerNumber - 1).sendMessage("지금은 당신의 차례가 아닙니다!");
            return;
        }

        if (count < 1 || count > 3) {
            clients.get(currentPlayer).sendMessage("잘못된 입력입니다. 1-3 사이의 숫자를 입력하세요:");
            return;
        }

        // 입력받은 숫자만큼 현재 숫자를 증가시키고 각 클라이언트에게 보냄
        StringBuilder numbers = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            currentNumber++;
            numbers.append(currentNumber).append(" ");
        }

        broadcastMessage(numbers.toString().trim());

        if (currentNumber >= 31) {
            endGame();
        } else {
            currentPlayer = 1 - currentPlayer;
            sendNextTurnMessage();
        }
    }

    private void endGame() {
        clients.get(currentPlayer).sendMessage(clients.get(currentPlayer).nickname + " 님이 졌습니다!");
        clients.get(1 - currentPlayer).sendMessage(clients.get(1 - currentPlayer).nickname + " 님이 이겼습니다!");
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    class ClientHandler implements Runnable {
        public String nickname;
        private Socket socket;
        private ServerApp server;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        // 플레이어 번호
        private int playerNumber;

        public ClientHandler(Socket socket, ServerApp server, int playerNumber) {
            this.socket = socket;
            this.server = server;
            this.playerNumber = playerNumber;
            ObjectOutputStream tempOut = null;
            ObjectInputStream tempIn = null;
            try {
                tempOut = new ObjectOutputStream(socket.getOutputStream());
                tempIn = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.out = tempOut;
            this.in = tempIn;
        }

        @Override
        public void run() {
            try {
                this.nickname = in.readUTF();
                while (true) {
                    // 클라이언트로부터 숫자를 입력받고 읽고 처리
                    String message = in.readUTF();
                    try {
                        int count = Integer.parseInt(message);
                        server.handlePlayerInput(playerNumber, count);
                    } catch (NumberFormatException e) {
                        sendMessage("잘못된 입력입니다. 숫자를 입력하세요:");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            try {
                out.writeUTF(message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String receiveMessage() {
            try {
                return in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
