package pn.contextnet;

import ckafka.data.Swap;
import ckafka.data.SwapData;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.application.ModelApplication;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import pn.Model.Integer;
import pn.thread.ReceiveData;
import pn.thread.SubscriberListener;
import pn.util.Debug;

import java.util.List;

public class MyProcessingNode extends ModelApplication {
    private Swap swap;
    private ObjectMapper objectMapper;

    /**
     * Constructor
     */
    public MyProcessingNode(){
        this.objectMapper = new ObjectMapper();
        this.swap = new Swap(objectMapper);
    }


    @Override
    public void recordReceived(ConsumerRecord consumerRecord) {
        System.out.println(String.format("Mensagem recebida de %s", consumerRecord.key()));
        try {
            SwapData data = swap.SwapDataDeserialization((byte[]) consumerRecord.value());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor
     * @param
     * @throws Exception
     */
    public MyProcessingNode(String interSCityIPAddress) throws Exception {
        this();
//        this.objectMapper = new ObjectMapper();
//        this.swap = new Swap(objectMapper);

        // a thread to receive data from ContextNet
        Thread receiveData = new ReceiveData(interSCityIPAddress);
        receiveData.start();

        // a thread to act as an actuator and receive messages whenever a new alert is created
        Thread subscriberListener;
        try {
            subscriberListener = new SubscriberListener(interSCityIPAddress);
            subscriberListener.start();
        } catch (Exception e) {
            Debug.error("Could not subscribe to receive news about alerts", e);
        }
    }

    public void sendGroupcastMessage(List<Integer> groups, String text) {
        System.out.println("Groupcast messager: sendGroupcastMessage\n");
        for(Integer group : groups){
            System.out.println(String.format("Sending |%s| to group %s.", text, group.getGroup()));
            try {
                sendRecord(createRecord("GroupMessageTopic", String.valueOf(group.getGroup()),
                        swap.SwapDataSerialization(createSwapData(text))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendUnicastMessage(String uuid, String text) {
        System.out.println(String.format("Sending |%s| to %s.", text, uuid));
        try {
            sendRecord(createRecord("PrivateMessageTopic", uuid,
                    swap.SwapDataSerialization(createSwapData(text))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
