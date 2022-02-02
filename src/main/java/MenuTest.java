import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Builder
@Getter
@ToString
public class MenuTest {

    private Long id;
    private String title;

    public static void main(String[] args) {
        MenuTest article = MenuTest.builder()
                .id(1L)
                .title("Test Article")
                .build();

        Gson gson = new Gson();
        String gsonJson = gson.toJson(article);
        System.out.println(gsonJson);

        String json = "{\"id\":2,\"title\":\"Other Test Article\"}";
        MenuTest gsonArticle = gson.fromJson(json, MenuTest.class);
        System.out.println(gsonArticle);


        int firstNum, secondNum, result;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter a first number:");
            firstNum = Integer.parseInt(br.readLine());
            System.out.println("Enter a second number:");
            secondNum = Integer.parseInt(br.readLine());
            result = firstNum * secondNum;
            System.out.println("The Result is: " + result);

            System.out.println("Press key:");
            System.out.println("You pressed: " + br.read());

        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        // get key using native hook
        System.out.println("et key using native hook:");
    }
}