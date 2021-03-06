import java.io.IOException;
import java.rmi.Remote;

public interface Election extends Remote {

  String vote(String electorName, String candidate) throws IOException, ClassNotFoundException;

  String result(String candidate) throws IOException, ClassNotFoundException;
}
