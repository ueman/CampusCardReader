/**
 * Copyright 2014 Jonas Uekoetter
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/


package de.lazyheroproductions.campuscardreader;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String [][] mTechLists;
    private TextView creditTextView;
    private TextView transactionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get an instance of the local broadcast manager to receive messages which are send inside the application
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(CardReaderIntentService.CAMPUS_CARD_INTENT));
        creditTextView = (TextView)findViewById(R.id.credit);
        transactionTextView = (TextView)findViewById(R.id.last_transaction);

        if(BuildConfig.DEBUG) {
            Log.i(this.getClass().getName(), "received intent on create");
        }
        startNfcIntentService(getIntent());

        // intercept all NFC related Intents and redirect them to this activity while this activity is activ and on the front
        // this is called the "foreground dispatch"
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        IntentFilter  nfcTech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{nfcTech};
        mTechLists = new String[][] {
                new String[] { IsoDep.class.getName() },
                {NfcA.class.getName()},
                {NdefFormatable.class.getName()}
        };
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(BuildConfig.DEBUG) {
            Log.i(this.getClass().getName(), "received intent on new intent");
        }
        startNfcIntentService(intent);
    }

    private void startNfcIntentService(Intent intent){
        // as far as I can tell is a double check with
        // "android.nfc.action.TECH_DISCOVERED".equals(intent.getAction()) || "android.nfc.action.TAG_DISCOVERED".equals(intent.getAction())
        // unnecessary, but take this as an assumption
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Intent newIntent = new Intent(getApplicationContext(), CardReaderIntentService.class);
            newIntent.putExtra(NfcAdapter.EXTRA_TAG, intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
            startService(newIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // re-enable foreground dispatch
        if (mAdapter != null){
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // disable foreground dispatch
        if (mAdapter != null){
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_about: startActivity(new Intent(this, AboutActivity.class)); break;
            //case R.id.action_settings: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if(!intent.getBooleanExtra(CardReaderIntentService.ERROR,true)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.new_card_was_read), Toast.LENGTH_SHORT).show();
                // update textviews to represent the received data
                creditTextView.setText(getResources().getText(R.string.credit) + " " + intent.getStringExtra(CardReaderIntentService.CREDIT));
                transactionTextView.setText(getResources().getText(R.string.last_transaction) + " " + intent.getStringExtra(CardReaderIntentService.LAST_TRANSACTION));
                if(BuildConfig.DEBUG) {
                    Log.d(this.getClass().getName(), "Got message");
                }
            }else{
                // there was an error while reading the nfc tag, show it to the user
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_read_tag), Toast.LENGTH_SHORT).show();
            }
        }
    };

}
