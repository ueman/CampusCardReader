//package de.lazyheroproductions.campuscardreader;
//
//// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
//// Jad home page: http://www.geocities.com/kpdus/jad.html
//// Decompiler options: braces fieldsfirst space lnc
//
//import android.content.DialogInterface;
//
//
//        import android.app.AlertDialog;
//        import android.content.Context;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.nfc.Tag;
//        import android.nfc.tech.IsoDep;
//        import android.os.AsyncTask;
//
//import java.io.IOException;
//
//public class SampleCardReader extends AsyncTask
//{
//
//    private final String M_STR_CONTENT_BALANCE = "Aktuelles Guthaben:";
//    private final String M_STR_CONTENT_LAST_TRANSACTION = "Letzte Transaktion:";
//    private final String M_STR_ERROR_OCCURED = "Es ist ein Fehler aufgetreten! Die Campuscard konnte leider nicht gelesen werden.";
//    private final String M_STR_TITLE = "Guthaben-Details";
//    private String balance;
//    private Context context;
//    private Intent intent;
//    private String lastTransaction;
//
//    public SampleCardReader(Context context1, Intent intent1)
//    {
//        context = context1;
//        intent = intent1;
//    }
//
//    protected String doInBackground(Void avoid[]) throws IOException {
//        IsoDep isodep = IsoDep.get((Tag)intent.getParcelableExtra("android.nfc.extra.TAG"));
//        Exception exception2;
//        byte balanceByte[];
//        byte lastTransactionByte[];
//        byte transceiveByteArray[] = {90, 95, -124, 21};
//        isodep.connect();
//        if (isodep.transceive(transceiveByteArray)[0] != 0){
//            throw new Exception();
//        }
//        goto _L1
//        _L3:
//        if (balanceByte != null && lastTransactionByte != null)
//        {
//            double balanceDouble = (double)(((0xff & balanceByte[4]) << 24) + ((0xff & balanceByte[3]) << 16) + ((0xff & balanceByte[2]) << 8) + (0xff & balanceByte[1])) / 1000D;
//            double transactionDouble = (double)(((0xff & lastTransactionByte[16]) << 24) + ((0xff & lastTransactionByte[15]) << 16) + ((0xff & lastTransactionByte[14]) << 8) + (0xff & lastTransactionByte[13])) / 1000D;
//            Object aobj[] = new Object[1];
//            aobj[0] = Double.valueOf(balanceDouble);
//            balance = String.format("%.2f\u20AC", aobj);
//            Object aobj1[] = new Object[1];
//            aobj1[0] = Double.valueOf(transactionDouble);
//            lastTransaction = String.format("%.2f\u20AC", aobj1);
//        }
//        return balance;
//        _L1:
//        try
//        {
//            byte abyte2[] = {108, 1};
//            balanceByte = isodep.transceive(abyte2);
//            if (balanceByte[0] != 0)
//            {
//                throw new Exception();
//            }
//            break MISSING_BLOCK_LABEL_289;
//        }
//        // Misplaced declaration of an exception variable
//        catch (Exception exception2)
//        {
//            balanceByte = null;
//            lastTransactionByte = null;
//            Exception exception;
//            byte abyte2[];
//            Exception exception4;
//            try
//            {
//                isodep.close();
//            }
//            catch (Exception exception3)
//            {
//                balanceByte = null;
//                lastTransactionByte = null;
//            }
//        }
//        finally
//        {
//            try
//            {
//                isodep.close();
//            }
//            catch (Exception exception1) { }
//            throw exception;
//        }
//        _L4:
//        if (true) goto _L3; else goto _L2
//        _L2:
//        abyte2[0] = -11;
//        abyte2[1] = 1;
//        lastTransactionByte = isodep.transceive(abyte2);
//        if (balanceByte[0] != 0)
//        {
//            throw new Exception();
//        }
//        try
//        {
//            isodep.close();
//        }
//        // Misplaced declaration of an exception variable
//        catch (Exception exception4) { }
//        if (false)
//        {
//        }
//        goto _L4
//    }
//
//    protected volatile void onPostExecute(Object obj)
//    {
//        onPostExecute((String)obj);
//    }
//
//    protected void onPostExecute(String s)
//    {
//        android.app.AlertDialog.Builder builder = (new android.app.AlertDialog.Builder(context)).setTitle("Guthaben-Details").setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
//
//            final ReadCardAsyncTask this$0;
//
//            public void onClick(DialogInterface dialoginterface, int i)
//            {
//                dialoginterface.cancel();
//            }
//
//
//            {
//                this$0 = ReadCardAsyncTask.this;
//                super();
//            }
//        }).setNegativeButton(null, null);
//        if (balance == null || lastTransaction == null)
//        {
//            builder.setMessage("Es ist ein Fehler aufgetreten! Die Campuscard konnte leider nicht gelesen werden.");
//        } else
//        {
//            builder.setMessage((new StringBuilder("Aktuelles Guthaben:\t\t")).append(balance).append("\n\n").append("Letzte Transaktion:").append("\t\t").append(lastTransaction).toString());
//        }
//        builder.create().show();
//        builder.setMessage("asdasdas");
//    }
//}
