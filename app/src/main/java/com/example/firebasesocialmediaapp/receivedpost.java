package com.example.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class receivedpost extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView receivedlistpost;
    private ArrayAdapter adapter;
    private ArrayList<String> usernames;
    private ImageView receivedimagepost;
    private TextView receiveddescription;
    private ArrayList<DataSnapshot> dataSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivedpost);
        receivedlistpost=findViewById(R.id.receivedlist);
        usernames=new ArrayList<>();
        adapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernames);
        receivedlistpost.setAdapter(adapter);
        receivedimagepost=findViewById(R.id.receivedimage);
        receiveddescription=findViewById(R.id.edtpostdescription);
        dataSnapshots=new ArrayList<>();


        receivedlistpost.setOnItemClickListener(this);

        FirebaseDatabase.getInstance().getReference().child("my_user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Received_post").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                dataSnapshots.add(snapshot);
                String receivedusername=(String)snapshot.child("From Whom").getValue();
                usernames.add(receivedusername);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataSnapshot mydatasnapshot=dataSnapshots.get(position);
        String downloadlink=(String)mydatasnapshot.child("Download Link").getValue();
        Picasso.get().load(downloadlink).into(receivedimagepost);
        receiveddescription.setText((String)mydatasnapshot.child("Description").getValue());
    }
}