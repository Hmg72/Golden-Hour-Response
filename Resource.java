package com.example.sarahshaikh.goldenhourresponse;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import org.w3c.dom.Text;
public class Resource extends AppCompatActivity {
private static final String TAG = Resource.class.getName();
private RecyclerView mPostList;
double lat;
String Number;
private FirebaseAuth mAuth;
private DatabaseReference mDatabase, mDBReference;
private TextView mText;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_resource);
if (ContextCompat.checkSelfPermission(Resource.this,
android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
if
(ActivityCompat.shouldShowRequestPermissionRationale(Resource.this,
android.Manifest.permission.SEND_SMS)) {
ActivityCompat.requestPermissions(Resource.this,
new String[]{android.Manifest.permission.SEND_SMS},
1);
} else {
ActivityCompat.requestPermissions(Resource.this,
new String[]{android.Manifest.permission.SEND_SMS},
1);
}
} else {
}
mDatabase = FirebaseDatabase.getInstance().getReference().child("PostNotification");
mDBReference = FirebaseDatabase.getInstance().getReference().child("Location");
mPostList = (RecyclerView) findViewById(R.id.post_list1);
mPostList.setHasFixedSize(true);
LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
mLayoutManager.setReverseLayout(true);
mPostList.setLayoutManager(mLayoutManager);
mAuth = FirebaseAuth.getInstance();
}
@Override
protected void onStart() {
super.onStart();
FirebaseRecyclerAdapter<Post1, Resource.BlogViewHolder1>
firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post1,
Resource.BlogViewHolder1>(
Post1.class, R.layout.post_row1,
Resource.BlogViewHolder1.class, mDatabase)
{
@Override
protected void populateViewHolder(Resource.BlogViewHolder1
viewHolder, Post1 model,final int position) {
viewHolder.setLocation(model.getLocation());
viewHolder.setTOA(model.getTOA());
viewHolder.setNOC(model.getNOC());
viewHolder.setImage(getApplicationContext(),
model.getImage());
viewHolder.mYes.setOnClickListener(new
View.OnClickListener() {
@Override
public void onClick(View v) {
final String post_key1 = getRef(position).getKey();
mDatabase.child(post_key1).child("phone").addValueEventListener(new
ValueEventListener() {
@Override
public void onDataChange(DataSnapshot
dataSnapshot) {
Number =
String.valueOf(dataSnapshot.getValue());
try {
SmsManager smsManager =
SmsManager.getDefault();
smsManager.sendTextMessage(Number, null,
"Yes. Casualty is being attended", null, null);
Toast.makeText(Resource.this, "Sent!",
               Toast.LENGTH_LONG).show();
} catch (Exception e) {
Toast.makeText(Resource.this, "Failed!",
Toast.LENGTH_LONG).show();
}
}
@Override
public void onCancelled(DatabaseError
databaseError){
Log.d(TAG, "onCancelled: some error");
}
});
}
});
viewHolder.mNo.setOnClickListener(new View.OnClickListener()
{
@Override
public void onClick(View v) {
final String post_key1 = getRef(position).getKey();
mDatabase.child(post_key1).child("phone").addValueEventListener(new
ValueEventListener() {
@Override
public void onDataChange(DataSnapshot
dataSnapshot) {
Number =
String.valueOf(dataSnapshot.getValue());
try {
SmsManager smsManager =
SmsManager.getDefault();
smsManager.sendTextMessage(Number, null,
"Sorry, I vcannot attend.", null, null);
Toast.makeText(Resource.this, "Sent!",
Toast.LENGTH_LONG).show();
} catch (Exception e) {
Toast.makeText(Resource.this, "Failed!",
Toast.LENGTH_LONG).show();
}
} @
Override
public void onCancelled(DatabaseError
databaseError){
Log.d(TAG, "onCancelled: some error");
}
});
}
});
}
};
mPostList.setAdapter(firebaseRecyclerAdapter);
}
public void Yes_clicked(View view) {
  }
public static class BlogViewHolder1 extends RecyclerView.ViewHolder {
View mView;
private Button mYes, mNo;
public BlogViewHolder1(View itemView) {
super(itemView);
mView = itemView;
mYes = (Button) mView.findViewById(R.id.post_yes);
mNo = (Button) mView.findViewById(R.id.post_no);
}
public void setLocation(String location) {
TextView post_location = (TextView)
mView.findViewById(R.id.post_location);
post_location.setText(location);
}
public void setImage(Context ctx, String image) {
ImageView post_image = (ImageView)
mView.findViewById((R.id.post_image));
Picasso.with(ctx).load(image).into(post_image);
}
public void setTOA(String toa) {
TextView post_toa = (TextView) mView.findViewById(R.id.post_toa);
post_toa.setText(toa);
}
public void setNOC(String noc) {
TextView post_noc = (TextView) mView.findViewById(R.id.post_noc);
post_noc.setText(noc);
}
}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
getMenuInflater().inflate(R.menu.main_menu, menu);
return super.onCreateOptionsMenu(menu);
}
@Override
public boolean onOptionsItemSelected(MenuItem item) {
if (item.getItemId() == R.id.action_camera) {
Intent intent = new Intent(Resource.this, MapsActivity.class);
startActivity(intent);
finish();
}
if (item.getItemId() == R.id.logout) {
mAuth.signOut();
  sendToStart();
}
return super.onOptionsItemSelected(item);
}
private void sendToStart() {
Intent start_intent = new Intent(Resource.this,
StartActivity.class);
startActivity(start_intent);
finish();
