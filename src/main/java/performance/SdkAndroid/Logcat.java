package performance.SdkAndroid;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import performance.constants.AndroidConstants;
import performance.utility.AndroidLogs;

public class Logcat implements Runnable {

  private String deviceId;
  private Map<String, Object> meta;
  static volatile boolean exit = false;
  private Process proc;


  public Logcat(String deviceId, Map<String, Object> meta) {

    this.deviceId = deviceId;
    this.meta = meta;
  }

  @Override
  public void run() {
      String command = "logcat > " + meta.get("logsFilePath");
      try {
        String output = AndroidLogs.getInstance().globalAdb(command, deviceId);
        System.out.println(output);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  }
}
