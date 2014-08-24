package jbapps.theawaymessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


public class Away extends Activity {

    public Away this_again = this;

    public LinearLayout stats = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away);

        Switch onSwitch = (Switch)findViewById(R.id.onSwitch);
        onSwitch.setChecked(AwayService.isServiceRunning());

        final EditText editText = (EditText)findViewById(R.id.editText);

        stats = (LinearLayout)findViewById(R.id.statsBoard);
        stats.setVisibility(View.INVISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                AwayService.getSharedService().awayMessage = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        onSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    //start service
                    startService(new Intent(this_again, AwayService.class));

                    //set the general services away message from the current text
                    AwayService.getSharedService().awayMessage = editText.getText().toString();

                    stats.setVisibility(View.VISIBLE);
                    getInitialStats();

                } else {
                    //stop the service
                    stopStatistics();
                    stopService(new Intent(this_again, AwayService.class));
                    AwayService.stopService();
                    stats.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.away, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getInitialStats() {

        int messages = (int)AwayService.getSharedService().textsAnswered;
        int time = (int)AwayService.getSharedService().timeAlive;
        ((TextView)findViewById(R.id.numberTextsAnswered)).setText(messages + "");
        ((TextView)findViewById(R.id.timeEnabled)).setText(time + "");
        AwayService.getSharedService().delegate = this;
    }

    public void manageStats(int timeElapsed, int messages){
        //sets up the correct observers
        System.out.println("Manage stats called.");
        ((TextView)findViewById(R.id.numberTextsAnswered)).setText(messages + "");
        ((TextView)findViewById(R.id.timeEnabled)).setText(timeElapsed + "");

    }

    public void stopStatistics(){
        AwayService.getSharedService().delegate = null;
    }


}
