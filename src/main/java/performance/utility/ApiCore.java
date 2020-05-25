package performance.utility;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.json.JSONObject;

public class ApiCore {
    public void post(List<JSONObject> jsonObjects) throws InterruptedException {
      for (JSONObject jsonObject : jsonObjects){
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

