package performance.SdkAndroid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import performance.service.PerformanceFeature;
import performance.utility.AndroidLogs;

class MemoryAndroid implements PerformanceFeature {

  private void getAppMemoryData(List<String> memory, Map<String, String> memorySubDataMap) {
    List<String> memory_sub_data = memory
        .subList(memory.indexOf(" App Summary") + 3, memory.indexOf(" Objects"));
    for (String line : memory_sub_data) {
      String[] key_value = line.trim().split(":");
      if (!Objects.equals(key_value[0], "") && !Objects.equals(key_value[1], "")) {
        if (key_value[0].trim().contains("TOTAL")) {
          key_value[1] = key_value[1].split("TOTAL")[0];
          memorySubDataMap.put(key_value[0].trim(), key_value[1].trim());
        } else {
          memorySubDataMap.put(key_value[0].trim(), key_value[1].trim());
        }
      }
    }
  }

  @Override
  public Object getPerformanceMetrics(String packageName, String deviceId) {
    Map<String, String> memorySubDataMap = new HashMap<>();
    List<String> memory = AndroidLogs.getInstance().getDump("meminfo", packageName, deviceId);
    if (memory.contains(" App Summary")) {
      getAppMemoryData(memory, memorySubDataMap);
    } else {
      Thread.currentThread().getStackTrace()[1].getMethodName();
    }
    return memorySubDataMap;
  }
}
