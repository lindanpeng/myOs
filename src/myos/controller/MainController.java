package myos.controller;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import myos.OS;
import myos.constant.UIResources;
import myos.manager.device.A;
import myos.manager.filesys.Catalog;
import myos.manager.process.Clock;
import myos.ui.MyTreeItem;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lindanpeng on 2017/12/29.
 */
public class MainController implements Initializable {
    @FXML
    private GridPane fatView;
    @FXML
    private Button startBtn;
    @FXML
    private TreeView<Catalog> catalogTreeView;
    @FXML
    private Text systemTimeTxt;
    @FXML
    private Text timesliceTxt;
    @FXML
    private TextArea cmdView;
    private OS os;
    private boolean launched=false;
    public MainController() throws Exception {
        os = new OS(this);
    }
    /*-------------------用户请求------------------------*/
    /**
     * 启动系统
     */
    public void launchOS() throws Exception {
        if (!launched) {
            os.start();
            launched=true;
            startBtn.setText("关闭系统");
            new UpdateUIThread().start();
        }else{
            closeOS();
            launched=false;
            startBtn.setText("启动系统");
        }

    }
    public void excuteCMD(KeyEvent event){
            if(event.getCode() == KeyCode.ENTER)
            {
                String str  = cmdView.getText();
                String command = str.replaceAll("\\s{1,}"," ");
                cmdView.clear();
                String[] instruction = command.split(" ");
                if(instruction.length>1) {
                    System.out.println(instruction[0] + " "+instruction[1].substring(0,instruction[1].length()-1));
                    String path = instruction[1].substring(0,instruction[1].length()-1);
                    if(instruction[0].contains("create")) {
                        createFile(path);
                    }else if(instruction[0].contains("delete")){
                        deleteFile(path);
                    }else if(instruction[0].contains("type")){
                        displayFile(path);
                    }else if(instruction[0].contains("copy")&&instruction.length==3){
                        copyFile(instruction[1],instruction[2].substring(0,instruction[2].length()-1));
                    }else if(instruction[0].contains("mkdir")){
                        makeDir(path);
                    }else if(instruction[0].contains("rmdir")){
                        rmDir(path);
                    }else {
                        return;
                    }
                }else
                {
                    return;
                }
            }
    }
    public void closeOS(){
        os.close();
    }
    private  void createFile(String path){
        System.out.println("创建文件");
        File file =new File(path);
        if(file.exists()){
            System.out.println("创建单个文件失败，目标文件已存在！");
            return;
        }
        if(path.endsWith(File.separator)){
            System.out.println("创建单个文件失败，目标文件不能为目录！");
            return;
        }
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return ;
            }
        }
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件成功！");
                return ;
            } else {
                System.out.println("创建单个文件失败！");
                return ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件失败！" + e.getMessage());
            return ;
        }
    }
    private  void deleteFile(String path){
        System.out.println("删除文件");
        File file =new File(path);
        if(file.exists()&&file.isFile()) {
            file.delete();
        }
    }
    private void displayFile(String path){
        System.out.println("显示文件");
        String temp=null;
        try{
            File file=new File(path);
            if(!file.exists()||file.isDirectory())
                throw new FileNotFoundException();
            BufferedReader br=new BufferedReader(new FileReader(file));
            StringBuffer sb=new StringBuffer();
            temp=br.readLine();
            while(temp!=null){
                sb.append(temp+" ");
                temp=br.readLine();
            }
            System.out.println(sb.toString());
        }catch (IOException ex){

        }
    }
    private void copyFile(String souPath,String desPath){
        System.out.println("复制文件");
        try {
            File file = new File(desPath);
            if (!file.exists()) {
                createFile(desPath);
                return;
            }
            FileInputStream in = new FileInputStream(souPath);
            FileOutputStream out = new FileOutputStream(file);
            int c;
            byte buffer[] = new byte[1024];
            while ((c = in.read(buffer)) != -1) {
                for (int i = 0; i < c; i++)
                    out.write(buffer[i]);
            }
            in.close();
            out.close();
        }catch (IOException ex){
            System.out.println(ex.toString());
        }
    }
    private void makeDir(String path){
        System.out.println("创建目录");
        File dir = new File(path);
        if (dir.exists()) {
            System.out.println("创建目录失败，目标目录已经存在");
            return ;
        }
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        if (dir.mkdirs()) {//创建目录
            System.out.println("创建目录成功！");
            return ;
        } else {
            System.out.println("创建目录失败！");
            return ;
        }
    }
    private void rmDir(String path){
        System.out.println("删除空目录");
        File dir= new File(path);
        if(dir.exists()&&dir.isDirectory()&&dir.length()==0) {
            dir.delete();
            System.out.println("删除目录成功！");
        }else{
            System.out.println("删除目录失败！");
            return;
        }
    }
    /*---------------------后台主动刷新---------------------------------*/
    /**
     * 构建目录树
     */
    public void initCatalogTree(Catalog root) throws Exception {
        TreeItem<Catalog> treeItem=new MyTreeItem(root);
        catalogTreeView.setRoot(treeItem);
        catalogTreeView.setCellFactory(new Callback<TreeView<Catalog>, TreeCell<Catalog>>() {
            public TreeCell<Catalog> call(TreeView<Catalog> param) {
                return new TreeCell<Catalog>(){

                    @Override
                    protected void updateItem(Catalog catalog,boolean empty){
                        super.updateItem(catalog,empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(catalog.getName());
                            if (catalog.isDirectory()){
                                setGraphic(UIResources.getDirectoryIcon());
                            }
                            else {
                                setGraphic(UIResources.getFileIcon());
                            }
                        }
                    }
                };
            }
        });
      //  catalogTreeView.setCellFactory((TreeView<Catalog> p)->new MyTreeCell());
        catalogTreeView.refresh();
    }

    /**
     * 删除树节点
     * @param catalog
     */
    public void removeTreeItem(Catalog catalog){
        TreeItem<Catalog> root= catalogTreeView.getRoot();
        if (root.isLeaf())
            return;
        for (TreeItem<Catalog> catalogTreeItem:root.getChildren()){
            if (catalogTreeItem.getValue().getName().equals(catalog.getName())){
                root.getChildren().removeAll(catalogTreeItem);
                catalogTreeView.refresh();
                return;
            }
            removeTreeItem(catalog);
        }

    }

    /**
     * 更新树节点
     * @param catalog
     */
    public void updateTreeItem(Catalog catalog){
        TreeItem<Catalog> root= catalogTreeView.getRoot();
        if (root.isLeaf())
            return;
        for (TreeItem<Catalog> catalogTreeItem:root.getChildren()){
            if (catalogTreeItem.getValue().getName().equals(catalog.getName())){
                catalogTreeItem.setValue(catalog);
                catalogTreeView.refresh();
                return;
            }
            removeTreeItem(catalog);
        }
    }

    /**
     * 更新磁盘使用情况
     */
    public void updateFatView(byte[] fat){
        for (int i=0;i<fat.length;i++){
            Pane pane = (Pane) fatView.getChildren().get(i);
            if (fat[i]!=0) {
                pane.setStyle("-fx-background-color: red");
            }
            else {
                pane.setStyle("-fx-background-color:coral");
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    private class UpdateUIThread extends Thread{
        @Override
        public void run(){
            while(!isInterrupted()){
                try {
                MainController.this.systemTimeTxt.setText(OS.clock.getSystemTime()+"");
                MainController.this.timesliceTxt.setText(OS.clock.getRestTime()+"");
                    Thread.sleep(Clock.TIMESLICE_UNIT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
