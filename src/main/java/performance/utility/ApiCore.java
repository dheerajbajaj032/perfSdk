package performance.utility;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import performance.base.test;

public class ApiCore {

  private Map<String, Object> deviceSpecs;

  public ApiCore(Map<String, Object> deviceSpecs) {
    this.deviceSpecs = deviceSpecs;
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
    RestAssured.baseURI = "http://localhost:8083";
    return  given().when().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
        body(jsonObject.toString()).post("/api/v1/results/deviceMeta")
        .then().extract().response().getBody().prettyPrint();
    }
}

