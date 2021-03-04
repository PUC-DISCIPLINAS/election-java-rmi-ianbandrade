import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;

public class Server extends UnicastRemoteObject implements Election {
  private static TreeMap<String, String> voteList = new TreeMap<>(); // MD5, Candidate
  private static TreeMap<String, String> candidates = new TreeMap<>(); // Code, Name
  private static TreeMap<String, Integer> votes = new TreeMap<>(); // Code, number of votes

  // servant
  public Server() throws RemoteException {
    super();
    fillMaps();
  }

  private void fillMaps() {
    try {
      String CSV_FILE = "src/assets/senadores.csv";
      FileReader fileReader = new FileReader(CSV_FILE);
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      String[] aux;

      for (String line = bufferedReader.readLine();
          line != null;
          line = bufferedReader.readLine()) {
        aux = line.split(";");
        candidates.put(aux[0], aux[1]);
        votes.put(aux[0], 0);
      }

    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public String vote(String electorName, String candidate) throws RemoteException {
    if (!candidates.containsKey(candidate)) return ("The " + candidate + " isn't registered");
    else if (voteList.containsKey(electorName)) return ("You've already voted");
    else if (!voteList.containsKey(electorName) && candidates.containsKey(candidate)) {
      voteList.put(electorName, candidate);
      votes.replace(candidate, (votes.get(candidate) + 1));
      System.out.println(
          "Elector "
              + electorName
              + " voted on "
              + candidates.get(candidate)
              + " - code: "
              + candidate);
      return ("Computed vote");
    }
    System.out.println("Unknown error on method vote");
    return ("Unknown error");
  }

  @Override
  public String result(String candidate) throws RemoteException {
    if (!votes.containsKey(candidate)) return ("The " + candidate + " isn't registered");
    else if (votes.containsKey(candidate))
      return (candidates.get(candidate) + " has " + votes.get(candidate).toString() + " votes");
    System.out.println("Unknown error on method result");
    return ("Unknown error");
  }

  public static void main(String[] args) {
    // server
    try {
      Registry registry = LocateRegistry.createRegistry(1888);
      Server server = new Server();

      System.out.println("Server initialized");
      System.out.println();
      System.out.println("Logs:");

      registry.rebind("Server", server);
    } catch (Exception e) {
      System.out.println("error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
