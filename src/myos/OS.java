package myos;

import myos.constant.OsConstant;
import myos.controller.MainController;
import myos.manager.filesys.FileOperator;
import myos.manager.memory.Memory;
import myos.manager.process.CPU;
import myos.manager.process.Clock;
import myos.manager.process.ProcessCreator;

import java.io.*;
import java.util.Scanner;

import static myos.constant.OsConstant.DISK_BLOCK_QUNTITY;
import static myos.constant.OsConstant.DISK_BLOCK_SIZE;

public class OS {
    public static RandomAccessFile disk; //模拟磁盘
    public static FileOperator fileOperator;//文件操作管理
    public static ProcessCreator processCreator;//进程创建器
    public static CPU cpu;//CPU
    public static Memory memory;//内存
    public static Clock clock;//时钟
    public static volatile boolean launched;
    public static MainController mainController;//界面控制类

    public OS(MainController mainController) throws Exception {
        initDisk();
        this.mainController = mainController;
        disk= new RandomAccessFile(OsConstant.DISK_FILE, "rw");
        memory = new Memory();
        cpu = new CPU();
        clock = new Clock();
        processCreator = new ProcessCreator();
        fileOperator = new FileOperator();
    }

    /**
     * 初始化系统
     */
    public void init() throws Exception {
        cpu.init();
        memory.init();
        clock.init();
        fileOperator.init();
    }

    /**
     * 初始化模拟磁盘
     */
    void initDisk() {
        File file = new File(OsConstant.DISK_FILE);
        FileOutputStream fout = null;
        //判断模拟磁盘是否已经创建
        if (!file.exists()) {
            try {
                fout = new FileOutputStream(file);
                byte[] bytes;
                for (int i = 0; i < DISK_BLOCK_QUNTITY; i++) {
                    bytes = new byte[DISK_BLOCK_SIZE];
                    //写入初始文件分配表
                    if (i == 0) {
                        //前三个盘块不可用
                        bytes[0] = -1;
                        bytes[1] = -1;
                        bytes[2] = -1;
                    }
                    //写入根目录
                    if (i == 2) {

                        bytes[0] = 'r';//根目录名为rt
                        bytes[1] = 't';
                        bytes[2] = 0;
                        bytes[3] = ' ';//保留空格
                        bytes[4] = ' ';//保留空格
                        bytes[5] = Byte.parseByte("00001000", 2);//目录属性
                        bytes[6] = -1;//起始盘号
                        bytes[7] = 0;//保留一字节未使用
                    }
                    fout.write(bytes);
                }
            } catch (FileNotFoundException e) {
                java.lang.System.out.println("打开/新建磁盘文件失败！");
                e.printStackTrace();
                java.lang.System.exit(0);
            } catch (IOException e) {
                java.lang.System.out.println("写入文件时发生错误");
                e.printStackTrace();
                java.lang.System.exit(0);
            } finally {
                if (fout != null) {
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

    }

    //启动系统
    public void start() throws Exception {
        //TODO
        init();
        new Thread(cpu).start();
        new Thread(clock).start();

    }

    /**
     * 关闭系统资源
     */
    public void close() {
            launched = false;
    }

}
