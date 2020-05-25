package performance.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class reader {

  List<String> keysList = new ArrayList<>();

  public reader() throws Exception {
    loadConfig();
  }

  public String getConfigValue(String arg){
    for (String key : keysList){
      if (key.contains(arg.toLowerCase())){
        return key.split("=")[1];
      }
    }
    return "";
  }

  private void loadConfig() throws Exception {
    getConfigList();
  }

  public void getConfigList() throws Exception {
    File file = new File(
        getClass().getClassLoader().getResource("config.properties").getFile()
    );
    try (FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader)) {
      String line;
      while ((line = br.readLine()) != null) {
        keysList.add(line.trim());
      }
    }
  }
}
