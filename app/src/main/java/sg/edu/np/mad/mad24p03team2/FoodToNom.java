package sg.edu.np.mad.mad24p03team2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetAllFood;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;

/**
 * Food2Nom
 * UI-Fragment allowing current user to upload picture of food list / menu
 * app will search DB for food recognised from picture
 */
public class FoodToNom extends Fragment implements IDBProcessListener, RecyclerViewInterface {

    private static final String TAG = "FoodToNom";

    private ArrayList<FoodItemClass> itemListInDB = null;

    private Uri imageUri = null;

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;

    private ImageView imageIv;
    private GetAllFood getAllFood;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //PULL FROM SINGLETON INSTEAD
        itemListInDB = SingletonFoodSearchResult.getInstance().getCompleteFoodItemList();
        if(itemListInDB.isEmpty()){
            //pull from Database
            getAllFood = new GetAllFood(requireActivity().getApplicationContext(), this);
            getAllFood.execute();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_to_nom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        foodAdapter = new FoodAdapter(getView().getContext(), itemListInDB, this, true);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));

        imageIv = view.findViewById(R.id.ImageURI);
        Button cameraIButton = view.findViewById(R.id.cameraIButton);
        Button galleryIButton = view.findViewById(R.id.galleryIButton);

        //Camera access needs permission
        cameraIButton.setOnClickListener(v -> {
            if (checkCameraPermissions())
                pickImageCamera();
            else
                requestCameraPermissions();
        });

        //read/write drive doesn't need permission
        galleryIButton.setOnClickListener(v -> pickImageGallery());
    }

    private void pickImageCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    //Result callback for Intent - to access photo taken from camera
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image is taken from camera
                        //we already have the image in imageUri using function pickImageCamera()
                        imageIv.setImageURI(imageUri);
                        //do text recognition
                        try {
                            runTextRecognition();
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Fail to run Text Recognition failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //cancelled
                        Toast.makeText(getContext(), "Picture from camera failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkCameraPermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermissions() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    //Result callback for Intent - to prompt user for Camera access permission
    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean grant) {
                    if (grant) {
                        //permission granted
                        pickImageCamera();
                    } else {
                        //permission not granted
                        Toast.makeText(getActivity(), "Camera permissions denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void runTextRecognition() throws IOException {
        try {
            //prepare image for engine
            InputImage inputImage = InputImage.fromFilePath(getContext(), imageUri);
            Task<Text> textTaskResult = GlobalUtil.MLTextRecognizer.process(inputImage).addOnSuccessListener(
                            text -> {
                                Log.d(TAG, "Success in recognising text");
                                //process recognised-text; map it to the food DB
                                //food names in DB are all in lowerCase
                                processIdentifiedText(text.getText().toLowerCase());
                            })
                    .addOnFailureListener(e -> {
                        //hide result pane
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Failed to recognise text due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            //Exception occured while preparing InputImage, dismiss dialog and print reason in Toast
            Toast.makeText(getActivity(), "Failed to prepared image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //identify food items from recognised text detected from Image
    //run through the recognisedtext to find matching food item names in DB
    private void processIdentifiedText(String recognisedText) {

        if (itemListInDB == null || itemListInDB.isEmpty()) {
            Toast.makeText(getActivity(), "No matching food found in FoodNav DB", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<FoodItemClass> filteredList = new ArrayList<FoodItemClass>();
        for (FoodItemClass fItem : itemListInDB) {
            if (recognisedText.contains(fItem.getName())) {
                //list to display food item and calories
                filteredList.add(fItem);
            }
        }

        foodAdapter.setFilteredList(filteredList);

        if (!filteredList.isEmpty())
            recyclerView.setVisibility(View.VISIBLE);  //show result pane
        else{
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Can't find food details in FoodNav", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    //return response for intent - to pick image from gallery
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image picked
                        Intent data = result.getData();
                        imageUri = data.getData();
                        //set to imageview
                        imageIv.setImageURI(imageUri);

                        //run text recognition
                        try {
                            runTextRecognition();
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "Fail to run Text recognition", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //cancelled
                        Toast.makeText(getActivity(), "Cannot choose from gallery", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        if(executeStatus)
            itemListInDB = SingletonFoodSearchResult.getInstance().getCompleteFoodItemList();
        else
            requireActivity().runOnUiThread(()->Toast.makeText(getActivity(), "Fail to load food from database", Toast.LENGTH_SHORT).show() );
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}

    @Override
    public void onItemClick(int itemPos) {}
}