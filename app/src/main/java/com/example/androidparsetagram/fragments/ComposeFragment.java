package com.example.androidparsetagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidparsetagram.MainActivity;
import com.example.androidparsetagram.Post;
import com.example.androidparsetagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;//Might be incorrect

public class ComposeFragment extends Fragment {
    public static final String TAG = "ComposeFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    private boolean isSaving = false;

    //Set up layout file; created when fragment is called to create view object heiarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    //Called after onCreateView (the method above); view setup occurs here (where onCreate is)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //findViewById does not work here due to Fragment extension; first call "view."
        etDescription = view.findViewById(R.id.etDescription);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        ivPostImage.setImageResource(R.drawable.ic_image_placeholder_foreground);

        //queryPosts();
        //Start if
        //if (!isSaving) {
            btnCaptureImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchCamera();//Implicit intent
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String description = etDescription.getText().toString();
                    if (description.isEmpty()) {
                        //Cannot do "Class.this"; use "getContext()"
                        Toast.makeText(/*MainActivity.this*/ getContext(),
                                "Description cannot be emoty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!photoFile.exists() || photoFile == null
                            || ivPostImage.getDrawable() == null) {
                        Toast.makeText(getContext(),
                                "There is no image!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!isSaving) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        isSaving = true;
                        Log.e(TAG, "isSaving: " + isSaving);
                        savePost(description, currentUser, photoFile);
                    } else
                        Toast.makeText(getContext(), "Please wait until post is posted!", Toast.LENGTH_SHORT).show();
                }
            });
        //} else {
        //
        //}
        //End if

    }

    private void launchCamera() {
        //Passes to phone media
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(),
                "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        //getPackageManager called on context, required to getContext()
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /*
    Called after returning from child activity
    */
    //Previously protected
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //Get the image
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            //Mat require Bitmap resize
            ivPostImage.setImageBitmap(takenImage);
        } else {
            //All "this." must be "getContext()"
            Toast.makeText(getContext(),
                    "Picture could not be taken!", Toast.LENGTH_SHORT).show();
        }
    }

    //Uniform resource identifier - String that identifies a resource
    public File getPhotoFileUri (String fileName) {
        //Obtain safe storage directory for photos; requires getContext()
        File mediaStorageDir =
                new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        //Create storage directory if one doesn't exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        //Return file target for photo based on file name
        /*File file = */ return new File (mediaStorageDir.getPath()
                + File.separator + fileName);

        //return file;
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Toast.makeText(getContext(), "Saving post...", Toast.LENGTH_SHORT).show();

        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);
        post.setImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving post: ", e);
                    Toast.makeText(getContext(),
                            "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post successfully saved!");
                Toast.makeText(getContext(), "Post successfully saved!", Toast.LENGTH_SHORT).show();
                etDescription.setText("");//Clears the description of previous input
                ivPostImage.setImageResource(R.drawable.ic_image_placeholder_foreground);//"0" represents blank resource
                isSaving = false;
            }
        });
    }

    /*
    Retrieve posts
     */

}