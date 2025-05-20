package com.example.labselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import androidx.activity.result.ActivityResultCallback;
import android.content.ContentResolver;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri imageUri = null;

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    imageView.setImageBitmap(imageBitmap);

                    try {
                        File imageFile = new File(getCacheDir(), "selfie.jpg");
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                        imageUri = Uri.fromFile(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
    ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        imageView.setImageURI(uri);
                        imageUri = uri;
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        Button btnTakeSelfie = findViewById(R.id.btnTakeSelfie);
        Button btnSendEmail = findViewById(R.id.btnSendEmail);

        btnTakeSelfie.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(takePictureIntent);
        });

        Button btnChooseFromGallery = findViewById(R.id.btnChooseFromGallery);
        btnChooseFromGallery.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        btnSendEmail.setOnClickListener(v -> {
            if (imageUri != null) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("application/image");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hodovychenko@op.edu.ua"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ANDROID Stepanova Alisa");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Додано селфі. Репозиторій: https://github.com/vevadesa/labselfie");
                emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(emailIntent, "Надіслати селфі..."));
            }
        });
    }
}
