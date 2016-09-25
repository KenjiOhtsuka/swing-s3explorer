package com.improve_future.s3explorer.form.component

interface MenuItemObserver {
    fun execute(command: MenuCommand)
}