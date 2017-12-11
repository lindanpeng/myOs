package myos.manager.filesys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindanpeng on 2017/12/6.
 */
public class FileOperator {
    private Fat fat;
    List<OpenedFile> openedFiles;
    public FileOperator(Fat fat){
        this.openedFiles=new ArrayList<>();
    }
    /**
     * 建立文件
     * @param fileName 文件名
     * @param property 文件属性
     */
    public void create(String fileName,int property){

    }

    /**
     * 打开文件
     * @param fileName 文件名
     * @param opType 操作类型(读或写)
     */
    public void open(String fileName,int opType){

    }

    /**
     * 读取文件
     * @param fileName 文件名
     * @param length 要读取的字节数
     */
    public void read(String fileName,int length){

    }

    /**
     * 写文件
     * @param fileName 文件名
     * @param buffer 要写入的缓冲区数据
     * @param length 数据的长度
     */
    public void write(String fileName,byte[] buffer,int length){

    }

    /**
     * 关闭文件
     * @param fileName 文件名
     */
    public void close(String fileName){

    }

    /**
     * 删除文件
     * @param  fileName 文件名
     */
    public void delte(String fileName){

    }
    /**
     * 显示文件
     * @param fileName 文件名
     */
    public void show(String fileName){

    }

    /**
     * 改变文件属性
     * @param fileName
     * @param newProperty
     */
    public void changeProperty(String fileName, int newProperty){

    }

    /**
     *建立目录
     * @param dirPath 目录路径
     */
    public void  md(String dirPath){

    }

    /**
     * 显示目录的内容
     * @param dirPath
     */
    public void dir(String dirPath){

    }

    /**
     * 删除空目录
     * @param dirPath 目录路径
     */
    public void rd(String dirPath){

    }
}
