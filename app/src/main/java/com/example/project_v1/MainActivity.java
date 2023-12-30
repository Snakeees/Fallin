package com.example.project_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.project_v1.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    public BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        nav = findViewById(R.id.bottomNavigationView);

        Intent intent = getIntent();
        int frag = intent.getIntExtra("frag", 0);
        Log.i("Frag ID", String.valueOf(frag));

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        if (frag == 1 && !(currentFragment instanceof WarningFragment)) {
            replaceFragment(new WarningFragment());
        } else if (!(currentFragment instanceof HomeFragment)) {
            replaceFragment(new HomeFragment());
        }
        setupBottomNavigationView();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 100);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 101);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 102);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 103);
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(overlayIntent);
        }
        String serviceString = getPackageName() + "/" + BootAccessibilityService.class.getCanonicalName();
        if (!isAccessibilityServiceEnabled(this, serviceString)) {
            Intent AccessibilityIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(AccessibilityIntent);
        }

    }



    private void setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_button:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.info_button:
                    replaceFragment(new InfoFragment());
                    break;
            }
            return true;
        });
    }

    public boolean isAccessibilityServiceEnabled(Context context, String service) {
        String enabledServices = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabledServices != null && enabledServices.contains(service);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment existingFragment : fragmentManager.getFragments()) {
            fragmentTransaction.remove(existingFragment);
        }
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.details) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if (!(currentFragment instanceof DetailsFragment)) {
                replaceFragment(new DetailsFragment());
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void hide() {
        if (nav.getVisibility() == View.VISIBLE) {
            nav.setVisibility(View.GONE);
        }
    }

    public void unhide() {
        if (nav.getVisibility() != View.VISIBLE) {
            nav.setVisibility(View.VISIBLE);
        }
    }

    public void setSelected() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof HomeFragment) {
            if (nav.getSelectedItemId() != R.id.home_button) {
                nav.setSelectedItemId(R.id.home_button);
            }
        } else if (currentFragment instanceof InfoFragment) {
            if (nav.getSelectedItemId() != R.id.info_button) {
                nav.setSelectedItemId(R.id.info_button);
            }
        } else {
            if (nav.getSelectedItemId() != R.id.none_button) {
                nav.setSelectedItemId(R.id.none_button);
            }
        }
    }

}
