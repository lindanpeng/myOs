package myos.manager;



import javax.sound.midi.SysexMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class Demo {
    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
        Thread thread=new Thread(new B());
        thread.start();
        for (int i=0;i<8;i++){
            for (int j=0;j<16;j++){
                System.out.println("<Pane prefHeight=\"200.0\" prefWidth=\"200.0\" GridPane.columnIndex=\""+j+"\" GridPane.rowIndex=\""+i+"\" />");
            }
        }
       // thread.start();
}
}
class A{
    protected  String name="hello";
    public A(){
        System.out.println(name);
    }
}
class B extends A implements Runnable{
    private String name;
    public B(){
        super();
        System.out.println(name);
    }

    @Override
    public void run() {
        System.out.println("hello");
    }
}
