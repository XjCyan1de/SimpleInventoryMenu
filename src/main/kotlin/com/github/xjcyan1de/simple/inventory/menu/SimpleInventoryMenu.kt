package com.github.xjcyan1de.simple.inventory.menu

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

class InventoryMenu(
        val plugin: Plugin,
        val rows: Int = 3,
        val title: String = " "
) : InventoryHolder, Listener {
    private val _inventory = Bukkit.createInventory(this,rows*9,title)
    private val buttons = HashSet<InventoryButton>()

    override fun getInventory(): Inventory = _inventory

    fun addButton(itemStack: ItemStack, clickHandler: Consumer<InventoryClickEvent>): InventoryMenu =
            addButton(itemStack) { clickHandler.accept(this) }
    fun addButton(itemStack: ItemStack, clickHandler: InventoryClickEvent.() -> Unit): InventoryMenu =
            addButton(inventory.firstEmpty(), itemStack, clickHandler)

    fun addButton(slot: Int, itemStack: ItemStack, clickHandler: Consumer<InventoryClickEvent>): InventoryMenu =
            addButton(slot, itemStack) { clickHandler.accept(this) }
    fun addButton(slot: Int, itemStack: ItemStack, clickHandler: InventoryClickEvent.() -> Unit): InventoryMenu = apply {
        val guiItem = InventoryButton(slot, itemStack, clickHandler)
        buttons.add(guiItem)
    }

    fun addButton(button: InventoryButton): InventoryMenu = apply {
        buttons.add(button)
    }

    fun open(player: Player) {
        inventory.clear()
        buttons.forEach {
            inventory.setItem(it.slot, it.itemStack)
        }
        player.openInventory(inventory)
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun clear() {
        inventory.clear()
        buttons.clear()
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory ?: return
        if (clickedInventory.holder != this) return

        buttons.find { it.slot == event.slot }?.also {
            event.isCancelled = true
            it.clickHandler(event)
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory.holder != this) return

        HandlerList.unregisterAll(this)
    }
}

data class InventoryButton(
        val slot: Int,
        val itemStack: ItemStack,
        val clickHandler: InventoryClickEvent.()->Unit
)
