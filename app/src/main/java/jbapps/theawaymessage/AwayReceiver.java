package jbapps.theawaymessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Justin on 8/14/14.
 */
public class AwayReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        String intentName = intent.getAction();

        System.out.println("Received a text message!!");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[])bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            }
            if (messages.length > -1) {
                System.out.println("Message recieved: " + messages[0].getMessageBody());
            }

            //pass them onto our service

            if (AwayService.isServiceRunning()) {
                Toast.makeText(context, "Replied to some text messages!", Toast.LENGTH_LONG).show();
                AwayService.getSharedService().receivedMessages(context, messages);
            }
        }

        System.out.println(intentName);
    }
}
