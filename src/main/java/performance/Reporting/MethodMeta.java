package performance.Reporting;

import java.util.Map;
import performance.base.core;

public class MethodMeta extends core {

  private Map<String, Object> jsonObject;

  public MethodMeta(Map<String, Object> jsonObject){
    this.jsonObject = jsonObject;
  }

  public void build(){
    if (!meta.containsValue(jsonObject.get("packageName"))){
      meta.put("packageName", jsonObject.get("packageName"));
    }

  }
}
