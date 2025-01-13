/**
 * @author lz520520
 * @date 2025/1/13 17:20
 */

package org.win_manage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class ClassCall {
    public static void main(String[] args) throws Exception {
        HashMap result = new HashMap();
        Class ProcessClazz = Class.forName("org.win_manage.Process");
        ArrayList processes = (ArrayList) ProcessClazz.getMethod("getProcesses").invoke(null);
        for (int i = 0; i < processes.size(); i++) {
            Object process = processes.get(i);
            HashMap data = new HashMap();
            for (Field field : process.getClass().getDeclaredFields()) {
                data.put(field.getName(), field.get(process));
            }
            result.put(String.valueOf(i), data);
        }
        result.put("count", processes.size());
        System.out.println(result);
    }
}