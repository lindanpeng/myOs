package myos.manager;


import java.io.UnsupportedEncodingException;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class Demo {
    public static void main(String[] args) throws UnsupportedEncodingException {

       java.lang.String name="独步清风";
       String str=new String(name.getBytes("utf-8"),"iso-8859-1");
        System.out.println(str);
       System.out.println(new String(str.getBytes("iso-8859-1"),"utf-8"));
       String str1=new String(new String("里的".getBytes("utf-8"),"iso-8859-1").getBytes("utf-8"),"iso-8859-1");
       System.out.println(str1);
    }
}
