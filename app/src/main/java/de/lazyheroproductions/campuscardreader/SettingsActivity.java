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
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends ActionBarActivity {

    private static final String LICENSE_FILE_PATH = "file:///android_asset/license.html";
    private static final String SHARED_PREFERENCES_KEY = "unitSharedPreferences";
    private static final String CURRENCY_UNIT_KEY = "unit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // set an on click listener for the see_source_button
        findViewById(R.id.see_source_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PROJECT_HOME)));
            }
        });
        // set an on click listener for the rate_button
        findViewById(R.id.about_screen_rate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send the user to the play store or otherwise to the play web store
                Uri uri = Uri.parse(Config.PLAY_STORE_URI + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.WEB_PLAY_STORE_URL + getPackageName())));
                }
            }
        });
        findViewById(R.id.visit_website_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.AUTHOR_WEBSITE)));
            }
        });
        findViewById(R.id.reset_database_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreditDatabase cDb = new CreditDatabase(getApplicationContext());
                cDb.resetDatabase();
            }
        });
        ((TextView)findViewById(R.id.app_version_textview)).setText(getResources().getText(R.string.app_version) + " " + BuildConfig.VERSION_NAME);
        findViewById(R.id.license_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLicenseClick();
            }
        });

        ((EditText)findViewById(R.id.currency_edittext)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SettingsActivity.saveUnit(getApplicationContext(), s.toString());
            }
        });
    }

    private void onLicenseClick(){
        WebView wv = new WebView(this);
        wv.loadUrl(LICENSE_FILE_PATH);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.license)
            .setView(wv)
            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
                }
            })
            .show();
    }

    public static void saveUnit(Context activityContext, String unitString){
        SharedPreferences sharedPref = activityContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CURRENCY_UNIT_KEY, unitString);
        editor.apply();
        Toast.makeText(activityContext,activityContext.getString(R.string.saved),Toast.LENGTH_SHORT).show();
    }

    public static String getUnit(Context activityContext){
        return activityContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).getString(CURRENCY_UNIT_KEY, activityContext.getString(R.string.currency));
    }

}
