package performance.SdkAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import performance.service.PerformanceFeature;
import performance.utility.AndroidLogs;

public class JankyFramesAndroid implements PerformanceFeature {

  private List<String> jankyKeyList = new ArrayList<>(Arrays.asList("Total frames rendered",
      "Janky frames","90th percentile","95th percentile","99th percentile","Number Missed Vsync",
      "Number High input latency","Number Slow UI thread","Number Slow bitmap uploads","Number Slow draw"));


  String jankyFramesCount(String arg, Map<String, String> jankySubDataMap) {
    List<String> jankyList = AndroidLogs.getInstance().getDump("gfxinfo", arg);
    if (jankyList.size() > 10) {
      getAppJankyData(jankyList, jankySubDataMap);
    } else {
      Thread.currentThread().getStackTrace()[1].getMethodName();
    }
    return jankySubDataMap.get("Janky frames");
  }

  private void getAppJankyData(List<String> janky, Map<String, String> jankySubDataMap) {
    for (String line : janky) {
      String[] janky_key = line.split(":");
      if (jankyKeyList != null && jankyKeyList.contains(janky_key[0].trim())) {
        jankySubDataMap.put(janky_key[0].trim(), janky_key[1].trim());
      }
    }
  }

  @Override
  public Object getPerformanceMetrics(String packageName, String deviceId) {
    Map<String, String> jankySubDataMap = new HashMap<String, String>();
    List<String> jankyList = AndroidLogs.getInstance().getDump("gfxinfo", packageName, deviceId);
    if (jankyList.size() > 10) {
      getAppJankyData(jankyList, jankySubDataMap);
    } else {
      String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    }
    return jankySubDataMap;
  }
}