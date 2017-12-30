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
import myos.manager.filesys.FileOperator;

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
    public void excuteCMD(KeyEvent event) throws  Exception{
            if(event.getCode() == KeyCode.ENTER)
            {
                String str  = cmdView.getText();
                String command = str.substring(0,str.length()-1).replaceAll("\\s{1,}"," ");//去除多余的空格和换行符
                cmdView.clear();
                String[] instruction = command.split(" ");
                if(instruction.length>1) {
                    System.out.println(instruction[0] + " "+instruction[1]);
                    if(instruction[0].contains("create")) {
                        os.fileOperator.create(instruction[1],4);
                    }else if(instruction[0].contains("delete")){
                        os.fileOperator.delete(instruction[1]);
                    }else if(instruction[0].contains("type")){

                    }else if(instruction[0].contains("copy")&&instruction.length==3){

                    }else if(instruction[0].contains("mkdir")){

                    }else if(instruction[0].contains("rmdir")){

                    }else if(instruction[0].contains("change")&&instruction.length==3){
                        int newProperty = Integer.valueOf(instruction[2]).intValue();
                        os.fileOperator.changeProperty(instruction[1],newProperty);
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
