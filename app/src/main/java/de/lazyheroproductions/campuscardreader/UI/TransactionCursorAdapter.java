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


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import de.lazyheroproductions.campuscardreader.Logic.CreditDatabase;
import de.lazyheroproductions.campuscardreader.R;

public class TransactionCursorAdapter extends CursorAdapter{

    public TransactionCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String unit = SettingsActivity.getUnit(context);
        ((TextView) view.findViewById(R.id.transaction_text))
                .setText(context.getString(R.string.transaction)
                + " " + cursor.getString(CreditDatabase.LAST_TRANSACTION_COLUMN_ID)+unit);
        ((TextView) view.findViewById(R.id.credit_text))
                .setText(context.getString(R.string.credit)
                + " " + cursor.getString(CreditDatabase.CREDIT_COLUMN_ID) + unit);
        ((TextView) view.findViewById(R.id.date_text)).setText(cursor.getString(CreditDatabase.DATE_COLUMN_ID));
        ((TextView) view.findViewById(R.id.primary_key)).setText(cursor.getString(CreditDatabase.ID_COLUMN_ID));
    }
}