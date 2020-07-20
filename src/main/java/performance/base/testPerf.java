package performance.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import performance.Reporting.MethodMeta;
import performance.SdkAndroid.All;
import performance.SdkAndroid.Logcat;
import performance.base.core;
import performance.utility.AndroidLogs;
import performance.utility.ApiCore;
import performance.utility.DeviceDetails;
import performance.utility.Reader;
import performance.utility.logUtil;

public class testPerf extends core {

  private ThreadLocal<All> sThread = new ThreadLocal<All>() {
    @Override
    protected All initialValue() {
      return null;
    }
  };
  private ThreadLocal<Logcat> sLogs = new ThreadLocal<Logcat>() {
    @Override
    protected Logcat initialValue() {
      return null;
    }
  };
  public Map<String, Object> deviceSpecs = new HashMap<>();
  Map<String,Integer> memInfoStartMap = new HashMap<>();
  public String deviceId = "";
  public String throttle = "";
  public String event = "";
  public String pNames = "";


  public void init(String packageName, String deviceId) {
    All all = new All(packageName, deviceId, sDeviceDetails);
    Thread thread = new Thread(all);
    thread.setName("Thread:" + packageName);
    thread.start();
    sThread.set(all);
  }

  @BeforeSuite
  public void config() throws Exception {
    deviceId = System.getProperty("device");
    throttle = System.getProperty("throttle");
    event = System.getProperty("event");
    Reader reader = new Reader();
    pNames = reader.getConfigValue("packagelist");
    if (throttle == null || throttle.isEmpty()) {
      throttle = reader.getConfigValue("throttletime");
    }
    if (event == null || event.isEmpty()) {
      event = reader.getConfigValue("eventcount");
    }
  }

  @BeforeMethod
  public void initialize() throws Exception {

    deviceSpecs = new DeviceDetails().getDeviceHardwareDetails(deviceId);
    File logsDirectory = new File(
        System.getProperty("user.dir") + "/src/main/java/performance/report");
      LocalDateTime date = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String dateString = ZonedDateTime.now().format(formatter);
      File logsFile = new File(logsDirectory + "/androidlogs_" + dateString + ".txt");
      if (!logsFile.exists()) {
        logsFile.createNewFile();
      }
      System.out.println("Logs File created: " + logsFile.getPath());
      meta.put("logsFilePath", logsFile.getPath());
      meta.put("logsDirectoryPath", logsDirectory.getPath());
      String command = "shell cat /proc/meminfo";
      String memInfoStart  = AndroidLogs.getInstance().globalAdb(command, deviceId);
      List<String> memInfoStartList = Arrays.asList(memInfoStart.split("\n"));
      memInfoStartMap = new HashMap<>();
      for (String stat : memInfoStartList){
        memInfoStartMap.put(stat.split(":")[0],
            Integer.parseInt(stat.split(":")[1].trim().replaceAll("kB","").trim()));
      }
      Logcat logcat = new Logcat(deviceId, meta);
      Thread thread = new Thread(logcat);
      thread.start();
      sLogs.set(logcat);
    }

  @Test
  public void test() throws Exception {
    deviceTest(deviceId);
  }

  @AfterMethod
  public void afterMethod() throws Exception {
    List<JSONObject> jsonObjects = new ArrayList<>();
    List<String> displayedLogs = new ArrayList<>();
    List<String> errorLogs = new ArrayList<>();
    ApiCore apiCore = new ApiCore(deviceSpecs);
    if (sThread.get() != null) {
      All all = sThread.get();
      all.stop();
      jsonObjects = new MethodMeta().build(sDeviceDetails);
      System.out.println("size :" + sDeviceDetails.size());
      System.out.println("Pushing Data to ES");
      // TODO: 25/05/20 disabled temperaroly 
      new ApiCore(deviceSpecs).post(jsonObjects);
    }
    String command = "shell cat /proc/meminfo";
    String memInfoEnd  = AndroidLogs.getInstance().globalAdb(command, deviceId);
    List<String> memInfoStartList = Arrays.asList(memInfoEnd.split("\n"));
    Map<String,Integer> memInfoEndMap = new HashMap<>();
    for (String stat : memInfoStartList){
      memInfoEndMap.put(stat.split(":")[0],
          Integer.parseInt(stat.split(":")[1].trim().replaceAll("kB","").trim()));
    }
    Map<String,Integer> memInfoDiffMap = new HashMap<>();
    JSONObject memInfoDiffObj = new JSONObject();
    for (String stat : memInfoStartMap.keySet()){
        memInfoDiffMap.put(stat, memInfoEndMap.get(stat) - memInfoStartMap.get(stat));
    }
    memInfoDiffObj.put("memoryDiff", memInfoDiffMap);
    System.out.println(memInfoDiffMap);
    File logFile = new File((String) meta.get("logsFilePath"));
    if (logFile.exists()) {
      try {
        Scanner myReader = new Scanner(logFile);
        while (myReader.hasNextLine()) {
          String data = myReader.nextLine();
          if (data.contains("Displayed")) {
            displayedLogs.add(data);
          }
          if (data.contains("exception")) {
            errorLogs.add(data);
          }
        }
        myReader.close();
        // TODO: 25/05/20 post android load time to es
        logUtil logUtil = new logUtil();
        apiCore.postLoadTime(logUtil.getActivityLoadTime(displayedLogs));
        apiCore.postErrorLogs(logUtil.getErrorLogs(errorLogs));
        apiCore.postMemoryDiff(memInfoDiffObj);
      } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }
  }

  public void deviceTest(String deviceId) throws Exception {
    List<String> filteredPackages = new ArrayList<>();
    List<String> packages = AndroidLogs.getInstance().getAdbShell("pm list packages", deviceId);
    String output = "None";
    if (!pNames.isEmpty()) {
      List<String> packagesToTest = Arrays.asList(pNames.split(","));
      if (!packagesToTest.isEmpty()) { packages = packagesToTest; }
    }
    for (String s : packages) {
      System.out.println("Package: " + s);
      if (s.contains("package:")){
         s = s.replaceAll("package:", "");
      }
      init(s.trim(), deviceId);
      String monkeyCommand = "shell monkey -p " + s.trim() + " "
          + "--ignore-crashes "
          + "--ignore-native-crashes "
          + "--ignore-timeouts "
          + "-v "
          + "--throttle "
          + throttle
          + " " + event;
      System.out.println(monkeyCommand);
      output = AndroidLogs.getInstance().globalAdb(monkeyCommand, deviceId);
      filteredPackages.add(s);
    }
    System.out.println(filteredPackages);
  }

}
