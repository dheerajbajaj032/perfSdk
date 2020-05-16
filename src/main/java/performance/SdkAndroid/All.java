package performance.SdkAndroid;

import java.util.HashMap;
import java.util.Map;
import performance.service.PerformanceFeature;

public class All implements PerformanceFeature, Runnable {

  private String packageName;
  private String deviceId;
  private volatile boolean exit = false;

  public All(String packageName, String deviceId) {
    this.packageName = packageName;
    this.deviceId = deviceId;
  }

  @Override
  public Object getPerformanceMetrics(String packageName, String deviceId) {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("memory", new MemoryAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("storage", new StorageAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("jankyList", new JankyFramesAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("cpu", new CpuInfoAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("package", packageName);
    System.out.println(jsonObject);
    return jsonObject;
  }

  @Override
  public void run() {
    while (!exit) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      try {
        getPerformanceMetrics(packageName, deviceId);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  public void stop() { exit = true; }
}
