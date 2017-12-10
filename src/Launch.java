import myos.constant.OsConstant;
import myos.manager.filesys.Fat;
import myos.manager.filesys.FileOperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static myos.constant.OsConstant.PHYSICAL_BLOCK_QUNTITY;
import static myos.constant.OsConstant.PHYSICAL_BLOCK_SIZE;

public class Launch {
    private byte[] memory=new byte[OsConstant.MEMORY_CAPACITY];
    private Fat fat;//文件分配表
    private FileOperator fileOperator;//文件操作管理
    public Launch(){
        initDisk();

    }

    /**
     * 初始化模拟磁盘
     */
    void initDisk(){
        File file = new File("resource/disk.dat");
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
    public static void main(String[] args) {
        new Launch();

    }
}
