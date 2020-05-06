package me.nik.combatplus.listeners;

import me.nik.combatplus.files.Config;
import me.nik.combatplus.utils.Messenger;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ItemFrameRotate implements Listener {

    // This Listener removes the ability to rotate item frames

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRotate(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof ItemFrame)) return;
        if (e.getPlayer().hasPermission("cp.bypass.rotate")) return;
        if (itemFrameRotationDisabledWorlds(e.getPlayer())) return;
        if (((ItemFrame) e.getRightClicked()).getItem().getType().equals(Material.AIR)) return;
        e.setCancelled(true);
        Messenger.debug(e.getPlayer(), "&3Item Frame Rotation &f&l>> &6Canceled: &a" + e.isCancelled());
    }

    private boolean itemFrameRotationDisabledWorlds(Player player) {
        for (String world : Config.get().getStringList("disable_item_frame_rotation.disabled_worlds")) {
            if (player.getWorld().getName().equalsIgnoreCase(world))
                return true;
        }
        return false;
    }
}
