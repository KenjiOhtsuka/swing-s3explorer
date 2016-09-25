package com.improve_future.s3explorer.form.component

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Action
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class FilePopupMenu(label: String) : BasePopupMenu(label) {
    private val downloadMenuItem = JMenuItem("Download")
    private val renameMenuItem = JMenuItem("Rename")
    private val deleteMenuItem = JMenuItem("Delete")

    init {
        add(downloadMenuItem)
        add(renameMenuItem)
        add(deleteMenuItem)

        downloadMenuItem.addActionListener {
            notifyObservers(MenuCommand.FileDownload)
        }

        renameMenuItem.addActionListener {
            //notifyObservers(MenuCommand.RenameFile)
        }

        deleteMenuItem.addActionListener {
            notifyObservers(MenuCommand.FileRemove)
        }
    }
}