package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import seniordesign.ratemybusinesspartners.models.User;

public class Register extends AppCompatActivity implements View.OnClickListener {
    String firstname, lastname, email, company, username, password, confirmpassword;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firstname = findViewById(R.id.register_editText_firstName).toString();
        lastname = findViewById(R.id.register_editText_lastName).toString();
        email = findViewById(R.id.register_editText_email).toString();
        company = findViewById(R.id.register_editText_company).toString();
        username = findViewById(R.id.register_editText_username).toString();
        password = findViewById(R.id.register_editText_password).toString();
        confirmpassword = findViewById(R.id.register_editText_confirmPassword).toString();

        register = (Button) findViewById(R.id.register_button_register);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.register_button_register:
                Intent intent = new Intent(this, CompanyProfile.class);
                User new_user = new User(firstname, lastname, email, company, username, password, confirmpassword);
                intent.putExtra("newUser", new_user);
                startActivity(intent);
                break;
        }
    }
}
