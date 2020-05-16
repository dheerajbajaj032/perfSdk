package performance.constants;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public interface AndroidConstants {

  String HOME_DIRECTORY = System.getProperty("user.home");

  String ADB_PATH =
      HOME_DIRECTORY + File.separator + "Library" + File.separator + "Android"
          + File.separator + "sdk" + File.separator + "platform-tools" + File.separator + "adb";

  String SYSTRACE_PATH =  HOME_DIRECTORY + File.separator + "Library" + File.separator + "Android"
      + File.separator + "sdk" + File.separator + "platform-tools" + File.separator + "systrace" +
      File.separator + "systrace.py";

  String SYSTRACE_TAGS = "freq am wm gfx view Dalvik input binder_driver";

  static String getSizeCommand(String filePath){
  String APK_SIZE = "ls -lhS " + filePath + " | awk '{print $5}'";
  return APK_SIZE;}

  String DISK_STATS = "shell dumpsys diskstats";

  interface PerformanceConfigurations {

    String HPROF_FILE = "leak.hprof";
    String ANDROID_LOCAL_DUMP_LOCATION = "/data/local/tmp/";
    String HPROF_BINARY_FILE = "leak_binary.hprof";
    String DUMP_REMOVE_COMMAND = "rm /data/local/tmp/*";
    String ERROR = "ERROR";
    List<String> HTML_KEY = Arrays.asList("class", "loader", "bytes");
    String MAT_EXTENSION = "org.eclipse.mat.api:suspects";
    Path MEMORYLEAK_DIR_PATH = Paths.get("../android-xtream").toAbsolutePath();
    String LEAK_RESOURCES_PATH = MEMORYLEAK_DIR_PATH + "/LeakResources";
    String LEAK_META = "meta";
    String FILE_PARSING_TAG = "b";
    String FILE_PARSING_CLASS = "important";
    String FILE_META_TAG = "li";
    String STATS_RESET_OUTPUT = "Battery stats reset.";
    String STATS_RESET_COMMAND = "batterystats --reset";
    String STATS_CHARGED_COMMAND = "batterystats --charged";
    String STATS_FILTER = "  CONNECTIVITY POWER SUMMARY ";
    String PROJECT_NAME = "airtel-wynkstudio-gcm";
    String CRED_PATH = HOME_DIRECTORY + File.separator + "Documents" + File.separator
        + "FirebaseCredentials" + File.separator
        + "AirtelWynkStudio-0372295c9c92.json";

    static String statsFilter(String command) {
      return STATS_FILTER + command;
    }

    String STATS_DURATION_COMMAND = "Logging duration for connectivity statistics";
    String STATS_CELLULAR_COMMAND = "Cellular";
    String STATS_WIFI_COMMAND = "Wifi";
    String STATS_WIFI_PACKETS_RECEIVED = "Wifi packets received";
    String STATS_CELLULAR_PACKETS_RECEIVED = "Cellular packets received";
  }
}
