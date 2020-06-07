# Simple Inventory Menu [![](https://jitpack.io/v/XjCyan1de/SimpleInventoryMenu.svg)](https://jitpack.io/#XjCyan1de/SimpleInventoryMenu)  [![](https://jitci.com/gh/XjCyan1de/SimpleInventoryMenu/svg)](https://jitci.com/gh/XjCyan1de/SimpleInventoryMenu)

Simple Inventory Menu. Nothing else.

#### Gradle Kotlin DSL:
```kotlin
repositories {
    maven { setUrl("https://jitpack.io/") }
}
dependencies {
    implementation("com.github.xjcyan1de", "SimpleInventoryMenu", "1.4")
}
```

#### Example:
```kotlin
val menu1 = InventoryMenu(plugin, player, 1, "My Menu") {
    addButton(ItemStack(Material.STONE)) {
        player.sendMessage("You clicked at stone block")
    }
}
menu1.open()
    
val menu2 = InventoryMenu(plugin, player, InventoryType.HOPPER, "Hopper Menu") {
    addButton(2, Material.DIAMOND, "&bShiny Diamond") {
        player.inventory.addItem(ItemStack(Material.DIAMOND))
    }
}
menu2.open()
```
