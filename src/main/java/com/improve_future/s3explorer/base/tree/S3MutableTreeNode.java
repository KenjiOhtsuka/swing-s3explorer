package com.improve_future.s3explorer.base.tree;

import com.amazonaws.services.s3.model.Bucket;

import javax.swing.tree.DefaultMutableTreeNode;

public class S3MutableTreeNode extends DefaultMutableTreeNode {
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
}
