package myos.manager;



import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class Demo {
    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
        ExecutorService executorService= Executors.newFixedThreadPool(2);
      B b=new B();
        executorService.execute(b);
        Thread.sleep(2000);
        executorService.execute(b);
        executorService.shutdown();
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
        System.out.println("hello");
    }
}
