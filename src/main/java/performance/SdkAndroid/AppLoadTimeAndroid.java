package performance.SdkAndroid;

import java.util.ArrayList;
import java.util.List;
import performance.service.PerformanceFeature;
import performance.utility.AndroidLogs;

public class AppLoadTimeAndroid implements PerformanceFeature {

  @Override
  public Object getPerformanceMetrics(String packageName, String deviceId) {
    List<String> appLoadTime = new ArrayList<>();
    List<String> loadTime = AndroidLogs.getInstance().getAdbLogCat("Displayed", deviceId);
    if (!loadTime.isEmpty()) {
      for (String line : loadTime) {
        if (line.contains(packageName)) {
          line = line.split("Displayed")[1];
          appLoadTime.add(line);
        }
      }
    }
    return appLoadTime;
  }
}
