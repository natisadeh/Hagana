package com.example.natis.hagana;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MainFragment extends Fragment {
    Button signInBtn;
    Button registerBtn;

    public MainFragment() {
        // Required empty public constructor
    }

    public interface Delegate{
        void onSignInPressed();
        void onRegisterPressed();
    }

    Delegate delegate;
    public void setDelegate(Delegate dlg){
        this.delegate = dlg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_main, container, false);
        signInBtn=(Button)view.findViewById(R.id.fragment_main_sign_in_button);
        registerBtn=(Button)view.findViewById(R.id.fragment_main_register_button);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onSignInPressed();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onRegisterPressed();
            }
        });


        return view;


    }
}
