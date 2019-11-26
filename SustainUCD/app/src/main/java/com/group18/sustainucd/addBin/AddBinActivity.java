package com.group18.sustainucd.addBin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.group18.sustainucd.R;

/**
 * This activity shows the picture taken by the user and implements a clickable imageviews to let
 * the user select what kind of things can go inside this bin. This class implements the toolbar
 * and the option menu logic. The rest is implemented in a fragment.
 */
public class AddBinActivity extends AppCompatActivity {

    public static final String PICTURE_PATH = "PicturePath";
    private static String TAG = "AddBinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_bin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Add the menu with the location button functionality
        //if the user has an app that can show the new bin location
        if (HasLocationApp()) {
            getMenuInflater().inflate(R.menu.menu_add_bin, menu);
            return true;
        }
        return false;
    }

    private boolean HasLocationApp() {
        //Simple example location, just for test
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q=37.7749,-122.4194(Bin)");

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");
        return mapIntent.resolveActivity(getPackageManager()) != null;
    }

}