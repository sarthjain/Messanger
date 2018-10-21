package com.example.risha.first.ui.SignUp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.risha.first.MainActivity;
import com.example.risha.first.R;
import com.example.risha.first.data.SharedPreferenceHelper;
import com.example.risha.first.data.StaticConfig;
import com.example.risha.first.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserDetails extends AppCompatActivity implements FragmentListener{

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private int currentFragment;
    private User userDetailsModel;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mProfilePicStorageReference;
    private FirebaseUser currentUser;

    Class[] FragmentList={
            NamePicInputFragment.class,
            GenderBdayInputFragment.class
    };

    public Fragment createFragment(int Index)
    {
        try
        {
            Fragment fragment = (Fragment)FragmentList[Index].newInstance();
            return fragment;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        currentFragment=0;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = mFirebaseDatabase.getReference("user").child(currentUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.progreesBar).setVisibility(View.GONE);
                findViewById(R.id.frame).setVisibility(View.VISIBLE);
                userDetailsModel = dataSnapshot.getValue(User.class);
                if(userDetailsModel!=null){
                    SharedPreferenceHelper.getInstance(UserDetails.this).saveUserInfo(userDetailsModel);
                    Intent i = new Intent(UserDetails.this,MainActivity.class);
                    i.putExtra("userdetails",userDetailsModel);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else{
                    userDetailsModel = new User();
                    userDetailsModel.number=mFirebaseAuth.getCurrentUser().getPhoneNumber();
                    getSupportFragmentManager().popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, createFragment(currentFragment))
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserDetails.this,"Not able to retreive data!!",Toast.LENGTH_SHORT).show();
                Log.w("UserDetailsActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void moveToNext()
    {

        if(currentFragment+1>=FragmentList.length)
        {

            findViewById(R.id.progreesBar).setVisibility(View.VISIBLE);
            findViewById(R.id.frame).setVisibility(View.GONE);
            mDatabaseReference = mFirebaseDatabase.getReference("user");
            if(!userDetailsModel.avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                mProfilePicStorageReference = mFirebaseStorage.getReference("images/profile_pics/"+userDetailsModel.name);
                UploadTask uploadTask = mProfilePicStorageReference.putFile(Uri.parse(userDetailsModel.avata));

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return mProfilePicStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            userDetailsModel.avata=downloadUri.toString();
                            addDetailsToFirebase();
                        } else {
                            // Handle failures
                            Toast.makeText(UserDetails.this, "Pic Not Uploaded! Try again later", Toast.LENGTH_LONG).show();
                            userDetailsModel.avata=StaticConfig.STR_DEFAULT_BASE64;
                            addDetailsToFirebase();
                        }
                    }
                });
            }else {
                addDetailsToFirebase();
            }
            return;
        }

        currentFragment++;


        Fragment fragment=createFragment(currentFragment);

        if(fragment==null)
            return;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public User getUserDetailsModel() {
        return userDetailsModel;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            currentFragment--;
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void addDetailsToFirebase(){
        mDatabaseReference.child(currentUser.getUid()).setValue(userDetailsModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SharedPreferenceHelper.getInstance(UserDetails.this).saveUserInfo(userDetailsModel);
                        Intent intent = new Intent(UserDetails.this, MainActivity.class);
                        intent.putExtra("userdetails", userDetailsModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findViewById(R.id.progreesBar).setVisibility(View.GONE);
                        findViewById(R.id.frame).setVisibility(View.VISIBLE);
                        Toast.makeText(UserDetails.this,"Try Again Later !",Toast.LENGTH_LONG).show();
                    }
                });
    }
}
