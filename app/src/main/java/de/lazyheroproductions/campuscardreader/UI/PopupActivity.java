package de.lazyheroproductions.campuscardreader.UI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import de.lazyheroproductions.campuscardreader.Logic.CardReaderIntentService;
import de.lazyheroproductions.campuscardreader.R;

public class PopupActivity extends AppCompatActivity {

    private double credit;
    private double lastTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get an instance of the local broadcast manager to receive messages which are send inside the application
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(CardReaderIntentService.CAMPUS_CARD_INTENT));
        startNfcIntentService(getIntent());
    }

    private void startNfcIntentService(Intent intent){
        // as far as I can tell is a double check with
        // NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
        // unnecessary, but this is an assumption
        // if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
            Intent newIntent = new Intent(getApplicationContext(), CardReaderIntentService.class);
            newIntent.putExtra(NfcAdapter.EXTRA_TAG, intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
            startService(newIntent);
        }
    }

    private void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("CampusCard gelesen")
                .setMessage("Du hast geld")
                .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNeutralButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        private Intent intent;

        @Override
        public void onReceive(Context context, Intent intent) {
            this.intent = intent;
            switch(intent.getIntExtra(CardReaderIntentService.STATUS_CODE, CardReaderIntentService.STATUS_OK)){
                case CardReaderIntentService.STATUS_OK: updateInfo(); break;
                case CardReaderIntentService.STATUS_UNKNOWN_ERROR: unknownError(); break;
                case CardReaderIntentService.STATUS_UNKNOWN_NFC_CARD_ERROR: unsupportedError(); break;
                default: break;
            }
        }

        private void updateInfo(){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.new_card_was_read), Toast.LENGTH_SHORT).show();
            credit = intent.getDoubleExtra(CardReaderIntentService.CREDIT,0);
            lastTransaction = intent.getDoubleExtra(CardReaderIntentService.LAST_TRANSACTION,0);
            showDialog();
        }

        public void unknownError(){
            // there was an error while reading the nfc tag, show it to the user
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_read_card), Toast.LENGTH_SHORT).show();
        }

        public void unsupportedError(){
            // an unknown card was read
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknown_card_was_read), Toast.LENGTH_SHORT).show();
        }
    };
}
