package myos.manager;


import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class Demo {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Integer[] arr=new Integer[10];
        arr[5]=10;
        List<Integer> integerList=Arrays.asList(arr);
        System.out.println(integerList.get(5));
        arr[5]=11;
        System.out.println(integerList.get(5));
    }
}
