import myos.constant.OsConstant;
import myos.manager.filesys.Fat;
import myos.manager.filesys.FileOperator;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static myos.constant.OsConstant.PHYSICAL_BLOCK_QUNTITY;
import static myos.constant.OsConstant.PHYSICAL_BLOCK_SIZE;

public class Launch {
    private static final String DISK_FILE ="resource/disk.dat";
    private byte[] memory=new byte[1024];
    private Fat fat;//文件分配表
    private FileOperator fileOperator;//文件操作管理

    public Launch(){
        init();
    }

    /**
     * 初始化系统
     */
    public void init(){
       initDisk();
       loadToMemory();
    }
    /**
     * 初始化模拟磁盘
     */
    void initDisk(){
        File file = new File(DISK_FILE);
        FileOutputStream fout=null;
        //判断模拟磁盘是否已经创建
        if (!file.exists()) {
            try {
                fout = new FileOutputStream(file);
                byte[] bytes;
                for(int i=0;i<PHYSICAL_BLOCK_QUNTITY;i++){
                    bytes=new byte[PHYSICAL_BLOCK_SIZE];
                    //写入初始文件分配表
                    if (i==0){
                        //前三个盘块不可用
                        bytes[0]=-1;
                        bytes[1]=-1;
                        bytes[2]=-1;
                    }
                    //写入根目录
                    if (i==2) {
                        bytes[0]='/';//根目录名
                        bytes[1]=0;
                        bytes[2]=0;
                        bytes[3]=' ';//保留空格
                        bytes[4]=' ';//保留空格
                        bytes[5]=Byte.parseByte("00001000",2);//目录属性
                        bytes[6]=2;//起始盘号
                        bytes[7]=0;//保留一字节未使用
                    }
                    fout.write(bytes);
                }
            } catch (FileNotFoundException e) {
                System.out.println("打开/新建磁盘文件失败！");
                e.printStackTrace();
                System.exit(0);
            }catch (IOException e){
                System.out.println("写入文件时发生错误");
                e.printStackTrace();
                System.exit(0);
            }finally {
                if (fout!=null){
                    try {
                        fout.close();
                    } catch (IOException e) {
                        System.out.println("关闭文件流时发生错误");
                        e.printStackTrace();
                    }
                }
            }

        } else {
            System.out.println("模拟磁盘已存在，无需重新创建");
        }
    }

    /**
     * 将磁盘文件载入到内存
     */
    void loadToMemory(){
          FileInputStream fin=null;
        try {
            fin=new FileInputStream(DISK_FILE);
           // byte[] buffer=new byte[192];//磁盘前三块
            fin.read(memory,0,192);//读取磁盘前三pan块到内存
            fat=new Fat(memory);//创建文件分配表
            fileOperator=new FileOperator(fat);//创建已打开文件表

        }catch (FileNotFoundException e) {
            System.err.println("打开模拟磁盘失败");
            e.printStackTrace();
        }catch (IOException e){
            System.err.println("读取模拟磁盘时失败");
            e.printStackTrace();
        }
        finally {
            if (fin!=null){
                try {
                    fin.close();
                } catch (IOException e) {
                     System.err.println("关闭磁盘文件失败");
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * yuyonghujiaohu
     */
    public void interactive(){
        Scanner scanner=new Scanner(System.in);
        String command;
        System.out.println("enter system interactive interface:");
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





    public static void main(String[] args) {
        Launch launch=new Launch();
        launch.interactive();
    }
}
