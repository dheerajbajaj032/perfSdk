package performance.base;

import java.util.HashMap;
import java.util.Map;

public class core {

  public ThreadLocal<Map<String, Object>> sTestDetails = new ThreadLocal<Map<String, Object>>(){
    @Override protected Map<String, Object> initialValue() {
      return null;
    }
  };

  public Map<String, Object> meta = new HashMap<>();


}
