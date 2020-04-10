package com.example.takehomeassignment08_lingyiz;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference p_massage;
    MessageAdapter m_messageAdapter;
    private ListView mMessageListView;

    EditText massage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        List<Data> friendlyMessages = new ArrayList<>();
        m_messageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageListView.setAdapter(m_messageAdapter);

        massage = (EditText)findViewById(R.id.messageEditText);
        p_massage = database.getReference().child("Massage");
        p_massage.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                addText_Display(dataSnapshot.getValue(Data.class));

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
        });
    }

    private void moveText_Display() {
    }

    private void removeText_Display() {
    }

    private void changeText_Display() {
    }

    private void addText_Display(Data data) {
        m_messageAdapter.add(data);

    }

    void readInfo(){

    }


    public void uploadInfo(String name, Data data, DatabaseReference parent){
        parent.push().setValue(data);
    }
    public void send(View view) {
        uploadInfo("USER", new Data(massage.getText().toString(), "name",null), database.getReference("Massage"));
        massage.setText("");
    }
}
