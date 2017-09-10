package com.example.natis.hagana;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import static android.app.Activity.RESULT_OK;
import com.example.natis.hagana.Model.ClientUser;
import com.example.natis.hagana.Model.Model;
import com.example.natis.hagana.Model.ModelFirebase;

import java.io.ByteArrayOutputStream;

public class EditFragment extends Fragment {
    private static final String ARG_PARAM = "userId";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    EditText firstName;
    EditText lastName;
    ImageView image;
    Button cancel;
    Button save;
    Bitmap imageBitmap;
    ClientUser user;
    String userId;

    ModelFirebase auth = new ModelFirebase();

    private EditFragment.OnFragmentInteractionListener listener;
    private FragmentTransaction ftr;
    private UserListFragment ful;
    private RadioButton female;
    private RadioButton male;

    public EditFragment() {
        setHasOptionsMenu(true);
    }

    public static EditFragment getInstance(String userId) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = auth.getCurrentUserId();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.blank, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Edit User");
        user = Model.instance().getOneUser(userId);
        final View view=inflater.inflate(R.layout.fragment_user_edit, container, false);
        firstName=(EditText) view.findViewById(R.id.fragment_edit_first_name);
        lastName=(EditText)view.findViewById(R.id.fragment_edit_last_name);
        female = (RadioButton) view.findViewById(R.id.fragment_edit_female);
        male=(RadioButton) view.findViewById(R.id.fragment_edit_male);
        image = (ImageView) view.findViewById(R.id.mainImageView);
        if (user.getImageBitMap()!=null){
            image.setImageBitmap(BitmapFactory.decodeByteArray(user.getImageBitMap(), 0, user.getImageBitMap().length));
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

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
                //mListener.onCancelSelected();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClientUser current = new ClientUser();
                        current.setUserId(userId);
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
                        if (imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, user.getUserId() + ".jpeg", new Model.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    user.setImageUrl(url);
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                    byte[] img = bos.toByteArray();
                                    user.setImageBitMap(img);
                                    Model.instance.updateUser(user);
                                    listener.onSaveSelected();
                                }

                                @Override
                                public void fail() {
                                    //notify operation fail,...
                                }
                            });
                        }
                        listener.onSaveSelected();
                        Model.instance().updateUser(current);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        getFragmentManager().popBackStack();
                    }
                });

                AlertDialog alert = builder.create();
                alert.setTitle("Are you sure ?");
                alert.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.main_edit:
                listener.onButtonSelected(user.getUserId());
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public interface OnFragmentInteractionListener {
        void onSaveSelected();
        void onCancelSelected();
        void onButtonSelected(String itemId);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditFragment.OnFragmentInteractionListener) {
            listener = (EditFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}



