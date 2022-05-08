package com.example.sarahshaikh.goldenhourresponse;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
public class MainActivity extends AppCompatActivity {
private static final String TAG = MainActivity.class.getName();
private RecyclerView mPostList;
private FirebaseAuth mAuth;
private DatabaseReference mDatabase;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);
mDatabase =
FirebaseDatabase.getInstance().getReference().child("PostNotification");
mPostList = (RecyclerView) findViewById(R.id.post_list);
mPostList.setHasFixedSize(true);
LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
mLayoutManager.setReverseLayout(true);
mPostList.setLayoutManager(mLayoutManager);
mAuth = FirebaseAuth.getInstance();
}
@Override
protected void onStart() {
super.onStart();
FirebaseUser currentUser = mAuth.getCurrentUser();
if (currentUser == null) {
sendToStart();
}
FirebaseRecyclerAdapter<Post, BlogViewHolder> firebaseRecyclerAdapter
= new FirebaseRecyclerAdapter<Post, BlogViewHolder>(
Post.class, R.layout.post_row, BlogViewHolder.class,
mDatabase)
{
@Override
protected void populateViewHolder(BlogViewHolder viewHolder,
Post model, final int position) {
viewHolder.setLocation(model.getLocation());
// Log.d(TAG, "populateViewHolder: model.getLocation() " +
model.getLocation());
viewHolder.setTOA(model.getTOA());
// Log.d(TAG, "populateViewHolder: model.getTOA()" +
model.getTOA());
viewHolder.setNOC(model.getNOC());
// Log.d(TAG, "populateViewHolder: model.getNOC()" +
model.getNOC());
viewHolder.setImage(getApplicationContext(),
model.getImage());
// Log.d(TAG, "populateViewHolder: model.getImage() " +
model.getImage());
viewHolder.mCurrentLoc.setOnClickListener(new
View.OnClickListener() {
@Override
        public void onClick(View v) {
final String post_key = getRef(position).getKey();
// Log.d(TAG, "onClick: post_key: " + post_key);
Intent geo_intent = new Intent(MainActivity.this,
MapsActivity1.class);
Bundle bundle = new Bundle();
bundle.putString("postKey", post_key);
geo_intent.putExtras(bundle);
startActivity(geo_intent);
}
});
viewHolder.mShare.setOnClickListener(new
View.OnClickListener() {
@Override
public void onClick(View v) {
final String post_key1 = getRef(position).getKey();
mDatabase.child(post_key1).addValueEventListener(new
ValueEventListener() {
@Override
public void onDataChange(DataSnapshot
dataSnapshot) {
String location =
String.valueOf(dataSnapshot.child("Location").getValue());
String toa =
String.valueOf(dataSnapshot.child("TOA").getValue());
String noc =
String.valueOf(dataSnapshot.child("NOC").getValue());
String img_link =
String.valueOf(dataSnapshot.child("Image").getValue());
Intent share_intent = new
Intent(Intent.ACTION_SEND);
share_intent.setType("text/plain");
share_intent.putExtra(Intent.EXTRA_SUBJECT,
"Accident");
share_intent.putExtra(Intent.EXTRA_TEXT,
"Location:" + location + "\n" + toa + "\n" + noc + "\n" + "Image Link:" +
img_link );
startActivity(Intent.createChooser(share_intent, "Share Using"));
}
@Override
public void onCancelled(DatabaseError
databaseError) {
}
});
}
});
  }
};
mPostList.setAdapter(firebaseRecyclerAdapter);
}
private void sendToStart() {
Intent start_intent = new Intent(MainActivity.this,
StartActivity.class);
startActivity(start_intent);
finish();
}
public static class BlogViewHolder extends RecyclerView.ViewHolder {
View mView;
private Button mCurrentLoc;
private Button mShare;
public BlogViewHolder(View itemView) {
super(itemView);
mView = itemView;
mCurrentLoc = (Button) mView.findViewById(R.id.current_loc);
mShare = (Button) mView.findViewById(R.id.share);
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
Intent intent = new Intent(MainActivity.this,
MapsActivity.class);
startActivity(intent);
finish();
}
if (item.getItemId() == R.id.logout) {
mAuth.signOut();
sendToStart();
}
return super.onOptionsItemSelected(item);
}
}
