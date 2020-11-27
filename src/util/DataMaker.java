/*
 * DataMaker.java
 * Author : 박찬형
 * Created Date : 2020-11-14
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMaker {
    public static List<Map<String, String>> make(String data){
        char[] charData = data.toCharArray();
        List<String> dataElements = new ArrayList<>();
        int count = 0;
        StringBuilder str = new StringBuilder();
        for(char c : charData){
            if(c == '{'){
                if(count != 0){
                    dataElements.remove(0);
                }
                count++;
            }
            str.append(c);
            if(c == '}'){
                count--;
                dataElements.add(str.toString());
                str = new StringBuilder();
            }
        }

        List<Map<String, String>> result = new ArrayList<>();
        for(String packetStr : dataElements){
            Map<String, String> packet = new HashMap<>();
            String[] array = packetStr.split("\\{\"|\":\"|\", \"|\"}");
            for(int i = 1; i < array.length - 1; i += 2){
                packet.put(array[i], array[i + 1]);
            }
            result.add(packet);
        }

        return result;
    }
}
