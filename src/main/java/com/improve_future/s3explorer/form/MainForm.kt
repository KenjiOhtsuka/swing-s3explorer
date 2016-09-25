package com.improve_future.s3explorer.form

import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.improve_future.s3explorer.form.component.S3MutableTreeNode
import com.improve_future.s3explorer.config.AwsS3Config
import com.improve_future.s3explorer.form.component.FilePopupMenu
import com.improve_future.s3explorer.form.component.MenuCommand
import com.improve_future.s3explorer.form.component.MenuItemObserver
import com.improve_future.s3explorer.service.AwsS3Service
import sun.swing.table.DefaultTableCellHeaderRenderer

import javax.swing.*
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import javax.swing.tree.*
import java.awt.*
import java.awt.event.*
import java.io.*
import java.text.SimpleDateFormat
import javax.swing.border.Border

class MainForm : JFrame(), MenuItemObserver {
    private val panel1 = JPanel()
    private val mainSplitPane = JSplitPane()
    /* Tree UI */
    private val tree1 = JTree()
    private val treeModel = tree1.model as DefaultTreeModel
    private val rootNode: DefaultMutableTreeNode
    private val treeScrollPane = JScrollPane(tree1)

    /* Table UI */
    private val listTable = JTable()
    private val tableModel = listTable.model as DefaultTableModel
    private val tableScrollPane = JScrollPane(listTable)
    /* Toolbar UI */
    private val toolBar = JToolBar()
    private val loadButton = JButton("Load")
    private val bucketLabel = JLabel("Bucket")
    private val locationSeparatorLabel = JLabel(":")
    private val keyLabel = JLabel("Key")
    /* Status Bar UI */
    private val statusLabel = JLabel("Status")
    /* Popup UI */
    private val filePopupMenu = FilePopupMenu("File")

    /* AWS configs */
    private lateinit var awsS3Config: AwsS3Config
    private lateinit var awsS3Service: AwsS3Service

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm")

    init {
        this.title = "S3Explorer"

        add(panel1, BorderLayout.CENTER)
        panel1.layout = BorderLayout()
        panel1.add(toolBar, BorderLayout.NORTH)
        toolBar.isFloatable = false
        toolBar.add(loadButton)
        toolBar.add(bucketLabel)
        toolBar.add(locationSeparatorLabel)
        toolBar.add(keyLabel)

        panel1.add(mainSplitPane, BorderLayout.CENTER)
        mainSplitPane.dividerLocation = 100
        mainSplitPane.leftComponent = treeScrollPane
        tree1.visibleRowCount = 20
        tree1.showsRootHandles = true
        tree1.isRootVisible = false
        mainSplitPane.rightComponent = tableScrollPane

        panel1.add(statusLabel, BorderLayout.SOUTH)

        // initialization
        treeModel.setAsksAllowsChildren(true)
        rootNode = treeModel.root as DefaultMutableTreeNode
        rootNode.removeAllChildren()
        treeModel.reload()
        tableModel.addColumn("")
        tableModel.addColumn("name")
        tableModel.addColumn("size")
        tableModel.addColumn("last modified")
        listTable.columnModel.getColumn(0).width = 10
        val rightCellRenderer = DefaultTableCellRenderer()
        rightCellRenderer.horizontalAlignment = JLabel.RIGHT
        listTable.columnModel.getColumn(2).cellRenderer = rightCellRenderer
        val rightHeaderRenderer = DefaultTableCellHeaderRenderer()
        rightHeaderRenderer.horizontalAlignment = JLabel.RIGHT
        listTable.columnModel.getColumn(2).headerRenderer = rightHeaderRenderer


        listTable.componentPopupMenu = filePopupMenu
        filePopupMenu.addObserver(this)

        //listTable.setTableHeader();
        loadButton.addActionListener {
            val fileChooser = JFileChooser()
            if (fileChooser.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                val file = fileChooser.selectedFile
                try {
                    val fr = FileReader(file)
                    val br = BufferedReader(fr)
                    try {
                        val line = br.readLine()
                        awsS3Config = AwsS3Config(line)
                        awsS3Service = AwsS3Service.getInstance(awsS3Config)
                        rootNode.removeAllChildren()
                        if (!awsS3Config.isBucketSpecific) {
                            listAllBuckets()
                        } else {

                        }
                    } catch (ex: IOException) {
                        val sb = StringBuffer()
                        sb.append("Error occurred in reading \"").append(fileChooser.selectedFile.path).append("\".")
                        JOptionPane.showMessageDialog(panel1, sb.toString())
                    }

                } catch (ex: FileNotFoundException) {
                    val sb = StringBuffer()
                    sb.append("File \"").append(fileChooser.selectedFile.path).append("\" is not found.")
                    JOptionPane.showMessageDialog(panel1, sb.toString())
                }
            }
        }

        tree1.addTreeSelectionListener(TreeSelectionListener { treeSelectionEvent ->
            val paths = treeSelectionEvent.paths

            when (tree1.selectionCount) {
                0 -> {
                }
                1 -> {
                    val selectedNode = tree1.lastSelectedPathComponent as S3MutableTreeNode
                    val bucketName: String
                    val key: String
                    if (selectedNode.isBucket) {
                        bucketName = selectedNode.userObject as String
                        key = ""
                    } else {
                        selectedNode.path
                        bucketName = selectedNode.bucket.name
                        key = selectedNode.s3Key + "/"
                    }
                    selectedNode.removeAllChildren()

                    tableModel.rowCount = 0
                    val ol = awsS3Service.findAllObjects(bucketName, key)
                    for (prefix in ol.commonPrefixes) {
                        val folderName = prefix.substring(key.length, prefix.length - 1)
                        val node = S3MutableTreeNode(folderName, true)
                        node.bucket = selectedNode.bucket
                        node.s3Key = prefix.substring(0, prefix.length - 1)
                        selectedNode.add(node)

                        tableModel.addRow(arrayOf<Any?>("d", folderName, null, null))
                    }
                    tree1.expandPath(TreePath(selectedNode.path))

                    for (summary in ol.objectSummaries) {
                        val name = summary.key.substring(
                                summary.key.lastIndexOf(S3MutableTreeNode.delimiter) + 1)
                        if (name.isEmpty()) continue
                        tableModel.addRow(
                                arrayOf("f", name, summary.size, dateFormatter.format(summary.lastModified)))
                    }

                    keyLabel.text = selectedNode.s3Key
                    bucketLabel.text = bucketName
                }
                else -> for (path in paths) {

                }
            }
        })

        listTable.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(event: MouseEvent) {
//                        val popup = FilePopupMenu()
//                        JOptionPane.showMessageDialog(null, "hello", 1,1  ,1)
                    }
                }
        )
        pack()
    }

    private fun listAllBuckets() {
        val buckets = awsS3Service!!.findAllBuckets()
        for (bucket in buckets) {
            val node = S3MutableTreeNode(bucket.name, true)
            node.bucket = bucket
            rootNode.add(node)
        }
        treeModel.reload()
    }

    override fun execute(command: MenuCommand) {
        when (command) {
            MenuCommand.FileDownload -> {
                val rowIndex = listTable.selectedRow
                val objectName = tableModel.getValueAt(rowIndex, 1)
                val folderKey = keyLabel.text
                val bucket = bucketLabel.text
                awsS3Service.downloadObject(
                        bucket,
                        folderKey + "/" + objectName)

            }
            MenuCommand.FileRemove -> {
                val rowIndex = listTable.selectedRow
                val objectName = tableModel.getValueAt(rowIndex, 1)
                val folderKey = keyLabel.text
                val bucket = bucketLabel.text
                awsS3Service.deleteObject(
                        bucket,
                        folderKey + "/" + objectName)
            }
        }
    }
}
