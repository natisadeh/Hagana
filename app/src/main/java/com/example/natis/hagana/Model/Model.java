package com.example.natis.hagana.Model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.URLUtil;

import java.util.List;

import static com.example.natis.hagana.Model.ModelFiles.saveImageToFile;

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

    public void getAllClientUsers(final GetAllUsersListener listener) {

        modelFirebase.getAllUsers(new GetAllUsersListener() {
            @Override
            public void onComplete(List<ClientUser> userList) {
                listener.onComplete(userList);
                for(ClientUser user : userList){
                    if (!ClientUserSQL.checkIfExist(modelSql.getReadableDatabase(), user.getUserId()))
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
        Log.d("TEST", "1");
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

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        modelFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });


    }


    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }
    public void getImage(final String url,final GetImageListener listener) {
        //check if image exsist localy
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelFiles.loadImageFromFileAsynch(fileName, new ModelFiles.LoadImageFromFileAsynch() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }else {
                    modelFirebase.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG","getImage from FB success " + fileName);
                            saveImageToFile(image,fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG","getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });
    }

    public void downloadPicture(byte[] imgArray, String imgName){
        Bitmap image = BitmapFactory.decodeByteArray(imgArray,0,imgArray.length);
        saveImageToFile(image,imgName);
    }
    public void downloadPicture(Bitmap imgArray, String imgName){
        saveImageToFile(imgArray,imgName);
    }


}
