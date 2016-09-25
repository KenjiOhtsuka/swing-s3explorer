package com.improve_future.s3explorer.form.component

import java.awt.MenuItem
import javax.swing.JPopupMenu

open class BasePopupMenu(label: String) : JPopupMenu(label) {
    private val observers = mutableListOf<MenuItemObserver>()

    fun addObserver(observer: MenuItemObserver) {
        observers.add(observer)
    }

    fun deleteObserver(observer: MenuItemObserver) {
        observers.remove(observer)
    }

    fun notifyObservers(command: MenuCommand) {
        observers.forEach {
            it.execute(command)
        }
    }
}