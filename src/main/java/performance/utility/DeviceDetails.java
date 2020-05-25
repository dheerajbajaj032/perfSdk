package performance.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceDetails {

  public Map<String, Object> getDeviceHardwareDetails(String deviceId) throws Exception {

    List<String> key_value = Arrays.asList(new reader().getConfigValue("deviceDetails").split(","));
    Map<String,Object> detailsMap = new HashMap<>();
    Map<String,Object> getPropMap = new HashMap<>();
    List<String> storageResult = AndroidLogs.getInstance().getAdbShell("getprop", deviceId);
    for (String key : storageResult){
      getPropMap.put(
          key.split(": ")[0].replaceAll("\\[", "").replaceAll("\\]",""),
          key.split(": ")[1].replaceAll("\\[", "").replaceAll("\\]",""));
    }
    for (String key : key_value){
      if (getPropMap.containsKey(key)){
        detailsMap.put(key,getPropMap.get(key));
      }
    }
    return detailsMap;
  }

}
