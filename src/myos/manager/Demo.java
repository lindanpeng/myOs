package myos.manager;



import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class Demo {
    public static void main(String[] args) throws UnsupportedEncodingException {
//       byte[] bytes={'r','t',0};
//       String str= new String(bytes);
//       OS.out.println(str.equals("rt"));
//       for (byte b:str.getBytes()){
//           OS.out.println(b);
//       }
//        ArrayList<Byte> list=new ArrayList<>();
//        list.add((byte)1);
//        list.add((byte)2);
//        Byte[] bytes=list.toArray(new Byte[list.size()]);
//        for (byte b:bytes)
//            System.out.println(b);
        List<String> list=new LinkedList<>();
        list.add("A");
        list.add("B");
        list.add("D");
        ListIterator<String> it=list.listIterator();
        while(it.hasNext()){
            String str=it.next();
            if (str.equals("A"))
                break;
        }
        it.add("C");
        for (String s:list){
            System.out.println(s);
        }
}
}
