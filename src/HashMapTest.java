import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {
    public static char generateRandomChar() {
        Random random = new Random();
        char c = (char) (random.nextInt(26) + 'a');
        return c;
    }
    public static int generateRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(1000) + 1;
        return number;
    }
    @Test
    public void unittest1(){
        HashMap<String, Integer> hashMap=new HashMap<String, Integer>();
        hashMap.put("abc",1);
        assertEquals(hashMap.get("abc"),1);
        hashMap.put("abc",2);
        assertEquals(hashMap.get("abc"),2);
    }
    @Test
    public void unittest2(){
        HashMap<Character, Integer> hashMap=new HashMap<Character, Integer>();
        for(char c='a';c<='z';c++){
            int val= (int) (Math.random()%10);
            hashMap.put(c,val);
            assertEquals(hashMap.get(c),val);
        }
    }

    @Test
    public void unittest3(){
        HashMap<Character, Integer> hashMap=new HashMap<Character, Integer>();
        ConcurrentHashMap<Character,Integer> concurrentHashMap=new ConcurrentHashMap<Character,Integer>();
        for(int x=0;x<10000;x++){
            char key=generateRandomChar();
            int val=generateRandomNumber();
            hashMap.put(key,val);
            concurrentHashMap.put(key,val);
        }
        for(char c='a';c<='z';c++){
            assertEquals(hashMap.get(c),concurrentHashMap.get(c));
            System.out.println(hashMap.get(c));
        }

    }
}