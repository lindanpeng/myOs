package myos.manager.filesys;

/**
 * Created by lindanpeng on 2017/12/6.
 *目录/文件登记项
 */
public class Catalog {
    private static int catalogSize=8;
    //目录项占用空间
    private byte[] bytes;
    //文件名
    private String name;
    //文件类型，如果是目录则为空格
    private String type;
    //文件属性
    private int property;
    //起始盘号
    private int startBlock;
    //文件长度，如果是目录则为0
    private int fileLength;
    public Catalog(byte[] bytes){
        this.bytes=bytes;
        this.name=new String(bytes,0,3);
        this.type=new String(bytes,3,2);
        this.property=bytes[5];
        this.startBlock=bytes[6];
        this.fileLength=bytes[7];
    }


}
