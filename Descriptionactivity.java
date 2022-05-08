package com.example.sarahshaikh.goldenhourresponse;
import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
public class DescriptionActivity extends AppCompatActivity {
public static final int CAMERA_REQUEST_CODE = 1888;
private StorageReference mStorage, filepath;
private ProgressDialog mprogress, mUploadProgress;
public String path, toa = null, noc = null, img, abc, phoneNumber;
double lat, lon;
ImageButton mImageButton, mMapHospitals;
private Button mPost;
private DatabaseReference mDatabase;
private DatabaseReference mDBReference;
private Uri uri;
private Uri download;
private FirebaseAuth mAuth;
private TextView mCurrentLocation;
ArrayAdapter adapter, adapter1;
Spinner TOAdropdown, NOCdropdown;
private static final int uniqueID = 45612;
private ImageButton mClose;
NotificationCompat.Builder notification;
NotificationManager mNotificationManager;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_description);
if (ContextCompat.checkSelfPermission(DescriptionActivity.this,
android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
if
(ActivityCompat.shouldShowRequestPermissionRationale(DescriptionActivity.this
, android.Manifest.permission.CAMERA)) {
ActivityCompat.requestPermissions(DescriptionActivity.this,
new String[]{android.Manifest.permission.CAMERA}, 1);
} else {
ActivityCompat.requestPermissions(DescriptionActivity.this,
new String[]{android.Manifest.permission.CAMERA}, 1);
}
} else {
} if (
ContextCompat.checkSelfPermission(DescriptionActivity.this,
android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
PackageManager.PERMISSION_GRANTED) {
if
(ActivityCompat.shouldShowRequestPermissionRationale(DescriptionActivity.this
, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
ActivityCompat.requestPermissions(DescriptionActivity.this,
new
String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
} else {
ActivityCompat.requestPermissions(DescriptionActivity.this,
new
String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
  }
} else {
} if (
ContextCompat.checkSelfPermission(DescriptionActivity.this,
android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
if
(ActivityCompat.shouldShowRequestPermissionRationale(DescriptionActivity.this
, android.Manifest.permission.SEND_SMS)) {
ActivityCompat.requestPermissions(DescriptionActivity.this,
new String[]{android.Manifest.permission.SEND_SMS},
1);
} else {
ActivityCompat.requestPermissions(DescriptionActivity.this,
new String[]{android.Manifest.permission.SEND_SMS},
1);
}
} else {
}
adapter = ArrayAdapter.createFromResource(this,
R.array.spinner_options, android.R.layout.simple_spinner_item);
adapter1 = ArrayAdapter.createFromResource(this,
R.array.spinner_options1, android.R.layout.simple_spinner_item);
mDatabase =
FirebaseDatabase.getInstance().getReference().child("PostNotification");
mDBReference =
FirebaseDatabase.getInstance().getReference().child("Location");
mImageButton = (ImageButton) findViewById(R.id.upload_image);
mStorage =
FirebaseStorage.getInstance().getReference().child("IncidentImages");
mprogress = new ProgressDialog(this);
mPost = (Button) findViewById(R.id.post_button);
mUploadProgress = new ProgressDialog(this);
mMapHospitals = (ImageButton) findViewById(R.id.map_hospitals);
mCurrentLocation = (TextView) findViewById(R.id.current_location);
mAuth = FirebaseAuth.getInstance();
//registerForContextMenu(mPost);
mMapHospitals.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
Intent hospital_intent = new
Intent(DescriptionActivity.this, GeoFenceMap.class);
startActivity(hospital_intent);
}
});
mDBReference.child(mAuth.getCurrentUser().getUid()).child("Loc").addValueEven
tListener(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {
abc = dataSnapshot.getValue(String.class);
mCurrentLocation.setText(abc);
}
  @Override
public void onCancelled(DatabaseError databaseError) {
}
});
mDBReference.child(mAuth.getCurrentUser().getUid()).child("Latitude").addVal
ueEventListener(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {
lat = dataSnapshot.getValue(Double.class);
}
@Override
public void onCancelled(DatabaseError databaseError) {
}
});
mDBReference.child(mAuth.getCurrentUser().getUid()).child("Longitude").addVa
lueEventListener(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {
lon = dataSnapshot.getValue(Double.class);
}
@Override
public void onCancelled(DatabaseError databaseError) {
}
});
mDBReference.child(mAuth.getCurrentUser().getUid()).child("phone").addValueE
ventListener(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {
phoneNumber = dataSnapshot.getValue(String.class);
}
@Override
public void onCancelled(DatabaseError databaseError) {
}
});
mImageButton.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
Intent image_intent = new
Intent(MediaStore.ACTION_IMAGE_CAPTURE);
startActivityForResult(image_intent, CAMERA_REQUEST_CODE);
}
  });
mPost.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
startPosting();
}
});
TOAdropdown = (Spinner) findViewById(R.id.typeOfAccident);
TOAdropdown.setAdapter(adapter);
TOAdropdown.setOnItemSelectedListener(new
AdapterView.OnItemSelectedListener() {
@Override
public void onItemSelected(AdapterView<?> parent, View view, int
position, long id) {
}
@Override
public void onNothingSelected(AdapterView<?> parent) {
}
});
NOCdropdown = (Spinner) findViewById(R.id.numberOfCasualties);
NOCdropdown.setAdapter(adapter1);
NOCdropdown.setOnItemSelectedListener(new
AdapterView.OnItemSelectedListener() {
@Override
public void onItemSelected(AdapterView<?> parent, View view, int
position, long id) {
noc = NOCdropdown.getSelectedItem().toString();
}
@Override
public void onNothingSelected(AdapterView<?> parent) {
}
});
} private void sendSMS() {
}
private void startPosting() {
mUploadProgress.setMessage("Posting the Incident....");
mUploadProgress.show();
if (uri != null && toa!=null && noc!= null) {
DatabaseReference newPost = mDatabase.push();
newPost.child("Location").setValue(abc);
newPost.child("Image").setValue(download.toString());
newPost.child("TOA").setValue("Type of Accident:" + toa);
newPost.child("NOC").setValue("Number of Casualties:" + noc);
newPost.child("Lat").setValue(lat);
newPost.child("Lon").setValue(lon);
newPost.child("phone").setValue(phoneNumber);
newPost.child("UId").setValue(mAuth.getUid().toString());
mUploadProgress.dismiss();
Toast.makeText(this, "Posted", Toast.LENGTH_LONG).show();
  mDatabase.addChildEventListener(new ChildEventListener() {
@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
@Override
public void onChildAdded(DataSnapshot dataSnapshot, String
s) {
mNotificationManager = (NotificationManager)
getSystemService(Context.NOTIFICATION_SERVICE);
Notification.Builder noti = new
Notification.Builder(DescriptionActivity.this);
Intent notificationIntent = new
Intent(DescriptionActivity.this,MainActivity.class);
PendingIntent pendingIntent =
PendingIntent.getActivity(DescriptionActivity.this, 0,notificationIntent, 0);
noti.setContentIntent(pendingIntent)
.setContentTitle("Emergency")
.setContentText("An incident has occured")
.setSmallIcon(R.mipmap.logo)
.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))
.setOnlyAlertOnce(true)
.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
.build();
noti.setAutoCancel(true);
noti.setLocalOnly(false);
mNotificationManager.notify(123, noti.build());
} @
Override
public void onChildChanged(DataSnapshot dataSnapshot, String
s) {
} @
Override
public void onChildRemoved(DataSnapshot dataSnapshot) {
} @
Override
public void onChildMoved(DataSnapshot dataSnapshot, String
s) {
} @
Override
public void onCancelled(DatabaseError databaseError) {
}
});
notification = new NotificationCompat.Builder(this);
notification.setAutoCancel(true);
initiatePopupWindow();
} else {
mUploadProgress.dismiss();
Toast.makeText(this, "Posting Failed",
Toast.LENGTH_LONG).show();
}
} private void initiatePopupWindow() {
switch(toa){
case "Road":{
Button mPolice, mAmb;
final PopupWindow pwindo;
try {
// We need to get the instance of the LayoutInflater
LayoutInflater inflater = (LayoutInflater)
DescriptionActivity.this
.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View layout_road = inflater.inflate(R.layout.road_popup,
(ViewGroup) findViewById(R.id.road_element));
pwindo = new PopupWindow(layout_road, 700, 1000, true);
pwindo.showAtLocation(layout_road, Gravity.CENTER, 0, 0);
mClose = (ImageButton)
layout_road.findViewById(R.id.road_close);
mClose.setOnClickListener(new View.OnClickListener() {
public void onClick(View popupView) {
pwindo.dismiss();
}
});
mPolice = (Button)
layout_road.findViewById(R.id.road_police);
mPolice.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
mAmb = (Button) layout_road.findViewById(R.id.road_amb);
mAmb.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
} catch (Exception e) {
e.printStackTrace();
}
}break;
case "Train":{
Button mPolice, mAmb;
final PopupWindow pwindo;
try {
// We need to get the instance of the LayoutInflater
LayoutInflater inflater = (LayoutInflater)
DescriptionActivity.this
.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View layout_train =
inflater.inflate(R.layout.train_popup,
(ViewGroup) findViewById(R.id.train_element));
pwindo = new PopupWindow(layout_train, 700, 1000, true);
  pwindo.showAtLocation(layout_train, Gravity.CENTER, 0,
0);
mClose = (ImageButton)
layout_train.findViewById(R.id.train_close);
mClose.setOnClickListener(new View.OnClickListener() {
public void onClick(View popupView) {
pwindo.dismiss();
}
});
mPolice = (Button)
layout_train.findViewById(R.id.train_police);
mPolice.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
mAmb = (Button)
layout_train.findViewById(R.id.train_amb);
mAmb.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
} catch (Exception e) {
e.printStackTrace();
}
}break;
case "Fire":{
Button mPolice, mAmb, mFire;
final PopupWindow pwindo;
try {
// We need to get the instance of the LayoutInflater
LayoutInflater inflater = (LayoutInflater)
DescriptionActivity.this
.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View layout_fire = inflater.inflate(R.layout.fire_popup,
(ViewGroup) findViewById(R.id.fire_element));
pwindo = new PopupWindow(layout_fire, 700, 1000, true);
pwindo.showAtLocation(layout_fire, Gravity.CENTER, 0, 0);
mClose = (ImageButton)
layout_fire.findViewById(R.id.fire_close);
mClose.setOnClickListener(new View.OnClickListener() {
  public void onClick(View popupView) {
pwindo.dismiss();
}
});
mPolice = (Button)
layout_fire.findViewById(R.id.fire_police);
mPolice.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
mFire = (Button)
layout_fire.findViewById(R.id.fire_fire);
mFire.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
mAmb = (Button) layout_fire.findViewById(R.id.fire_amb);
mAmb.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
} catch (Exception e) {
e.printStackTrace();
}
}break;
case "Building Collapse":{
Button mPolice, mFire, mAmb;
final PopupWindow pwindo;
try {
// We need to get the instance of the LayoutInflater
LayoutInflater inflater = (LayoutInflater)
DescriptionActivity.this
.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View layout_bldg =
inflater.inflate(R.layout.building_popup,
(ViewGroup) findViewById(R.id.bldg_element));
pwindo = new PopupWindow(layout_bldg, 700, 1000, true);
pwindo.showAtLocation(layout_bldg, Gravity.CENTER, 0, 0);
mClose = (ImageButton)
layout_bldg.findViewById(R.id.bldg_close);
mClose.setOnClickListener(new View.OnClickListener() {
public void onClick(View popupView) {
  pwindo.dismiss();
}
});
mPolice = (Button)
layout_bldg.findViewById(R.id.bldg_police);
mPolice.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
mFire = (Button)
layout_bldg.findViewById(R.id.bldg_fire);
mFire.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
mAmb = (Button) layout_bldg.findViewById(R.id.bldg_amb);
mAmb.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
call();
}
});
} catch (Exception e) {
e.printStackTrace();
}
}break;
default: break;
}
} private void call() {
String number = "8433650889";
Intent intent = new Intent(Intent.ACTION_CALL);
intent.setData(Uri.parse("tel:" + number));
if (ActivityCompat.checkSelfPermission(DescriptionActivity.this,
Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
{
}
Log.d("MainActivity", "Inside call");
startActivity(intent);
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
// System.out.println(resultCode);
if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
mprogress.setMessage("Uploading...");
mprogress.show();
Bundle extras = data.getExtras();
final Bitmap bitmap = (Bitmap) extras.get("data");
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
path =
MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap,
"Title", null);
uri = Uri.parse(path);
System.out.println(path);
filepath = mStorage.child(uri.getLastPathSegment() + ".jpg");
Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
filepath.putFile(uri).addOnSuccessListener(new
OnSuccessListener<UploadTask.TaskSnapshot>() {
@Override
public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
{
mprogress.dismiss();
download = taskSnapshot.getDownloadUrl();
mImageButton.setImageURI(uri);
Toast.makeText(DescriptionActivity.this, "Image
Uploaded", Toast.LENGTH_LONG).show();
}
}).addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
Toast.makeText(DescriptionActivity.this, "Image
Uploading Failed....", Toast.LENGTH_LONG).show();
}
});
}
}
}
