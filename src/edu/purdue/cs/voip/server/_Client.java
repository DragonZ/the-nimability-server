import java.net.InetAddress;
import java.sql.Time;

public class Client {

  static final int AVALIABLE = 0;
  static final int ONCALL = 1;
  static final int STANDBY = 2;
  
  long lastQueryTime= System.currentTimeMillis();
  int status = AVALIABLE;
  String name;
  InetAddress ip;
  int port;
  
  public Client () {
	  
  } 
  
  public int getInterval()
  {
	  return (int) ((System.currentTimeMillis()- lastQueryTime)/1000);
  }
  
  
}
