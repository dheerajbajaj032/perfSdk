package performance.Reporting;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.json.JSONObject;

public class MethodMeta {

  public List<JSONObject> build(List<JSONObject> sDeviceDetails) {
    MultiMap map = new MultiValueMap();
    List<MultiMap> res = new ArrayList<>();
    List<Integer> metricList = new ArrayList<>();
    List<JSONObject> copy = new ArrayList<>(sDeviceDetails);

    for (JSONObject obj : copy) {
        String pName = obj.get("packageName").toString();
        obj.remove("packageName");
        map.put(pName,obj);
    }
    return average(map);
  }

  private List<JSONObject> average(MultiMap map) {
    List<JSONObject> finalList = new ArrayList<>();
    JSONObject eachPackage = null;
    for (Object name : map.keySet()){
      eachPackage = new JSONObject();
      JSONObject jsonObject = new JSONObject();
      int total = 1;
      total = ((List<JSONObject>) map.get(name)).size();
      int singleArrayLength = ((List<JSONObject>) map.get(name)).get(0).length();
      int cpu = 0;
      int jankyList = 0;
      int storage = 0;
      int graphics = 0;
      int totalHeap = 0;
      int privateOthers = 0;
      int nativeHeap = 0;
      int code = 0;
      int java = 0;
      int system = 0;
      int stack = 0;

      for (int i=0; i <total ; i ++){

        int mcpu = (int) ((List<JSONObject>) map.get(name)).get(i).get("cpu");
        int mjankyList = (int) ((List<JSONObject>) map.get(name)).get(i).get("jankyList");
        int mstorage = (int) ((List<JSONObject>) map.get(name)).get(i).get("storage");
        int mgraphics = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("Graphics");
        int mtotalHeap = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("TOTAL");
        int mprivateOthers = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("Private Other");
        int mnativeHeap = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("Native Heap");
        int mcode = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("Code");
        int mjava = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("Java Heap");
        int msystem = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("System");
        int mstack = (int) ((JSONObject) ((List<JSONObject>) map.get(name)).get(i).get("memory")).get("Stack");

        cpu = cpu + mcpu;
        jankyList = jankyList + mjankyList;
        storage = storage + mstorage;
        graphics = graphics + mgraphics;
        totalHeap = totalHeap + mtotalHeap;
        privateOthers = privateOthers + mprivateOthers;
        nativeHeap = nativeHeap + mnativeHeap;
        code = code + mcode;
        java = java + mjava;
        system = system + msystem;
        stack = stack + mstack;
      }
      cpu = cpu / total;
      jankyList = jankyList / total;
      storage = storage / total;
      graphics = graphics / total;
      totalHeap = totalHeap / total;
      privateOthers = privateOthers / total;
      nativeHeap = nativeHeap / total;
      code = code / total;
      java = java / total;
      system = system / total;
      stack = stack / total;

      jsonObject.put("cpu", cpu);
      jsonObject.put("jankyList", jankyList);
      jsonObject.put("storage", storage);
      jsonObject.put("graphics", graphics);
      jsonObject.put("totalHeap", totalHeap);
      jsonObject.put("privateOthers", privateOthers);
      jsonObject.put("nativeHeap", nativeHeap);
      jsonObject.put("code", code);
      jsonObject.put("java", java);
      jsonObject.put("system", system);
      jsonObject.put("stack", stack);
      eachPackage.put("packageName", name.toString());
      eachPackage.put("metrics", jsonObject);
      finalList.add(eachPackage);
    }
    return finalList;
  }
}
