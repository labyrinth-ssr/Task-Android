package com.example.mytask.ui.home;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.versionedparcelable.ParcelField;

import com.example.mytask.database.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Node implements Parcelable {
    // 包含id,parentid,name
    Task task;
    /**
     * 是否展开
     */
    private boolean isExpand = true;
    private boolean isChecked = false;
    private boolean isHideChecked = true;
    /**
     * 节点级别
     */
    private int level;
    /**
     * 节点展示图标
     */
    private int icon;
    /**
     * 节点所含的子节点, 保持更新
     */
    private List<Node> childrenNodes = new ArrayList<Node>();
    /**
     * 节点的父节点，保持更新
     */
    private Node parent;

    public Node() {
    }

    public Node(Task task) {
        super();
        this.task = task;
    }

    protected Node(Parcel in) {
        isExpand = in.readByte() != 0;
        isChecked = in.readByte() != 0;
        isHideChecked = in.readByte() != 0;
        level = in.readInt();
        icon = in.readInt();
        childrenNodes = in.createTypedArrayList(Node.CREATOR);
        parent = in.readParcelable(Node.class.getClassLoader());
    }

    public static final Creator<Node> CREATOR = new Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel in) {
            return new Node(in);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

//    public Node(Node node) throws IOException, ClassNotFoundException {
//        if (node!=null){
//            if (node.task!=null)
//                task = node.task ;
//            isExpand = node.isExpand;
//            isChecked = node.isChecked;
//            isHideChecked = node.isHideChecked;
//            level=node.level;
//            icon = node.icon;
//            if (node.childrenNodes !=null && node.childrenNodes.size()!=0){
//                childrenNodes = new ArrayList<>();
//                for (Node child:node.childrenNodes){
//                    childrenNodes.add(new Node(child));
//                }
//            }
//            if (node.parent!=null)
//                parent = new Node(node.parent);
//        }
//
//    }

    public Task getTask(){
        return this.task;
    }

    public void setTask(Task task){
        this.task = task;
    }

    public int getId() {
        return (int) this.task.getTaskId();
    }

    public void setId(int id) {
        this.task.setTaskId(id);
    }

    public int getparentId() {
        return (int) this.task.getParentTaskId();
    }

    public void setparentId(int parentId) {
        this.task.setParentTaskId(parentId);
    }

    public boolean isExpand() {
        return isExpand;
    }

    /**
     * 当父节点收起，其子节点也收起
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {
            for (Node node : childrenNodes) {
                node.setExpand(isExpand);
            }
        }
    }

    public String getName() {
        return this.task.getTaskName();
    }

    public void setName(String name) {
        this.task.setTaskName(name);
    }

    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public List<Node> getChildrenNodes() {
        return childrenNodes;
    }

    public void setChildrenNodes(List<Node> childrenNodes) {
        this.childrenNodes = childrenNodes;
    }

    static public Node getNodeById(List<Node> nodeList, Long id){
        for (Node node:nodeList){
            if (node.getId() == id)
                return node;
        }
        return null;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * 判断是否是根节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断是否是叶子节点
     *
     * @return
     */
    public boolean isLeaf() {
        return childrenNodes.size() == 0;
    }


    /**
     * 判断父节点是否展开
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isHideChecked() {
        return isHideChecked;
    }

//    public long size = 0L;

//    public Long getNoteSize() {
//        return getNoteSize(this);
//    }

//    public Long getNoteSize(Node node){
//        if (node.isLeaf()) {
//            return node.size;
//        } else {
//            return getChildrenNodeSize(node.childrenNodes);
//        }
//    }
//
//    private Long getChildrenNodeSize(List<Node> childrenList) {
//        long countSize = 0L;
//        for (Node node : childrenList) {
//            if (node.isLeaf()) {
//                countSize += node.size;
//            } else {
//                countSize += getChildrenNodeSize(node.childrenNodes);
//            }
//        }
//        return countSize;
//    }
    @Override
    public String toString() {
        return "Node{" +
                "id=" + task.getTaskId() +
                ", parentId=" + task.getParentTaskId() +
                ", isExpand=" + isExpand +
                ", isChecked=" + isChecked +
                ", isHideChecked=" + isHideChecked +
                ", name='" + task.getTaskName() + '\'' +
                ", level=" + getLevel() +
                ", icon=" + icon +
//                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
//        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return isExpand == node.isExpand && isChecked == node.isChecked && isHideChecked == node.isHideChecked && level == node.level && icon == node.icon && task.equals(node.task) && Objects.equals(childrenNodes, node.childrenNodes) && Objects.equals(parent, node.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, isExpand, isChecked, isHideChecked, level, icon, childrenNodes, parent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (isExpand ? 1 : 0));
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isHideChecked ? 1 : 0));
        dest.writeInt(level);
        dest.writeInt(icon);
        dest.writeTypedList(childrenNodes);
        dest.writeParcelable(parent, flags);
    }
}
