import utils.HashMD5;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
  static final String EXIT = "exit";
  static String message = "blank";
  static Election election;
  static Registry registry;

  public static void main(String[] args) {

    String option, candidate, electorName;
    int count = 0;
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
            try {
              message = election.vote(new HashMD5(electorName).getElector(), candidate);
              System.out.println(message);
            } catch (RemoteException e) {
              System.out.println("Retry connection to server...");
              while (true) {
                try {
                  election = (Election) registry.lookup("Server");
                  message = election.vote(new HashMD5(electorName).getElector(), candidate);
                  System.out.println();
                  System.out.println(message);
                  break;
                } catch (RemoteException e2) {
                  try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    count++;
                    if (count == 9) throw new InterruptedException();
                  } catch (InterruptedException e3) {
                    System.out.println();
                    System.out.println("Reconnection failed!");
                    count = 0;
                    break;
                  }
                } catch (ClassNotFoundException classNotFoundException) {
                  classNotFoundException.printStackTrace();
                }
              }
            } catch (IOException | ClassNotFoundException e) {
              e.printStackTrace();
            }
            break;

          case "2":
            System.out.println();
            System.out.println(electorName + ", type a candidate number to show their votes:");
            System.out.print("> ");
            candidate = inReader.nextLine();
            System.out.println();
            try {
              message = election.result(candidate);
              System.out.println(message);
            } catch (RemoteException e) {
              System.out.println("Retry connection to server...");

              while (true) {
                try {
                  election = (Election) registry.lookup("Server");
                  message = election.result(candidate);
                  System.out.println();
                  System.out.println(message);
                  break;
                } catch (RemoteException e2) {
                  try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    count++;
                    if (count == 4) throw new InterruptedException();
                  } catch (InterruptedException e3) {
                    System.out.println();
                    System.out.println("Reconnection failed!");
                    count = 0;
                    break;
                  }
                } catch (ClassNotFoundException classNotFoundException) {
                  classNotFoundException.printStackTrace();
                }
              }
            } catch (IOException | ClassNotFoundException e) {
              e.printStackTrace();
            }
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
    } catch (IOException e) {
      System.out.println("Server error: " + e.getMessage());
    } catch (NotBoundException e) {
      System.out.println("Client error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
