package de.lazyheroproductions.campuscardreader;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Date;

public class MainActivity extends ActionBarActivity{

    private AdView adView;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String [][] mTechLists;

    private LineChartView transactionLineChart;
    private LineChartView creditLineChart;
    private BarChartView averageBarChart;

    private TextView creditTextView;
    private TextView transactionTextView;
    private static final String FORMAT_STRING = "%.2f\u20AC"; // two numbers after the comma and a €-sign

    private double credit;
    private double lastTransaction;

    private CreditDatabase cDb;
    private CreditData cData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(BuildConfig.DEBUG) {
            Log.i(this.getClass().getName(), "received intent on create");
        }
        cDb = new CreditDatabase(getApplicationContext());
        setUpLocalBroadCastReceiver();
        startNfcIntentService(getIntent());
        setUpReadCardPart();
        setUpStatistics();
        ratingCounter();
        setUpNfcStuff();
        if(!isNfcEnabled()){
            enableNfcDialog();
        }
        setUpAdView();
    }

    private void ratingCounter(){
        final String COUNT = "ratingCounter";
        final String DIALOG_WAS_OPENED = "ratingWasOpened";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getInt(COUNT, 0)>4 && !pref.getBoolean(DIALOG_WAS_OPENED,false)){
            pref.edit().putBoolean(DIALOG_WAS_OPENED, true).apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.do_you_want_to_rate_this_app)
                    .setPositiveButton(R.string.yes_i_love_it, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rate();
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

    private void rate(){
        Uri uri = Uri.parse(Config.PLAY_STORE_URI + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.WEB_PLAY_STORE_URL + getPackageName())));
        }
    }

    private void setUpLocalBroadCastReceiver(){
        // get an instance of the local broadcast manager to receive messages which are send inside the application
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(CardReaderIntentService.CAMPUS_CARD_INTENT));
    }

    private void setUpNfcStuff(){
        // intercept all NFC related Intents and redirect them to this activity while this activity is activated and on the front
        // this is called the "foreground dispatch"
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        IntentFilter  nfcTech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{nfcTech};
        mTechLists = new String[][] {
                new String[] { IsoDep.class.getName() },
                {NfcA.class.getName()}
        };
    }

    private boolean isNfcEnabled(){
        NfcAdapter adapter = ((NfcManager) getSystemService(Context.NFC_SERVICE)).getDefaultAdapter();
        return (adapter != null && adapter.isEnabled());
    }

    private void enableNfcDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.turn_on_nfc_message))
            .setTitle(getResources().getString(R.string.nfc_is_turned_off))
            .setPositiveButton(R.string.nfc_settings, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (android.os.Build.VERSION.SDK_INT >= 16) {
                            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                        } else {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
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

    private void setUpAdView() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(Config.AD_UNIT_ID);
        ((LinearLayout)findViewById(R.id.main_layout)).addView(adView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(Config.MEMOPAD)
                .addTestDevice(Config.NEXUS)
                .addTestDevice(Config.LIFETAB)
                .build();
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

        });
        adView.loadAd(adRequest);
    }

    private void setUpStatistics(){
        cData = cDb.getData();
        transactionLineChart = (LineChartView) findViewById(R.id.transaction_linechart);
        creditLineChart =  (LineChartView) findViewById(R.id.credit_linechart);
        averageBarChart = (BarChartView) findViewById(R.id.average_barchart);
        setUpTransactionLineChart();
        setUpCreditLineChart();
        setUpAverageBarChart();
        setUpAllTimeSpendings();
    }

    public void updateStatistics(){
        cDb.addEntry(credit, lastTransaction, getDate(), "");
        this.cData = cDb.getData();
        // update all the charts!
        setUpAllTimeSpendings();
        averageBarChart.reset();
        setUpAverageBarChart();
        creditLineChart.reset();
        setUpCreditLineChart();
        transactionLineChart.reset();
        setUpTransactionLineChart();
    }

    private void setUpAllTimeSpendings(){
        TextView spendings = (TextView) findViewById(R.id.all_time_spendings_textview);
        float f = cData.getSumTransactions();
        String s = DataAnalysisTools.format(f);
        spendings.setText(s);
    }

    private void setUpTransactionLineChart(){
        String lineLabels[] = cData.getDatesHumanReadable();
        float lineValues[] = cData.getTransactions();
        int minMaxStep[] = DataAnalysisTools.calculateAxisBorderValuesLineCharts(lineValues);
        LineSet dataSet = new LineSet();
        if (lineLabels.length != 0 || lineValues.length != 0) {
            dataSet.addPoints(lineLabels, lineValues);
        } else {
            dataSet.addPoint(getResources().getString(R.string.no_data_to_show), 0f);
        }
        dataSet.setDots(true)
                .setSmooth(true)
                .setDotsColor(this.getResources().getColor(R.color.purple_primary)) //line_bg
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(this.getResources().getColor(R.color.line)) //line
                .setLineColor(this.getResources().getColor(R.color.line)) //line
                .setLineThickness(Tools.fromDpToPx(3));
        transactionLineChart.addData(dataSet);

        Paint mLineGridPaint = new Paint();
        mLineGridPaint.setColor(this.getResources().getColor(R.color.purple_primary_dark)); //line_grid
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        transactionLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(ChartView.GridType.HORIZONTAL, mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(minMaxStep[0],minMaxStep[1], 2) // TODO compute this numbers somehow :D
                .setLabelsMetric(SettingsActivity.getUnit(this))
                .show();
    }

    private void setUpCreditLineChart(){
        String lineLabels[] = cData.getDatesHumanReadable();
        float lineValues[] = cData.getCredits();
        int minMaxStep[] = DataAnalysisTools.calculateAxisBorderValuesLineCharts(lineValues);
        LineSet dataSet = new LineSet();
        if( lineLabels.length != 0 || lineValues.length != 0) {
            dataSet.addPoints(lineLabels, lineValues);
        }else{
            dataSet.addPoint(getResources().getString(R.string.no_data_to_show),0f);
        }
        dataSet.setDots(true)
                .setSmooth(false)
                .setDotsColor(this.getResources().getColor(R.color.indigo_primary)) //line_bg
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(this.getResources().getColor(R.color.line)) //line
                .setLineColor(this.getResources().getColor(R.color.line)) //line
                .setLineThickness(Tools.fromDpToPx(3));
        creditLineChart.addData(dataSet);

        Paint mLineGridPaint = new Paint();
        mLineGridPaint.setColor(this.getResources().getColor(R.color.indigo_primary_dark)); //line_grid
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        creditLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(ChartView.GridType.HORIZONTAL,mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(minMaxStep[0],minMaxStep[1], 2) // TODO compute this numbers somehow :D
                .setLabelsMetric(SettingsActivity.getUnit(this))
                .show();
    }

    private void setUpAverageBarChart(){
        Paint mBarGridPaint = new Paint();
        mBarGridPaint.setColor(this.getResources().getColor(R.color.deep_orange_primary_dark));//bar grid
        mBarGridPaint.setStyle(Paint.Style.STROKE);
        mBarGridPaint.setAntiAlias(true);
        mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        BarSet barSet = new BarSet();
        Bar bar;
        bar = new Bar(this.getResources().getString(R.string.average_transactions), cData.getTransactionAverage());
        bar.setColor(this.getResources().getColor(R.color.deep_orange_300));
        barSet.addBar(bar);

        bar = new Bar(this.getResources().getString(R.string.average_credit), cData.getCreditAverage());
        bar.setColor(this.getResources().getColor(R.color.deep_orange_300));
        barSet.addBar(bar);

        averageBarChart.addData(barSet);

        averageBarChart.setSetSpacing(Tools.fromDpToPx(40));//20?
        averageBarChart.setBarSpacing(Tools.fromDpToPx(40));

        int[] minMaxStep = DataAnalysisTools.calculateAxisBorderValuesBarCharts(cData.getCreditAverage(),cData.getTransactionAverage());
        averageBarChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setAxisBorderValues(minMaxStep[0], minMaxStep[1], 2) // TODO compute this numbers somehow :D
                .setGrid(ChartView.GridType.FULL, mBarGridPaint)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setLabelsMetric(SettingsActivity.getUnit(this))
                .setBorderSpacing(Tools.fromDpToPx(40))
                .show();
    }

    private void setUpReadCardPart(){
        creditTextView = (TextView) findViewById(R.id.credit);
        transactionTextView = (TextView) findViewById(R.id.last_transaction);
        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatistics();
                v.setEnabled(false);
            }
        });
    }

    public void updateReadCardData(double credit, double lastTransaction){
        creditTextView.setText(getResources().getText(R.string.credit) + " " + format(credit));
        transactionTextView.setText(getResources().getText(R.string.last_transaction) + " " + format(lastTransaction));
        findViewById(R.id.add_button).setEnabled(true);
        findViewById(R.id.put_card_to_device_textview).setVisibility(View.GONE);
        findViewById(R.id.credit).setVisibility(View.VISIBLE);
        findViewById(R.id.last_transaction).setVisibility(View.VISIBLE);
        findViewById(R.id.add_button).setVisibility(View.VISIBLE);
    }

    private String format(double d){
        return String.format(FORMAT_STRING, d);
    }

    @Override
    public void onResume() {
        super.onResume();
        // re-enable foreground dispatch
        if (mAdapter != null){
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
        }
        adView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // disable foreground dispatch
        if (mAdapter != null){
            mAdapter.disableForegroundDispatch(this);
        }
        adView.pause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        adView.destroy();
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
        // NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
        // unnecessary, but this is an assumption
        // if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
            Intent newIntent = new Intent(getApplicationContext(), CardReaderIntentService.class);
            newIntent.putExtra(NfcAdapter.EXTRA_TAG, intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
            startService(newIntent);
        }
    }

    private long getDate(){
        return new Date().getTime();
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
            updateReadCardData(credit, lastTransaction);
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
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
//        return super.onOptionsItemSelected(item);
    }
}
