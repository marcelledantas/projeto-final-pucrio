package gd.contextnet;

import ckafka.data.Swap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gd.Model.MobileObject;
import gd.Model.PersonSituation;
import gd.Model.Region;
import gd.main.MyGroupDefinerMain;
import gd.util.Debug;
import gd.util.StaticLibrary;
import main.java.ckafka.GroupDefiner;
import main.java.ckafka.GroupSelection;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyGroupDefiner implements GroupSelection {
    private static List<Region> regionList;
    /**
     * maps persons (string id) to a hashset of groups (list of integer)
     */
    private Map<String, HashSet<Integer>> personGroup =  new HashMap<>();


    /**
     * Creates a Group Definer based on ContextNet IP address, ContextNet port number and region description filename
     *
     * @param filename
     */
    public MyGroupDefiner(String workdir, String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        Swap swap = new Swap(objectMapper);
        new GroupDefiner(this, swap);

        this.personGroup = new HashMap<String, HashSet<Integer>>();
        this.regionList = StaticLibrary.readFilenamesFile(workdir, filename);

        /*
         * reads each region file
         */
        for (Region region : regionList) {
            region.setPoints(StaticLibrary.readRegion(workdir, region.getFilename()));
        }
    }


    @Override
    public Set<Integer> groupsIdentification() {
        Set<Integer> setOfGroups = new HashSet<Integer>();

        for (Region region : regionList) {
            setOfGroups.add(region.getNumber());
        }

        return setOfGroups;
    }

    @Override
    public Set<Integer> getNodesGroupByContext(ObjectNode objectNode) {
        boolean isNewPerson;
        MobileObject mobileObject = null;
        HashSet<Integer> difGroups;

        try {
            /* objectNode
                {
                    "ID":"01111111-1111-1111-1111-111111111119",
                    "messageCount":4,
                    "longitude":-43.18559736525978,
                    "latitude":-22.936826006961283,
                    "positionZ":0.19229573,
                    "date":"Fri Sep 01 11:20:01 BRT 2023"
                }
             */
            mobileObject = new MobileObject(new JSONObject((objectNode.toString())));

        } catch (Exception e) {
            Debug.warning("Could not create a mobile object", e);
        }


        HashSet<Integer> newGroups = new HashSet<Integer>(2, 1);
        // search the regions where the mobileObject may be
        for (Region region : regionList) {
            assert mobileObject != null;
            if (region.contains(mobileObject)) {
                newGroups.add(region.getNumber());
//                System.out.println("NÚMERO DO GRUPO " + region.getNumber());
            }
        }

        // check if this mobile object enter a new group
        difGroups = (HashSet<Integer>) newGroups.clone();
        assert mobileObject != null;
        if (this.personGroup.containsKey(mobileObject.getId())) {
            // gets the all new groups removing old groups from the new groups
            difGroups.removeAll(this.personGroup.get(mobileObject.getId()));
            isNewPerson = false;
        } else {
            isNewPerson = true;
        }
        if (difGroups.size() > 0) {
            // send a message to PN with difGroups
            PersonSituation personSituation = new PersonSituation(mobileObject, isNewPerson, difGroups);
            MyGroupDefinerMain.dataToPNQueue.add(personSituation);
            synchronized (MyGroupDefinerMain.dataToPNQueue) { //TODO: Possivelmente um problema
                MyGroupDefinerMain.dataToPNQueue.notify();
            }
        }
        this.personGroup.put(mobileObject.getId(), newGroups);

        return newGroups;
    }

    @Override
    public String kafkaConsumerPrefix() {
        return "gd.one.consumer";
    }

    @Override
    public String kafkaProducerPrefix() {
        return "gd.one.producer";
    }

}
