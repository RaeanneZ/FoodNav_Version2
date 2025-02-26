package sg.edu.np.mad.mad24p03team2.SingletonClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SingletonDietConstraints {

    public enum DIET_CONSTRAINTS {
        VEGAN, GLUTEN_FREE, VEGETARIAN, SUGAR_FREE, DAIRY_FREE, EGGLESS, NUT_FREE, NO_SEAFOOD, SOY_FREE;
    }

    ArrayList<String> allEggs = new ArrayList<>();
    ArrayList<String> allSeafood = new ArrayList<>();
    ArrayList<String> allNuts = new ArrayList<>();
    ArrayList<String> allDairy = new ArrayList<>();
    ArrayList<String> allSoy = new ArrayList<>();
    //store the list of things in each category that is undesirable to have in diet
    public ArrayList<ArrayList<String>> allAllergens = new ArrayList<>();
    public ArrayList<String> allVegan = new ArrayList<>();
    public ArrayList<String> allVegetarian = new ArrayList<>();
    public ArrayList<String> allGluten = new ArrayList<>();

    //because this is a diabetic friendly diet plan, to check on sugar is a must
    private ArrayList<String> allSugar = new ArrayList<>();

    private ArrayList<DIET_CONSTRAINTS> userDietPref = new ArrayList<>();

    private static volatile SingletonDietConstraints INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonDietConstraints() {

        Collections.addAll(this.allSoy, "soy", "soya", "miso", "tofu", "edamame",
                "hydrolyzed vegetable protein", "hpv", "textured vegetable protein", "tvp",
                "lecithin", "monodiglyceride", "monosodium glutamate", "msg", "vegetable oil", "vitamin e",
                "vegetable broth", "vegatable gum", "vegetable starch", "hyrolyzed soy protein",
                "natto", "soy albumin", "soy cheese", "soy fiber", "soy yogurt", "soy bean", "shoyo sauce",
                "soy sauce", "soy milk", "soy nuts", "soy flour", "soy sprout", "tamari", "tempeh");

        Collections.addAll(this.allNuts, "peanut", "nut", "almond", "coconut", "ginkgo", "macadmia",
                "beechnut", "walnut", "butternut", "cashew", "hickory", "lychee nut", "almond paste", "nangai",
                "chestnut", "pecan", "pistachio", "nut butter", "pecan", "pesto", "pili", "pine nut", "praline",
                "shea nut");

        Collections.addAll(this.allSugar, "syrup", "sugar", "dextrose", "caramel", "malt", "molasses",
                "sucrose", "lactose", "glucose", "fructose", "dextran", "dextrin", "ethyl maltol", "maltose",
                "treacle", "maltodextrin", "diatase", "sorbitol", "galactose", "concentrate", "honey",
                "agava nectar", "juice", "florida crystal", "rapadura", "sucanat", "maltose", "d-ribose", "saccharide");

        Collections.addAll(this.allGluten, "wheat", "bran", "couscous", "durum",
                "einkorn", "farina", "matzoh", "seitan", "spelt", "malt", "semolina", "starch", "pasta",
                "bread", "bulgur", "rye", "barley", "malt", "triticale", "cereal", "cracker", "flour",
                "atta", "chapati", "graham", "seitan", "semolina", "kamut", "einkorn", "emmer",
                "dinkel", "brewer yeast", "triticum vulgare", "triticale", "hordeum vulgare",
                "secale cereale", "triticum spelta");


        Collections.addAll(this.allSeafood, "abalone", "barnacle", "anchovies", "bass",
                "catfish", "cod", "grouper", "clam", "escargot", "oyster", "shellfish", "octopus",
                "crevette", "cuttlefish",
                "haddock", "pike", "salmon", "snapper", "tilapia", "tuna", "trout", "fish", "crawfish",
                "crab", "krill", "lobster", "shrimp", "mussels", "squid", "prawn", "calamari",
                "mollusks", "scallops");

        Collections.addAll(this.allEggs, "ovalbumin", "lecithin", "globulin", "egg",
                "eggs", "eggnog", "surimi", "vitellin", "ovomucoid", "ovomucin",
                "lysozyme", "albumin", "albumen", "ovovitellin", "lysozyme", "meringue");

        Collections.addAll(this.allDairy, "milk", "butter", "buttermilk", "casein",
                "caseinate", "cheese", "cream", "curds", "lactose", "lactulose", "lactate", "custard",
                "yogurt", "condense milk", "evaporated milk", "lacto acidophilus", "lactalbumin",
                "lactoglobulin", "nougats", "sour cream", "rennet", "whey", "skim milk powder", "skim milk",
                "milk fat", "milk chocolate");

        this.allVegetarian.addAll(allEggs);
        Collections.addAll(this.allVegetarian, "pork", "meat", "beef", "chicken",
                "honey", "lamb", "veal", "turkey", "ham", "bacon", "gelatin",
                "e120", "e322", "e411", "e422", "e471", "e542", "e631", "e901", "e904", "e910", "e920",
                "e921", "e913", "e966", "collagen", "elastin", "keratin", "aspic", "lard", "tallow",
                "cochineal", "carmine", "isinglass", "castoreum", "fatty acid", "shellac", "vitamin d3",
                "albumen", "albumin", "horse", "goose", "duck", "quail", "honey", "beeswax", "royal jelly", "bee pollen");

        this.allVegan.addAll(this.allDairy);
        this.allVegan.addAll(this.allEggs);
        this.allVegan.addAll(this.allVegetarian);
    }

    // public static method to retrieve the singleton instance
    public static SingletonDietConstraints getInstance() {
        // Check if the instance is already created
        if (INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonDietConstraints.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonDietConstraints();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public ArrayList<DIET_CONSTRAINTS> getDietProfile(){
        return userDietPref;
    }

    public ArrayList<String> getDietProfileInDBFormat(){
        ArrayList<String> constraintString = new ArrayList<>();

        for(DIET_CONSTRAINTS constraint : userDietPref){
            constraintString.add(constraint.name().toLowerCase());
        }
        return constraintString;
    }

    public void setDietProfile(ArrayList<String> preference){
        //clear previous setting
        userDietPref.clear();

        if(!preference.isEmpty()) {
            for (String pref : preference) {
                userDietPref.add(DIET_CONSTRAINTS.valueOf(pref.toUpperCase()));
            }
        }
    }

    public HashMap<DIET_CONSTRAINTS, ArrayList<String>> checkIngredients(String ingredients){

        HashMap<SingletonDietConstraints.DIET_CONSTRAINTS, ArrayList<String>> resultMap = new HashMap<>();

        for(SingletonDietConstraints.DIET_CONSTRAINTS constraint : userDietPref){
            ArrayList<String> returnList = SingletonDietConstraints.getInstance().checkDietConstraint(ingredients, constraint);
            resultMap.putIfAbsent(constraint, returnList);
        }

        return resultMap;
    }

    private ArrayList<String> checkDietConstraint(String ingredients, DIET_CONSTRAINTS constraints) {

        ArrayList<String> filteredList = new ArrayList<>();
        ArrayList<String> constraintList = new ArrayList<>();

        switch (constraints) {
            case VEGAN:
                constraintList = allVegan;
                break;
            case VEGETARIAN:
                constraintList = allVegetarian;
                break;
            case SUGAR_FREE:
                constraintList = allSugar;
                break;
            case GLUTEN_FREE:
                constraintList = allGluten;
                break;
            case NO_SEAFOOD:
                constraintList = allSeafood;
                break;
            case EGGLESS:
                constraintList = allEggs;
                break;
            case NUT_FREE:
                constraintList = allNuts;
                break;
            case SOY_FREE:
                constraintList = allSoy;
                break;
            case DAIRY_FREE:
                constraintList = allDairy;
        }

        //check against user's allergies
        String ingredientsLower = ingredients.toLowerCase();
        for (String item : constraintList) {
            if (ingredientsLower.contains(item)) {
                    filteredList.add(item);
            }
        }
        return filteredList;
    }

    public void onDestroy(){
        INSTANCE = null;
    }
}
