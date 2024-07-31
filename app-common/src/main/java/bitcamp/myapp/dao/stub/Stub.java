package bitcamp.myapp.dao.stub;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Stub {
  private ObjectOutputStream out;
  private ObjectInputStream in;

  public Stub(ObjectOutputStream out, ObjectInputStream in) {
    this.out = out;
    this.in = in;
  }

  public String playerTurn(int currentNumber, int count) throws Exception {
    out.writeUTF("playerTurn");
    out.writeInt(currentNumber);
    out.writeInt(count);
    out.flush();
    String result = in.readUTF();
    int newCurrentNumber = in.readInt();
    return result + " 현재 숫자: " + newCurrentNumber;
  }

  public String computerTurn(int currentNumber) throws Exception {
    out.writeUTF("computerTurn");
    out.writeInt(currentNumber);
    out.flush();
    String result = in.readUTF();
    int newCurrentNumber = in.readInt();
    return result + " 현재 숫자: " + newCurrentNumber;
  }
}
