package bitcamp.myapp.dao.skel;

import bitcamp.myapp.dao.GameDao;
import bitcamp.myapp.dao.GameServiceDao;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Skel extends Thread {
  private Socket clientSocket;

  public Skel(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

      GameDao gameService = new GameServiceDao();

      while (true) {
        String request = in.readUTF();

        if (request.equals("quit")) {
          break;
        }

        switch (request) {
          case "playerTurn":
            int currentNumber = in.readInt();
            int count = in.readInt();
            String playerResult = gameService.playerTurn(currentNumber, count);
            out.writeUTF(playerResult);
            out.writeInt(gameService.getCurrentNumber());
            out.flush();
            break;

          case "computerTurn":
            currentNumber = in.readInt();
            String computerResult = gameService.computerTurn(currentNumber);
            out.writeUTF(computerResult);
            out.writeInt(gameService.getCurrentNumber());
            out.flush();
            break;

          default:
            out.writeUTF("Unknown request");
            out.flush();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
