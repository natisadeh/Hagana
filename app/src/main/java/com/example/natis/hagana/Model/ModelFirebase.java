package com.example.natis.hagana.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseUser current;
    private String id;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    //----firebase storage and realtime databse-------
    private FirebaseDatabase database = FirebaseDatabase.getInstance();//firebase databse reference
    public StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();//firebase storage reference(read and write images)
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public ModelFirebase() {
        //------------Authentication------------
        if(mAuth.getCurrentUser() != null) {
            id = mAuth.getCurrentUser().getUid();
        }
        //initialize the FirebaseAuth instance and the AuthStateListener method so you can track whenever the user signs in or out.
        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               current = firebaseAuth.getCurrentUser();
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    public String getCurrentUserId() {return id;}
    public void createAccount(final ClientUser user, final Model.LoginListener listener,final Model.saveUserRemote sur) {

        listener.printToLogMessage(TAG, "createAccount:" + user.getEmail());
        if (!listener.validateFormInRegister()) {
            return;
        }

        listener.showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(listener.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.printToLogMessage(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            listener.makeToastAuthFailed();
                            listener.hideProgressBar();
                        }
                        else
                        {
                            listener.updateRegisterActivityIfSuccess();
                            //now adding user to loacl and remote database
                            //enter the token that we got from firebase into userID
                            user.setUserId(mAuth.getCurrentUser().getUid());
                            sur.saveUserToRemote(user);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    public void addUser(ClientUser user,final Model.LoginListener viewlistener)
    {
        viewlistener.printToLogMessage("TAG","saved to Firebase storage");
        //saving user details to storage
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId",user.getUserId());
        result.put("firstName",user.getfirstName());
        result.put("lastName",user.getlastName());
        result.put("gender", user.getGender());

         DatabaseReference myRef = database.getReference("users").child(user.getUserId());
         myRef.setValue(result);
        viewlistener.hideProgressBar();
    }

    public void addNote(ClientUser user) {
        updateUser(user);
    }
    /**
     * send email verification to verify user email
     * @param listener listener which generates functions from the Activity
     */
    public void sendEmailVerification(final Model.LoginListener listener) {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser fbUser = mAuth.getCurrentUser();
        //first checking that we make account
        if (fbUser != null) {
            fbUser.sendEmailVerification()
                    .addOnCompleteListener(listener.getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // [START_EXCLUDE]
                            // Re-enable button

                            if (task.isSuccessful()) {
                                listener.makeToastVerifyEmail("Verification email sent to " + fbUser.getEmail());
                            } else {
                                listener.printToLogException(TAG, "sendEmailVerification", task.getException());
                                listener.makeToastVerifyEmail("Failed to send verification email.");
                            }
                            // [END_EXCLUDE]
                        }
                    });
        }
        else
        {
            listener.makeToastVerifyEmail("Can't verify Email before making account");
        }
        // [END send_email_verification]
    }

    public void signInAfterRegister(String email, String password,final Model.LoginListener listener)
    {
        //you have to sign out  and then sign in,in order to get the new email verified status
        mAuth.signOut();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.printToLogMessage(TAG,"signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            listener.printToLogWarning(TAG, "signInWithEmail:failed", task.getException());
                            listener.makeToastAuthFailed();

                        }
                        FirebaseUser fbUser=mAuth.getCurrentUser();
                        if(fbUser!=null)
                        {
                            if(mAuth.getCurrentUser().isEmailVerified())
                            {
                                listener.goToListFragment();//moved to changeStatus in firebase
                            }
                            else
                            {
                                listener.makeToastVerifyEmail("Please verify email first before signing in");
                                //signOut();

                            }

                        }
                        listener.hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]

    }
    public void signIn(String email, String password, final Model.LoginListener listener) {
        listener.printToLogMessage(TAG, "signIn:" + email);
        if (!listener.validateFormInSignIn()) {
            return;
        }

        listener.showProgressBar();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.printToLogMessage(TAG,"signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            listener.printToLogWarning(TAG, "signInWithEmail:failed", task.getException());
                            listener.makeToastAuthFailed();
                            listener.hideProgressBar();
                        }
                        else
                        {
                            listener.goToListFragment();
                        }


                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    public Boolean checkIfUserAuthonticated(Model.LoginListener loginListener) {
        //Log.d("TAG", mAuth.getCurrentUser().getUid());
        if(mAuth.getCurrentUser()!=null)
        {
            loginListener.goToListFragment();
            return true;
            //loginListener.goToMainActivity();
        }
        return false;
    }

    public void signOut(Model.SignOutListener signOutListener) {

        signOutListener.showProgressBar();
        mAuth.signOut();
        signOutListener.hideProgressBar();
        mAuth.removeAuthStateListener(mAuthListener);
        signOutListener.goToMainActivity();
    }



    public void getAllUsers(final Model.GetAllUsersListener callback){

        DatabaseReference myRef = database.getReference("users");
        ValueEventListener listner = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ClientUser> list = new LinkedList<ClientUser>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    ClientUser user = snap.getValue(ClientUser.class);
                    list.add(user);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public interface GetUserCallback {
        void onComplete(ClientUser user);

        void onCancel();
    }

    public void getOneUser(String usrId, final GetUserCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(usrId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ClientUser user = dataSnapshot.getValue(ClientUser.class);
                callback.onComplete(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    public void updateUser(ClientUser user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ClientUser temp = new ClientUser();
        temp.setUserId(user.getUserId());
        temp.setfirstName(user.getfirstName());
        temp.setlastName(user.getlastName());
        temp.setNotes(user.getNotes());
        DatabaseReference myRef = database.getReference("users").child(user.getUserId());
        myRef.setValue(temp);
    }

    public String getConnectedUserID(){
        return mAuth.getCurrentUser().getUid();
    }

    /*Start Image Section*/
    public void saveImage(Bitmap imageBmp, String name, final Model.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });


    }


    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                listener.onFail();
            }
        });
    }
    /*End Image Section*/
}
