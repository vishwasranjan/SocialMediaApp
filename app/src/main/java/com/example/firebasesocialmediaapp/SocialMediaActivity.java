package com.example.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SocialMediaActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private FirebaseAuth mAuth;
    private ImageView postimage;
    private Button btnpost;
    private TextView edtdescription;
    private ListView userlistview;
    private Bitmap bitmap;
    private String Imagename;
    private ArrayList<String> usernames;
    private ArrayAdapter adapter;
    private ArrayList<String> uids;
    private String Imagedownloadlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);
        mAuth = FirebaseAuth.getInstance();
        postimage=findViewById(R.id.postimageview);
        btnpost=findViewById(R.id.btnpost);
        edtdescription=findViewById(R.id.edtdescription);
        userlistview=findViewById(R.id.userlistview);
        usernames=new ArrayList<>();
        adapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernames);
        userlistview.setAdapter(adapter);
        uids=new ArrayList<>();
        userlistview.setOnItemClickListener(SocialMediaActivity.this);

        postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickimage();
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutitem:
                mAuth.signOut();
                finish();
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.receiveditem:
                Intent intent1=new Intent(this,receivedpost.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void pickimage()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
        }
        else {
            getimage();
        }
    }

    private void getimage() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100)
        {
            if (grantResults.length==1 &&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getimage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==200)
        {
            if (resultCode==Activity.RESULT_OK)
            {
                if (data != null)
                {
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getApplicationContext().getContentResolver(), data.getData());
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                            postimage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                            postimage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }
    }


    public void uploadimage()
    {
        if (bitmap!=null) {
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Your Image is being processed......Please wait");
            progressDialog.show();
            progressDialog.setCancelable(false);

            // Get the data from an ImageView as bytes
            postimage.setDrawingCacheEnabled(true);
            postimage.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            Imagename = UUID.randomUUID() + ".png";

            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("My_Images").child(Imagename).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(SocialMediaActivity.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Toast.makeText(SocialMediaActivity.this, "Image is Uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                    edtdescription.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("my_user").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            uids.add(snapshot.getKey());
                            String username=(String)snapshot.child("Username").getValue()+" ";
                            usernames.add(username);
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

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful())
                            {
                                Imagedownloadlink=task.getResult().toString();
                            }
                        }
                    });
                }


            });

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("From Whom",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        hashMap.put("Image Name",Imagename);
        hashMap.put("Download Link",Imagedownloadlink);
        hashMap.put("Description",edtdescription.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("my_user").child(uids.get(position)).child("Received_post").push().setValue(hashMap);
        Toast.makeText(this,"Sent Sucessfully",Toast.LENGTH_SHORT).show();
        
    }
}