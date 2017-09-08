package com.example.natis.hagana;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.natis.hagana.Model.ClientUser;
import com.example.natis.hagana.Model.Model;
import com.example.natis.hagana.Model.ModelFirebase;


public class AddNoteFragment extends Fragment {
    EditText note;
    Button done;
    ModelFirebase auth = new ModelFirebase();
    private FragmentTransaction ftr;
    private UserListFragment ful;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_note, container, false);
        note=(EditText) view.findViewById(R.id.fragment_note_text);
        done=(Button)view.findViewById(R.id.fragment_note_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id  = auth.getCurrentUserId();
                ClientUser current = new ClientUser();
                current.setUserId(id);
                current = Model.instance().getOneUser(current.getUserId());
                current.setNotes(note.getText().toString());
                Model.instance().updateUser(current);
                getFragmentManager().popBackStack();
//                ful = new UserListFragment();
//                ftr = getFragmentManager().beginTransaction();
//                //add to the screen
//                ftr.replace(R.id.main_container, ful);
//                ftr.addToBackStack("");
//                ftr.commit();
            }
        });

        return view;
    }

    public ClientUser setUserNote(ClientUser user)
    {
        user.setNotes(note.getText().toString());
        return user;
    }

}
