package com.group18.sustainucd.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.group18.sustainucd.Permissions;
import com.group18.sustainucd.database.Bin;
import com.group18.sustainucd.database.BinsManager;
import com.group18.sustainucd.R;
import com.group18.sustainucd.showBin.ShowBinActivity;

import java.util.List;

/**
 * This Fragment implements the user interface and the logic for the home screen
 * A list of bins that are near is showed. First bin is the nearest. The last known location is taken
 * in background. It implements also a floating action button used to take a photo and then add
 * a new bin.
 */
public class HomeFragment extends Fragment implements BinsListAdapter.OnClickListener,
        BinsManager.BinsDatabaseListener, OnSuccessListener<Location> {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private BinsListAdapter adapter;
    //Location client
    private FusedLocationProviderClient client;
    private int howManyBinsToShow = 10;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Location initialization
        client = LocationServices.getFusedLocationProviderClient(getContext());
        adapter = new BinsListAdapter(this, getContext());
        //Ask for access fine location permission, if not already granted
        if (!Permissions.HasAccessFineLocationPermission(getContext()))
            Permissions.AskAccessFineLocationPermission(this, REQUEST_ACCESS_FINE_LOCATION);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //Lookup the recyclerview in activity layout
        recyclerView = (RecyclerView) root.findViewById(R.id.bins_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Get location and then look for nearest bins
        if (Permissions.HasAccessFineLocationPermission(getContext()))
            GetLocation();
        //source: https://developer.android.com/training/swipe/add-swipe-interface
        swipeRefreshLayout = root.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Refresh operation
                        if (Permissions.HasAccessFineLocationPermission(getContext()))
                            Update();
                        else
                            Permissions.AskAccessFineLocationPermission(HomeFragment.this, REQUEST_ACCESS_FINE_LOCATION);
                    }
                }
        );

        return root;
    }

    /** Function that ask again for location in order to update the list of bins */
    private void Update() {
        //Get location and then look for nearest bins
        GetLocation();
    }

    //Event triggered when the user clicks on a bin. This will transition from here to ShowBinActivity
    @Override
    public void OnBinClick(int position) {
        Log.d(TAG, "Bin clicked: "+position);
        Intent showBinIntent = new Intent(getActivity(), ShowBinActivity.class);
        //Set static bitmap
        ShowBinActivity.bitmapToShow = adapter.getBinAtPosition(position).bitmap;

        showBinIntent.putExtra(ShowBinActivity.PICTURE_PATH, adapter.getBinAtPosition(position).pictureFileName);
        showBinIntent.putExtra(ShowBinActivity.LATITUDE, adapter.getBinAtPosition(position).latitude);
        showBinIntent.putExtra(ShowBinActivity.LONGITUDE, adapter.getBinAtPosition(position).longitude);
        showBinIntent.putExtra(ShowBinActivity.PAPER, adapter.getBinAtPosition(position).paper);
        showBinIntent.putExtra(ShowBinActivity.FOOD, adapter.getBinAtPosition(position).food);
        showBinIntent.putExtra(ShowBinActivity.PLASTIC, adapter.getBinAtPosition(position).plastic);
        showBinIntent.putExtra(ShowBinActivity.GLASS, adapter.getBinAtPosition(position).glass);
        showBinIntent.putExtra(ShowBinActivity.BATTERY, adapter.getBinAtPosition(position).battery);
        showBinIntent.putExtra(ShowBinActivity.ELECTRONICS, adapter.getBinAtPosition(position).electronic);

        startActivity(showBinIntent);
    }

    //Event triggered when the database is loaded
    @Override
    public void OnBinsDatabaseLoaded() {
        Log.i(TAG, "Database loaded");
        SetBinsToShow();
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
        }
    }

    // Ask for last location and attach this fragment to the OnSuccess Listener
    private void GetLocation()
    {
        client.getLastLocation().addOnSuccessListener(this);
    }

    //Calculate the distance of all the bins and then create a subset of howManyBinsToShow bins
    //sorted by distance. Show this on screen.
    private void SetBinsToShow() {
        BinsManager.CalculateDistances(adapter.getCurrentLatitude(), adapter.getCurrentLongitude());
        List<Bin> binsToShow = BinsManager.GetNearestKBins(howManyBinsToShow,
                adapter.getCurrentLatitude(), adapter.getCurrentLongitude());

        adapter.SetList(binsToShow);
        Log.i(TAG, adapter.getItemCount()+" bins");
    }

    //Success on location request
    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            adapter.SetLocation(location.getLatitude(), location.getLongitude());
            Log.i(TAG, "Location OnSuccess");
            if (!BinsManager.HasBeenInitialized())
                BinsManager.Initialize(getActivity(), HomeFragment.this);
            else
                OnBinsDatabaseLoaded();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Not Refreshed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Pressing on the refresh button will update the list
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Refresh operations
        if (item.getItemId() == R.id.action_refresh) {
            swipeRefreshLayout.setRefreshing(true);
            //Refresh operation
            if (Permissions.HasAccessFineLocationPermission(getContext()))
                Update();
            else
                Permissions.AskAccessFineLocationPermission(HomeFragment.this, REQUEST_ACCESS_FINE_LOCATION);
        }
        return super.onOptionsItemSelected(item);
    }

    //If the location permission has been granted then look for the last known location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetLocation();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED &&
                    swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}