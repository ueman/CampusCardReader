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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import de.lazyheroproductions.campuscardreader.Logic.CreditDatabase;
import de.lazyheroproductions.campuscardreader.R;

public class TransactionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private CreditDatabase cdb;
    private TransactionCursorAdapter tca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        cdb = new CreditDatabase(getApplicationContext());
        tca = new TransactionCursorAdapter(this, cdb.getListCursor());
        ListView transactionListView = (ListView) findViewById(R.id.transaction_list);
        transactionListView.setAdapter(tca);
        transactionListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_this_transaction)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cdb.deleteEntry(((TextView)view.findViewById(R.id.primary_key)).getText() + "");
                        tca.changeCursor(cdb.getListCursor());
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

}
