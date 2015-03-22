package de.lazyheroproductions.campuscardreader;

import java.util.ArrayList;
import java.util.Random;

public class CreditData{
    private int entries; // number of entries in the arrays
    private ArrayList<Float> credits;
    private ArrayList<Float> transactions;
    private ArrayList<Long> dates;
    private ArrayList<String> datesHumanReadable;
    private ArrayList<String> infos;
    private float sumCredit;
    private float sumTransactions;
    private boolean reverseOrder = false;

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
        if(reverseOrder) {
            reverse(result);
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
        if(reverseOrder) {
            reverse(result);
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
        if(reverseOrder) {
            reverse(result);
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
        if(reverseOrder) {
            reverse(result);
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
        if(reverseOrder) {
            reverse(result);
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

    private void reverse(float[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        float tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    private void reverse(long[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        long tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    public void reverse(String[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        String tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    public void setReverseOrder(boolean b){
        this.reverseOrder = !b;
    }

    public CreditData getRandom(){
        int lengthOfRandomData = getDatesHumanReadable().length;
        if(lengthOfRandomData != 0) {
            Random r = new Random();
            ArrayList<Float> imFloatingAway = new ArrayList<>();
            for (int i = 0; i < lengthOfRandomData; i++) {
                imFloatingAway.add((float) r.nextInt(20));
            }
            credits = imFloatingAway;
            ArrayList<Float> nowMyHeadHurts = new ArrayList<>();
            for (int i = 0; i < lengthOfRandomData; i++) {
                nowMyHeadHurts.add((float) r.nextInt(20));
            }
            transactions = nowMyHeadHurts;
            sumCredit = r.nextInt(100);
            sumTransactions = r.nextInt(100);
        }
        return this;
    }
}