package io.renren.algo;

import com.alibaba.druid.sql.visitor.functions.Char;

import java.util.HashMap;
import java.util.Map;

public class HashAlgo {

    // 赎金问题
    public boolean ransomOk() {
        return true;
    }

    // 同构
    public boolean isomorphic(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        Map<Character, Character> a2b = new HashMap<>();
        Map<Character, Character> b2a = new HashMap<>();
        for (int i = 0; i < a.length(); i++) {
            Character ac = a.charAt(i);
            Character bc = b.charAt(i);
            if (a2b.containsKey(ac) && b2a.containsKey(bc)) {
                continue;
            }
            if (!a2b.containsKey(ac) && !b2a.containsKey(bc)) {
                a2b.put(ac, bc);
                b2a.put(bc, ac);
                continue;
            }
            return false;
        }
        return true;
    }


    // 同模式
    public boolean samePattern(String a, String b) {
        Map<String, Character> str2char = new HashMap<>();
        Map<Character, String> char2str = new HashMap<>();

        String[] split = b.split(" ");
        char[] charArray = a.toCharArray();
        if (split.length != charArray.length) {
            return false;
        }
        for (int i = 0; i < split.length; i++) {
            Character c = str2char.get(split[i]);
            String s = char2str.get(charArray[i]);
            if (c != null && s != null) {
                continue;
            }
            if (c == null && s== null) {
                char2str.put(c, s);
                str2char.put(s, c);
                continue;
            }
            return false;
        }

        return true;
    }

    public boolean isAnagram(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        Map<Character, Integer> amap = new HashMap<>();
        Map<Character, Integer> bmap = new HashMap<>();
        for (int i = 0; i < a.length(); i++) {
            Character ac = a.charAt(i);
            Character bc = b.charAt(i);

            if (amap.containsKey(ac)) {
                amap.put(ac, amap.get(ac) + 1);
            } else {
                amap.put(ac, 1);
            }

            if(bmap.containsKey(bc)) {
                bmap.put(bc, bmap.get(bc) + 1);
            } else {
                bmap.put(bc, 1);
            }
        }
        for (Map.Entry<Character, Integer> entry : amap.entrySet()) {
            Character key = entry.getKey();
            Integer value = entry.getValue();
            Integer i = bmap.get(key);
            System.out.println("check a: " + key + " ->  " + value);
            if (!value.equals(i)) {
                System.out.println("check b: " + key + " ->  " + i);
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String a = "aba";
        String b = "efe";
        HashAlgo hashAlgo = new HashAlgo();
        System.out.println("isomorphic: " +  hashAlgo.isomorphic(a, b));
        System.out.println("same pattern: "  + hashAlgo.samePattern("aba", "x yy x"));
        System.out.println("anagram: "  + hashAlgo.isAnagram("eat", "tea"));
    }
}
