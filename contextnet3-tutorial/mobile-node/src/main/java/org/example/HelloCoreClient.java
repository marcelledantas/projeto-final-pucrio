package org.example;

import ckafka.data.SwapData;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.sddl.message.Message;
import main.java.ckafka.mobile.CKMobileNode;
import main.java.ckafka.mobile.tasks.SendLocationTask;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HelloCoreClient extends CKMobileNode {

    private int stepNumber = 0;

    @Override
    public void connected(NodeConnection nodeConnection) {
        try {
            logger.debug("Connected");
            final SendLocationTask sendlocationtask = new SendLocationTask(this);
            this.scheduledFutureLocationTask = this.threadPool.scheduleWithFixedDelay(
                    sendlocationtask, 5000, 60000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Error scheduling SendLocationTask", e);
        }
    }

    @Override
    public void newMessageReceived(NodeConnection nodeConnection, Message message) {
        try {
            SwapData swp = fromMessageToSwapData(message);
            String str = new String(swp.getMessage(), StandardCharsets.UTF_8);
            System.out.println("Message: " + str);
        } catch (Exception e) {
            System.out.println("Error reading new message received");
        }
    }
    // Origem: -43.33528348294681 -23.05111584059184
    // Destino -43.4495035617561 -22.870186322362624

    @Override
    public SwapData newLocation(Integer messageCounter) {
        this.logger.debug("Getting new location");
        ObjectNode location = this.objectMapper.createObjectNode();
        Float amountZ = ThreadLocalRandom.current().nextFloat();

//        double stepX = (-43.4495035617561 - (-43.55312363257685)) / 10;
//        double stepY = (-22.870186322362624 - (-23.05111584059184)) / 10;

//        Double amountX = -43.55312363257685 + stepX * this.stepNumber;
//        Double amountY = -23.05111584059184 + stepY * this.stepNumber;

        Double amountX = -43.33528348294175;
        Double amountY = -22.879841526836287;

//       this.stepNumber = (this.stepNumber+1) % 10;

        location.put("ID", this.mnID.toString());
        location.put("messageCount", messageCounter);
        location.put("longitude", amountX);
        location.put("latitude", amountY);
        location.put("positionZ", amountZ);
        location.put("date", (new Date()).toString());

        try {
            SwapData locationData = new SwapData();
            locationData.setContext(location);
            locationData.setDuration(60);
            return locationData;
        } catch (Exception var7) {
            this.logger.error("Location Swap Data could not be created", var7);
            return null;
        }
    }

    @Override
    public void disconnected(NodeConnection nodeConnection) {

    }

    @Override
    public void unsentMessages(NodeConnection nodeConnection, List<Message> list) {

    }

    @Override
    public void internalException(NodeConnection nodeConnection, Exception e) {

    }

}
//Funcionando
//    // Origem: -43.18559736525978 -22.936826006961283
//    // Destino -43.23232376069340 -22.978883470478085
//
//    double stepX = (-43.23232376069340 - (-43.18559736525978)) / 10;
//    double stepY = (-22.978883470478085 - (-22.936826006961283)) / 10;
//
//    Double amountX = -43.18559736525978 + stepX * this.stepNumber;
//    Double amountY = -22.936826006961283 + stepY * this.stepNumber;
//        this.stepNumber = (this.stepNumber+1) % 10;