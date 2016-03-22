package com.manzolik.gmanzoli.mytrains.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by gmanzoli on 26/02/16.
 */
public class TrainStatus implements JSONPopulable{

    private int delay;
    private String trainDescription;
    private String lastCheckedStation;
    private Calendar lastUpdate;
    private boolean departed;
    private final TrainReminder associatedReminder;
    private Calendar targetTime;
    private boolean targetPassed;
    private Calendar expectedDeparture;



    public TrainStatus(TrainReminder associatedReminder) {
        this.associatedReminder = associatedReminder;
    }

    public boolean isTargetPassed() {
        return targetPassed;
    }

    public int getDelay() {
        return delay;
    }

    public String getTrainDescription() {
        return trainDescription;
    }

    public String getLastCheckedStation() {
        return lastCheckedStation;
    }

    public Calendar getLastUpdate() {
        return lastUpdate;
    }

    public boolean isDeparted() {
        return departed;
    }

    public Station getTargetStation() { return associatedReminder.getTargetStaion();}

    public Calendar getTargetTime() {
        return targetTime;
    }

    public Calendar getExpectedDeparture() {
        return expectedDeparture;
    }

    @Override
    public void populate(JSONObject data) {
        // Partenza prevista per il treno
        expectedDeparture = Calendar.getInstance();
        expectedDeparture.setTime(new Date(data.optLong("orarioPartenza")));

        // Codice + Tipologia del treno
        trainDescription = data.optString("catergoria") + " " + data.optInt("numeroTreno");

        lastCheckedStation = data.optString("stazioneUltimoRilevamento");

        departed = !(lastCheckedStation.equals("--"));
        lastUpdate = Calendar.getInstance();
        lastUpdate.setTime(new Date(data.optLong("oraUltimoRilevamento")));

        if (!departed){
            long expectedDelay = Calendar.getInstance().getTimeInMillis() - expectedDeparture.getTimeInMillis();
            delay = (int) expectedDelay / 60000; // Conversione in minuti
        } else {
            delay = data.optInt("ritardo");
        }

        Station targetStation = associatedReminder.getTargetStaion();
        targetTime = Calendar.getInstance();
        JSONArray stopsArray = data.optJSONArray("fermate");
        for (int i = 0; i < stopsArray.length(); i++) {
            try {
                JSONObject obj = stopsArray.getJSONObject(i);
                System.out.println(obj);
                if (obj.getString("id").equals(targetStation.getCode())){
                    if (obj.getInt("actualFermataType") == 0){ // fermata ancora da prendere
                        this.targetPassed = false;
                        System.out.println("Partenza teorica da StazioneTarget");
                        System.out.println(obj.optLong("arrivo_teorico") + delay * 60000);
                        long tt = obj.optLong("arrivo_teorico") + delay * 60000;
                        targetTime.setTime(new Date(tt));
                        System.out.println(targetTime.getTime().toString());

                    } else {
                        this.targetPassed = true;
                        targetTime.setTime(new Date(obj.getLong("partenzaReale")));
                    }

                    i = stopsArray.length() +1; // BRUTTA COSA, sono una brutta persona

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
