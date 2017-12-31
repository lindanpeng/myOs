package myos.manager;



import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class Demo {
    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
    getInstruction();
}
    static     public byte[] getInstruction()
    {
        String[] instruction ={"mov ax,50","inc ax","mov bx,111","dec bx","! a 1","end"};
        ArrayList<Byte> ins=new ArrayList<>();
        for(int i=0;i<instruction.length;i++)
        {
            String[] str=instruction[i].split("[\\s|,]");
            byte first=(byte)0;
            byte second =(byte)0;
            if(str.length>1) {
                if (str[1].contains("a"))
                    second = 0;
                else if (str[1].contains("b")) {
                    second = 4;
                } else if (str[1].contains("b")) {
                    second = 8;
                } else {
                    second = 12;
                }
            }
            if(str[0].contains("mov"))
            {
                first = (byte)80;
                ins.add((byte)(first+second));
                ins.add(Byte.valueOf(str[2]));
            }else if(str[0].contains("inc")){
                first = (byte)16;
                ins.add((byte)(first+second));
            }else if(str[0].contains("dec")){
                first = (byte)32;
                ins.add((byte)(first+second));
            }else if(str[0].contains("!")){
                first = (byte)48;
                ins.add((byte)(first+second+Byte.valueOf(str[2])));
            }else if(str[0].contains("end")){
                ins.add((byte)64);
            }
        }
        byte[] instruct= new byte[ins.size()];
        for(int i=0;i<instruct.length;i++)
        {
            instruct[i] = ins.get(i);
        }
        return  instruct;
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
