package com.improve_future.s3explorer.form;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.improve_future.s3explorer.base.tree.S3MutableTreeNode;
import com.improve_future.s3explorer.config.AwsS3Config;
import com.improve_future.s3explorer.service.AwsS3Service;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainForm {
    private JPanel panel1;
    private JTree tree1;
    private JTable listTable;
    private DefaultTableModel tableModel;
    private JButton loadButton;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    private AwsS3Config awsS3Config;
    private AwsS3Service awsS3Service;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MainForm() {
        // initialization
        treeModel = (DefaultTreeModel) tree1.getModel();
        treeModel.setAsksAllowsChildren(true);
        rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        treeModel.reload();
        tableModel = (DefaultTableModel) listTable.getModel();
        tableModel.addColumn("name");
        tableModel.addColumn("size");
        tableModel.addColumn("last modified");

        //listTable.setTableHeader();

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileReader fr = new FileReader(file);
                        BufferedReader br = new BufferedReader(fr);
                        try {
                            String line = br.readLine();
                            awsS3Config = new AwsS3Config(line);
                            awsS3Service = AwsS3Service.getInstance(awsS3Config);
                            rootNode.removeAllChildren();
                            listAllBuckets();
                        } catch (IOException ex) {
                            StringBuffer sb = new StringBuffer();
                            sb.
                                    append("Error occurred in reading \"").
                                    append(fileChooser.getSelectedFile().getPath()).
                                    append("\".");
                            JOptionPane.showMessageDialog(panel1, sb.toString());
                        }
                    } catch (FileNotFoundException ex) {
                        StringBuffer sb = new StringBuffer();
                        sb.
                                append("File \"").
                                append(fileChooser.getSelectedFile().getPath()).
                                append("\" is not found.");
                        JOptionPane.showMessageDialog(panel1, sb.toString());
                    }
                }
            }
        });

        tree1.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                TreePath[] paths = treeSelectionEvent.getPaths();

                switch (tree1.getSelectionCount()) {
                    case 0:
                        break;
                    case 1:
                        S3MutableTreeNode selectedNode =
                                (S3MutableTreeNode) tree1.getLastSelectedPathComponent();
                        String bucketName = "";
                        //String prefix;
                        if (selectedNode.getDepth() == 0) {
                            bucketName = (String) selectedNode.getUserObject();
                            //prefix = "";
                        } else {
                            selectedNode.getPath();
                        }
                        selectedNode.removeAllChildren();

                        tableModel.setRowCount(0);
                        ObjectListing ol = awsS3Service.findAllObjects(bucketName, "");
                        for (String prefix : ol.getCommonPrefixes()) {
                            String folderName = prefix.substring(0, prefix.length() - 1);
                            S3MutableTreeNode node = new S3MutableTreeNode(folderName, true);
                            node.setBucket(selectedNode.getBucket());
                            selectedNode.add(node);

                            tableModel.addRow(new Object[] {folderName, null, null});
                        }
                        tree1.expandPath(new TreePath(selectedNode.getPath()));

                        for (S3ObjectSummary summary : ol.getObjectSummaries()) {
                            tableModel.addRow(
                                    new Object[] {
                                            summary.getKey(),
                                            summary.getSize(),
                                            dateFormatter.format(summary.getLastModified())});
                        }
                        break;
                    default:
                        for (TreePath path : paths) {

                        }

                }
            }
        });
    }

    void listAllBuckets() {
        List<Bucket> buckets = awsS3Service.findAllBuckets();
        for (Bucket bucket : buckets) {
            S3MutableTreeNode node = new S3MutableTreeNode(bucket.getName(), true);
            node.setBucket(bucket);
            rootNode.add(node);
        }
        treeModel.reload();
    }

    public static void main(String[] args) {
        JFrame jframe = new JFrame("MainForm");
        jframe.setContentPane(new MainForm().panel1);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);
    }
}
