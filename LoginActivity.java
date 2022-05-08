package com.example.sarahshaikh.goldenhourresponse;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class LoginActivity extends AppCompatActivity {
private EditText mEmail, mPassword,mPhone;
private Button mLoginBtn;
private static final String TAG = "Zainab";
private FirebaseAuth mAuth;
private DatabaseReference mDatabase;
String account;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_login);
mAuth = FirebaseAuth.getInstance();
mDatabase =
FirebaseDatabase.getInstance().getReference().child("Location");
mEmail = (EditText) findViewById(R.id.log_email);
mPassword = (EditText) findViewById(R.id.log_password);
mLoginBtn = (Button) findViewById(R.id.log_loginBtn);
// mPhone = (EditText) findViewById(R.id.phone_login);
mLoginBtn.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
String email = mEmail.getText().toString();
String password = mPassword.getText().toString();
// String phone = mPhone.getText().toString();
if(!TextUtils.isEmpty(email) ||
!TextUtils.isEmpty(password)) {
mAuth.signInWithEmailAndPassword(email, password)
.addOnCompleteListener(new
OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull
Task<AuthResult> task) {
if (task.isSuccessful()) {
// Sign in success, update UI with
the signed-in user's information
Log.d(TAG,
"signInWithEmail:success");
  // FirebaseUser user =
mAuth.getCurrentUser();
String user =
mAuth.getCurrentUser().getUid();
mDatabase.child(user).child("account").addValueEventListener(new
ValueEventListener() {
@Override
public void
onDataChange(DataSnapshot dataSnapshot) {
account =
String.valueOf(dataSnapshot.getValue());
switch(account){
case "User/Volunteer":{
Intent maiin_intent =
new Intent(LoginActivity.this, MainActivity.class);
startActivity(maiin_intent);
finish();
}break;
case "Resource":{
Intent maiin_intent =
new Intent(LoginActivity.this, Resource.class);
startActivity(maiin_intent);
finish();
}break;
}
}
@Override
public void
onCancelled(DatabaseError databaseError) {
}
});
} else {
// If sign in fails, display a
message to the user.
Log.w(TAG,
"signInWithEmail:failure", task.getException());
Toast.makeText(LoginActivity.this,
"Authentication failed.",
Toast.LENGTH_SHORT).show();
}
}
