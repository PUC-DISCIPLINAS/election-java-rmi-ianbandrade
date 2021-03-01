import utils.HashMD5;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
  static String message = "blank";

  static Election election;

  static Registry registry;

  static final String EXIT = "exit";

  public static void main(String[] args) {
    String option, candidate, electorName;
    final Scanner inReader = new Scanner(System.in);

    try {
      registry = LocateRegistry.getRegistry(1888);
      election = (Election) registry.lookup("Server");

      System.out.println("Type your name:");
      System.out.print("> ");
      electorName = inReader.nextLine();
      System.out.println();
      System.out.println("Welcome " + electorName);

      do {
        System.out.println();
        System.out.println("Number menu");
        System.out.println("1 - Vote\n2 - Show votes");
        System.out.println();
        System.out.println("Type 'exit' to finish.");
        System.out.println();
        System.out.print("> ");
        option = inReader.nextLine();

        switch (option) {
          case "1":
            System.out.println();
            System.out.println("Hmm, " + electorName + " type a candidate number to vote:");
            System.out.print("> ");
            candidate = inReader.nextLine();
            System.out.println();
            message = election.vote(new HashMD5(electorName).getElector(), candidate);
            System.out.println(message);
            break;

          case "2":
            System.out.println();
            System.out.println(electorName + ", type a candidate number to show their votes:");
            System.out.print("> ");
            candidate = inReader.nextLine();
            System.out.println();
            message = election.result(candidate);
            System.out.println(message);
            break;

          default:
            if (option.equalsIgnoreCase(EXIT)) break;
            System.out.println();
            System.out.println("I don't understand " + "'" + option + "'");
            break;
        }
      } while (!option.equalsIgnoreCase(EXIT));

      System.out.println();
      System.out.println("Disconnecting...");
    } catch (RemoteException e) {
      System.out.println("Server error: " + e.getMessage());
    } catch (NotBoundException e) {
      System.out.println("Client error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
