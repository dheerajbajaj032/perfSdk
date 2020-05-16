package performance.SdkAndroid;

import java.util.List;
import performance.service.PerformanceFeature;
import performance.utility.AndroidLogs;

class CpuInfoAndroid implements PerformanceFeature {

  @Override
  public Object getPerformanceMetrics(String packageName, String deviceId) {
    List<String> cpuResult = AndroidLogs.getInstance().getDumpAndGrep("cpuinfo", "TOTAL", deviceId);
    try {
      if (cpuResult.size() != 0) {
        for (String cpu : cpuResult) {
          if (cpu.contains("TOTAL")) {
            return cpu.split("TOTAL")[0].trim().replace("%", "");
          } else {
            return "0";
          }
        }
      } else {
        return "0";
      }
    } catch (Exception ignored) {
    }
    return "0";
  }
}
