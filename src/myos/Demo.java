package myos;



import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class Demo {
    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
//        LinkedList<String> list=new LinkedList<>();
//        list.add("a");
//        list.add("b");
//        list.add("c");
//        ListIterator<String> iterator=list.listIterator();
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        if (iterator.hasPrevious()){
//            System.out.println(iterator.previous());
//        }
        ExecutorService executorService= Executors.newFixedThreadPool(2);
        B b1=new B();
        B b2=new B();
        executorService.execute(b1);
        executorService.execute(b2);
        executorService.shutdownNow();
        executorService.execute(b1);
        executorService.execute(b2);
    }
}

class A{
    protected  String name="hello";
    public A(){

    }
}
class B extends A implements Runnable{
    private String name;
    public B(){
        super();

    }

    @Override
    public void run() {
        try {
        for (int i=0;i<1000;i++){
            System.out.println(i);
                Thread.sleep(2000);
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
