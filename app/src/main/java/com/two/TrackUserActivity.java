package com.two;


import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TrackUserActivity extends AccessibilityService {

    // Tag for logging
    private static final String TAG = "MyAccessibilityService";

    // Variable to hold the last detected package name
    private String lastPackageName = "";

    // Handler to handle delayed tasks
    private Handler handler = new Handler();

    // Variable to hold the currently potential package name
    private String currentPotentialPackageName = "";

    // Set of package names to ignore (e.g., system UI packages)
    private Set<String> ignoredPackageNames = new HashSet<>(Arrays.asList(
            "com.android.systemui", "com.android.settings", "com.google.android.googlequicksearchbox"
    ));

    // Runnable to handle the debounce logic
    private Runnable debounceRunnable = new Runnable() {
        @Override
        public void run() {
            // If the potential package name has changed, update the last package name and log it
            if (!currentPotentialPackageName.equals(lastPackageName)) {
                lastPackageName = currentPotentialPackageName;
                Log.d(TAG, "Current app package name: " + lastPackageName);
            }
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Check if the event type is window state changed or window content changed
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            // Get the root node info of the currently active window
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                // Extract package name information from the node info
                CharSequence packageName = nodeInfo.getPackageName();
                if (packageName != null) {
                    String packageNameString = packageName.toString();
                    // Check if the package name is not in the ignored package names and has changed
                    if (!ignoredPackageNames.contains(packageNameString) &&
                            !packageNameString.equals(currentPotentialPackageName)) {

                        // Update the current potential package name
                        currentPotentialPackageName = packageNameString;

                        // Remove any pending debounce runnable
                        handler.removeCallbacks(debounceRunnable);

                        // Post the debounce runnable with a delay of 500ms
                        handler.postDelayed(debounceRunnable, 500);
                    }
                }
            }
        }
        // We are checking is the event type is just user click
        else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            Log.d(TAG, "User clicked");

        }// We are checking is the event type is just user long click
        else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            Log.d(TAG, "User long clicked");

        }
        // We are checking is the event type is just user long click
        else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            Log.d(TAG, "User long clicked");

        }
    }

    @Override
    public void onInterrupt() {
        // Remove any pending debounce runnable when the service is interrupted
        handler.removeCallbacks(debounceRunnable);
    }

    @Override
    public void onDestroy() {
        // Clean up by removing any pending debounce runnable when the service is destroyed
        super.onDestroy();
        handler.removeCallbacks(debounceRunnable);
    }

}
