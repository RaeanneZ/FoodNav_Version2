package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;


import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * AccountClass
 * Database User Account Object Class
 */
public class AccountClass {
    private int id;
    private String name;
    private String email;
    private Date birthDate;
    private String dietPlanOpt;
    private String gender;
    private float height;
    private float weight;
    private String trackBloodSugar;

    public AccountClass(){
        dietPlanOpt = "Diabetic Friendly";
    }

    public AccountClass(int id, String name, String email){
        this();
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDietPlanOpt() {
        return dietPlanOpt;
    }
    public void setDietPlanOpt(String dietPlanOpt) {
        this.dietPlanOpt = dietPlanOpt;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isTrackBloodSugar() {
        return this.trackBloodSugar.compareToIgnoreCase("T") == 0;
    }
    public String getTrackBloodSugar(){
        return this.trackBloodSugar;
    }

    public void setTrackBloodSugar(String trackBloodSugar){
        this.trackBloodSugar = trackBloodSugar;
    }
    public void setTrackBloodSugar(boolean trackBloodSugar) {
        if(trackBloodSugar)
            this.trackBloodSugar = "T";
        else
            this.trackBloodSugar = "F";
    }

    @NonNull
    @Override
    public String toString() {
        Log.d("AccountClass", "Account ID = "+id);
        Log.d("AccountClass", "Account Name = "+name);
        Log.d("AccountClass", "Account Email = "+email);
        Log.d("AccountClass", "Account Birthdate = "+birthDate.toString());
        Log.d("AccountClass", "Account DietPlanOpt = "+dietPlanOpt);
        Log.d("AccountClass", "Account Gender = "+gender);
        Log.d("AccountClass", "Account Height = "+height);
        Log.d("AccountClass", "Account Weight = "+weight);
        Log.d("AccountClass", "Account TrackBloodSuagr = "+trackBloodSugar);
        return super.toString();
    }
}
