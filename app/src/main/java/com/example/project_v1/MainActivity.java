package com.example.project_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.example.project_v1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int CHECK_PERMISSION_DELAY_MS = 5000;  // 5 seconds




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




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
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (!allPermissionsGranted()) {
            // If Call permission not granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 100);
            }

            // If SMS permission not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 101);
            }

            // If overlay permission not granted
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }

            // If accessibility service not enabled
            String serviceString = getPackageName() + "/" + BootAccessibilityService.class.getCanonicalName();
            if (!isAccessibilityServiceEnabled(this, serviceString)) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }

            handler.postDelayed(this::checkAndRequestPermissions, CHECK_PERMISSION_DELAY_MS);
        }
    }

    private boolean allPermissionsGranted() {
        boolean callPermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
        boolean smsPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        boolean overlayPermissionGranted = Settings.canDrawOverlays(this);
        String serviceString = getPackageName() + "/" + BootAccessibilityService.class.getCanonicalName();
        boolean accessibilityEnabled = isAccessibilityServiceEnabled(this, serviceString);

        return callPermissionGranted && smsPermissionGranted && overlayPermissionGranted && accessibilityEnabled;
    }

// ... Other code, including your isAccessibilityServiceEnabled() method ...


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

}
