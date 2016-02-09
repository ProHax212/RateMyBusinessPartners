package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import seniordesign.ratemybusinesspartners.models.User;

public class Login extends AppCompatActivity implements View.OnClickListener {
    String username, password;
    Button login;
    TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //EditText username, password;
        Button login;
        TextView register;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = findViewById(R.id.login_editText_username).toString();
        password = findViewById(R.id.login_editText_password).toString();

        login = (Button) findViewById((R.id.login_button_login));
        login.setOnClickListener(this);

        register = (TextView) findViewById((R.id.login_textView_register));
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_button_login:
                Intent login_intent = new Intent(this, CompanyProfile.class);
                login_intent.putExtra(User.LOGIN_USERNAME, username);
                login_intent.putExtra(User.LOGIN_PASSWORD, password);
                startActivity(login_intent);
            case R.id.login_textView_register:
                startActivity(new Intent(this, Register.class));
        }
    }
}
