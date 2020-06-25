package performance.utility;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class ApiCore {

  private Map<String, Object> deviceSpecs;
  private String baseURL = "";

  public ApiCore(Map<String, Object> deviceSpecs) throws Exception {
    this.deviceSpecs = deviceSpecs;
    baseURL = new Reader().getConfigValue("restapi");
  }

  public void post(List<JSONObject> jsonObjects) throws InterruptedException {
      for (JSONObject jsonObject : jsonObjects){
        jsonObject.put("device", new JSONObject(deviceSpecs));
        System.out.println(pushData(jsonObject));
        Thread.sleep(2000);
      }
    }

  private String pushData(JSONObject jsonObject) {
    System.out.println(jsonObject);
    RestAssured.baseURI = baseURL;
    return  given().when().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
        body(jsonObject.toString()).post("/api/v1/results/deviceMeta")
        .then().extract().response().getBody().prettyPrint();
    }

  public void postLoadTime(JSONObject jsonObjects) throws InterruptedException {
      System.out.println(pushLoadTime(jsonObjects));
  }

  private String pushLoadTime(JSONObject jsonObjects) {
    System.out.println(jsonObjects);
    RestAssured.baseURI = baseURL;
    return  given().when().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
        body(jsonObjects.toString()).post("/api/v1/loadtime")
        .then().extract().response().getBody().prettyPrint();
    }

  public void postErrorLogs(JSONObject jsonObjects) throws InterruptedException {
    System.out.println(pushErrorLogs(jsonObjects));
  }

  private String pushErrorLogs(JSONObject jsonObject) {
    System.out.println(jsonObject);
    RestAssured.baseURI = baseURL;
    return  given().when().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
        body(jsonObject.toString()).post("/api/v1/error")
        .then().extract().response().getBody().prettyPrint();
  }

  public void postMemoryDiff(JSONObject jsonObjects) throws InterruptedException {
    System.out.println(pushMemoryDiff(jsonObjects));
  }

  private String pushMemoryDiff(JSONObject jsonObject) {
    System.out.println(jsonObject);
    RestAssured.baseURI = baseURL;
    return  given().when().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
        body(jsonObject.toString()).post("/api/v1/memory/diff")
        .then().extract().response().getBody().prettyPrint();
  }
  }

