package sg.edu.np.mad.mad24p03team2.Abstract_Interfaces;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodDB;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

/**
 * APIHandler
 * When user search for food that does not exist in the backend table,
 * call api to generate similar food and auto create food in backend TABLE.
 * AUTO-expansion of database options.
 */
public class ApiHandler {
    private static final String API_URL = "https://api.api-ninjas.com/v1/nutrition";
    private static final String API_KEY = "be9GHx5wik7ZfCDV7PrdIA==CycI438gUmQQw08e"; // Replace with your actual API key

    private OkHttpClient client;
    private Gson gson;

    public ApiHandler() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void getNutritionInfo(String query, final NutritionCallback callback) {
        String url = API_URL + "?query=" + query;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("APIHandler","Reponse is Successful!");
                    String jsonResponse = response.body().string();
                    Log.d("APIHandler","jsonResponse = "+jsonResponse);
                    Type listType = new TypeToken<List<FoodItemClass>>() {}.getType();
                    List<FoodItemClass> nutritionList = gson.fromJson(jsonResponse, listType);
                    try {
                        callback.onSuccess(nutritionList);
                    }
                    catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public void fetchNutritionInfo(String query, FoodDB foodDB) {
        getNutritionInfo(query, new ApiHandler.NutritionCallback() {
            @Override
            public void onSuccess(List<FoodItemClass> nutritionList) throws SQLException {
                for (FoodItemClass info : nutritionList) {
                    Log.d("NutritionInfo", "Name: " + info.getName() + ", Calories: " + info.getCalories());
                    foodDB.CreateRecord(info);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ApiClient", "Error: ", e);
            }
        });
    }

    public interface NutritionCallback {
        void onSuccess(List<FoodItemClass> nutritionList) throws SQLException;
        void onFailure(Exception e);
    }
}
