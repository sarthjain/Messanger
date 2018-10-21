package com.example.risha.first.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.risha.first.ui.SignUp.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.risha.first.R;
import com.example.risha.first.data.StaticConfig;
import com.hbb20.CountryCodePicker;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    @BindView(R.id.Number)
    EditText number;

    @BindView(R.id.otp_view)
    OtpView otpView;

    @BindView(R.id.trydiffnumber)
    TextView diffNumber;

    @BindView(R.id.phoneLayout)
    RelativeLayout phoneLayout;

    @BindView(R.id.OtpLayout)
    RelativeLayout otpLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        ccp.registerCarrierNumberEditText(number);

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override public void onOtpCompleted(String otp) {
                //verifying the code
                verifyVerificationCode(otpView.getText().toString());
            }
        });

        diffNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneLayout.setVisibility(View.VISIBLE);
                otpLayout.setVisibility(View.GONE);
                number.setText("",null);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null && otpLayout.getVisibility()==View.VISIBLE) {
                    otpView.setText(code);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                System.out.print(e);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
            }
        };
        if(mAuth.getCurrentUser()!=null){
            StaticConfig.UID = mAuth.getCurrentUser().getUid();
            Intent intent = new Intent(LoginActivity.this, UserDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(LoginActivity.this, UserDetails.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            final Snackbar snackbar = Snackbar.make(number, message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    public void validateOtp(View v){
        verifyVerificationCode(otpView.getText().toString());
    }

    public void gotoOtpPage(View v){
        if(ccp.isValidFullNumber()) {
            otpLayout.setVisibility(View.VISIBLE);
            phoneLayout.setVisibility(View.GONE);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    ccp.getFormattedFullNumber(),
                    60,
                    TimeUnit.SECONDS,
                    TaskExecutors.MAIN_THREAD,
                    mCallbacks);
        }
        else{
            final Snackbar snackbar = Snackbar.make(number, "Invalid Number", Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

}








//public class LoginActivity extends AppCompatActivity {
//    private static String TAG = "LoginActivity";
//    FloatingActionButton fab;
//    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
//            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
//    private EditText editTextUsername, editTextPassword;
//    private LovelyProgressDialog waitingDialog;
//
//    private AuthUtils authUtils;
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    private FirebaseUser user;
//    private boolean firstTimeAccess;
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        editTextUsername = (EditText) findViewById(R.id.et_username);
//        editTextPassword = (EditText) findViewById(R.id.et_password);
//        firstTimeAccess = true;
//        initFirebase();
//    }
//
//
//    /**
//     * Khởi tạo các thành phần cần thiết cho việc quản lý đăng nhập
//     */
//    private void initFirebase() {
//        //Khoi tao thanh phan de dang nhap, dang ky
//        mAuth = FirebaseAuth.getInstance();
//        authUtils = new AuthUtils();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    StaticConfig.UID = user.getUid();
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    if (firstTimeAccess) {
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        LoginActivity.this.finish();
//                    }
//                } else {
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                firstTimeAccess = false;
//            }
//        };
//
//        //Khoi tao dialog waiting khi dang nhap
//        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
//
//    public void clickRegisterLayout(View view) {
//        getWindow().setExitTransition(null);
//        getWindow().setEnterTransition(null);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ActivityOptions options =
//                    ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
//            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER, options.toBundle());
//        } else {
//            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == StaticConfig.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
//            authUtils.createUser(data.getStringExtra(StaticConfig.STR_EXTRA_USERNAME), data.getStringExtra(StaticConfig.STR_EXTRA_PASSWORD));
//        }
//    }
//
//    public void clickLogin(View view) {
//        String username = editTextUsername.getText().toString();
//        String password = editTextPassword.getText().toString();
//        if (validate(username, password)) {
//            authUtils.signIn(username, password);
//        } else {
//            Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        setResult(RESULT_CANCELED, null);
//        finish();
//    }
//
//    private boolean validate(String emailStr, String password) {
//        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
//        return (password.length() > 0 || password.equals(";")) && matcher.find();
//    }
//
//    public void clickResetPassword(View view) {
//        String username = editTextUsername.getText().toString();
//        if (validate(username, ";")) {
//            authUtils.resetPassword(username);
//        } else {
//            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Dinh nghia cac ham tien ich cho quas trinhf dang nhap, dang ky,...
//     */
//    class AuthUtils {
//        /**
//         * Action register
//         *
//         * @param email
//         * @param password
//         */
//        void createUser(String email, String password) {
//            waitingDialog.setIcon(R.drawable.ic_add_friend)
//                    .setTitle("Registering....")
//                    .setTopColorRes(R.color.colorPrimary)
//                    .show();
//            mAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//                            waitingDialog.dismiss();
//                            // If sign in fails, display a message to the user. If sign in succeeds
//                            // the auth state listener will be notified and logic to handle the
//                            // signed in user can be handled in the listener.
//                            if (!task.isSuccessful()) {
//                                new LovelyInfoDialog(LoginActivity.this) {
//                                    @Override
//                                    public LovelyInfoDialog setConfirmButtonText(String text) {
//                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                dismiss();
//                                            }
//                                        });
//                                        return super.setConfirmButtonText(text);
//                                    }
//                                }
//                                        .setTopColorRes(R.color.colorAccent)
//                                        .setIcon(R.drawable.ic_add_friend)
//                                        .setTitle("Register false")
//                                        .setMessage("Email exist or weak password!")
//                                        .setConfirmButtonText("ok")
//                                        .setCancelable(false)
//                                        .show();
//                            } else {
//                                initNewUserInfo();
//                                Toast.makeText(LoginActivity.this, "Register and Login success", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                LoginActivity.this.finish();
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            waitingDialog.dismiss();
//                        }
//                    })
//            ;
//        }
//
//
//        /**
//         * Action Login
//         *
//         * @param email
//         * @param password
//         */
//        void signIn(String email, String password) {
//            waitingDialog.setIcon(R.drawable.ic_person_low)
//                    .setTitle("Login....")
//                    .setTopColorRes(R.color.colorPrimary)
//                    .show();
//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//                            // If sign in fails, display a message to the user. If sign in succeeds
//                            // the auth state listener will be notified and logic to handle the
//                            // signed in user can be handled in the listener.
//                            waitingDialog.dismiss();
//                            if (!task.isSuccessful()) {
//                                Log.w(TAG, "signInWithEmail:failed", task.getException());
//                                new LovelyInfoDialog(LoginActivity.this) {
//                                    @Override
//                                    public LovelyInfoDialog setConfirmButtonText(String text) {
//                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                dismiss();
//                                            }
//                                        });
//                                        return super.setConfirmButtonText(text);
//                                    }
//                                }
//                                        .setTopColorRes(R.color.colorAccent)
//                                        .setIcon(R.drawable.ic_person_low)
//                                        .setTitle("Login false")
//                                        .setMessage("Email not exist or wrong password!")
//                                        .setCancelable(false)
//                                        .setConfirmButtonText("Ok")
//                                        .show();
//                            } else {
//                                saveUserInfo();
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                LoginActivity.this.finish();
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            waitingDialog.dismiss();
//                        }
//                    });
//        }
//
//        /**
//         * Action reset password
//         *
//         * @param email
//         */
//        void resetPassword(final String email) {
//            mAuth.sendPasswordResetEmail(email)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            new LovelyInfoDialog(LoginActivity.this) {
//                                @Override
//                                public LovelyInfoDialog setConfirmButtonText(String text) {
//                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            dismiss();
//                                        }
//                                    });
//                                    return super.setConfirmButtonText(text);
//                                }
//                            }
//                                    .setTopColorRes(R.color.colorPrimary)
//                                    .setIcon(R.drawable.ic_pass_reset)
//                                    .setTitle("Password Recovery")
//                                    .setMessage("Sent email to " + email)
//                                    .setConfirmButtonText("Ok")
//                                    .show();
//                        }
//                    })
//            .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    new LovelyInfoDialog(LoginActivity.this) {
//                        @Override
//                        public LovelyInfoDialog setConfirmButtonText(String text) {
//                            findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    dismiss();
//                                }
//                            });
//                            return super.setConfirmButtonText(text);
//                        }
//                    }
//                            .setTopColorRes(R.color.colorAccent)
//                            .setIcon(R.drawable.ic_pass_reset)
//                            .setTitle("False")
//                            .setMessage("False to sent email to " + email)
//                            .setConfirmButtonText("Ok")
//                            .show();
//                }
//            });
//        }
//
//        /**
//         * Luu thong tin user info cho nguoi dung dang nhap
//         */
//        void saveUserInfo() {
//            FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    waitingDialog.dismiss();
//                    HashMap hashUser = (HashMap) dataSnapshot.getValue();
//                    User userInfo = new User();
//                    userInfo.name = (String) hashUser.get("name");
//                    userInfo.email = (String) hashUser.get("email");
//                    userInfo.avata = (String) hashUser.get("avata");
//                    SharedPreferenceHelper.getInstance(LoginActivity.this).saveUserInfo(userInfo);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//        /**
//         * Khoi tao thong tin mac dinh cho tai khoan moi
//         */
//        void initNewUserInfo() {
//            User newUser = new User();
//            newUser.email = user.getEmail();
//            newUser.name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
//            newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
//            FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
//        }
//    }
//}
