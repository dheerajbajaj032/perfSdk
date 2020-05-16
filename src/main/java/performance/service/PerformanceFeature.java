package performance.service;
import performance.utility.AndroidLogs;

public interface PerformanceFeature {

  Object getPerformanceMetrics(String packageName, String deviceId);

  default Object getApkAnalyser(String packageName, String path, String deviceId) throws InterruptedException {
    return path;
  }
}
