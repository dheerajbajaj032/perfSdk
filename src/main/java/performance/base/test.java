package performance.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import performance.Reporting.MethodMeta;
import performance.SdkAndroid.All;
import performance.utility.AndroidLogs;
import performance.utility.ApiCore;
import performance.utility.DeviceDetails;

public class test extends core {

  private ThreadLocal<All> sThread = new ThreadLocal<All>() {
    @Override protected All initialValue() {
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

  }

  @Test
  public void test() throws InterruptedException {
    deviceTest("emulator-5554");
  }

  @AfterMethod
  public void afterMethod() throws InterruptedException {
    List<JSONObject> jsonObjects = new ArrayList<>();
    if (sThread.get() != null) {
      All all = sThread.get();
      all.stop();
      jsonObjects = new MethodMeta().build(sDeviceDetails);
      System.out.println("size :" + sDeviceDetails.size());
      System.out.println("Pushing Data to ES");
      new ApiCore(deviceSpecs).post(jsonObjects);
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
