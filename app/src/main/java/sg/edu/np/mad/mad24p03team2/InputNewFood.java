package sg.edu.np.mad.mad24p03team2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.CreateFoodRecord;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetAllFood;


public class InputNewFood extends Fragment implements IDBProcessListener {

    private static final String TAG = "InputNewFood";

    //Create Food Class
    GetAllFood getAllFood = null;
    CreateFoodRecord createFoodRecord = null;

    private Uri imageUri = null;
    ArrayList<LineItem> organisedText;
    HashMap<String, LineItem> filteredInfo;
    private EditText sugarEt;
    private EditText fatsEt;
    private EditText proteinEt;
    private EditText carbsEt;
    private EditText caloriesEt;
    private EditText foodNameEt;
    private EditText servingSize;

    private HashMap<String, Boolean> Identifiers = null;


    public InputNewFood() {

        // Required empty public constructor

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_input_new_food, container, false);
    }

    private void initTextScan() {

        organisedText = new ArrayList<LineItem>();
        filteredInfo = new HashMap<String, LineItem>();

        Identifiers = new HashMap<String, Boolean>() {{
            put("fat", false);
            put("carbohydrate", false);
            put("sugar", false);
            put("energy", false);
            put("protein", false);
        }};
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createFoodRecord = new CreateFoodRecord(getActivity().getApplicationContext());

        sugarEt = view.findViewById(R.id.sugarEt);
        proteinEt = view.findViewById(R.id.proteinEt);
        carbsEt = view.findViewById(R.id.carboEt);
        fatsEt = view.findViewById(R.id.fatsEt);
        caloriesEt = view.findViewById(R.id.kcal);
        servingSize = view.findViewById(R.id.servingSize);
        foodNameEt = view.findViewById(R.id.nameEt);


        Button saveBtn = view.findViewById(R.id.save);
        saveBtn.setOnClickListener(v -> {

            //init getAllFood because if new food is saved to DB, update singletonFoodSearch
            getAllFood = new GetAllFood(getActivity().getApplicationContext());

            //Check all data are filled
            String sugar = "";
            String protein = "";
            String carbs = "";
            String fats = "";
            String calories = "";
            String name = "";
            String serving = "";

            if (sugarEt.getText() != null) {
                sugar = sugarEt.getText().toString();
            }
            if (proteinEt.getText() != null) {
                protein = proteinEt.getText().toString();
            }
            if (carbsEt.getText() != null) {
                carbs = carbsEt.getText().toString();
            }
            if (fatsEt.getText() != null) {
                fats = fatsEt.getText().toString();
            }
            if (caloriesEt.getText() != null) {
                calories = caloriesEt.getText().toString();
            }
            if (foodNameEt.getText() != null) {
                name = foodNameEt.getText().toString();
            }
            if (servingSize.getText() != null) {
                serving = servingSize.getText().toString();
            }

            if (sugar.isEmpty() || protein.isEmpty() || carbs.isEmpty() ||
                    fats.isEmpty() || calories.isEmpty() || name.isEmpty() || serving.isEmpty()) {
                Toast.makeText(this.getContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            } else {
                createFoodRecord.execute(sugar, protein, carbs, fats, calories, name.toLowerCase(), serving);

                //remove 'this' from frag-stack
                FragmentActivity activity = getActivity();
                if (activity instanceof MainActivity2) {
                    ((MainActivity2) activity).removeFragment(this);
                }
            }
        });

        FragmentActivity factivity = getActivity();
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> {
                //remove 'this' from frag-stack
            if (factivity instanceof MainActivity2) {
                ((MainActivity2) factivity).removeFragment(this);
            }
        });

        Button cameraIButton = view.findViewById(R.id.cameraIButton);
        cameraIButton.setOnClickListener(v -> {
            //reset variables for new input
            initTextScan();
            if (GlobalUtil.checkCameraPermissions(getContext()))
                pickImageCamera();
            else {
                Log.d(TAG, "Request for Camera Permission");
                requestCameraPermissions();
            }
        });
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
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //we already have the image in imageUri using function pickImageCamera()
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
                                //process recognised-text; map it to the food DB
                                //food names in DB are all in lowerCase
                                processIdentifiedText(text);
                                updateUI();
                            })
                    .addOnFailureListener(e -> {
                        //hide result pane
                        Toast.makeText(getActivity(), "Failed to recognise text due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            //Exception occured while preparing InputImage, dismiss dialog and print reason in Toast
            Toast.makeText(getActivity(), "Failed to prepared image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {

        int keywordIdx = 0;
        String lineSubstring = "";
        String value = "";
        String trimmedValue = "";

        //setValues of detected keyword
        for (String keyword : filteredInfo.keySet()) {
            String text = filteredInfo.get(keyword).getText();
            if (text != null) {
                keywordIdx = text.indexOf(keyword);
                lineSubstring = text.substring(keywordIdx);
                value = extractValue(lineSubstring);

                trimmedValue = value;
                if (value.contains("g")) {
                    trimmedValue = value.substring(0, value.indexOf('g'));
                } else if (value.contains("kcal")) {
                    trimmedValue = value.substring(0, value.indexOf("kcal"));
                }

                // Log.d(TAG, "Extracted Value = "+value);
                //dun print if not number
                if (!trimmedValue.isEmpty() && checkIfNumeric(trimmedValue)) {

                    if (keyword.contains("fat")) {
                        fatsEt.setText(trimmedValue);
                    } else if (keyword.contains("energy")) {
                        caloriesEt.setText(trimmedValue);
                    } else if (keyword.contains("carb")) {
                        carbsEt.setText(trimmedValue);
                    } else if (keyword.contains("protein")) {
                        proteinEt.setText(trimmedValue);
                    } else if (keyword.contains("sugar")) {
                        sugarEt.setText(trimmedValue);
                    }
                }
            }
        }
    }

    private String extractValue(String searchString) {
        //Keyword value1 value2; find value2
        String value = " ";
        Scanner scan = new Scanner(searchString);
        for (int i = 0; i < 3; i++) {
            try {
                value = scan.next();
                Log.d(TAG, "Scan NextLine = " + value);
            } catch (Exception e) {
                Log.d(TAG, "Error in reading value : " + e.getMessage());
            }
        }

        scan.close();
        return value;
    }

    private boolean checkIfNumeric(String sNum) {
        try {
            double v = Double.parseDouble(sNum);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void processIdentifiedText(Text result) {
        //compare each line bounding box top, if the same, they belong to the same line
        //reset line array first
        organisedText.clear();
        //start mlkit process
        for (Text.TextBlock block : result.getTextBlocks()) {
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                reorganiseTextDetected(line);
            }
        }

        //picked out lines with keywords, specific MacrosValue
        for (LineItem item : organisedText) {
            // Log.d(TAG, "Organised Text = " + item.getText());
            for (String keyword : Identifiers.keySet()) {
                //compare if not found
                if (item.getText().contains(keyword)) {
                    // Log.d(TAG, "Found for " + keyword);
                    Identifiers.put(keyword, true); //update found flag
                    filteredInfo.put(keyword, item);
                }

                //check if all keywords has been found, still contain False aka Not Found flag?
                if (!Identifiers.containsValue(false)) {
                    return; // all keys found
                }
            }
        }
    }

    private void reorganiseTextDetected(Text.Line element) {

        /*
        text detected are not by rows but column appended to next column of data
        reorganise is to find out which are the data that are within the same row
         */
        Point[] points = element.getCornerPoints();

        //between two rect, rectA and rectB, get the mid point of rectB point[0].y and point[3].y
        //they belong to the same row if the mid point of B falls within rectA point[0].y and point[3].y
        //point[0] (x,y)            point[1](x,y)
        //point[3] (x,y)            point[2](x,y)

        int elementCalPt = points[3].y;
        int height = 0;
        if (points != null) {
            height = points[3].y - points[0].y;
            elementCalPt -= height / 2;
        }

        //compare with identified lines
        for (LineItem item : organisedText) {
            Point[] pt = item.getCornerPoints();
            if (elementCalPt >= pt[0].y && elementCalPt <= pt[3].y) {
                //line found - save all in lowercase for easier comparison
                item.appendString(" " + element.getText().toLowerCase());
                return;
            }
        }
        //if not found at all, form new line
        LineItem newLine = new LineItem(element.getCornerPoints(), element.getText().toLowerCase());
        organisedText.add(newLine);

    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        // refresh Singleton
        requireActivity().runOnUiThread(()-> getAllFood.execute());
    }

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}


    class LineItem {
        private Point[] cornerPoints;
        private String text;

        public LineItem() {
            cornerPoints = new Point[4];
            text = "";
        }

        public LineItem(Point[] points, String s) {
            cornerPoints = points;
            text = s;
        }

        public void appendString(String s) {
            text += s;
        }

        public String getText() {
            return text;
        }

        public void setCornerPoints(Point[] points) {
            cornerPoints = points;
        }

        public Point[] getCornerPoints() {
            return cornerPoints;
        }
    }
}
