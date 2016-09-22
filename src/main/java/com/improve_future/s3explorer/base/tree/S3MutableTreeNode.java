package com.improve_future.s3explorer.base.tree;

import com.amazonaws.services.s3.model.Bucket;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class S3MutableTreeNode extends DefaultMutableTreeNode implements TreeNode{
    public static String delimiter = "/";

    public S3MutableTreeNode() {
        super();
    }

    public S3MutableTreeNode(Object userObject) {
        super(userObject);
    }

    public S3MutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    private Bucket bucket;

    public Bucket getBucket() {
        return this.bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public boolean isBucket() {
        return this.getLevel() == 1;
    }

    private String s3key = "";

    public String getS3Key() {
        return this.s3key;
    }

    public void setS3Key(String s3key) {
        this.s3key = s3key;
    }

    public String getPrefix() {
        //if (this.getLevel() <= 1) return "";
        DefaultMutableTreeNode p = (DefaultMutableTreeNode) this;
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        while ((p = (DefaultMutableTreeNode) p.getParent()) != null) {
            if (p.getLevel() <= 1) break;
            if (isFirst) isFirst = false;
            else sb.insert(0, delimiter);
            sb.insert(0, p.getUserObject());
        }
        return sb.toString();
    }

    public String getKey() {
        if (this.isBucket()) return "";
        StringBuilder sb = new StringBuilder();
        if (this.getLevel() == 2) return (String) this.getUserObject();
        sb.append(this.getPrefix()).append(delimiter).append(this.getUserObject());
        return sb.toString();
    }
}
