package com.cynobit.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    private DealAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        FirebaseUtil.openFbReference("traveldeals", this);
        RecyclerView rvDeals = findViewById(R.id.rvDeals);
        adapter = new DealAdapter();
        rvDeals.setAdapter(adapter);
        LinearLayoutManager dealsLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        if (FirebaseUtil.loggedInUserEmail != null) {
            setTitle(getResources().getString(R.string.app_name) + " : " + FirebaseUtil.loggedInUserEmail);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        } else {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User Logged Out.");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                FirebaseUtil.launched = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FirebaseUtil.RC_SIGN_IN) {
            invalidateOptionsMenu();
            if (FirebaseUtil.loggedInUserEmail != null) {
                setTitle(getResources().getString(R.string.app_name) + " : " + FirebaseUtil.loggedInUserEmail);
            }
        }
    }

    @Override
    public void onBackPressed() {
        FirebaseUtil.launched = false;
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
        adapter.notifyDataSetChanged();
    }
}
