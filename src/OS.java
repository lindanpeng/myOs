import myos.constant.OsConstant;
import myos.manager.filesys.Catalog;
import myos.manager.filesys.FileOperator;
import myos.manager.memory.Memory;
import myos.manager.process.CPU;
import myos.manager.process.Clock;
import myos.manager.process.ProcessOperator;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static myos.constant.OsConstant.DISK_BLOCK_QUNTITY;
import static myos.constant.OsConstant.DISK_BLOCK_SIZE;

public class OS {

    private FileOperator fileOperator;//文件操作管理
    private ProcessOperator processOperator;
    private CPU cpu;
    private Memory memory;
    private Clock clock;

    //模拟磁盘
    private RandomAccessFile disk;
    public OS(){
        init();
    }

    /**
     * 初始化系统
     */
    public void init(){
       initDisk();
       memory=new Memory();
       cpu=new CPU(memory);
       clock=new Clock(cpu);
       processOperator=new ProcessOperator(cpu,memory);
       fileOperator=new FileOperator(disk,processOperator);
       new Thread(cpu).start();
       new Thread(clock).start();
    }
    /**
     * 初始化模拟磁盘
     */
    void initDisk(){
        File file = new File(OsConstant.DISK_FILE);
        FileOutputStream fout=null;
        //判断模拟磁盘是否已经创建
        if (!file.exists()) {
            try {
                fout = new FileOutputStream(file);
                byte[] bytes;
                for(int i = 0; i< DISK_BLOCK_QUNTITY; i++){
                    bytes=new byte[DISK_BLOCK_SIZE];
                    //写入初始文件分配表
                    if (i==0){
                        //前三个盘块不可用
                        bytes[0]=-1;
                        bytes[1]=-1;
                        bytes[2]=-1;
                    }
                    //写入根目录
                    if (i==2) {

                        bytes[0]='r';//根目录名为rt
                        bytes[1]='t';
                        bytes[2]=0;
                        bytes[3]=' ';//保留空格
                        bytes[4]=' ';//保留空格
                        bytes[5]=Byte.parseByte("00001000",2);//目录属性
                        bytes[6]=-1;//起始盘号
                        bytes[7]=0;//保留一字节未使用
                    }
                    fout.write(bytes);
                }
            } catch (FileNotFoundException e) {
                java.lang.System.out.println("打开/新建磁盘文件失败！");
                e.printStackTrace();
                java.lang.System.exit(0);
            }catch (IOException e){
                java.lang.System.out.println("写入文件时发生错误");
                e.printStackTrace();
                java.lang.System.exit(0);
            }finally {
                if (fout!=null){
                    try {
                        fout.close();
                    } catch (IOException e) {
                        java.lang.System.out.println("关闭文件流时发生错误");
                        e.printStackTrace();
                    }
                }
            }

        } else {
            java.lang.System.out.println("模拟磁盘已存在，无需重新创建");
        }

        try {
            disk=new RandomAccessFile(OsConstant.DISK_FILE,"rw");
        } catch (FileNotFoundException e) {
            java.lang.System.err.println("未找到磁盘模拟文件");
        }
    }

    /**
     * 将磁盘文件载入到内存
     */
    void loadToMemory(){
//          FileInputStream fin=null;
//        try {
//            fin=new FileInputStream(DISK_FILE);
//            byte[] buffer=new byte[128];
//            fin.read(buffer,0,128);//读取磁盘前两个盘块
//            List<Byte> fat=new ArrayList<>(buffer.length);
//            for (byte b:buffer)
//                fat.add(b);
//           fileOperator=new FileOperator(fat);//创建已打开文件表
//
//        }catch (FileNotFoundException e) {
//            OS.err.println("打开模拟磁盘失败");
//            e.printStackTrace();
//        }catch (IOException e){
//            OS.err.println("读取模拟磁盘时失败");
//            e.printStackTrace();
//        }
//        finally {
//            if (fin!=null){
//                try {
//                    fin.close();
//                } catch (IOException e) {
//                     OS.err.println("关闭磁盘文件失败");
//                    e.printStackTrace();
//                }
//            }
//        }

    }

    /**
     * 用户交互
     */
    public void interactive() throws Exception {
        Scanner scanner=new Scanner(java.lang.System.in);
        String command;
        java.lang.System.out.println("欢迎进入用户交互界面:");
        do {
            command=scanner.nextLine();
            String[] opAndArgs=command.split(" *");
            if (opAndArgs[0].equals("createFile")){
                fileOperator.create(opAndArgs[1],Integer.valueOf(opAndArgs[2]));
            }
            if (opAndArgs[0].equals("openFile")){
                fileOperator.open(opAndArgs[1],Integer.valueOf(opAndArgs[2]));
            }
        }while (!command.equals("exit"));
    }

    /**
     * 关闭系统资源
     */
    public void close(){
        try {
            disk.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test() throws Exception {
       // fileOperator.create("rt/c",4);
      //  fileOperator.open("rt/b",0);
     //  fileOperator.append("rt/b",",myson".getBytes(),6);
        List<Catalog> catalogs=fileOperator.dir("rt");
        for (Catalog catalog:catalogs){
            System.out.println(catalog.getName());
        }
        //byte[] bytes=fileOperator.read("rt/a",3);
        //fileOperator.close("rt/a");
        //fileOperator.delete("rt/a");
        //fileOperator.open("rt/a",0);
       // fileOperator.read("rt/a",3);
    }

    public static void main(String[] args) throws Exception {
        OS OS =new OS();
        OS.test();
        OS.close();
    }
}
