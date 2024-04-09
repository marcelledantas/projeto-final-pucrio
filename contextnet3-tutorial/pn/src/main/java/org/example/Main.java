package org.example;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // set environment variables if needed
        Map<String,String> env = new HashMap<String, String>();
        env.putAll(System.getenv());
        if(System.getenv("app.consumer.topics") == null)
            env.put("app.consumer.topics", "AppModel");
        if(System.getenv("app.consumer.auto.offset.reset") == null)
            env.put("app.consumer.auto.offset.reset", "latest");
        if(System.getenv("app.consumer.bootstrap.servers") == null)
            env.put("app.consumer.bootstrap.servers", "127.0.0.1:9092");
        if(System.getenv("app.consumer.group.id") == null)
            env.put("app.consumer.group.id", "gw-consumer");
        if(System.getenv("app.producer.retries") == null)
            env.put("app.producer.retries", "3");
        if(System.getenv("app.producer.enable.idempotence") == null)
            env.put("app.producer.enable.idempotence", "true");
        if(System.getenv("app.producer.linger.ms") == null)
            env.put("app.producer.linger.ms", "1");
        if(System.getenv("app.producer.acks") == null)
            env.put("app.producer.acks", "all");

        try {
            setEnv(env);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scanner keyboard = new Scanner(System.in);
        ProcessingNode PN = new ProcessingNode();

        while(true) {
//            PN.sendUnicastMessage(keyboard);
            PN.sendGroupcastMessage(keyboard);
        }
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

}