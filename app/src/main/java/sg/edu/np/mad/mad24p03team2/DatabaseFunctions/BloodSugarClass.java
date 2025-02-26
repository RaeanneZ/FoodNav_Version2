package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.provider.Settings;

import java.util.Date;

import sg.edu.np.mad.mad24p03team2.GlobalUtil;

/**
 * BloodSugarClass
 * Object class to hold all blood sugar details
 */
public class BloodSugarClass {
    private int id;
    private float bloodSugarReading;
    private String mealName;

    private String timestamp;

    // Constructors
    public BloodSugarClass() {
        id = 0;
        bloodSugarReading = 0f;
        timestamp = GlobalUtil.DateFormatter.format(new Date());
        mealName = "Breakfast";
    };

    public BloodSugarClass(int id, float bloodSugarReading, String mealName, String timestamp) {
        this.id = id;
        this.bloodSugarReading = bloodSugarReading;
        this.mealName = mealName;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public float getBloodSugarReading() {
        return bloodSugarReading;
    }
    public void setBloodSugarReading(float bloodSugarReading) {
        this.bloodSugarReading = bloodSugarReading;
    }

    public String getMealName() {
        return mealName;
    }
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
