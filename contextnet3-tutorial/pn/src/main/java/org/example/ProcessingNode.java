package org.example;

import ckafka.data.Swap;
import ckafka.data.SwapData;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.application.ModelApplication;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProcessingNode extends ModelApplication{

//    Map<String, Integer> bairroMap = loadBairroMap("/Bairros/RioDeJaneiro.lista");

    private Swap swap;
    private ObjectMapper objectMapper;

    public ProcessingNode() {
        this.objectMapper = new ObjectMapper();
        this.swap = new Swap(objectMapper);
    }

    @Override
    public void recordReceived(ConsumerRecord record) {
        System.out.println(String.format("Mensagem recebida de %s", record.key()));
        try {
            SwapData data = swap.SwapDataDeserialization((byte[]) record.value());
            String text = new String(data.getMessage(), StandardCharsets.UTF_8);
            System.out.println("Mensagem recebida = " + text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUnicastMessage(Scanner keyboard) {
        System.out.println("UUID:\nHHHHHHHH-HHHH-HHHH-HHHH-HHHHHHHHHHHH");
        String uuid = keyboard.nextLine();
        System.out.print("Message: ");
        String messageText = keyboard.nextLine();
        System.out.println(String.format("Sending |%s| to %s.", messageText, uuid));
        try {
            sendRecord(createRecord("PrivateMessageTopic", uuid,
                    swap.SwapDataSerialization(createSwapData(messageText))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGroupcastMessage(Scanner keyboard) {

        Map<String, Integer> bairroMap = loadBairroMap("RioDeJaneiro.lista");

        System.out.println("Groupcast message. Digite um bairro do RJ:\n");

        String input = keyboard.nextLine();

        Integer group = bairroMap.get(input);

        if (group != null) {
            System.out.println("Bairro: " + input);
        } else {
            System.out.println("Bairro invalido. Digite um bairro valido.");
        }

        System.out.print("Alerta: ");
        String messageText = keyboard.nextLine();
        System.out.println(String.format("Enviando |%s| para %s.", messageText, input));
        try {
            sendRecord(createRecord("GroupMessageTopic", group.toString(),
                    swap.SwapDataSerialization(createSwapData(messageText))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Integer> loadBairroMap(String filePath) {
        Map<String, Integer> bairroMap = new HashMap<>();

        try {
            File file = new File(filePath);
            System.out.println("Absolute Path: " + file.getAbsolutePath());

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int number = Integer.parseInt(parts[0]);
                    String path = parts[1];
                    String name = extractNameFromPath(path);
                    bairroMap.put(name, number);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bairroMap;
    }

    private static String extractNameFromPath(String path) {
        int startIndex = path.indexOf("/Bairro") + "/Bairro".length();
        int endIndex = path.lastIndexOf('.');
        if (startIndex >= "/Bairro".length() && endIndex > startIndex) {
            return path.substring(startIndex, endIndex);
        } else {
            return "InvalidPath";
        }
    }
}
