package sg.edu.np.mad.mad24p03team2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.google.mlkit.nl.translate.Translator;

import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietConstraints;

/**
 * NomNotion
 */
public class NomNotion extends Fragment {

    private static final String TAG = "NomNotion";

    private Uri imageUri = null;
    private Button cameraIButton;
    private Button galleryIButton;
    private ImageView imageIv;

    HorizontalScrollView svConstraints;
    CardView cvSugar;
    TextView txtViewSugar;
    ImageView ivSugar;
    ImageView ivSugarFree;

    CardView cvDairy;
    TextView txtViewDairy;
    ImageView ivDairy;
    ImageView ivDairyFree;

    CardView cvGluten;
    TextView txtViewGluten;
    ImageView ivGluten;
    ImageView ivGlutenFree;

    CardView cvNuts;
    TextView txtViewNuts;
    ImageView ivNuts;
    ImageView ivNutsFree;

    CardView cvSoy;
    TextView txtViewSoy;
    ImageView ivSoy;
    ImageView ivSoyFree;

    CardView cvSeafood;
    TextView txtViewSeafood;
    ImageView ivSeafood;
    ImageView ivSeafoodFree;

    CardView cvEgg;
    TextView txtViewEgg;
    ImageView ivEgg;
    ImageView ivEggFree;

    CardView cvVegan;
    TextView txtViewVegan;
    ImageView ivVegan;
    ImageView ivNotVegan;

    CardView cvVegeterian;
    TextView txtViewVegeterian;
    ImageView ivVegeterian;
    ImageView ivNotVegeterian;

    Translator translatorKorean = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initTranslatorPack();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nom_notion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        svConstraints = view.findViewById(R.id.scrollViewConstraints);
        cvSugar = view.findViewById(R.id.cardViewSugar);
        txtViewSugar = view.findViewById(R.id.textViewSugar);
        ivSugar = view.findViewById(R.id.image_sugar);
        ivSugarFree = view.findViewById(R.id.image_sugarfree);

        cvDairy = view.findViewById(R.id.cardViewDairy);
        txtViewDairy = view.findViewById(R.id.textViewDairy);
        ivDairy = view.findViewById(R.id.image_Dairy);
        ivDairyFree = view.findViewById(R.id.image_Dairyfree);

        cvGluten = view.findViewById(R.id.cardViewGluten);
        txtViewGluten = view.findViewById(R.id.textViewGluten);
        ivGluten = view.findViewById(R.id.image_gluten);
        ivGlutenFree = view.findViewById(R.id.image_glutenfree);

        cvEgg = view.findViewById(R.id.cardViewEgg);
        txtViewEgg = view.findViewById(R.id.textViewEgg);
        ivEgg = view.findViewById(R.id.image_egg);
        ivEggFree = view.findViewById(R.id.image_eggfree);

        cvVegan = view.findViewById(R.id.cardViewVegan);
        txtViewVegan = view.findViewById(R.id.textViewVegan);
        ivVegan = view.findViewById(R.id.image_vegan);
        ivNotVegan = view.findViewById(R.id.image_NotVegan);

        cvVegeterian = view.findViewById(R.id.cardViewVegeterian);
        txtViewVegeterian = view.findViewById(R.id.textViewVegeterian);
        ivVegeterian = view.findViewById(R.id.image_vegeterian);
        ivNotVegeterian = view.findViewById(R.id.image_NotVegeterian);

        cvSeafood = view.findViewById(R.id.cardViewSeafood);
        txtViewSeafood = view.findViewById(R.id.textViewSeafood);
        ivSeafood = view.findViewById(R.id.image_Seafood);
        ivSeafoodFree = view.findViewById(R.id.image_SeafoodFree);

        cvSoy = view.findViewById(R.id.cardViewSoy);
        txtViewSoy = view.findViewById(R.id.textViewSoy);
        ivSoy = view.findViewById(R.id.image_Soy);
        ivSoyFree = view.findViewById(R.id.image_Soyfree);

        cvNuts = view.findViewById(R.id.cardViewNuts);
        txtViewNuts = view.findViewById(R.id.textViewNuts);
        ivNuts = view.findViewById(R.id.image_Nuts);
        ivNutsFree = view.findViewById(R.id.image_Nutsfree);

        imageIv = view.findViewById(R.id.ImageURI);
        cameraIButton = view.findViewById(R.id.cameraIButton);
        galleryIButton = view.findViewById(R.id.galleryIButton);

        //Camera access needs permission
        cameraIButton.setOnClickListener(v -> {
            if (GlobalUtil.checkCameraPermissions(getContext()))
                pickImageCamera();
            else {
                Log.d(TAG, "Request for Camera Permission");
                requestCameraPermissions();
            }
        });

        //read/write drive doesn't need permission
        galleryIButton.setOnClickListener(v -> pickImageGallery());
    }

    private void initTranslatorPack(){
        TranslatorOptions option = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KOREAN)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        translatorKorean = Translation.getClient(option);

        translatorKorean.downloadModelIfNeeded().addOnSuccessListener(unused ->
                Log.d(TAG, "Downloaded korean translator pack successfully")).
                addOnFailureListener(e -> Log.d(TAG, "Fail to download korean translator pack"));
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
                                //food names in DB are all in lowerCase
                                translateKRToEN(text.getText().toLowerCase());
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide result pane
                            svConstraints.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Failed to recognise text due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            //Exception occured while preparing InputImage, dismiss dialog and print reason in Toast
            Toast.makeText(getActivity(), "Failed to prepared image due to " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void translateKRToEN(String detectedText){
        Log.d(TAG, "Recognised Text = "+detectedText);

        if(translatorKorean == null){
            Log.d(TAG,"Korean Translator is null");
        }else{
            Task<String> result = translatorKorean.translate(detectedText).addOnSuccessListener(s -> {
                processIdentifiedText(s);
                svConstraints.setVisibility(View.VISIBLE);
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Fail to translate recognized text", Toast.LENGTH_SHORT).show();
                    svConstraints.setVisibility(View.GONE);
                }
            });
        }
    }

    private void processIdentifiedText(String recognisedText) {
        //check dietConstraints
        HashMap<SingletonDietConstraints.DIET_CONSTRAINTS, ArrayList<String>> dietConstraintItems =
                SingletonDietConstraints.getInstance().checkIngredients(recognisedText);

        //hide all card
        resetConstraintCardVisibility(View.GONE);

        //update UI accordingly
        Set<SingletonDietConstraints.DIET_CONSTRAINTS> dietConstraintTypes = dietConstraintItems.keySet();
        for (SingletonDietConstraints.DIET_CONSTRAINTS dc : dietConstraintTypes) {
            ArrayList<String> cIngredients = dietConstraintItems.get(dc);

            if(cIngredients == null)
                break; //move on to the next dc

            //setIngredient listing
            String listing = "";
            for (String item : cIngredients) {
                if(listing.length() < 8) { //per category limit display of item to 8
                    if (listing.isEmpty())
                        listing += item;
                    else
                        listing += ", " + item;
                }
            }

            switch (dc) {
                case VEGAN:
                    if (cIngredients.isEmpty()) {
                        ivVegan.setVisibility(View.VISIBLE);
                        ivNotVegan.setVisibility(View.GONE);
                        txtViewVegan.setText("");
                    } else {
                        ivVegan.setVisibility(View.GONE);
                        ivNotVegan.setVisibility(View.VISIBLE);
                        txtViewVegan.setText(listing);
                    }
                    cvVegan.setVisibility(View.VISIBLE);
                    break;
                case VEGETARIAN:
                    if (cIngredients.isEmpty()) {
                        ivVegeterian.setVisibility(View.VISIBLE);
                        ivNotVegeterian.setVisibility(View.GONE);
                        txtViewVegeterian.setText("");
                    } else {
                        ivVegeterian.setVisibility(View.GONE);
                        ivNotVegeterian.setVisibility(View.VISIBLE);
                        txtViewVegeterian.setText(listing);
                    }
                    cvVegeterian.setVisibility(View.VISIBLE);
                    break;
                case SUGAR_FREE:
                    if (cIngredients.isEmpty()) {
                        ivSugarFree.setVisibility(View.VISIBLE);
                        ivSugar.setVisibility(View.GONE);
                        txtViewSugar.setText("");
                    } else {
                        ivSugarFree.setVisibility(View.GONE);
                        ivSugar.setVisibility(View.VISIBLE);
                        txtViewSugar.setText(listing);
                    }
                    cvSugar.setVisibility(View.VISIBLE);
                    break;
                case GLUTEN_FREE:
                    if (cIngredients.isEmpty()) {
                        ivGlutenFree.setVisibility(View.VISIBLE);
                        ivGluten.setVisibility(View.GONE);
                        txtViewGluten.setText("");
                    } else {
                        ivGlutenFree.setVisibility(View.GONE);
                        ivGluten.setVisibility(View.VISIBLE);
                        txtViewGluten.setText(listing);
                    }
                    cvGluten.setVisibility(View.VISIBLE);
                    break;
                case NO_SEAFOOD:
                    if (cIngredients.isEmpty()) {
                        ivSeafoodFree.setVisibility(View.VISIBLE);
                        ivSeafood.setVisibility(View.GONE);
                        txtViewSeafood.setText("");
                    } else {
                        ivSeafoodFree.setVisibility(View.GONE);
                        ivSeafood.setVisibility(View.VISIBLE);
                        txtViewSeafood.setText(listing);
                    }
                    cvSeafood.setVisibility(View.VISIBLE);
                    break;
                case EGGLESS:
                    if (cIngredients.isEmpty()) {
                        ivEggFree.setVisibility(View.VISIBLE);
                        ivEgg.setVisibility(View.GONE);
                        txtViewEgg.setText("");
                    } else {
                        ivEggFree.setVisibility(View.GONE);
                        ivEgg.setVisibility(View.VISIBLE);
                        txtViewEgg.setText(listing);
                    }
                    cvEgg.setVisibility(View.VISIBLE);
                    break;
                case NUT_FREE:
                    if (cIngredients.isEmpty()) {
                        ivNutsFree.setVisibility(View.VISIBLE);
                        ivNuts.setVisibility(View.GONE);
                        txtViewNuts.setText("");
                    } else {
                        ivNutsFree.setVisibility(View.GONE);
                        ivNuts.setVisibility(View.VISIBLE);
                        txtViewNuts.setText(listing);
                    }
                    cvNuts.setVisibility(View.VISIBLE);
                    break;
                case SOY_FREE:
                    if (cIngredients.isEmpty()) {
                        ivSoyFree.setVisibility(View.VISIBLE);
                        ivSoy.setVisibility(View.GONE);
                        txtViewSoy.setText("");
                    } else {
                        ivSoyFree.setVisibility(View.GONE);
                        ivSoy.setVisibility(View.VISIBLE);
                        txtViewSoy.setText(listing);
                    }
                    cvSoy.setVisibility(View.VISIBLE);
                    break;
                case DAIRY_FREE:
                    if (cIngredients.isEmpty()) {
                        ivDairyFree.setVisibility(View.VISIBLE);
                        ivDairy.setVisibility(View.GONE);
                        txtViewDairy.setText("");
                    } else {
                        ivDairyFree.setVisibility(View.GONE);
                        ivDairy.setVisibility(View.VISIBLE);
                        txtViewDairy.setText(listing);
                    }
                    cvDairy.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void resetConstraintCardVisibility(int visibility) {

            cvDairy.setVisibility(visibility);
            cvEgg.setVisibility(visibility);
            cvGluten.setVisibility(visibility);
            cvNuts.setVisibility(visibility);
            cvSoy.setVisibility(visibility);
            cvSeafood.setVisibility(visibility);
            cvVegan.setVisibility(visibility);
            cvVegeterian.setVisibility(visibility);
            cvSugar.setVisibility(visibility);
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
}