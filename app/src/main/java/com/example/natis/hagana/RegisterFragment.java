package com.example.natis.hagana;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;

import com.example.natis.hagana.Model.ClientUser;

public class RegisterFragment extends Fragment {
    EditText fNameET;
    EditText lNameET;
    EditText emailET;
    EditText passwordET;


    TableLayout tableGender;

    //buttons
    Button verifyEmailBtn;
    Button registerBtn;

    RadioButton female;
    RadioButton male;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public interface Delegate{
        void onRegisterButtonClick(ClientUser user);
        void onVerifyEmailClick(ClientUser user);
    }

    Delegate delegate;
    public void setDelegate(Delegate dlg){
        this.delegate = dlg;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_register, container, false);
        fNameET=(EditText)view.findViewById(R.id.fragment_register_fName_editText);
        lNameET=(EditText)view.findViewById(R.id.fragment_register_lName_editText);
        emailET=(EditText)view.findViewById(R.id.fragment_register_email_editText);
        passwordET=(EditText)view.findViewById(R.id.fragment_register_password_editText);
        verifyEmailBtn=(Button)view.findViewById(R.id.fragment_register_verifyEmail_btn);
        registerBtn=(Button)view.findViewById(R.id.fragment_register_btn);
        verifyEmailBtn.setEnabled(false);
        tableGender= (TableLayout) view.findViewById(R.id.fragment_register_table_gender);
        tableGender.setVisibility(View.VISIBLE);
        female = (RadioButton) view.findViewById(R.id.fragment_register_female);
        male=(RadioButton) view.findViewById(R.id.fragment_register_male);

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(male.isChecked()) {
                    male.setChecked(false);
                }
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(female.isChecked()) {
                    female.setChecked(false);
                }
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateForm())
                {
                    return;
                }
                delegate.onRegisterButtonClick(setUserDetails());



            }
        });
        verifyEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateForm())
                {
                    return;
                }
                delegate.onVerifyEmailClick(setUserDetails());

            }
        });
        return view;
    }

    public ClientUser setUserDetails()
    {
        ClientUser user = new ClientUser();
        user.setfirstName(fNameET.getText().toString());
        user.setlastName(lNameET.getText().toString());
        user.setEmail(emailET.getText().toString());
        user.setPassword(passwordET.getText().toString());
        user.setNotes(" ");
        if (female.isChecked())
            user.setGender("Female");
        else if(male.isChecked())
            user.setGender("Male");
        else {
            Log.d("TAG","no one is checked");
        }
        user.setImageUrl("0"); //for empty pic
        return user;
    }

    public boolean validateForm()
    {
        boolean valid = true;

        //checking first name field
        String fName = fNameET.getText().toString();
        if (TextUtils.isEmpty(fName)) {
            fNameET.setError("Required.");
            valid = false;
        } else {
            fNameET.setError(null);
        }

        //checking last name field
        String lName = lNameET.getText().toString();
        if (TextUtils.isEmpty(lName)) {
            lNameET.setError("Required.");
            valid = false;
        } else {
            lNameET.setError(null);
        }


        //checking email field
        String email = emailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailET.setError("Required.");
            valid = false;
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Invalid Email");
            valid = false;
        }
        else {
            emailET.setError(null);
        }

        //checking password field
        String password = passwordET.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordET.setError("Required.");
            valid = false;
        }
        else if(password.length()<6)
        {
            passwordET.setError("Mininum 6 Characters.");
            valid = false;
        }
        else {
            passwordET.setError(null);
        }

        return valid;
    }

    public void enableAllTextFields(boolean enable)
    {
        fNameET.setEnabled(enable);
        lNameET.setEnabled(enable);
        emailET.setEnabled(enable);
        passwordET.setEnabled(enable);
    }
    public void enableOrDisableButtons(boolean enableRegiter,boolean enableVerify)
    {
        registerBtn.setEnabled(enableRegiter);
        verifyEmailBtn.setEnabled(enableVerify);

    }

    public void changeRegisterButtonText()
    {
        registerBtn.setTag("2");
        registerBtn.setText(getString(R.string.sign_in));
    }

    public String getRegisterBtnTag()
    {
        return registerBtn.getTag().toString();
    }
}
