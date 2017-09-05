package com.example.natis.hagana;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.natis.hagana.Dialogs.MyProgressBar;
import com.example.natis.hagana.Model.ClientUser;
import com.example.natis.hagana.Model.Model;

import java.util.LinkedList;
import java.util.List;


public class UserListFragment extends Fragment {
    ListView list;
    List<ClientUser> data;
    UserListAdapter UserListAdapter;
    private OnFragmentInteractionListener mListener;

    private Button addNote;
    private FragmentTransaction ftr;
    private AddNoteFragment fan;
    private EditFragment fed;
    private Button edit;

    public interface Delegate{
        void showProgressBar();
        void hideProgressBar();
    }

    Delegate delegate;
    public void setDelegate(Delegate dlg){
        this.delegate = dlg;
    }

    public UserListFragment() {
    }

    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("All Notes List");
        View contentView = inflater.inflate(R.layout.fragment_user_list, container, false);
        list = (ListView) contentView.findViewById(R.id.list_items);
        addNote=(Button)contentView.findViewById(R.id.fragment_note_btn);
        edit = (Button) contentView.findViewById(R.id.fragment_edit_btn);
        data = new LinkedList<ClientUser>();
        UserListAdapter = new UserListAdapter();
        list.setAdapter(UserListAdapter);

        Model.instance().getAllClientUsers(new Model.GetAllUsersListener() {
            @Override
            public void onComplete(List<ClientUser> list) {
                data = list;
                delegate.hideProgressBar();
                UserListAdapter.notifyDataSetChanged();
            }

            @Override
            public void showProgressBar() {}

            @Override
            public void hideProgressBar() {}

        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG","item " + position + "was selected");
                mListener.onItemSelected(data.get(position).getUserId());

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fed = new EditFragment();
                ftr = getFragmentManager().beginTransaction();
                //add to the screen
                ftr.replace(R.id.main_container, fed);
                ftr.addToBackStack("");
                ftr.commit();
            }
        });
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fan = new AddNoteFragment();
                ftr = getFragmentManager().beginTransaction();
                //add to the screen
                ftr.replace(R.id.main_container, fan);
                ftr.addToBackStack("");
                ftr.commit();
            }
        });
        return contentView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentInteractionListener {
        void onItemSelected(String itemId);
    }
    class UserListAdapter extends BaseAdapter {
        private LayoutInflater inflater = (LayoutInflater) getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.user_list_row, null);
            }
            TextView name = (TextView) convertView.findViewById(R.id.strow_name);
            TextView note = (TextView) convertView.findViewById(R.id.strow_note);

            final ClientUser userInPosition = (ClientUser) data.get(position);
            name.setText(userInPosition.getfirstName()+" "+userInPosition.getlastName());
            if (userInPosition.getNotes() == null) {
                note.setText("No Notes Yet");
            }
            note.setText(userInPosition.getNotes());

            return convertView;
        }
    }
}
