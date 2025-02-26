package sg.edu.np.mad.mad24p03team2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.util.Locale;
import java.util.Set;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.MealClass;

import java.text.SimpleDateFormat;

/**
 * GlobalUtil
 * Consistency and Code-Reuse
 * Static declaration of common functions or variables that is used in more than one class
 */
public class GlobalUtil {

    public static final String SHARED_PREFS_FILE_KEY = "FoodNav.SharedPrefs";
    public static final String SHARED_PREFS_LOGIN_KEY = "LoginName";
    public static final String SHARED_PREFS_LOGIN_PSWD = "LoginPswd";

    //public static TextRecognizer MLTextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    public static TextRecognizer MLTextRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

    public static SimpleDateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static SimpleDateFormat UIDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    public static EncryptedSharedPreferences getEncryptedSharedPreference(Context applicationContext) {
        //create a master key for encryption of shared preference
        EncryptedSharedPreferences sharedPreferences = null;

        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (Exception e) {
            Log.d("GlobalUtil", "Encryption Key Failed - " + e.getMessage());
            sharedPreferences = null;
        }

        if (masterKeyAlias != null) {
            //Initialise/open an instance of EncryptedSharedPreference
            try {
                sharedPreferences =
                        (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                                GlobalUtil.SHARED_PREFS_FILE_KEY,
                                masterKeyAlias,
                                applicationContext,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            } catch (Exception e) {
                Log.d("GlobalUtil", "Fail to create EncryptedSharedPreference - " + e.getMessage());
                sharedPreferences = null;
            }
        }
        return sharedPreferences;
    }

    public static MealMacros getMealTotalMacros(MealClass meal) {

        MealMacros mMacros = new MealMacros();

        if (meal != null && meal.getSelectedFoodList() != null) {
            Set<FoodItemClass> foodList = meal.getSelectedFoodList().keySet();
            if (foodList.isEmpty())
                return mMacros;

            int qty = 1;
            double tCarbs = 0d;
            double tFats = 0d;
            double tSugar = 0d;
            double tCals = 0d;

            for (FoodItemClass foodItem : foodList) {
                // update UI
                try {
                    qty = meal.getSelectedFoodList().get(foodItem);

                } catch (NullPointerException e) {
                    qty = 1;
                }

                tSugar += foodItem.getSugar_g() * qty;
                tCarbs += foodItem.getCarbohydrates_total_g() * qty;
                tFats += foodItem.getFat_total_g() * qty;
                tCals += foodItem.getCalories() * qty;
            }

            mMacros.settCalories(tCals);
            mMacros.settCarbs(tCarbs);
            mMacros.settFats(tFats);
            mMacros.settSugar(tSugar);
        }

        return mMacros;
    }

    public static String formatBirthDatesForUIDisplay(int day, int month, int year){
        String mthInString = String.valueOf(month);
        String dayOfMthInString = String.valueOf(day);

        if(month < 10){
            mthInString = "0"+month;
        }
        if(day < 10){
            dayOfMthInString = "0"+day;
        }
        return (year + "-" + mthInString + "-" + dayOfMthInString);
    }

    public static boolean checkCameraPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
    }

}
