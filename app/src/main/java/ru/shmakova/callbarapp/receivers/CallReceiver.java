package ru.shmakova.callbarapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.shmakova.callbarapp.R;

/**
 * Created by shmakova on 22.07.16.
 */

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";
    private static final String BASE_URL = "https://yandex.ru/search/";

    private Context context;
    private View popupView;
    private ProgressBar progressBar;
    private TextView phoneTextView;
    private TextView phoneInfo;
    private Button closeButton;
    private WindowManager windowManager;
    private String phoneNumber;
    private static boolean isCalling = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if (!isCalling) {
                        isCalling = true;
                        Log.d(TAG, incomingNumber);
                        phoneNumber = incomingNumber;
                        showPhoneNumberInfoPopup(phoneNumber);
                    }
                } else {
                    isCalling = false;
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
        hidePhoneNumberInfoPopup();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.OPAQUE);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popupView = inflater.inflate(R.layout.popup, null);

        phoneTextView = (TextView) popupView.findViewById(R.id.phone);
        phoneTextView.setText(incomingNumber);

        progressBar = (ProgressBar) popupView.findViewById(R.id.progress_bar);

        phoneInfo = (TextView) popupView.findViewById(R.id.info);
        new LoadPhoneInfoTask().execute(incomingNumber);

        closeButton = (Button) popupView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePhoneNumberInfoPopup();
            }
        });

        windowManager.addView(popupView, layoutParams);
    }

    /**
     * Hides phone number info popup
     */
    private void hidePhoneNumberInfoPopup() {
        if (popupView != null && windowManager != null) {
            windowManager.removeView(popupView);
            windowManager = null;
        }
    }

    private class LoadPhoneInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = context.getString(R.string.no_information);

            try {
                Document doc = Jsoup.connect(BASE_URL)
                        .data("text", strings[0])
                        .timeout(5000)
                        .get();

                Element phoneInfoElement = doc.select(".organic .organic__text").first();

                if (phoneInfoElement != null) {
                    result = phoneInfoElement.text();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            phoneInfo.setText(result);
        }
    }
}
