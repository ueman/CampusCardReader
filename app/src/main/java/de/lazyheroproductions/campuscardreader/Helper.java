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

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.preference.PreferenceManager;

public class Helper {

    public static boolean isNfcEnabled(Context c){
        NfcAdapter adapter = ((NfcManager) c.getSystemService(Context.NFC_SERVICE)).getDefaultAdapter();
        return (adapter != null && adapter.isEnabled());
    }

    public static void showEnableNfcDialog(final Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(R.string.turn_on_nfc_message)
                .setTitle(R.string.nfc_is_turned_off)
                .setPositiveButton(R.string.nfc_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (android.os.Build.VERSION.SDK_INT >= 16) {
                            c.startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                        } else {
                            c.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing to do here
                    }
                })
                .create().show();
    }

    public static void ratingCounter(final Context c){
        final String COUNT = "ratingCounter";
        final String DIALOG_WAS_OPENED = "ratingWasOpened";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        if(pref.getInt(COUNT, 0)>4 && !pref.getBoolean(DIALOG_WAS_OPENED,false)){
            pref.edit().putBoolean(DIALOG_WAS_OPENED, true).apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setMessage(R.string.do_you_want_to_rate_this_app)
                    .setPositiveButton(R.string.yes_i_love_it, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rate(c);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.no_dont_ask_me_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            pref.edit().putInt(COUNT,pref.getInt(COUNT,0)+1).apply();
        }
    }

    public static void rate(Context c){
        Uri uri = Uri.parse(Config.PLAY_STORE_URI + c.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            c.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.WEB_PLAY_STORE_URL + c.getPackageName())));
        }
    }

}
