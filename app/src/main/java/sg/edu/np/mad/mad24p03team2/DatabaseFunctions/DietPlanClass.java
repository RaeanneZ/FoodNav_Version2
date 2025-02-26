package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

/**
 * DietPlanClass
 * Database Diet Plan Object class
 */
public class DietPlanClass {
    private int id;
    private String name;
    private int reccCarbIntake;
    private int reccSugarIntake;
    private int reccFatsIntake;
    private String gender;

    private int reccCaloriesIntake;

    public DietPlanClass() { }
    public DietPlanClass(int id, String name, int reccCarbIntake, int reccSugarIntake, int reccFatsIntake, String gender, int reccCaloriesIntake){
        this.id = id;
        this.name = name;
        this.reccCarbIntake = reccCarbIntake;
        this.reccSugarIntake = reccSugarIntake;
        this.reccFatsIntake = reccFatsIntake;
        this.gender = gender;
        this.reccCaloriesIntake = reccCaloriesIntake;
    }

    public int getReccCaloriesIntake() {
        return reccCaloriesIntake;
    }

    public void setReccCaloriesIntake(int reccCaloriesIntake) {
        this.reccCaloriesIntake = reccCaloriesIntake;
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

    public int getReccCarbIntake() {
        return reccCarbIntake;
    }
    public void setReccCarbIntake(int reccCarbIntake) {
        this.reccCarbIntake = reccCarbIntake;
    }

    public int getReccSugarIntake() {
        return reccSugarIntake;
    }
    public void setReccSugarIntake(int reccSugarIntake) {
        this.reccSugarIntake = reccSugarIntake;
    }

    public int getReccFatsIntake() {
        return reccFatsIntake;
    }
    public void setReccFatsIntake(int reccFatsIntake) {
        this.reccFatsIntake = reccFatsIntake;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
}
