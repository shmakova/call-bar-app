package ru.shmakova.callbarapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import ru.shmakova.callbarapp.R;

/**
 * Created by shmakova on 22.07.16.
 */

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";
    private Context context;
    private View popupView;
    private WindowManager windowManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                if (state == TelephonyManager.CALL_STATE_RINGING){
                    Log.d(TAG, incomingNumber);

                    if (!incomingNumber.isEmpty()) {
                        showPhoneNumberInfoPopup(incomingNumber);
                    }
                } else {
                    hidePhoneNumberInfoPopup();
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

    }

    /**
     * Show phone number info
     * @param incomingNumber
     */
    private void showPhoneNumberInfoPopup(String incomingNumber) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.OPAQUE);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (popupView == null) {
            popupView = inflater.inflate(R.layout.popup, null);

            TextView phoneTextView = (TextView) popupView.findViewById(R.id.phone);
            phoneTextView.setText(incomingNumber);

            windowManager.addView(popupView, layoutParams);
        }

    }

    /**
     * Hides phone number info popup
     */
    private void hidePhoneNumberInfoPopup() {
        if (popupView != null && windowManager != null) {
            windowManager.removeView(popupView);
            windowManager = null;
            popupView = null;
        }
    }
}
