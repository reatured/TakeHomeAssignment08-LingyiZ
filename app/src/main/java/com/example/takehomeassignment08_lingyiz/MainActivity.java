package com.example.takehomeassignment08_lingyiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    public String userName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference p_massage;
    MessageAdapter m_messageAdapter;
    private ListView mMessageListView;
    List<Data> friendlyMessages;


    EditText massage;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ChildEventListener childEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotoReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        friendlyMessages = new ArrayList<>();
        updateAdapter(friendlyMessages);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotoReference = mFirebaseStorage.getReference().child("chat_photo");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to FriendlyChat.", Toast.LENGTH_SHORT).show();
                    OnSignedInInitialize(user.getDisplayName());
                } else {
                    OnSignedOutClear();
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder().setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


        massage = (EditText) findViewById(R.id.messageEditText);
        p_massage = database.getReference().child("Massage");
        attachDataBaseReadListenser();
    }

    private void updateAdapter(List<Data> friendlyMessages) {
        m_messageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageListView.setAdapter(m_messageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.online_chat, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void OnSignedOutClear() {
        userName = null;
        m_messageAdapter.clear();
        detachDataBaseReadListenser();
    }


    private void OnSignedInInitialize(String displayName) {
        userName = displayName;
    }

    private void moveText_Display() {
    }

    private void removeText_Display() {
    }

    private void changeText_Display() {
    }

    private void addText_Display(Data data) {
        friendlyMessages.add(data);

    }

    void readInfo() {

    }


    public void uploadInfo(String name, Data data, DatabaseReference parent) {
        parent.push().setValue(data);
    }

    public void send(View view) {
        uploadInfo(userName, new Data(massage.getText().toString(), userName, null), database.getReference("Massage"));
        massage.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        attachDataBaseReadListenser();
        updateAdapter(friendlyMessages);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDataBaseReadListenser();
//        m_messageAdapter.clear();
    }

    private void detachDataBaseReadListenser() {
        if (childEventListener != null) {
            p_massage.removeEventListener(childEventListener);
        }
        childEventListener = null;
    }

    void attachDataBaseReadListenser() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    addText_Display(dataSnapshot.getValue(Data.class));
                    updateAdapter(friendlyMessages);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    changeText_Display();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    removeText_Display();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    moveText_Display();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error in action", Toast.LENGTH_SHORT).show();
                }
            };
            p_massage.addChildEventListener(childEventListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = i.getData();
            StorageReference photoRef = mChatPhotoReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                            // Set the download URL to the message box, so that the user can send it to the database
                            Data friendlyMessage = new Data(null, userName, downloadUrl);
                            p_massage.child("Massage").push().setValue(friendlyMessage);
                            System.out.println(downloadUrl);
                        }
                    });
        }
    }

    public void pickPhoto(View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/jpg");
        i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(i.createChooser(i, "Complete Action Using"), RC_PHOTO_PICKER);
    }


}
