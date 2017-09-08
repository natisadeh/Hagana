package com.example.natis.hagana;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.natis.hagana.Model.ClientUser;
import com.example.natis.hagana.Model.Model;
import com.example.natis.hagana.Model.ModelFirebase;

public class EditFragment extends Fragment {
    EditText firstName;
    EditText lastName;

    Button cancel;
    Button save;

    ModelFirebase auth = new ModelFirebase();

    private FragmentTransaction ftr;
    private UserListFragment ful;
    private RadioButton female;
    private RadioButton male;

    public EditFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_user_edit, container, false);
        firstName=(EditText) view.findViewById(R.id.fragment_edit_first_name);
        lastName=(EditText)view.findViewById(R.id.fragment_edit_last_name);
        female = (RadioButton) view.findViewById(R.id.fragment_edit_female);
        male=(RadioButton) view.findViewById(R.id.fragment_edit_male);

        cancel = (Button) view.findViewById(R.id.editCancelBtn);
        save = (Button) view.findViewById(R.id.editSaveBtn);

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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ful = new UserListFragment();
                ftr = getFragmentManager().beginTransaction();
                //add to the screen
                ftr.replace(R.id.main_container, ful);
                ftr.addToBackStack("");
                ftr.commit();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id  = auth.getCurrentUserId();
                ClientUser current = new ClientUser();
                current.setUserId(id);
                current = Model.instance().getOneUser(current.getUserId());
                if (firstName.getText().toString() != null)
                    current.setfirstName(firstName.getText().toString());
                if (lastName.getText().toString() != null)
                    current.setlastName(lastName.getText().toString());
                if (female.isChecked())
                    current.setGender("Female");
                else if(male.isChecked())
                    current.setGender("Male");
                else {
                    Log.d("TAG","no one is checked");
                }
                Model.instance().updateUser(current);
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
