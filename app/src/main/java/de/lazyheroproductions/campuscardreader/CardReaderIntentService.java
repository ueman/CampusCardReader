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
import android.widget.Toast;

import java.io.IOException;

public class CardReaderIntentService extends IntentService {

    private static final String SERVICE_NAME = "CardReaderIntentService";
    public static final String CAMPUS_CARD_INTENT = "campusCardIntent";
    public static final String CREDIT = "credit";
    public static final String LAST_TRANSACTION = "lastTransaction";
    public static final String ERROR = "error";

    private byte[] selectAid = {(byte)90, (byte)95, (byte)-124, (byte)21};      //select application id command
    private byte[] creditPayload = {(byte)108, (byte)1};                        //select credit file
    private byte[] transactionPayload = {(byte)-11, (byte)1};                   //select last transaction file

    private byte[] resultOk;
    private byte[] creditBytes;
    private byte[] lastTransactionBytes;

    public CardReaderIntentService(){
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(this.getClass().getName(), "CardReaderService started");
        IsoDep isodep = IsoDep.get((Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        try {
            isodep.connect();
            resultOk = isodep.transceive(selectAid);
            if(resultOk[0]==0) {
                creditBytes = isodep.transceive(creditPayload);
                Log.i(this.getClass().getName(), creditBytes.toString());
                lastTransactionBytes = isodep.transceive(transactionPayload);
            }else{
                Log.w(this.getClass().getName(),"Wrong result");
            }
            isodep.close();
        }catch(IOException e) {
            Log.w(this.getClass().getName(), "Failed to receive data");
            Toast.makeText(this, "Failed to read the nfc tag", Toast.LENGTH_SHORT).show();
        }
        sendBroadcast();
    }

    private void sendBroadcast(){
        Intent intent = new Intent(CardReaderIntentService.CAMPUS_CARD_INTENT);
        if(creditBytes[0]==0&&lastTransactionBytes[0]==0){
            intent.putExtra(CardReaderIntentService.CREDIT, formatCredit(creditBytes));
            intent.putExtra(CardReaderIntentService.LAST_TRANSACTION, formatTransaction(lastTransactionBytes));
            intent.putExtra(CardReaderIntentService.ERROR, false);
        }else{
            intent.putExtra(CardReaderIntentService.ERROR, true);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String formatCredit(byte[] array){
        double balanceDouble = (double)(((0xff & array[4]) << 24) + ((0xff & array[3]) << 16) + ((0xff & array[2]) << 8) + (0xff & array[1])) / 1000D;
        return String.format("%.2f\u20AC", balanceDouble);
    }

    private String formatTransaction(byte[] array){
        double transactionDouble = (double)(((0xff & array[16]) << 24) + ((0xff & array[15]) << 16) + ((0xff & array[14]) << 8) + (0xff & array[13])) / 1000D;
        return String.format("%.2f\u20AC", transactionDouble);
    }
}
