package jbapps.theawaymessage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class AwayService extends Service {

    //list of recent contacts that have been called. this will be refreshed as time passes
    //private List<Contact> recentContacts;
    public Map<String, String> customAwayMessages = new HashMap<String, String>();

    //the current away message
    public String awayMessage = "";

    private static AwayService sharedService;

    //the statistics timer
    public Timer timer;

    public long timeAlive = 0;
    public long textsAnswered = 0;

    public Away delegate;


    public static AwayService optionalGetSharedService(){
        return sharedService;
    }


    public static AwayService getSharedService() {
        if (sharedService == null){
            sharedService = new AwayService();
        }

        return sharedService;
    }

    public static boolean isServiceRunning() {
        return (sharedService != null);
    }

    public static void stopService() {
        sharedService = null;
    }

    public AwayService() {
        sharedService = this;
        //register the singleton

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeAlive++;
                if (delegate != null){
                    delegate.manageStats((int)timeAlive, (int)textsAnswered);
                }
            }
        }, new java.util.Date(), 1);
    }

    public void receivedMessages(Context context, SmsMessage[] messages) {

        //loop through. ideally / hopefully this should only be one text
        for (SmsMessage message : messages){
            String sender = message.getOriginatingAddress();
            //try to respond, see if this sender exists

            String responseMessage = awayMessage;

            if (customAwayMessages.get(sender) != null){
                responseMessage = customAwayMessages.get(sender);
            }

            if (responseMessage == null || responseMessage.length()==0){
                Toast.makeText(context, "Error: Away Message is empty!", Toast.LENGTH_LONG).show();
                return;
            }

            //respond to the text
            SmsManager.getDefault().sendTextMessage(sender, null, responseMessage, null, null);
            textsAnswered++;
            if (delegate != null){
                delegate.manageStats((int)timeAlive, (int)textsAnswered);
            }
        }




    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
