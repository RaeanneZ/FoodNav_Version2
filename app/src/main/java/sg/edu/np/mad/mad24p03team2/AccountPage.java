package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;

import java.util.Objects;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.AccountClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateUserProfile;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonBloodSugarResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietConstraints;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietPlanResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSecurityInfoResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSignUp;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonTodayMeal;

/**
 * accountPage
 * UI-Fragment that display menu for application and profile customisation
 */
public class AccountPage extends Fragment {

    private ImageView maleIconView;
    private ImageView femaleIconView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accountpage, container, false);
        TextView changePassword = view.findViewById(R.id.changePassword);

        // Find the switch by its id
        maleIconView = view.findViewById(R.id.male_icon);
        femaleIconView = view.findViewById(R.id.female_icon);
        Button logoutBtn = view.findViewById(R.id.logoutButton);

        logoutBtn.setOnClickListener(v -> {

            //Logout - remove shared preferences
            EncryptedSharedPreferences sharedPreferences =
                    GlobalUtil.getEncryptedSharedPreference(requireActivity().getApplicationContext());

            if (sharedPreferences != null) {
                sharedPreferences.edit().putString(GlobalUtil.SHARED_PREFS_LOGIN_KEY, "").apply();
                sharedPreferences.edit().putString(GlobalUtil.SHARED_PREFS_LOGIN_PSWD, "").apply();
            }

            //Destroy all Singleton
            DestroyAllSingleton();

            //This is to reset activity stack, ensure no past activities history
            Intent intent = new Intent(getActivity(), LogoutAnimate.class);
            startActivity(intent);
            getActivity().finishAffinity();

        });


        TextView editProfile = view.findViewById(R.id.editProfile);
        String username = SingletonSession.getInstance().GetAccount().getName();
        TextView tempName = view.findViewById(R.id.tempName);
        tempName.setText(username);

        // Set click listener for the buttons
        changePassword.setOnClickListener(v -> loadChangePasswordActivity());

        ImageView passwordArrow = view.findViewById(R.id.imageView9);
        passwordArrow.setOnClickListener(v -> loadChangePasswordActivity());

        editProfile.setOnClickListener(v -> loadEditProfileActivity());

        ImageView profileArrow = view.findViewById(R.id.arrow);
        profileArrow.setOnClickListener(v -> loadEditProfileActivity());

        TextView setDietConstraint = view.findViewById(R.id.dietPref);
        setDietConstraint.setOnClickListener(v -> loadDietConstraintActivity());

        ImageView dietArrow = view.findViewById(R.id.arrowDiet);
        dietArrow.setOnClickListener(v -> loadDietConstraintActivity());

        // Populate profile details including profile picture
        populateProfileDetails();

        return view;
    }

    private void loadDietConstraintActivity() {
        Intent dietConstraintsIntent = new Intent(getActivity(), DietConstraintActivity.class);
        startActivity(dietConstraintsIntent);
    }

    private void loadEditProfileActivity() {
        Intent editProfileIntent = new Intent(getActivity(), EditProfile.class);
        startActivity(editProfileIntent);
    }

    private void loadChangePasswordActivity() {
        Intent changePasswordIntent = new Intent(getActivity(), ChangePassword.class);
        startActivity(changePasswordIntent);
    }

    private void populateProfileDetails() {
        AccountClass currentUserProfile = SingletonSession.getInstance().GetAccount();
        if (currentUserProfile != null) {
            setProfileImage(currentUserProfile.getGender());
        } else {
            Toast.makeText(getContext(), "Failed to load profile details", Toast.LENGTH_SHORT).show();
        }
    }

    private void setProfileImage(String gender) {
        if (gender.equalsIgnoreCase("M")) {
            maleIconView.setVisibility(View.VISIBLE);
            femaleIconView.setVisibility(View.GONE);
        } else if (gender.equalsIgnoreCase("F")) {
            maleIconView.setVisibility(View.GONE);
            femaleIconView.setVisibility(View.VISIBLE);
        } else {
            maleIconView.setVisibility(View.GONE);
            femaleIconView.setVisibility(View.GONE);
        }
    }

    public void DestroyAllSingleton(){
        SingletonTodayMeal.getInstance().onDestroy();
        SingletonBloodSugarResult.getInstance().onDestroy();
        SingletonDietConstraints.getInstance().onDestroy();
        SingletonFoodSearchResult.getInstance().onDestroy();
        SingletonSecurityInfoResult.getInstance().onDestroy();
        SingletonSession.getInstance().onDestroy();
        SingletonDietPlanResult.getInstance().onDestroy();
        SingletonSignUp.getInstance().onDestroy();
    }

}
