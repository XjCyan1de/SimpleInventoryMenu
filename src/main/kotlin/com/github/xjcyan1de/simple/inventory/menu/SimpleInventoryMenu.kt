package com.github.xjcyan1de.simple.inventory.menu

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

open class InventoryMenu
private constructor(
        val plugin: Plugin,
        val player: Player,
        val inventoryType: InventoryType,
        val rows: Int,
        val title: String
) : InventoryHolder, Listener {
    constructor(plugin: Plugin, player: Player, inventoryType: InventoryType, title: String) : this(plugin, player, inventoryType, 3, title)

    constructor(plugin: Plugin, player: Player, rows: Int, title: String) : this(plugin, player, InventoryType.CHEST, rows, title)

    private val _inventory = if (inventoryType == InventoryType.CHEST)
        Bukkit.createInventory(this, rows * 9, title) else
        Bukkit.createInventory(this, inventoryType, title)
    private val buttons = HashMap<Int, InventoryButton>()

    override fun getInventory(): Inventory = _inventory

    fun addButton(material: Material, text: String, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu =
            addButton(material, text.split("\n"), clickHandler)

    fun addButton(material: Material, vararg text: String, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu =
            addButton(material, text.toList(), clickHandler)

    fun addButton(material: Material, text: List<String>, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu {
        val itemStack = createItem(material, text)
        return addButton(itemStack, clickHandler)
    }

    fun addButton(itemStack: ItemStack, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu =
            addButton(inventory.firstEmpty(), itemStack, clickHandler)

    @JvmOverloads
    fun addButton(material: Material, text: String, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(material, text.split("\n")) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(material: Material, vararg text: String, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(material, text.toList()) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(material: Material, text: List<String>, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(material, text) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(itemStack: ItemStack, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(itemStack) { clickHandler.accept(this) }

    /**
     * |----------------------------|
     * | 0  1  2  3  4  5  6  7  8  |
     * | 9  10 11 12 13 14 15 16 17 |
     * | 18 19 20 21 22 23 24 25 26 |
     * | 27 28 29 30 31 32 33 34 35 |
     * | 36 37 38 39 40 41 42 43 44 |
     * | 45 46 47 48 49 50 51 52 53 |
     * |----------------------------|
     * | 54 55 56 57 58 59 60 61 62 |
     * | 63 64 65 66 67 68 69 70 71 |
     * | 72 73 74 75 76 77 78 79 80 |
     * |----------------------------|
     * | 81 82 83 84 85 86 87 88 89 |
     * |----------------------------|
     */
    fun addButton(slot: Int, material: Material, text: String, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu =
            addButton(slot, material, text.split("\n"), clickHandler)

    fun addButton(slot: Int, material: Material, vararg text: String, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu =
            addButton(slot, material, text.toList(), clickHandler)

    fun addButton(slot: Int, material: Material, text: List<String>, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu {
        val itemStack = createItem(material, text)
        return addButton(slot, itemStack, clickHandler)
    }

    fun addButton(slot: Int, itemStack: ItemStack, clickHandler: InventoryClickEvent.() -> Unit = {}): InventoryMenu = apply {
        val guiItem = InventoryButton(itemStack, clickHandler)
        buttons[slot] = guiItem
    }

    @JvmOverloads
    fun addButton(slot: Int, material: Material, text: String, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(slot, material, text.split("\n")) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(slot: Int, material: Material, vararg text: String, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(slot, material, text.toList()) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(slot: Int, material: Material, text: List<String>, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(slot, material, text) { clickHandler.accept(this) }

    @JvmOverloads
    fun addButton(slot: Int, itemStack: ItemStack, clickHandler: Consumer<InventoryClickEvent> = Consumer {}): InventoryMenu =
            addButton(slot, itemStack) { clickHandler.accept(this) }

    fun open() {
        inventory.clear()
        buttons.forEach { (slot, button) ->
            inventory.setItem(slot, button.itemStack)
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

        buttons[event.slot]?.also {
            event.isCancelled = true
            it.clickHandler(event)
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory.holder != this) return

        HandlerList.unregisterAll(this)
    }

    private fun createItem(material: Material, text: List<String>): ItemStack {
        val itemStack = ItemStack(material)
        val itemMeta = itemStack.itemMeta
        itemMeta.addItemFlags(*ItemFlag.values())
        if (text.isNotEmpty()) itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.translateAlternateColorCodes('&', text[0]))
        if (text.size > 1) itemMeta.lore = text.subList(1, text.size).map { ChatColor.RESET.toString() + ChatColor.translateAlternateColorCodes('&', it) }
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}

data class InventoryButton(
        val itemStack: ItemStack,
        val clickHandler: InventoryClickEvent.() -> Unit
)

val InventoryClickEvent.player: Player get() = whoClicked as Player

@JvmName("PluginInventoryMenu")
fun Plugin.InventoryMenu(player: Player, inventoryType: InventoryType, title: String, builder: InventoryMenu.() -> Unit = {}): InventoryMenu =
        InventoryMenu(this, player, inventoryType, title, builder)

@JvmName("PluginInventoryMenu")
fun Plugin.InventoryMenu(player: Player, rows: Int, title: String, builder: InventoryMenu.() -> Unit = {}): InventoryMenu =
        InventoryMenu(this, player, rows, title, builder)

fun InventoryMenu(plugin: Plugin, player: Player, inventoryType: InventoryType, title: String, builder: InventoryMenu.() -> Unit = {}): InventoryMenu =
        InventoryMenu(plugin, player, inventoryType, title).apply(builder)

fun InventoryMenu(plugin: Plugin, player: Player, rows: Int, title: String, builder: InventoryMenu.() -> Unit = {}): InventoryMenu =
        InventoryMenu(plugin, player, rows, title).apply(builder)

fun InventoryMenu.InventoryMenu(inventoryType: InventoryType, title: String, builder: InventoryMenu.() -> Unit = {}): InventoryMenu =
        InventoryMenu(plugin, player, inventoryType, title, builder)

fun InventoryMenu.InventoryMenu(rows: Int, title: String, builder: InventoryMenu.() -> Unit = {}): InventoryMenu =
        InventoryMenu(plugin, player, rows, title, builder)
