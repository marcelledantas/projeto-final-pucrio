package org.example;

import ckafka.data.Swap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.java.ckafka.GroupDefiner;
import main.java.ckafka.GroupSelection;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainGD implements GroupSelection {


    public MainGD(){
        ObjectMapper objectMapper = new ObjectMapper();
        Swap swap = new Swap(objectMapper);
        new GroupDefiner(this, swap);
    }

    public static void main(String[] args) {

        // set environment variables if needed
        Map<String,String> env = new HashMap<String, String>();
        env.putAll(System.getenv());
        if(System.getenv("gd.one.consumer.topics") == null)
            env.put("gd.one.consumer.topics", "GroupReportTopic");
        if(System.getenv("gd.one.consumer.auto.offset.reset") == null)
            env.put("gd.one.consumer.auto.offset.reset", "latest");
        if(System.getenv("gd.one.consumer.bootstrap.servers") == null)
            env.put("gd.one.consumer.bootstrap.servers", "127.0.0.1:9092");
        if(System.getenv("gd.one.consumer.group.id") == null)
            env.put("gd.one.consumer.group.id", "gw-gd");
        if(System.getenv("gd.one.producer.bootstrap.servers") == null)
            env.put("gd.one.producer.bootstrap.servers", "127.0.0.1:9092");
        if(System.getenv("gd.one.producer.retries") == null)
            env.put("gd.one.producer.retries", "3");
        if(System.getenv("gd.one.producer.enable.idempotence") == null)
            env.put("gd.one.producer.enable.idempotence", "true");
        if(System.getenv("gd.one.producer.linger.ms") == null)
            env.put("gd.one.producer.linger.ms", "1");


        try {
            setEnv(env);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new MainGD();
    }

    private static void setEnv(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        }
        catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }

    }

    @Override
    public Set<Integer> groupsIdentification() {
        Set<Integer> setOfGroups = new HashSet<Integer>();

        setOfGroups.add(20);
        setOfGroups.add(100);
        setOfGroups.add(101);
        setOfGroups.add(102);

        return setOfGroups;
    }

    @Override
    public Set<Integer> getNodesGroupByContext(ObjectNode objectNode) {
        Set<Integer> setOfGroups = new HashSet<Integer>();
        int groups = 3;
        String latitude = objectNode.get("longitude").textValue();
        String uuid = objectNode.get("ID").textValue();
        int digit_id = Integer.parseInt(uuid.substring(uuid.length() - 1));

        setOfGroups.add((digit_id % groups) + 100);

        setOfGroups.add(20);

        return setOfGroups;
    }

    @Override
    public String kafkaConsumerPrefix() {
        return "gd.one.consumer";
    }

    @Override
    public String kafkaProducerPrefix() {
        return "gd.one.consumer";
    }
}