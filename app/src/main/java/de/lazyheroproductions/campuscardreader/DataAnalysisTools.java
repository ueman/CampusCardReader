package de.lazyheroproductions.campuscardreader;

/**
 * Created by Jonas on 10.01.2015.
 */
public class DataAnalysisTools {

    public static final String FORMAT_TYPE = "%.2f\u20AC"; // two numbers after the comma and a €-sign

    public static String format(double d){
        return String.format(FORMAT_TYPE, d);
    }

    public static String format(float d){
        return String.format(FORMAT_TYPE, d);
    }

    public static double addEveryItemOfArray(double[] data){
        double amount = 0d;
        for(double d: data){
            amount +=d;
        }
        return amount;
    }

    public static float addEveryItemOfArray(float[] data){
        float amount = 0f;
        for(float d: data){
            amount +=d;
        }
        return amount;
    }

    public static int[] getMinMaxStep(double[] dataSet){
        int max = nextBiggerInteger(highestEntry(dataSet));
        int min = nextSmallerInteger(smallestEntry(dataSet));
        int data[] = {min, max, calculateStep(max, min)};
        return data;
    }

    public static int[] getMinMaxStep(float[] dataSet){
        int max = nextBiggerInteger(highestEntry(dataSet));
        int min = nextBiggerInteger(smallestEntry(dataSet));
        int data[] = {min, max, calculateStep(max, min)};
        return data;
    }

    public static double highestEntry(double[] dataSet){
        double max = dataSet[0];
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i] > max) {
                max = dataSet[i];
            }
        }
        return max;
    }

    public static float highestEntry(float[] dataSet){
        float max = dataSet[0];
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i] > max) {
                max = dataSet[i];
            }
        }
        return max;
    }

    public static double smallestEntry(double[] dataSet){
        double min = dataSet[0];
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i] < min) {
                min = dataSet[i];
            }
        }
        return min;
    }

    public static float smallestEntry(float[] dataSet){
        float min = dataSet[0];
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i] < min) {
                min = dataSet[i];
            }
        }
        return min;
    }

    public static int calculateStep(int max, int min){
        if(min==0||max==0){
            // do stuff
        }
        int difference = ((max-min)/5);
        double x = difference/3;
        int magicNumber = (int)Math.floor(x);
        boolean plus = true;
        while(max%magicNumber==0&&min%magicNumber==0){
            if(plus) {
                magicNumber++;
                if(magicNumber>10){
                    plus = false;
                }
            }else{
                magicNumber--;
            }
        }
        return magicNumber;
    }

    public static int nextBiggerInteger(double number){
        return (int)Math.ceil(number);
    }

    public static int nextSmallerInteger(double number){
        return (int)Math.floor(number);
    }

    public static int[] calculateAxisBorderValuesLineCharts(float[] array){
        //TODO better name and better algorithm
        // but it works for now
        if(array.length==0){
            return new int[]{-1,1};
        }
        float high = highestEntry(array);
        float low =  smallestEntry(array);
        int highestEntry = (int)high;
        int smallestEntry = (int) low;
        int[] steps = new int[2];

        if(smallestEntry < low) {
            if (smallestEntry % 2 == 0) {
                steps[1] = smallestEntry;
            } else {
                steps[1] = (smallestEntry - 1);
            }
        }else{
            if (smallestEntry % 2 == 0) {
                steps[1] = smallestEntry-2;
            } else {
                steps[1] = (smallestEntry - 1);
            }
        }

        if(highestEntry > high) {
            if (highestEntry % 2 == 0) {
                steps[1] = highestEntry;
            } else {
                steps[1] = (highestEntry + 1);
            }
        }else{
            if (highestEntry % 2 == 0) {
                steps[1] = highestEntry+2;
            } else {
                steps[1] = (highestEntry + 1);
            }
        }

//        if((steps[1]-steps[0])/2<2){
//            steps[0]-=2;
//            steps[1]+=2;
//        }

        return steps;
    }

    public static int[] calculateAxisBorderValuesBarCharts(float one, float two){
        //TODO better name and better algorithm
        // but it works for now
        if(one==0 && two == 0){
            return new int[]{-1,1};
        }
        float high = (one>two)?one:two;
        float low =  (one<two)?one:two;
        int highestEntry = (int)high;
        int smallestEntry = (int) low;
        int[] steps = new int[2];

        if(smallestEntry < low) {
            if (smallestEntry % 2 == 0) {
                steps[1] = smallestEntry;
            } else {
                steps[1] = (smallestEntry - 1);
            }
        }else{
            if (smallestEntry % 2 == 0) {
                steps[1] = smallestEntry-2;
            } else {
                steps[1] = (smallestEntry - 1);
            }
        }

        if(highestEntry > high) {
            if (highestEntry % 2 == 0) {
                steps[1] = highestEntry;
            } else {
                steps[1] = (highestEntry + 1);
            }
        }else{
            if (highestEntry % 2 == 0) {
                steps[1] = highestEntry+2;
            } else {
                steps[1] = (highestEntry + 1);
            }
        }

//        if((steps[1]-steps[0])/2<2){
//            steps[0]-=2;
//            steps[1]+=2;
//        }

        return steps;
    }

    // TODO possible analysis
    // durchschnitts werte in balken diagramm?!
    // durchschnittliche ausgaben
    // durchschnittliches guthaben auf der karte
    // durchschnittliche aufladungen
    // verläufe nur für die letzen 7 tage
    // graphen für aufladungen -> wie ausgaben verlauf
    // lifetime verläufe ohne punkte?
}
