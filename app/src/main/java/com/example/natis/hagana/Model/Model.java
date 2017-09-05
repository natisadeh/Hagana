package com.example.natis.hagana.Model;

import android.app.Activity;
import android.graphics.Bitmap;

import java.util.List;

public class Model {

    ModelFirebase modelFirebase;
    private ModelSQL modelSql;

    final public static Model instance = new Model();

    public static Model instance() {
        return instance;
    }

    private Model() {
        modelSql = new ModelSQL(MyApplication.getMyContext());
        modelFirebase = new ModelFirebase();
    }

    public ModelSQL getModelSQL() {return this.modelSql;}
    public void getAllClientUsers(final GetAllUsersListener listener) {

        modelFirebase.getAllUsers(new GetAllUsersListener() {
            @Override
            public void onComplete(List<ClientUser> userList) {
                listener.onComplete(userList);
                for(ClientUser user : userList){
                        ClientUserSQL.addUser(modelSql.getWritableDatabase(),user);
                }
            }

            @Override
            public void showProgressBar() {

            }

            @Override
            public void hideProgressBar() {

            }
        });

    }

    public ClientUser getOneUser(String uid){//, final ModelFirebase.GetUserCallback callback) {
        return ClientUserSQL.getUser(modelSql.getReadableDatabase(),uid);
    }



    public interface LoginListener{
        /**
         * showing progress bar
         */
        void showProgressBar();

        /**
         * hide progress bar
         */
        void hideProgressBar();

        /**
         * make toast with the text:Authentication failed
         * (the message have to be declared inside activity,that why there are no parameters declared inside firebase class)
         */
        void makeToastAuthFailed();

        /**
         * make toast for verify email in firebase with message declared in firebase class
         * @param msg message to show in the toast
         */
        void makeToastVerifyEmail(String msg);

        /**
         * valide the form of register activity
         * @return true if the form is legit false otherwise
         */
        boolean validateFormInRegister();

        /**
         * valide the form of sighIn activity
         * @return true if the form is legit false otherwise
         */
        boolean validateFormInSignIn();

        /**
         * getting the activity that all the things running inside it
         * @return ActivityMain
         */
        Activity getActivity();

        /**
         * printing to log warning
         * @param tag tag to print
         * @param msg message message to print
         * @param tr the stack trace of warning
         */
        void printToLogWarning(String tag, String msg, Throwable tr);

        /**
         * rinting message to log
         * @param tag tag to print
         * @param msg message to print
         */
        void printToLogMessage(String tag, String msg);

        /**
         * printing to log Exception
         * @param tag tag to print
         * @param msg message message to print
         * @param tr the stack trace of warning
         */
        void printToLogException(String tag, String msg, Throwable tr);


        /**
         * if the registration worked,update the buttons(register button disabled and verify email enabled)
         */
        void updateRegisterActivityIfSuccess();

        void goToMainActivity();
        void goToListFragment();
    }

    public interface saveUserRemote{
        void saveUserToRemote(ClientUser user);
    }

    public interface changeFragmentListner{
        void goToDetailsFragment();
        void goToListFragment();
        void goToEditFragment();
    }
    public void addUser(final ClientUser user, final LoginListener listener){

        saveUserRemote sur=new saveUserRemote() {
            @Override
            public void saveUserToRemote(ClientUser user) {
                modelFirebase.addUser(user,listener);
            }
        };
        modelFirebase.createAccount(user,listener,sur);

    }

    public void verifyEmail(LoginListener listener){
        modelFirebase.sendEmailVerification(listener);
    }

    public void signInAfterRegister(String email,String password,LoginListener listener){

        modelFirebase.signInAfterRegister(email,password,listener);
    }

    public void signIn(String email,String password,LoginListener listener){

        modelFirebase.signIn(email,password,listener);

    }

    public Boolean checkIfUserAuthonticated(LoginListener loginListener) {
        return modelFirebase.checkIfUserAuthonticated(loginListener);
    }
    public interface SignOutListener{
        void goToMainActivity();
        void showProgressBar();
        void hideProgressBar();
    }
    public void signOut(SignOutListener signOutListner)
    {
        modelFirebase.signOut(signOutListner);
    }

    public interface GetAllUsersListener{
        void onComplete(List<ClientUser> userList);
        void showProgressBar();
        void hideProgressBar();
    }


    public String getConnectedUserID(){
        return modelFirebase.getConnectedUserID();
    }

    public void updateUser(ClientUser user) {
        modelFirebase.updateUser(user);
        ClientUserSQL.updateUser(modelSql.getWritableDatabase(), user);
    }

    public void addNote(ClientUser user, String note) {
        user.setNotes(note);
        ClientUserSQL.addNote(modelSql.getWritableDatabase(), user, note);
        modelFirebase.addNote(user);
    }

    /*Start Image Section*/
    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }


    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }

}
