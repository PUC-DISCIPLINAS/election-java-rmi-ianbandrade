import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.TreeMap;

public class Server extends UnicastRemoteObject implements Election {
  private static TreeMap<String, String> voteList = new TreeMap<>(); // MD5, Candidate
  private static final TreeMap<String, String> candidates = new TreeMap<>(); // Code, Name
  private static Map<String, Integer> votes = new TreeMap<>(); // Code, number of votes

  private final String VOTES_TXT = "src/assets/votes.txt";
  private final String VOTE_LIST_TXT = "src/assets/voteList.txt";

  // servant
  public Server() throws RemoteException {
    super();
    fillMaps();
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

  private void fillMaps() {
    try {
      String SENATORS_CSV = "src/assets/senators.csv";
      BufferedReader bufferedReader = new BufferedReader(new FileReader(SENATORS_CSV));
      FileOutputStream fileOutputStream = null;
      ObjectOutputStream outputStream = null;

      String[] aux;

      for (String line = bufferedReader.readLine();
          line != null;
          line = bufferedReader.readLine()) {

        aux = line.split(";");
        candidates.put(aux[0], aux[1]);
        votes.put(aux[0], 0);
      }

      File voteFile = new File(VOTES_TXT);
      if (!voteFile.exists()) {
        voteFile.createNewFile();
      } else if (voteFile.length() == 0) {
        outputStream = new ObjectOutputStream(new FileOutputStream(voteFile));
        outputStream.writeObject(votes);
      } else {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(voteFile));
        votes = (Map<String, Integer>) objectInputStream.readObject();
      }

      File voteListFile = new File(VOTE_LIST_TXT);
      if (!voteListFile.exists()) {
        voteListFile.createNewFile();
      } else if (voteListFile.length() != 0) {
        ObjectInputStream objectInputStream =
            new ObjectInputStream(new FileInputStream(voteListFile));
        voteList = (TreeMap<String, String>) objectInputStream.readObject();
      }

    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public String vote(String electorName, String candidate)
      throws IOException, ClassNotFoundException {
    if (!candidates.containsKey(candidate)) {
      return ("The " + candidate + " isn't registered");
    } else if (voteList.containsKey(electorName)) {
      return ("You've already voted");
    } else if (!voteList.containsKey(electorName) && candidates.containsKey(candidate)) {
      voteList.put(electorName, candidate);
      addOnFIle(VOTE_LIST_TXT);
      votes.replace(candidate, (votes.get(candidate) + 1));
      addOnFIle(VOTES_TXT);

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
  public String result(String candidate) throws IOException, ClassNotFoundException {

    if (!votes.containsKey(candidate)) return ("The " + candidate + " isn't registered");
    else if (votes.containsKey(candidate)) {
      return (candidates.get(candidate) + " has " + votes.get(candidate).toString() + " votes");
    }
    System.out.println("Unknown error on method result");
    return ("Unknown error");
  }

  private void addOnFIle(String filePath) throws IOException, ClassNotFoundException {
    File file = new File(filePath);

    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
    if (filePath.equalsIgnoreCase(VOTES_TXT)) {
      outputStream.writeObject(votes);
      refreshList(VOTES_TXT);
    } else {
      outputStream.writeObject(voteList);
      refreshList(VOTE_LIST_TXT);
    }
  }

  private void refreshList(String filePath) throws IOException, ClassNotFoundException {
    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));

    if (filePath.equalsIgnoreCase(VOTES_TXT))
      votes = (Map<String, Integer>) objectInputStream.readObject();
    else voteList = (TreeMap<String, String>) objectInputStream.readObject();
  }
}
