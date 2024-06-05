package com.example.mapapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class MarkerActivity extends AppCompatActivity {
    private TextView name;
    private EditText editTextTitle, editTextMessage, editTextName;
    private ImageView uploadedImage;
    private ProgressBar uploadProgressBar;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> resultLauncher;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_marker_acitvity);

        name = findViewById(R.id.name);
        editTextTitle = findViewById(R.id.editText);
        editTextMessage = findViewById(R.id.editTextMessage);
        editTextName = findViewById(R.id.editTextName);
        uploadedImage = findViewById(R.id.UploadedImage);
        ImageButton buttonAttach = findViewById(R.id.buttonAttach);
        uploadProgressBar = findViewById(R.id.UploadProgressBar);
        Button buttonDone = findViewById(R.id.buttonDone);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        databaseReference = FirebaseDatabase.getInstance().getReference("Markers");

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == MarkerActivity.RESULT_OK && result.getData() != null) {
                            imageUri = result.getData().getData();
                            uploadedImage.setImageURI(imageUri);
                        }
                    }
                });

        buttonAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                resultLauncher.launch(intent);
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                String id = UUID.randomUUID().toString();
                String title = editTextTitle.getText().toString().trim();
                String message = editTextMessage.getText().toString().trim();
                String nameText = editTextName.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    editTextTitle.setError("Требуется название");
                    isValid = false;
                }
                if (TextUtils.isEmpty(message)) {
                    editTextMessage.setError("Требуется текст сообщения");
                    isValid = false;
                }

                if (TextUtils.isEmpty(nameText)) {
                    editTextName.setError("Требуется имя автора");
                    isValid = false;
                }

                if (isValid && imageUri != null) {
                    uploadProgressBar.setVisibility(View.VISIBLE);
                    buttonAttach.setEnabled(false);
                    buttonCancel.setEnabled(false);
                    buttonDone.setEnabled(false);

                    StorageReference storageReference = storage.getReference().child("images/" + UUID.randomUUID().toString());
                    storageReference.putFile(imageUri).continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String imageURL = downloadUri.toString();
                            double latitude = getIntent().getDoubleExtra("Latitude", 0.0);
                            double longitude = getIntent().getDoubleExtra("Longitude", 0.0);

                            MarkerData markerData = new MarkerData(id, title, message, nameText, imageURL, latitude, longitude);
                            databaseReference.child(id).setValue(markerData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    uploadProgressBar.setVisibility(View.GONE);
                                    buttonAttach.setEnabled(true);
                                    buttonCancel.setEnabled(true);
                                    buttonDone.setEnabled(true);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MarkerActivity.this, "Загрузка завершена", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MarkerActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            uploadProgressBar.setVisibility(View.GONE);
                            buttonAttach.setEnabled(true);
                            buttonCancel.setEnabled(true);
                            buttonDone.setEnabled(true);
                            Toast.makeText(MarkerActivity.this, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }});
    }
}