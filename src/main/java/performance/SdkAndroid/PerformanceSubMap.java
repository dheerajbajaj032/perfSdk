package performance.SdkAndroid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class PerformanceSubMap {

  Object getOptimised(String matrixName, Object object) {

    switch (matrixName) {

      case "loadTime":
        return getloadTimeOptimised(object);
      case "memory":
        return getMemoryOptimised(object);
      case "jankyList":
        return getJankyOptimised(object);
      case "cpu":
        return getCpuOptimised(object);
      case "storage":
        return getStorageOptimised(object);
      case "network":
        return getNetworkOptimised(object);
      case "all":
        return getAllOptimised(object);
      case "memoryLeak":
        return getMemoryLeakOptimised(object);
      case "resetstats":
        return getResetStats(object);
      case "firebase":
        return getFirebaseOptimised(object);
      default:
        return "None";
    }
  }

  private Object getFirebaseOptimised(Object object) { return object; }

  private Object getResetStats(Object object) {
    return object;
  }

  private Object getMemoryLeakOptimised(Object object) {
    return object;
  }

  private Object getAllOptimised(Object object) {
    return new JSONObject(invoke(object));
  }

  private Object getNetworkOptimised(Object object) {
    return object;
  }

  private int converterNetwork(String wifi_data_received) {
    int res = 0;
    if (wifi_data_received.contains("MB")) {
      res = Math.round(Float.parseFloat(wifi_data_received.split("MB")[0]));
    }
    return res;
  }

  private Object getStorageOptimised(Object object) {
    int res = 0;
    String storage;
    try {
      Map<String, String> mMap = (Map<String, String>) object;
      if (mMap.get("%used").contains("%")) {
        storage = mMap.get("%used");
        res = Integer.parseInt(storage.replace("%", ""));
      }
    } catch (Exception e) {
      res = 0;
    }
    return res;
  }

  private Object getJankyOptimised(Object object) {
    int res = 0;
    try {
      Map<String, String> mMap = (Map<String, String>) object;
      String janky;
      if (mMap.get("Janky frames").contains(" ")) {
        janky = mMap.get("Janky frames").split(" ")[0];
        res = Integer.parseInt(janky.trim());
      }
    } catch (Exception e) {
      res = 0;
    }
    return res;
  }

  private Object getCpuOptimised(Object object) {
    int res;
    try {
      String mStr = (String) object;
      res = Integer.parseInt(mStr.trim());
    } catch (Exception e) {
      res = 0;
    }
    return res;
  }

  private Object getMemoryOptimised(Object object) {
    Map<String, String> mMap = new HashMap<>();
    Map<String, Integer> memoryMap = new HashMap<>();
      mMap = (Map<String, String>) object;
      for (String m : mMap.keySet()){
        memoryMap.put(m,Integer.parseInt(mMap.get(m)));
      }
    return memoryMap;
  }

//  private Object getloadTimeOptimised(Object object){
//    return object;
//  }

  private List<Object> getloadTimeOptimised(Object object) {
    List<Object> metaList = new ArrayList<>();
    try {
      List<String> objectList = (List<String>) object;
      List<Object> mList = new ArrayList<>();
      for (String m : objectList) {
        String[] s = m.split(":");
        if (!mList.contains(s[0].trim())){
          Map<String, Object> mMap = new HashMap<>();
          mMap.put("activity", s[0].trim());
          mMap.put("time", converter(s[1].trim()));
          metaList.add(mMap);
          mList.add(s[0].trim());
        }
      }
    } catch (Exception ignored) {}
    return metaList;
  }

  private float converter(String s) {
    String tmp = s.replaceAll("[^0-9]+", "");
    return (float) (Double.parseDouble(tmp) / 1000);
  }

  private Map<String, Object> invoke(Object object) {
    Map<String, Object> mMap = (Map<String, Object>) object;
    Map<String, Object> m = new HashMap<>();
    for (String metrics : mMap.keySet()) {
      if (metrics.equalsIgnoreCase("loadTime")) {
        m.put(metrics, getloadTimeOptimised(mMap.get(metrics)));
      } else if (metrics.equalsIgnoreCase("memory")) {
        m.put(metrics, getMemoryOptimised(mMap.get(metrics)));
      } else if (metrics.equalsIgnoreCase("jankyList")) {
        m.put(metrics, getJankyOptimised(mMap.get(metrics)));
      } else if (metrics.equalsIgnoreCase("storage")) {
        m.put(metrics, getStorageOptimised(mMap.get(metrics)));
      } else if (metrics.equalsIgnoreCase("cpu")) {
        m.put(metrics, getCpuOptimised(mMap.get(metrics)));
      } else {
        m.put(metrics, mMap.get(metrics));
      }
    }
    return m;
  }

}
