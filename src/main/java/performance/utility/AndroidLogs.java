package performance.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import performance.constants.AndroidConstants;
import performance.constants.AndroidConstants.PerformanceConfigurations;

public class AndroidLogs {

  private static AndroidLogs mInstance;

  private AndroidLogs() {
  }

  public static AndroidLogs getInstance() {
    if (mInstance == null) {
      String OS = System.getProperty("os.name").toLowerCase();
      OSValidator.setPropValues(OS);
      mInstance = new AndroidLogs();
    }
    return mInstance;
  }

  public Object[] runtimeCommand(String strCommand, int counter, boolean printToConsole,
      boolean waitFor, long... timeout) throws InterruptedException {
    assert timeout.length <= 1;
    long waitTime = timeout.length > 0 ? timeout[0] : (1 * 60 * 1000);
    String output = "";
    int exitValue = -1;
    try {
      CommandLine command = new CommandLine(OSValidator.shellType);
      if (OSValidator.shellType.trim().equalsIgnoreCase("cmd")) {
        command.addArgument("/c", false);
      } else {
        command.addArgument("-l", false);
        command.addArgument("-c", false);
      }
      command.addArgument(strCommand, false);
      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      PumpStreamHandler psh = new PumpStreamHandler(stdout);
      DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
      DefaultExecutor executor = new DefaultExecutor();
      executor.setStreamHandler(psh);
      try {
        executor.execute(command, resultHandler);
        if (waitFor) {
          resultHandler.waitFor(waitTime);
        }
        exitValue = resultHandler.getExitValue();
        if (printToConsole) {
          System.out.println(stdout);
        }
        output = stdout.toString();
      } catch (IOException | InterruptedException | IllegalStateException e1) {
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      counter--;
      Thread.sleep(1000L);
    }
    return new Object[]{output, counter, exitValue};
  }

  public List<String> getAdbLogCat(String command, String deviceId) {
    return invokeProcess(command, deviceId);
  }

  private List<String> invokeProcess(String command, String deviceId) {
    List<String> result = new ArrayList<>();
    String line;
    try {
      String adbCommand = AndroidConstants.ADB_PATH + " -s " + deviceId
          + " logcat -d ActivityManager:I *:S | grep -i " + command;
      Object[] obj = runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
      String output = (String) obj[0];
      result = Arrays.asList(output.split("\n"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  private List<String> invokeProcessFiltered(String command, String deviceId) {
    List<String> result = new ArrayList<>();
    String line;
    try {
      String adbCommand =
          AndroidConstants.ADB_PATH + " -s " + deviceId + " shell logcat -d filterspecs '*:S "
              + command + "'";
      Object[] obj = runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
      String output = (String) obj[0];
      result = Arrays.asList(output.split("\n"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  public List<String> getDump(String command, String packageName, String deviceId) {
    String line;
    List<String> result = new ArrayList<>();
    try {
      String adbCommand =
          AndroidConstants.ADB_PATH + " -s " + deviceId + " shell dumpsys " + command + " "
              + packageName;
      Object[] obj = runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
      String output = (String) obj[0];
      result = Arrays.asList(output.split("\n"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  public List<String> getDump(String command, String deviceId) {
    String line;
    List<String> result = new ArrayList<>();
    try {
      String adbCommand =
          AndroidConstants.ADB_PATH + " -s " + deviceId + " shell dumpsys " + command;
      Object[] obj = runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
      String output = (String) obj[0];
      result = Arrays.asList(output.split("\n"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  public List<String> getDumpAndGrep(String command, String packageName, String deviceId) {
    String line;
    List<String> result = new ArrayList<>();
    try {
      String adbCommand =
          AndroidConstants.ADB_PATH + " -s " + deviceId + " shell dumpsys " + command
              + " | grep -i " + packageName;
      Object[] obj = runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
      String output = (String) obj[0];
      result = Arrays.asList(output.split("\n"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  public List<String> getAdbShell(String command, String deviceId) {
    String line;
    List<String> result = new ArrayList<>();
    try {
      String adbCommand = AndroidConstants.ADB_PATH + " -s " + deviceId + " shell " + command;
      Object[] obj = runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
      String output = (String) obj[0];
      result = Arrays.asList(output.split("\n"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void getHprof(String packageName, String filename, String deviceId)
      throws InterruptedException {
    String adbCommand =
        AndroidConstants.ADB_PATH + " -s " + deviceId + " shell am dumpheap " + packageName + " "
            + PerformanceConfigurations.ANDROID_LOCAL_DUMP_LOCATION + filename;
    runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
  }

  public void pullHprof(String inputFile, String outputPath, String deviceId)
      throws InterruptedException {
    String adbCommand = AndroidConstants.ADB_PATH + " -s " + deviceId + " pull "
        + PerformanceConfigurations.ANDROID_LOCAL_DUMP_LOCATION + inputFile + " " + outputPath;
    runtimeCommand(adbCommand, 1, false, true, 60 * 1000);
  }

  public String globalAdb(String command, String deviceId) throws InterruptedException {
    String adbCommand = AndroidConstants.ADB_PATH + " -s " + deviceId + " " + command;
    System.out.println(adbCommand);
    Object[] obj = runtimeCommand(adbCommand, 1, false, true, 1800 * 1000);
    String output = (String) obj[0];
    return output;
  }

  public String getTraceFunc(String packageName, String timeout, String destination, String deviceId) throws InterruptedException {
    String traceCommand = "python2.7 " + AndroidConstants.SYSTRACE_PATH + " --serial " + deviceId + " " + AndroidConstants.SYSTRACE_TAGS
        + " -t " + timeout + " -o " + destination + " -b 16384" + " -a " + packageName;
    Object[] obj = runtimeCommand(traceCommand, 1, true, true, 60 * 1000);
    String output = (String) obj[0];
    List<String> outputList = Arrays.asList(output.split("\n"));
    String url = "";
    for (String out : outputList){
      if (out.contains("Wrote trace HTML file:")){
        url = out.split("Wrote trace HTML file: ")[1].trim();
      }
    }
    return url;
  }
}
