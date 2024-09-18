package com.example.activity_information;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register2 extends AppCompatActivity {

    RadioButton rdb_Male;
    RadioButton rdb_Female;
    RadioButton rdb_Others;
    Button registerBtn;
    EditText editbirthdate;
    ImageButton btnBirthday;
    FirebaseAuth mAuth;
    Spinner provinceSpinner;
    Dialog loaderDialog;

    TextView textView;


    private void showDatePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(Register2.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Format date as "dd/MM/yyyy"
                editText.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
            }
        }, year, month, day);
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register2);
        mAuth = FirebaseAuth.getInstance();

        // Apply insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Retrieve data passed from Register activity
        String userName = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String phoneNum = getIntent().getStringExtra("phoneNum");

        // Initialize your views here (outside of the insets listener)
        rdb_Male = findViewById(R.id.rdbMale);
        rdb_Female = findViewById(R.id.rdbFemale);
        rdb_Others = findViewById(R.id.rdbOthers);
        registerBtn = findViewById(R.id.button_next);
        editbirthdate = findViewById(R.id.birthdate);
        btnBirthday = findViewById(R.id.btnBirthdate);
        provinceSpinner = findViewById(R.id.province_spinner);
        textView=findViewById(R.id.already_account);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });


        //province spinner
        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(this,
                R.array.philippines_provinces, android.R.layout.simple_spinner_item);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);


        //birthdate
        btnBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(editbirthdate);
            }
        });

        editbirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(editbirthdate);
            }
        });

        // Set up the click listener for the register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                showLoadingDialog();

                final String selectedGender;

                if (rdb_Male.isChecked()) {
                    selectedGender = rdb_Male.getText().toString();
                } else if (rdb_Female.isChecked()) {
                    selectedGender = rdb_Female.getText().toString();
                } else if (rdb_Others.isChecked()) {
                    selectedGender = rdb_Others.getText().toString();
                } else {
                    selectedGender = ""; // Handle case where no gender is selected
                }

                String birthdate = editbirthdate.getText().toString();
                String province = provinceSpinner.getSelectedItem().toString();
                String interests = ((EditText) findViewById(R.id.editTextText)).getText().toString();

                // Validate inputs
                if (TextUtils.isEmpty(province)) {
                    Toast.makeText(Register2.this, "Please select a province", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(selectedGender)) {
                    Toast.makeText(Register2.this, "Please select a gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(interests)) {
                    Toast.makeText(Register2.this, "Please enter your interests", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(birthdate)) {
                    Toast.makeText(Register2.this, "Please select a birthdate", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registration success
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();
                                        User user = new User(userId, userName, email, phoneNum, selectedGender, birthdate, province, interests);
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                                        // Save user details
                                        usersRef.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Show success message
                                                    Toast.makeText(Register2.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    // Handle failure of saving user data in Realtime Database
                                                    Toast.makeText(Register2.this, "Failed to save user data to database.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    // Handle authentication failure
                                    String errorMsg = "Authentication failed.";
                                    if (task.getException() != null) {
                                        errorMsg += " Reason: " + task.getException().getMessage();
                                    }

                                    // Log the reason for authentication failure
                                    Log.e("Register2", errorMsg);
                                    Toast.makeText(Register2.this, errorMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });

    }

    private void saveData(){

    }


    private void showLoadingDialog(){
        loaderDialog = new Dialog(Register2.this);
        View loaderViewer = getLayoutInflater().inflate(R.layout.loader, null);
        loaderDialog.setContentView(loaderViewer);
        loaderDialog.setCancelable(false);
        loaderDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loaderDialog.show();
    }

    private void hideLoadingDialog(){
        if(loaderDialog != null && loaderDialog.isShowing()){
            loaderDialog.dismiss();
        }
    }
}