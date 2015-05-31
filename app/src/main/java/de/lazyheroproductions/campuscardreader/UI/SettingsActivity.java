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

package de.lazyheroproductions.campuscardreader.UI;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import de.lazyheroproductions.campuscardreader.BuildConfig;
import de.lazyheroproductions.campuscardreader.Config;
import de.lazyheroproductions.campuscardreader.Logic.CreditDatabase;
import de.lazyheroproductions.campuscardreader.R;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LICENSE_FILE_PATH = "file:///android_asset/license.html";
    private static final String SHARED_PREFERENCES_KEY = "unitSharedPreferences";
    private static final String CURRENCY_UNIT_KEY = "unit";
    private static final String ORDER_KEY = "order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ((TextView)findViewById(R.id.app_version_textview)).setText(getString(R.string.app_version) + " " + BuildConfig.VERSION_NAME);
        ((EditText)findViewById(R.id.currency_edittext)).setText(getUnit(this));
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

        CheckBox graphOrderCheckBox = (CheckBox)findViewById(R.id.graph_order_checkbox);
        if(isOrderByOldestFirst(this)){
            graphOrderCheckBox.setChecked(true);
        }
        ((CheckBox)findViewById(R.id.graph_order_checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveOrder(getApplicationContext(),b);
            }
        });

        //onclicklistener!
        findViewById(R.id.see_source_button).setOnClickListener(this);
        findViewById(R.id.about_screen_rate_button).setOnClickListener(this);
        findViewById(R.id.visit_website_button).setOnClickListener(this);
        findViewById(R.id.reset_database_button).setOnClickListener(this);
        findViewById(R.id.license_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.see_source_button: startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PROJECT_HOME))); break;
            case R.id.visit_website_button: startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.AUTHOR_WEBSITE))); break;
            case R.id.about_screen_rate_button: onRateClick(); break;
            case R.id.reset_database_button: onDeleteDbClick(); break;
            case R.id.license_button: onLicenseClick(); break;
            case View.NO_ID: break;
        }
    }

    private void onRateClick(){
        // send the user to the play store or otherwise to the play web store
        Uri uri = Uri.parse(Config.PLAY_STORE_URI + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.WEB_PLAY_STORE_URL + getPackageName())));
        }
    }

    private void onDeleteDbClick(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_database)
                .setMessage(R.string.really_delete_data)
                .setPositiveButton(R.string.reset_database, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreditDatabase cDb = new CreditDatabase(getApplicationContext());
                        cDb.resetDatabase();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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
        SharedPreferences.Editor editor = getPrefs(activityContext).edit();
        editor.putString(CURRENCY_UNIT_KEY, unitString);
        editor.apply();
    }

    public static String getUnit(Context activityContext){
        return getPrefs(activityContext).getString(CURRENCY_UNIT_KEY, activityContext.getString(R.string.currency));
    }

    public static void saveOrder(Context activityContext, boolean orderByOldestFirst){
        SharedPreferences.Editor editor = getPrefs(activityContext).edit();
        editor.putBoolean(ORDER_KEY, orderByOldestFirst);
        editor.apply();
    }

    public static boolean isOrderByOldestFirst(Context activityContext){
        return getPrefs(activityContext).getBoolean(ORDER_KEY, true);
    }

    private static SharedPreferences getPrefs(Context c){
        return c.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

}
