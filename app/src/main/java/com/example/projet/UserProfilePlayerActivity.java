package com.example.projet;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;

public class UserProfilePlayerActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_CODE_SEND_SMS = 1;
    private UserModel playerProfile;

    private TeamModel team;
    private TournamentModel tournament;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_player);

        team = (TeamModel) getIntent().getSerializableExtra("TeamModel");
        tournament = (TournamentModel) getIntent().getSerializableExtra("TournamentModel");
        key = getIntent().getStringExtra("key");

        playerProfile = (UserModel) getIntent().getSerializableExtra("player");

        Button sendButton = findViewById(R.id.sendMessageButton);
        TextView name = findViewById(R.id.namesLabelPlayer);
        TextView phone = findViewById(R.id.phoneNumberPlayer);
        ImageView avatarImage = findViewById(R.id.avatarImagePlayer);

        String display = playerProfile.firstName + " " + playerProfile.lastName;
        name.setText(display);
        if (!playerProfile.phoneNumber.isEmpty()) {
            phone.setText(playerProfile.phoneNumber);
        } else {
            phone.setText(getString(R.string.unknown));
        }

        sendButton.setOnClickListener(view -> askPermissionAndSendSMS());
        if(playerProfile.getUri().equals("default")) {
            avatarImage.setImageResource(R.drawable.avatar);
        }
        else {
            Picasso.get().load(Uri.parse(playerProfile.getUri())).into(avatarImage);
        }
    }

    public void askPermissionAndSendSMS() {
        // With Android Level >= 23, you have to ask the user
        // for permission to send SMS.
        // 23

        // Check if we have send SMS permission
        int sendSmsPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        if (sendSmsPermission != PackageManager.PERMISSION_GRANTED) {
            // If don't have permission so prompt the user.
            this.requestPermissions(
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSION_REQUEST_CODE_SEND_SMS
            );
            return;
        }
        this.alertSms();
    }

    private void alertSms() {
        if (!playerProfile.phoneNumber.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfilePlayerActivity.this);
            builder.setTitle(getString(R.string.send_message))
                    .setMessage(getString(R.string.send_message_message));
            final EditText messageInputDialog = new EditText(UserProfilePlayerActivity.this);
            messageInputDialog.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            messageInputDialog.setSingleLine(false);
            builder.setView(messageInputDialog);

            builder.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> this.sendSMS_by_smsManager(messageInputDialog));
            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
            builder.create().show();
        }

    }

    @SuppressLint("RestrictedApi")
    private void sendSMS_by_smsManager(EditText messageInputDialog) {

        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            // Send Message
            smsManager.sendTextMessage(playerProfile.getPhoneNumber(),
                    null,
                    messageInputDialog.getText().toString(),
                    null,
                    null);

            Log.i(LOG_TAG, "Your sms has successfully sent!");
            Toast.makeText(getApplicationContext(), getString(R.string.message_confirm),
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Your sms has failed...", ex);
            Toast.makeText(getApplicationContext(), getString(R.string.error_send_message) + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    // When you have the request results
    @SuppressLint("RestrictedApi")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        if (requestCode == MY_PERMISSION_REQUEST_CODE_SEND_SMS) {// Note: If request is cancelled, the result arrays are empty.
            // Permissions granted (SEND_SMS).
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(LOG_TAG, "Permission granted!");
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_LONG).show();

                this.alertSms();
            }
            // Cancelled or denied.
            else {
                Log.i(LOG_TAG, "Permission denied!");
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public void onBackPressed() {
        Intent teamIntent = new Intent(this, TeamPlayersActivity.class);
        teamIntent.putExtra("TeamModel", team);
        teamIntent.putExtra("TournamentModel", tournament);
        teamIntent.putExtra("key", key);
        startActivity(teamIntent);
        finish();
    }
}