package com.example.hsai01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The {@code MainActivity} class serves as the main entry point for the application.
 * It handles user interactions, such as taking a photo, and communicates with the
 * {@link GeminiManager} to process the photo and display the results.
 *
 * <p>
 *     <b>Remarks:</b>
 *     <ul>
 *         <li>This activity uses the camera to capture images.</li>
 *         <li>It interacts with the {@link GeminiManager} to send prompts and images to the Gemini AI model.</li>
 *         <li>The results from the Gemini AI model are displayed in a {@link TextView}.</li>
 *         <li>A {@link ProgressDialog} is used to indicate that the image is being processed.</li>
 *         <li>The activity uses {@link Toast} to display messages to the user.</li>
 *         <li>The activity uses {@link Log} to display messages in the logcat.</li>
 *     </ul>
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    TextView tV;
    GeminiManager geminiManager;
    private static final int REQUEST_STAMP_CAPTURE = 201;

    /**
     * Called when the activity is first created.
     * Initializes the layout, {@link TextView}, and {@link GeminiManager}.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tV = findViewById(R.id.tV);
        geminiManager = GeminiManager.getInstance();
    }

    /**
     * Called when the user clicks the "take photo" button.
     * Launches the camera to capture an image.
     *
     * @param view The view that was clicked.
     */
    public void takePhoto(View view) {
        Intent takePicIntent = new Intent();
        takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicIntent, REQUEST_STAMP_CAPTURE);
        }
    }

    /**
     * Processes the selected image in Gemini AI.
     *
     * <p>
     *     <b>Remarks:</b>
     *     <ul>
     *         <li>This method is called when an activity that was started with {@link #startActivityForResult} exits.</li>
     *         <li>It checks if the result code is {@link Activity#RESULT_OK} and if there is data in the intent.</li>
     *         <li>It extracts the image from the intent and sends it to the {@link GeminiManager} for processing.</li>
     *         <li>It displays a {@link ProgressDialog} while the image is being processed.</li>
     *         <li>It displays the result from the {@link GeminiManager} in the {@link TextView}.</li>
     *         <li>It displays an error message in the {@link TextView} if an error occurs.</li>
     *         <li>It uses {@link Log} to display messages in the logcat.</li>
     *     </ul>
     * </p>
     *
     * @param requestCode The call sign of the intent that requested the result.
     * @param resultCode  A code that symbolizes the status of the result of the activity.
     * @param data_back   The data returned.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);
        ProgressDialog pD = new ProgressDialog(this);
        pD.setMessage("Proccessing photo...");
        pD.show();
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data_back.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                String prompt = "כתוב לי מהו הפרי או הירק שצולם ובנוסף תן לי מתכון אשר כולל אותו.\n" +
                        "אם אתה לא מוצא פרי או ירק בתמונה תן לי הנחיה לצלם את התמונה מחדש כך שהפרי או הירק יופיע בבירור בתמונה.";
                Toast.makeText(this, "Data OK", Toast.LENGTH_SHORT).show();
                geminiManager.sendMessageWithPhoto(prompt, imageBitmap, new GeminiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        pD.dismiss();
                        tV.setText(result);
                        Log.i("MainActivity", "Success: " + result);
                    }

                    @Override
                    public void onError(Throwable error) {
                        pD.dismiss();
                        tV.setText("Error: " + error.getMessage());
                        Log.i("MainActivity", "Error: " + error.getMessage());
                    }
                });
            }
        }
    }
}