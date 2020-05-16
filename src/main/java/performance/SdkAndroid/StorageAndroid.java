package performance.SdkAndroid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import performance.service.PerformanceFeature;
import performance.utility.AndroidLogs;

public class StorageAndroid implements PerformanceFeature {

  @Override
  public Object getPerformanceMetrics(String packageName, String deviceId) {
    List<String> storageList;
    Map<String, String> result = new HashMap<>();
    List<String> storageResult = AndroidLogs.getInstance().getAdbShell("df -h", deviceId);
    for (String line : storageResult) {
      if (line.contains("storage/emulated")) {
        storageList = Arrays.asList(line.split("\\s+"));
        result.put("used", storageList.get(2));
        result.put("avail", storageList.get(3));
        result.put("%used", storageList.get(4));
      }
    }
    return result;
  }
}
