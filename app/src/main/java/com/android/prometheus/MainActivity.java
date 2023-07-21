package com.android.prometheus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private TextView date, first_name, last_name, email, password, date_of_birth, details_output;
    private RadioGroup gender_group;
    private RadioButton gender_button;
    private Button clear;

    DatabaseHandler db = new DatabaseHandler(this);

    InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; ++i)
        {
            if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz]*").matcher(String.valueOf(source.charAt(i))).matches())
            {
                return "";
            }
        }

        return null;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gender_group = findViewById(R.id.genderGroup);
        clear = findViewById(R.id.clear);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        date_of_birth = findViewById(R.id.date_of_birth);
        password = findViewById(R.id.password);
        details_output = findViewById(R.id.details_output);

        first_name.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(24)});
        last_name.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(24)});
        password.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(24)});
        details_output.setText("");
        details_output.setVisibility(View.GONE);
        clear.setVisibility(View.GONE);

        Button b = findViewById(R.id.register);
        b.setOnClickListener(v -> {

            int selected_gender = gender_group.getCheckedRadioButtonId();
            gender_button = findViewById(selected_gender);

            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            String datePattern = "^(1[0-2]|0[1-9])/(3[01]" + "|[12][0-9]|0[1-9])/[0-9]{4}$";


            if (first_name.getText().toString().equals("")) {
                first_name.setError("Empty first name");
            }  else if (last_name.getText().toString().equals("")) {
                last_name.setError("Empty last name");
            } else if (email.getText().toString().equals("")) {
                email.setError("Empty email address");
            } else if (!email.getText().toString().trim().matches(emailPattern)) {
                email.setError("Invalid email address");
            } else if (date_of_birth.getText().toString().equals("")) {
                date_of_birth.setError("Select date of birth");
            } else if (!date_of_birth.getText().toString().trim().matches(datePattern)) {
                date_of_birth.setError("Date format should be 'mm/dd/yyyy'");
            } else if (password.getText().toString().equals("")) {
                password.setError("Empty password");
            } else {

                if (first_name.getError() == null && last_name.getError() == null && email.getError() == null &&
                        date_of_birth.getError() == null && password.getError() == null) {
                    addUser(new User(first_name.getText().toString(), last_name.getText().toString(),
                            gender_button.getText().toString(), email.getText().toString(), date_of_birth.getText().toString(),
                            password.getText().toString()));
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    showData();
                }
                first_name.setText("");
                last_name.setText("");
                email.setText("");
                date_of_birth.setText("");
                password.setText("");
            }
        });
    }


    public void addUser(User newUser) {
        boolean insertUser = db.addUser(newUser);
        if(insertUser)
            toastMessage("Registration Successful!");
        else
            toastMessage("Something went wrong, please try again");
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showData() {
        details_output.setMovementMethod(new ScrollingMovementMethod());
        List<User> users_data = db.getAllUsers();
        String output = "";
        for (User u : users_data) {
            output = "Id: " + u.getId() + "\n"
                    + "First name: " + u.getFirst_name() + "\n"
                    + "Last name: " + u.getLast_name() + "\n"
                    + "Gender: " + u.getGender() + "\n"
                    + "Email: " + u.getEmail() + "\n"
                    + "DOB: " + u.getDate_of_birth() + "\n"
                    + "Registration Date & Time: " + "\n";
        }
        details_output.setText(output);
        details_output.setVisibility(View.VISIBLE);
        clear.setVisibility(View.VISIBLE);
        clear.setOnClickListener(v -> {
            details_output.setText("");
            details_output.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
        });
    }
}
