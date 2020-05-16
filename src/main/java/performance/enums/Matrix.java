package performance.enums;

public enum Matrix {
  LOADTIME("loadTime"),
  MEMORY("memory"),
  JANKYLIST("jankyList"),
  JANKYCOUNT("jankyCount"),
  CPU("cpu"),
  DEVICESTORAGE("storage"),
  ALL("all"),
  MEMORYLEAK("memoryLeak"),
  FIREBASE("firebase"),
  NETWORK("network"),
  RESETSTATS("resetstats"),
  APKANALYSER("apkanalyser");

  private String name;

  Matrix(String s) {
    this.name = s;
  }

  public String getName() {
    return this.name;
  }

}

