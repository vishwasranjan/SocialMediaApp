package com.example.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class receivedpost extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
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
        receivedlistpost.setOnItemLongClickListener(this);

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
                int i=0;
                for (DataSnapshot snapshot1:dataSnapshots)
                {
                    if (snapshot1.getKey().equals(snapshot.getKey()))
                    {
                        dataSnapshots.remove(i);
                        usernames.remove(i);
                    }
                    i++;
                }
                adapter.notifyDataSetChanged();
                receivedimagepost.setVisibility(View.INVISIBLE);
                receiveddescription.setText(" ");
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
        receivedimagepost.setVisibility(View.VISIBLE);
        receiveddescription.setText((String)mydatasnapshot.child("Description").getValue());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        AlertDialog.Builder alertdialogue=new AlertDialog.Builder(this);
        alertdialogue.setMessage("Do You want to delete ths post");
        alertdialogue.setTitle("Caution!!");
        alertdialogue.setCancelable(false);
        alertdialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Deleting Image
                FirebaseStorage.getInstance().getReference().child("My_Images").child(dataSnapshots.get(position).child("Image Name").getValue().toString());

                //Deleting the user received post
                FirebaseDatabase.getInstance().getReference().child("my_user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Received_post").child(dataSnapshots.get(position).getKey()).removeValue();

            }
        });
        alertdialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertdialogue.show();

        return false;
    }
}