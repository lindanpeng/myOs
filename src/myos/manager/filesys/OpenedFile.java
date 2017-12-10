package myos.manager.filesys;

/**
 * Created by lindanpeng on 2017/12/6.
 *已打开文件
 */
public class OpenedFile {
    //文件路径名
    private String pathName;
    //文件属性
    private String property;
    //文件长度
    private int length;
    //操作类型
    private int opType;
    //读指针
    private Pointer readPointer;
    //写指针
    private Pointer writePointer;
}
class Pointer{
    //块号
    private int blockNo;
    //块内地址
    private int address;

}
