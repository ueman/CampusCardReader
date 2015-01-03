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

import android.app.IntentService;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

public class CardReaderIntentService extends IntentService {

    private static final String SERVICE_NAME = "CardReaderIntentService";
    public static final String CAMPUS_CARD_INTENT = "campusCardIntent";
    public static final String CREDIT = "credit";
    public static final String LAST_TRANSACTION = "lastTransaction";
    public static final String UNKNOWN_ERROR = "error";
    private boolean unknownErrorBool = false;
    public static final String UNKNOWN_CAMPUS_CARD_ERROR = "unknownCampusCardError";
    private boolean unknownCampusCardErrorBool = false;
    private static final String FORMAT_TYPE = "%.2f\u20AC"; // two numbers after the comma and a €-sign

    // commands which needs to be send to the nfc tag
    private final byte[] selectAid = {(byte)90, (byte)95, (byte)-124, (byte)21};      //select application command
    private final byte[] creditPayload = {(byte)108, (byte)1};                        //select credit file
    private final byte[] transactionPayload = {(byte)-11, (byte)1};                   //select last transaction file

    // this are the responses of the nfc tag
    private byte[] resultOk;
    private byte[] creditBytes;
    private byte[] lastTransactionBytes;

    public CardReaderIntentService(){
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(BuildConfig.DEBUG) {
            Log.i(this.getClass().getName(), "CardReaderService started");
        }

        // get an instance of the nfc tag to communicate
        IsoDep isodep = IsoDep.get((Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        try {
            // connect to the nfc tag
            isodep.connect();
            // select application which contains the credit and last transaction
            resultOk = isodep.transceive(selectAid);
            if(resultOk[0]==0) {
                // get the credit
                creditBytes = isodep.transceive(creditPayload);
                // get the last transaction
                lastTransactionBytes = isodep.transceive(transactionPayload);
            }else{
                if(BuildConfig.DEBUG) {
                    Log.w(this.getClass().getName(), "Wrong result: "+arrayToString(resultOk));
                }
            }
            isodep.close();
        }catch(IOException e) {
            if(BuildConfig.DEBUG) {
                Log.e(this.getClass().getName(), e.getMessage());
            }
            unknownErrorBool = true;
        }catch(NullPointerException e){
            if(BuildConfig.DEBUG) {
                Log.e(this.getClass().getName(), e.getMessage());
            }
            unknownCampusCardErrorBool = true;
        }

        // send the gathered data back to the activity
        sendBroadcast();
    }

    private void sendBroadcast(){
        Intent intent = new Intent(CAMPUS_CARD_INTENT);
        if(creditBytes!= null && lastTransactionBytes != null && creditBytes[0] == 0 && lastTransactionBytes[0] == 0){
            intent.putExtra(CREDIT, formatCredit(creditBytes));
            intent.putExtra(LAST_TRANSACTION, formatTransaction(lastTransactionBytes));
            intent.putExtra(UNKNOWN_ERROR, unknownErrorBool);
        }else{
            intent.putExtra(UNKNOWN_ERROR, unknownErrorBool);
            intent.putExtra(UNKNOWN_CAMPUS_CARD_ERROR, unknownCampusCardErrorBool);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String formatCredit(byte[] array){
        double balanceDouble = (double)(((0xff & array[4]) << 24) + ((0xff & array[3]) << 16) + ((0xff & array[2]) << 8) + (0xff & array[1])) / 1000D;
        return String.format(FORMAT_TYPE, balanceDouble);
    }

    private String formatTransaction(byte[] array){
        double transactionDouble = (double)(((0xff & array[16]) << 24) + ((0xff & array[15]) << 16) + ((0xff & array[14]) << 8) + (0xff & array[13])) / 1000D;
        return String.format(FORMAT_TYPE, transactionDouble);
    }

    private String arrayToString(byte[] array){
        String s = "";
        for(int i= 0; i< array.length;i++){
            s += array[i]+" ";
        }
        return s;
    }
}
