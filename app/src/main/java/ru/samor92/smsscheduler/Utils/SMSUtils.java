package ru.samor92.smsscheduler.Utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import java.util.ArrayList;

/**
 * Created by Alexander Samorokovsky on 18.09.2015.
 */
public class SMSUtils {

    private static final String SENT_INTENT = "SENT_INTENT";
    private static final String DELIVERY_INTENT = "DELIVERY_INTENT";

    private static final int UNEXPECTED_ERROR = -100;

    public static int sendMessage(String address, String message, Context context) {
        try {
            final int[] resultCode = {0};

            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SENT_INTENT), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERY_INTENT), PendingIntent.FLAG_UPDATE_CURRENT);
            context.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    resultCode[0] = getResultCode();
                }
            }, new IntentFilter(SENT_INTENT));

            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                }
            }, new IntentFilter(DELIVERY_INTENT));

            smsManager.sendTextMessage(address, null, message.trim(), sentIntent, deliveryIntent);
            return resultCode[0];
        } catch (Exception ex) {
            ex.printStackTrace();
            return UNEXPECTED_ERROR;
        }
    }

    public static int sendMessage(String[] address, String message, Context context) {
        int resultCode = 0;
        for (String _address : address) {
            resultCode = sendMessage(_address, message, context);
        }
        return resultCode;
    }

    public static int sendMultipartMessage(String address, String message, Context context){
        final int[] resultCode = {0};

        try{
            SmsManager smsManager = SmsManager.getDefault();

            ArrayList<String> parts = smsManager.divideMessage(message);
            int numParts = parts.size();

            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();

            Intent mSendIntent = new Intent(SENT_INTENT);
            Intent mDeliveryIntent = new Intent(DELIVERY_INTENT);

            for (int i = 0; i < numParts; i++) {
                sentIntents.add(PendingIntent.getBroadcast(context, 0, mSendIntent, 0));
                deliveryIntents.add(PendingIntent.getBroadcast(context, 0, mDeliveryIntent, 0));
            }

            context.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    resultCode[0] = getResultCode();
                }
            }, new IntentFilter(SENT_INTENT));

            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                }
            }, new IntentFilter(DELIVERY_INTENT));

            smsManager.sendMultipartTextMessage(address, null, parts, sentIntents, deliveryIntents);

            return resultCode[0];
        } catch (Exception ex) {
            ex.printStackTrace();
            return UNEXPECTED_ERROR;
        }
    }

    public static int sendMultipartMessage(String[] address, String message, Context context){
        int resultCode = 0;
        for (String _address : address) {
            resultCode = sendMultipartMessage(_address, message, context);
        }
        return resultCode;
    }
}
