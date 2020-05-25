package performance.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import performance.constants.AndroidConstants;

public class logUtil {

  public List<JSONObject> getActivityLoadTime(List<String> loadTime) {
    List<JSONObject> appLoadTimeObj = new ArrayList<>();
    Map<String, Object> activityTimeMap = null;
    if (!loadTime.isEmpty()) {
      for (String line : loadTime) {
          activityTimeMap = new HashMap<>();
          String[] displayed = line.split("Displayed")[1].split(" ");
          activityTimeMap.put("activity", displayed[1].replace(":", ""));
          String tmp = displayed[2].replaceAll("[+m]", "").replace("s", ".");
          if (tmp.endsWith(".")){
            tmp = tmp.substring(0, tmp.length()-1);
          }
          if (!tmp.contains(".")){
            tmp = String.valueOf(Double.parseDouble(tmp)/1000);
          }
          activityTimeMap.put("time", tmp);
        appLoadTimeObj.add(new JSONObject(activityTimeMap));
      }
    }
    return appLoadTimeObj;
  }

}
