package hackveda.devanshu.tmheart.activity;

import android.os.Bundle;

import java.util.HashMap;

public class TMEquations {
    public float getIdealWeight(float height){

       // Ideal Weight = ((height(cm)/100) x (height(cm)/100)) x 22
        float ideal_weight = ((height / 100) * (height / 100)) * 22;
        return ideal_weight;
    }

    public int getMaxHeartRate(int age){

        //220 - Age = Answer
        int max_heart_rate = 220 - age;
        return max_heart_rate;
    }

    public Bundle getWeightStatus(float currentWeight, float ideal_weight) {

        Bundle weightData = new Bundle();
        weightData.putString("WeightStatus", "NOSTATUS");
        weightData.putFloat("WeightValue", 0);

        float weightvalue = 0;
        String weightstatus = null;
        if ((ideal_weight - 0.5) > (currentWeight - 0.5)) {
            weightvalue = ideal_weight - currentWeight;
            weightstatus = "UnderWeight";
        } else if ((ideal_weight + 0.5) < currentWeight) {
            weightvalue = currentWeight - ideal_weight;
            weightstatus = "OverWeight";
        } else {
            weightvalue = (float) 0.0;
            weightstatus = "Perfect";
        }

        weightData.putString("WeightStatus", weightstatus);
        weightData.putFloat("WeightValue", weightvalue);

        return weightData;
    }
}
