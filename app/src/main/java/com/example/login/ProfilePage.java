package com.example.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePage extends AppCompatActivity implements AdapterView.OnItemSelectedListener , LocationListener
{

    String[] gender = {"Male", "Female"};
    ImageView image;
    private TextView userMobile, userLocation;
    private static final int GalleryPick = 1;
    ProgressDialog loadingBar;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 401;
    private Button UpdateAccountSettings;
    private EditText userName, userEmail, userGender, userPincode, userInterest;
    private CircleImageView userProfileImage;
    private String currentUserID, knownName;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImagesRef;
    private String saveCurrentDate, saveCurrentTime, postRandomName;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    protected LocationManager locationManager;
    private Geocoder geocoder;
    protected double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid(); //fetching the user ID from firebase
        RootRef = FirebaseDatabase.getInstance().getReference("Profile Data");//Referring to database for creating profile table
        //UsersRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images"); //Storage reference

        image = (ImageView) findViewById(R.id.profile_image);
        userEmail = (EditText) findViewById(R.id.et_Email);
        //userMobile = (TextView)findViewById(R.id.tv_Mobile);
        //userGender = (EditText)findViewById(R.id.et_Gender);
        userPincode = (EditText) findViewById(R.id.et_Pincode);
        userInterest = (EditText) findViewById(R.id.et_Interests);
        UpdateAccountSettings = (Button) findViewById(R.id.btn_update);
        userLocation = (TextView) findViewById(R.id.et_Location);
        userName = (EditText) findViewById(R.id.name);

        loadingBar = new ProgressDialog(this);

        fetchLocationData();

        /*final String Mobile=getIntent().getStringExtra("Number");
        userMobile.setText(Mobile);*/

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
// for openng gallery
            }
        });


        RootRef.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image1 = dataSnapshot.getValue().toString();
                    Picasso.get().load(image1).into(image);
                }
            }

            // fetching image
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String NAME = userName.getText().toString().trim();
                String EMAIL = userEmail.getText().toString().trim();
                // String PHONE = Mobile;
                String LOCATION = knownName;

                //String GENDER = userGender.getText().toString().trim();
                //String LOCATION = userLocation.getText().toString().trim();
                String INTEREST = userInterest.getText().toString().trim();
                String PINCODE = userPincode.getText().toString().trim();

                if (TextUtils.isEmpty(NAME)) {
                    Toast.makeText(ProfilePage.this, "Please Enter the details", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(EMAIL)) {
                    Toast.makeText(ProfilePage.this, "Please Enter the details", Toast.LENGTH_SHORT).show();
                    return;

                }

                /*if(TextUtils.isEmpty(PHONE))
                {
                    Toast.makeText(ProfilePage.this, "Please Enter the details", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(TextUtils.isEmpty(LOCATION))
                {
                    Toast.makeText(ProfilePage.this, "Please Enter the details", Toast.LENGTH_SHORT).show();
                    return;

                }*/
                if (TextUtils.isEmpty(INTEREST)) {
                    Toast.makeText(ProfilePage.this, "Please Enter the details", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(PINCODE)) {
                    Toast.makeText(ProfilePage.this, "Please Enter the details", Toast.LENGTH_SHORT).show();
                    return;

                }


                UserDetails userDetails = new UserDetails(EMAIL, PINCODE, LOCATION, INTEREST, NAME);

                FirebaseUser currentUserID = mAuth.getCurrentUser();
                RootRef.child(currentUserID.getUid()).setValue(userDetails);

                Intent i = new Intent(ProfilePage.this, Home_activity.class);
                startActivity(i);
                finish();

                Toast.makeText(ProfilePage.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();


            }
        });
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected (item);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            loadingBar.setTitle("Upload");
            loadingBar.setMessage("Please Wait while image to upload...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Uri ImageUri = data.getData();


            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                Uri resultUri = result.getUri();
                //Link for the saved image


                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String downloadUrl = uri.toString();
                                RootRef.child("image").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfilePage.this, "Image saved to database", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(ProfilePage.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }


                                            }
                                        });
                            }
                        });
                    }
                });
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), gender[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLocationChanged(Location location) {
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        Log.e("latitude", "latitude--" + latitude);
//To avoide Application Not Responding
        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);


            if (addresses != null && addresses.size() > 0) {
                //address = addresses.get(0).getAddressLine(0);
                //String city = addresses.get(0).getLocality();
                //String state = addresses.get(0).getAdminArea();
                //String country = addresses.get(0).getCountryName();
                //String postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getAddressLine(0);

                userLocation.setText(knownName + " " /*+ city + " " */);


            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude", "status");

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude", "enable");


    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude", "disable");
    }


    public void fetchLocationData() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return;
        }
        Toast.makeText(this, "Location is triggered from here acc", Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Toast.makeText(this, "Toast call is triggered", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }

    }
}