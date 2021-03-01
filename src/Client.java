import utils.HashMD5;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
  static String message = "blank";

  static Election election;

  public static void main(String[] args) {
    String candidate = "321", electorName = "Patrick";

    try {

      Registry rgsty = LocateRegistry.getRegistry(1888);
      election = (Election) rgsty.lookup("1888");

      message = election.vote(new HashMD5(electorName).getElector(), candidate);

      System.out.println("Vote: " + message);

      message = election.result(candidate);
      System.out.println("Votos do candidato " + candidate + ": " + message);

    } catch (Exception e) {
      System.out.println("Client error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
