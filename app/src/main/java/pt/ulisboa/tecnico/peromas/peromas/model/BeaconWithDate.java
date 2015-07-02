package pt.ulisboa.tecnico.peromas.peromas.model;

import org.altbeacon.beacon.Beacon;

import java.util.Date;

/**
 * Created by sampaio on 20-06-2015.
 */
public class BeaconWithDate {
    private String id;
    private Date lastTimeReceived;
    private Beacon beacon;

    public BeaconWithDate(String id, Date lastTimeReceived, Beacon beacon){
        this.id = id;
        this.lastTimeReceived = lastTimeReceived;
        this.beacon = beacon;
    }

    public String getId() {
        return id;
    }

    public Date getLastTimeReceived() {
        return lastTimeReceived;
    }

    public void setLastTimeReceived(Date lastTimeReceived) {
        this.lastTimeReceived = lastTimeReceived;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }
}
