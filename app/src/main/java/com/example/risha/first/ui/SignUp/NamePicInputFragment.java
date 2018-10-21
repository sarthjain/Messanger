package com.example.risha.first.ui.SignUp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.risha.first.BuildConfig;
import com.example.risha.first.R;
import com.example.risha.first.ui.PhotoDialog;
import com.example.risha.first.util.ImagePreview;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class NamePicInputFragment extends Fragment {

    private Button next;
    private TextInputEditText usernameEditText;
    private CircleImageView profilePicture;
    private AppCompatImageView profilePicEdit;
    private FragmentListener listener;
    private static final int PERMS_REQUEST_CODE = 140;
    private String[] Permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA };
    PhotoDialog photoDialog;
    Context context;
    private static final int GALLERY = 2;
    private static final int CAMERA = 1;
    private static final int PREVIEW = 390;
    private Uri uri;

    public NamePicInputFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name_pic_input, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        if(context instanceof FragmentListener){
            listener =(FragmentListener)context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        next = (Button)view.findViewById(R.id.Next);
        usernameEditText = (TextInputEditText) view.findViewById(R.id.userNameEditText);
        profilePicture = (CircleImageView)view.findViewById(R.id.profilePic);
        profilePicEdit = (AppCompatImageView)view.findViewById(R.id.profilePicEdit);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getUserDetailsModel().name=usernameEditText.getText().toString().trim();
                if(uri!=null)
                    listener.getUserDetailsModel().avata=uri.toString();
                listener.moveToNext();
            }
        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0)
                    next.setEnabled(true);
                else
                    next.setEnabled(false);
            }
        });


        profilePicEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (( ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ))
                {


                    photoDialog=new PhotoDialog(context);
                    photoDialog.setOnCameraClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClickImageFromCamera();
                        }
                    });
                    photoDialog.setOnGalleryClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetImageFromGallery();
                        }
                    });
                    photoDialog.show();
                }
                else
                {
                    requestPermissions(Permissions, PERMS_REQUEST_CODE);
                }
            }
        });
    }


    public void ClickImageFromCamera()
    {
        Intent CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri= FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider",file);
        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        CamIntent.putExtra("return-data", false);
        CamIntent.putExtra("outputX", 960);
        CamIntent.putExtra("outputY", 540);
        CamIntent.putExtra("aspectX", 16);
        CamIntent.putExtra("aspectY", 9);
        CamIntent.putExtra("scale", true);
        startActivityForResult(CamIntent, CAMERA);
    }

    public void GetImageFromGallery()
    {
        Intent GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CAMERA:
                    ImageCropFunction();
                    break;
                case GALLERY:
                    if (data != null)
                    {
                        uri = data.getData();
                        ImageCropFunction();
                    }
                    break;
                case PREVIEW:
                    Uri resultUri = Uri.parse(data.getStringExtra("Uri"));
                        uri = resultUri;
                        profilePicture.setImageURI(resultUri);
                        Glide.with(this)
                                .fromUri()
                                .load(Uri.parse(resultUri.toString()))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.user)
                                .centerCrop()
                                .dontAnimate()
                                .into(profilePicture);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void ImageCropFunction()
    {
        Intent intent = new Intent(getContext(), ImagePreview.class);
        intent.putExtra("ImageUri", uri.toString());
            String newProfilePic = "Profile" + String.valueOf(System.currentTimeMillis()) + ".png";
            intent.putExtra("AspectX", 1);
            intent.putExtra("AspectY", 1);
            intent.putExtra("MinX", 200);
            intent.putExtra("MinY", 200);
            intent.putExtra("Output", Uri.fromFile(new File(context.getFilesDir(), newProfilePic)).toString());
        startActivityForResult(intent, PREVIEW);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int x: grantResults) {
            if (x != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
        }
        if (requestCode == PERMS_REQUEST_CODE)
        {
                profilePicEdit.performClick();
        }
    }

}
