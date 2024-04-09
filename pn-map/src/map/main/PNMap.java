package map.main;


import ckafka.data.Swap;
import ckafka.data.SwapData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.java.application.ModelApplication;
import map.Model.MobileObject;
import map.Model.Region;

import map.util.GeographicMap;
import map.util.StaticLibrary;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.awt.*;
import java.util.List;

/**
 * @author Marcelle Dantas
 */

public class PNMap extends ModelApplication {

    /** Array of regions */
    private static List<Region> regionList;

    /** JMapViewer-based map */
    private GeographicMap map;

    private Swap swap;
    private ObjectMapper objectMapper;

    static {
        System.setProperty("http.agent", "Gluon Mobile/1.0.3");
    }

    public PNMap() {
        this.objectMapper = new ObjectMapper();
        this.swap = new Swap(objectMapper);

        String workdir = "/Users/imarc/IdeaProjects/region-alert-v3.0/pn-map/";
        String filename = "Bairros/RioDeJaneiro.lista";

        regionList = StaticLibrary.readFilenamesFile(workdir, filename);

        /*
         * reads each region file
         */
        for (Region region : regionList) {
            region.setPoints(StaticLibrary.readRegion(workdir, region.getFilename()));
        }

        // TODO: remover trecho de código de desenho do mapa
        /*
         * checks if there is an graphic environment available (true if not, otherwise, false)
         */
        if (!GraphicsEnvironment.isHeadless() && !StaticLibrary.forceHeadless) {
            map = new GeographicMap(regionList);
            map.setVisible(true);
        }
    }

    //record passa a conter as informações de contexto do nó móvel
    @Override
    public void recordReceived(ConsumerRecord record) {
//        System.out.println(String.format("Mensagem recebida de %s", record.key()));
        try {
            SwapData data = swap.SwapDataDeserialization((byte[]) record.value());
            ObjectNode contextInfo = data.getContext();
            double latitude = Double.parseDouble(String.valueOf(contextInfo.get("latitude")));
            double longitude = Double.parseDouble(String.valueOf(contextInfo.get("longitude")));
            String id = String.valueOf(contextInfo.get("ID"));
            String idMap = getId(id);
            MobileObject mobileObject = new MobileObject(idMap,latitude,longitude);

            if(!GraphicsEnvironment.isHeadless() && !StaticLibrary.forceHeadless) {
                map.removeItem(mobileObject);
                map.addItem(mobileObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getId(String id){
        String idMap = id.substring(1, id.length() - 1);
        return idMap.substring(idMap.length() - 4);
    }

}
