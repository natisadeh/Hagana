package com.example.natis.hagana;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.natis.hagana.Dialogs.MyProgressBar;
import com.example.natis.hagana.Model.ClientUser;
import com.example.natis.hagana.Model.Model;

public class MainActivity extends Activity
        implements UserListFragment.OnFragmentInteractionListener {
    private FragmentTransaction ftr;
    private MainFragment mainFragment;
    private RegisterFragment registerFragment;
    private SignInFragment signInFragment;
    private UserListFragment userListFragment;
    private Model.LoginListener loginListener;
    private MyProgressBar progressBar;
    static final int REQUEST_WRITE_STORAGE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar=new MyProgressBar(this);
        //constracting the fragments in this activity
        mainFragment=new MainFragment();
        signInFragment=new SignInFragment();
        registerFragment=new RegisterFragment();
        userListFragment = new UserListFragment();
        ftr = getFragmentManager().beginTransaction();
        //add to the screen
        ftr.add(R.id.main_container,mainFragment);
        ftr.commit();


        loginListener= new Model.LoginListener() {
            @Override
            public void showProgressBar() {
                progressBar.showProgressDialog();
            }

            @Override
            public void hideProgressBar() {
                progressBar.hideProgressDialog();
            }

            @Override
            public void makeToastAuthFailed() {
                Toast.makeText(MainActivity.this, R.string.auth_failed,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void makeToastVerifyEmail(String msg)
            {
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean validateFormInRegister() {
                return registerFragment.validateForm();
            }

            @Override
            public boolean validateFormInSignIn() {
                return signInFragment.validateForm();
            }


            @Override
            public Activity getActivity() {
                return MainActivity.this;
            }

            @Override
            public void printToLogWarning(String tag, String msg, Throwable tr) {
                Log.w(tag, msg,tr);
            }

            @Override
            public void printToLogMessage(String tag, String msg) {
                Log.d(tag,msg);
            }

            @Override
            public void printToLogException(String tag, String msg, Throwable tr) {
                Log.e(tag,msg,tr);
            }


            @Override
            public void updateRegisterActivityIfSuccess() {
                registerFragment.enableOrDisableButtons(true,true);
                registerFragment.enableAllTextFields(false);
                registerFragment.changeRegisterButtonText();
            }

            @Override
            public void goToMainActivity() {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void goToListFragment() {
                ftr = getFragmentManager().beginTransaction();
                //add to the screen
                ftr.replace(R.id.main_container, userListFragment);
                ftr.addToBackStack("");
                ftr.commit();
            }


        };

        userListFragment.setDelegate(new UserListFragment.Delegate() {
            @Override
            public void showProgressBar() {
                progressBar.showProgressDialog();
            }

            @Override
            public void hideProgressBar() {
                progressBar.hideProgressDialog();
            }
        });


        Model.instance().checkIfUserAuthonticated(loginListener);

        mainFragment.setDelegate(new MainFragment.Delegate() {
            @Override
            public void onSignInPressed() {
                ftr = getFragmentManager().beginTransaction();
                //add to the screen
                ftr.replace(R.id.main_container,signInFragment);
                ftr.addToBackStack("");
                ftr.commit();
            }

            @Override
            public void onRegisterPressed() {
                ftr = getFragmentManager().beginTransaction();
                //add to the screen
                ftr.replace(R.id.main_container,registerFragment);
                ftr.addToBackStack("");
                ftr.commit();
            }
        });

        registerFragment.setDelegate(new RegisterFragment.Delegate() {

            @Override
            public void onRegisterButtonClick(ClientUser user) {
                if(registerFragment.getRegisterBtnTag().equals("1"))
                {
                    Model.instance().addUser(user,loginListener);
                }
                else
                {
                    Model.instance().signInAfterRegister(user.getEmail(),user.getPassword(),loginListener);
                }


            }

            @Override
            public void onVerifyEmailClick(ClientUser user) {
                Model.instance().verifyEmail(loginListener);
            }

        });

        signInFragment.setDelegate(new SignInFragment.Delegate() {
            @Override
            public void onSignInPressed(String email, String password) {
                Model.instance().signIn(email,password,loginListener);
            }
        });

        boolean hasPermission =
                (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, Menu.NONE, "Log Out");
        return onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case Menu.FIRST:
                Model.instance.signOut(new Model.SignOutListener() {
                    @Override
                    public void goToMainActivity() {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void showProgressBar() {
                        progressBar.showProgressDialog();
                    }

                    @Override
                    public void hideProgressBar() {
                        progressBar.hideProgressDialog();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed()
    {
        if(Model.instance().checkIfUserAuthonticated(loginListener)) {
            FragmentTransaction tran = getFragmentManager().beginTransaction();
            tran.replace(R.id.main_container, userListFragment);
            tran.addToBackStack("");
            tran.commit();
        }
    }

    @Override
    public void onItemSelected(String itemId) {

    }
}
