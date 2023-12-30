package com.fallin.fallin;

import androidx.annotation.NonNull;
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

import com.fallin.fallin.databinding.ActivityMainBinding;
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
        checkPermissions();
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

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 100);
        } else {
            checkSendSMSPermission();
        }
    }

    private void checkSendSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 101);
        } else {
            checkCoarseLocationPermission();
        }
    }

    private void checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 102);
        } else {
            checkFineLocationPermission();
        }
    }

    private void checkFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 103);
        } else {
            checkOverlayAndAccessibilitySettings();
        }
    }

    private void checkOverlayAndAccessibilitySettings() {
        String serviceString = getPackageName() + "/" + BootAccessibilityService.class.getCanonicalName();
        if (!Settings.canDrawOverlays(this)) {
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(overlayIntent);
        }
        if (!isAccessibilityServiceEnabled(this, serviceString)) {
            Intent AccessibilityIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            AccessibilityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            AccessibilityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(AccessibilityIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                checkSendSMSPermission();
                break;
            case 101:
                checkCoarseLocationPermission();
                break;
            case 102:
                checkFineLocationPermission();
                break;
            case 103:
                checkOverlayAndAccessibilitySettings();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }


}
