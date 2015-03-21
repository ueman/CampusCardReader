package de.lazyheroproductions.campuscardreader;

/**
 * Created by Jonas on 10.01.2015.
 */
public class DataAnalysisTools {

    public static final String FORMAT_TYPE = "%.2f\u20AC"; // two numbers after the comma and a €-sign

    public static String format(float d){
        return String.format(FORMAT_TYPE, d);
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

    public static float smallestEntry(float[] dataSet){
        float min = dataSet[0];
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i] < min) {
                min = dataSet[i];
            }
        }
        return min;
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
}
