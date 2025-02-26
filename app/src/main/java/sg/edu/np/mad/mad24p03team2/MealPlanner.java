package sg.edu.np.mad.mad24p03team2;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodDB;

// Ensure this import
import sg.edu.np.mad.mad24p03team2.BuildConfig;

public class MealPlanner extends Fragment {

    private Button generateButton;
    private CheckBox checkBoxDairy, checkBoxGluten, checkBoxNuts, checkBoxSoy, checkBoxSugar, checkBoxEgg, checkBoxVegan, checkBoxVegetarian, checkBoxSeafood;
    private TextView breakfastTextView, lunchTextView, dinnerTextView;
    private FoodDB foodDB;
    private String stringURLEndPoint = "https://api.openai.com/v1/chat/completions"; // Replace with your API endpoint
    private String apiKey = BuildConfig.API_KEY; // Use BuildConfig to access the API key

    // Variables to hold the current meal plan
    private String currentBreakfast = "";
    private String currentLunch = "";
    private String currentDinner = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_planner, container, false);

        generateButton = view.findViewById(R.id.button3);
        checkBoxDairy = view.findViewById(R.id.checkBoxDairy);
        checkBoxGluten = view.findViewById(R.id.checkBoxGluten);
        checkBoxNuts = view.findViewById(R.id.checkBoxNuts);
        checkBoxSoy = view.findViewById(R.id.checkBoxSoy);
        checkBoxSugar = view.findViewById(R.id.checkBoxSugar);
        checkBoxEgg = view.findViewById(R.id.checkBoxEgg);
        checkBoxVegan = view.findViewById(R.id.checkBoxVegan);
        checkBoxVegetarian = view.findViewById(R.id.checkBoxVegeterian);
        checkBoxSeafood = view.findViewById(R.id.checkBoxSeafood);
        breakfastTextView = view.findViewById(R.id.textViewBreakfastContent);
        lunchTextView = view.findViewById(R.id.textViewLunchContent);
        dinnerTextView = view.findViewById(R.id.textViewDinnerContent);

        foodDB = new FoodDB(getContext());
        generateButton.setOnClickListener(v -> generateMealPlan());

        return view;
    }

    private void generateMealPlan() {
        int calorieLimit = 1200;
        List<String> availableFoods = getAllFoodsFromDB();
        String dietaryPreferences = getDietaryPreferences();
        String prompt = createPrompt(availableFoods, calorieLimit, dietaryPreferences);
        callAPI(prompt);
    }

    private List<String> getAllFoodsFromDB() {
        List<String> foods = new ArrayList<>();
        try {
            ResultSet resultSet = foodDB.GetAllRecord();
            while (resultSet != null && resultSet.next()) {
                foods.add(resultSet.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    private String getDietaryPreferences() {
        StringBuilder preferences = new StringBuilder();
        if (checkBoxDairy.isChecked()) preferences.append("dairy-free, ");
        if (checkBoxGluten.isChecked()) preferences.append("gluten-free, ");
        if (checkBoxNuts.isChecked()) preferences.append("nut-free, ");
        if (checkBoxSoy.isChecked()) preferences.append("soy-free, ");
        if (checkBoxSugar.isChecked()) preferences.append("sugar-free, ");
        if (checkBoxEgg.isChecked()) preferences.append("eggless, ");
        if (checkBoxVegan.isChecked()) preferences.append("vegan, ");
        if (checkBoxVegetarian.isChecked()) preferences.append("vegetarian, ");
        if (checkBoxSeafood.isChecked()) preferences.append("no seafood, ");

        if (preferences.length() > 0) {
            preferences.setLength(preferences.length() - 2); // Remove the last comma and space
        }

        return preferences.toString();
    }

    private String createPrompt(List<String> foods, int calorieLimit, String dietaryPreferences) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a meal plan for a day including breakfast, lunch, and dinner. ");
        prompt.append("The total calorie intake should be as close as possible to ").append(calorieLimit).append(" calories. ");
        if (!dietaryPreferences.isEmpty()) {
            prompt.append("Ensure that meals are ").append(dietaryPreferences).append(". ");
        }
        prompt.append("Here are the available foods: ");
        for (String food : foods) {
            prompt.append(food).append(", ");
        }
        return prompt.toString();
    }

    public void callAPI(String prompt) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray jsonArrayMessage = new JSONArray();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("role", "user");
            jsonObjectMessage.put("content", prompt);
            jsonArrayMessage.put(jsonObjectMessage);

            jsonObject.put("messages", jsonArrayMessage);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                stringURLEndPoint, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String content;
                try {
                    content = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Ensure UI updates happen on the main thread
                getActivity().runOnUiThread(() -> {
                    // Split the response into breakfast, lunch, and dinner
                    String[] meals = content.split("(?=Breakfast:|Lunch:|Dinner:)");
                    for (String meal : meals) {
                        if (meal.startsWith("Breakfast:")) {
                            currentBreakfast = extractFoodNames(meal.trim());
                            breakfastTextView.setText(currentBreakfast);
                        } else if (meal.startsWith("Lunch:")) {
                            currentLunch = extractFoodNames(meal.trim());
                            lunchTextView.setText(currentLunch);
                        } else if (meal.startsWith("Dinner:")) {
                            currentDinner = extractFoodNames(meal.trim());
                            dinnerTextView.setText(currentDinner);
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Authorization", "Bearer " + apiKey);
                mapHeader.put("Content-Type", "application/json");
                return mapHeader;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        int intTimeoutPeriod = 60000; // 60 seconds timeout duration defined
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private String extractFoodNames(String mealContent) {
        StringBuilder foodNames = new StringBuilder();
        String[] lines = mealContent.split("\n");
        for (String line : lines) {
            if (!line.startsWith("Breakfast:") && !line.startsWith("Lunch:") && !line.startsWith("Dinner:") && !line.isEmpty()) {
                foodNames.append(line.trim()).append("\n");
            }
        }
        return foodNames.toString().trim();
    }
}
