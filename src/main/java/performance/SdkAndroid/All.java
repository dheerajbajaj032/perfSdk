package performance.SdkAndroid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import performance.service.PerformanceFeature;

public class All implements PerformanceFeature, Runnable {

  private String packageName;
  private String deviceId;
  private volatile boolean exit = false;
  private List<JSONObject> sDeviceDetails;

  public All(String packageName, String deviceId, List<JSONObject> sDeviceDetails) {
    this.packageName = packageName;
    this.deviceId = deviceId;
    this.sDeviceDetails = sDeviceDetails;
  }

  @Override
  public Object getPerformanceMetrics(String packageName, String deviceId) {
    Map<String, Object> jsonObject = new HashMap<>();
    JSONObject optimised = new JSONObject();
    jsonObject.put("memory", new MemoryAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("storage", new StorageAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("jankyList", new JankyFramesAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("cpu", new CpuInfoAndroid().getPerformanceMetrics(packageName, deviceId));
    jsonObject.put("packageName", packageName);
    optimised = (JSONObject) new PerformanceSubMap().getOptimised("all", jsonObject);
    System.out.println(optimised);
    JSONObject memoryMeta = (JSONObject) optimised.get("memory");
    if (!memoryMeta.isEmpty()) {
      sDeviceDetails.add(optimised);
    }
    return optimised;
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
