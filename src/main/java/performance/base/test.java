package performance.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import performance.Reporting.MethodMeta;
import performance.SdkAndroid.All;
import performance.SdkAndroid.Logcat;
import performance.utility.AndroidLogs;
import performance.utility.ApiCore;
import performance.utility.DeviceDetails;
import performance.utility.logUtil;

public class test extends core {

  private ThreadLocal<All> sThread = new ThreadLocal<All>() {
    @Override protected All initialValue() {
      return null;
    }
  };
  private ThreadLocal<Logcat> sLogs = new ThreadLocal<Logcat>() {
    @Override protected Logcat initialValue() {
      return null;
    }
  };
  public Map<String, Object> deviceSpecs = new HashMap<>();

  public void init(String packageName, String deviceId){
    All all = new All(packageName, deviceId, sDeviceDetails);
    Thread thread = new Thread(all);
    thread.setName("Thread:" + packageName);
    thread.start();
    sThread.set(all);
  }

  @BeforeMethod
  public void initialize() throws Exception {
    deviceSpecs = new DeviceDetails().getDeviceHardwareDetails("emulator-5554");
    File logsDirectory = new File(System.getProperty("user.dir") + "/src/main/java/performance/report");
    if (logsDirectory.exists()){
      LocalDateTime date = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String dateString = ZonedDateTime.now().format(formatter);
      File logsFile = new File(logsDirectory + "/androidlogs_" + dateString + ".txt");
      if (!logsFile.exists()) {
        logsFile.createNewFile();
      }
        System.out.println("Logs File created: " + logsFile.getPath());
        meta.put("logsFilePath", logsFile.getPath());
        meta.put("logsDirectoryPath",logsDirectory.getPath());
        Logcat logcat = new Logcat("emulator-5554", meta);
        Thread thread = new Thread(logcat);
        thread.start();
        sLogs.set(logcat);
    }
  }

  @Test
  public void test() throws InterruptedException {
    deviceTest("emulator-5554");
  }

  @AfterMethod
  public void afterMethod() throws InterruptedException {
    List<JSONObject> jsonObjects = new ArrayList<>();
    List<String> displayedLogs = new ArrayList<>();
    ApiCore apiCore = new ApiCore(deviceSpecs);
    if (sThread.get() != null) {
      All all = sThread.get();
      all.stop();
      jsonObjects = new MethodMeta().build(sDeviceDetails);
      System.out.println("size :" + sDeviceDetails.size());
      System.out.println("Pushing Data to ES");
      // TODO: 25/05/20 disabled temperaroly 
      //new ApiCore(deviceSpecs).post(jsonObjects);
    }
      File logFile = new File((String) meta.get("logsFilePath"));
      if (logFile.exists()){
        try {
          Scanner myReader = new Scanner(logFile);
          while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if (data.contains("Displayed")) {
              displayedLogs.add(data);
            }
          }
          myReader.close();
          System.out.println(new logUtil().getActivityLoadTime(displayedLogs));
          // TODO: 25/05/20 post android load time to es 
          //apiCore.post(new logUtil().getActivityLoadTime(displayedLogs));
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      }
    }

  public void deviceTest(String deviceId) throws InterruptedException {
    List<String> filteredPackages = new ArrayList<>();
    List<String> packages = AndroidLogs.getInstance().getAdbShell("pm list packages", deviceId);
    String output = "None";
    for (String s : packages){
      if (s.contains("com.google.android.youtube") || s.contains("com.android.vending")){
        System.out.println("Package: " + s);
        init(s.split("package:")[1], deviceId);
        String monkeyCommand = "shell monkey -p " + s.split("package:")[1] + " "
            + "--ignore-crashes "
            + "--ignore-native-crashes "
            + "--ignore-timeouts "
            + "-v "
            + "--throttle 2000 25";
        System.out.println(monkeyCommand);
        output = AndroidLogs.getInstance().globalAdb(monkeyCommand, deviceId);

      filteredPackages.add(s.split("package:")[1]);}
    }
    System.out.println(filteredPackages);
  }

}
