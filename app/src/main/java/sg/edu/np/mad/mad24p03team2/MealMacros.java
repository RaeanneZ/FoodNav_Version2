package sg.edu.np.mad.mad24p03team2;

/**
 * MealMacros
 * Object class to store total macros of each meal
 */
public class MealMacros {
    double tFats= 0d;
    double tSugar = 0d;
    double tCarbs = 0d;
    double tCalories = 0d;

    public MealMacros() {
    }

    public double gettFats() {
        return tFats;
    }

    public void settFats(double tFats) {
        this.tFats = tFats;
    }

    public double gettSugar() {
        return tSugar;
    }

    public void settSugar(double tSugar) {
        this.tSugar = tSugar;
    }

    public double gettCarbs() {
        return tCarbs;
    }



    public void settCarbs(double tCarbs) {
        this.tCarbs = tCarbs;
    }

    public double gettCalories() {
        return tCalories;
    }

    public void settCalories(double tCalories) {
        this.tCalories = tCalories;
    }
}
