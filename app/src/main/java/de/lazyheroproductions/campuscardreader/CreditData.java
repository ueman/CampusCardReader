package de.lazyheroproductions.campuscardreader;

import java.util.ArrayList;

public class CreditData{
    private int entries; // number of entries in the arrays
    private ArrayList<Float> credits;
    private ArrayList<Float> transactions;
    private ArrayList<Long> dates;
    private ArrayList<String> datesHumanReadable;
    private ArrayList<String> infos;
    private float sumCredit;
    private float sumTransactions;

    public CreditData(int arrayLength){
        entries = arrayLength;
        credits = new ArrayList<>();
        transactions = new ArrayList<>();
        dates = new ArrayList<>();
        datesHumanReadable = new ArrayList<>();
        infos = new ArrayList<>();
    }

    public int getEntries() {
        return entries;
    }

    public void setEntries(int entries) {
        this.entries = entries;
    }

    public float[] getCredits() {
        float[] result = new float[credits.size()];
        for (int i = 0; i <credits.size(); i++) {
            result[i] = credits.get(i);
        }
        return result;
    }

    public void addCredit(float credits) {
        this.credits.add(credits);
    }

    public float[] getTransactions() {
        float[] result = new float[transactions.size()];
        for (int i = 0; i <transactions.size(); i++) {
            result[i] = transactions.get(i);
        }
        return result;
    }

    public void addTransaction(float transactions) {
        this.transactions.add(transactions);
    }

    public long[] getDates() {
        long[] result = new long[dates.size()];
        for (int i = 0; i <dates.size(); i++) {
            result[i] = dates.get(i);
        }
        return result;
    }

    public void addDate(long dates) {
        this.dates.add(dates);
    }

    public String[] getDatesHumanReadable() {
        String[] result = new String[datesHumanReadable.size()];
        for (int i = 0; i <datesHumanReadable.size(); i++) {
            result[i] = datesHumanReadable.get(i);
        }
        return result;
    }

    public void addDateHumanReadable(String datesHumanReadable) {
        this.datesHumanReadable.add(datesHumanReadable);
    }

    public String[] getInfos() {
        String[] result = new String[infos.size()];
        for (int i = 0; i <infos.size(); i++) {
            result[i] = infos.get(i);
        }
        return result;
    }

    public void addInfos(String infos) {
        this.infos.add(infos);
    }

    public float getSumCredit() {
        return sumCredit;
    }

    public void setSumCredit(float sumCredit) {
        this.sumCredit = sumCredit;
    }

    public float getSumTransactions() {
        return sumTransactions;
    }

    public void setSumTransactions(float sumTransactions) {
        this.sumTransactions = sumTransactions;
    }

    public float getTransactionAverage(){
        return getSumTransactions()/getEntries();
    }

    public float getCreditAverage(){
        return getSumCredit()/getEntries();
    }

}