package me.nik.combatplus.listeners;

import me.nik.combatplus.files.Config;
import me.nik.combatplus.utils.Messenger;
import me.nik.combatplus.utils.WorldUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DamageModifiers implements Listener {

    private final WorldUtils worldUtils = new WorldUtils();

    private final double oldPickaxeDamage = Config.get().getDouble("advanced.settings.modifiers.old_pickaxes_damage");
    private final double oldAxeDamage = Config.get().getDouble("advanced.settings.modifiers.old_axes_damage");
    private final double oldShovelDamage = Config.get().getDouble("advanced.settings.modifiers.old_shovels_damage");
    private final double oldSwordDamage = Config.get().getDouble("advanced.settings.modifiers.old_swords_damage");

    private void disableSweep(EntityDamageEvent e, Entity player, ItemStack handItem) {
        if (handItem.containsEnchantment(Enchantment.SWEEPING_EDGE) && isEnabled("combat.settings.disable_sweep_attacks.ignore_sweeping_edge"))
            return;
        Entity ent = e.getEntity();
        double x = ent.getVelocity().getX();
        double y = ent.getVelocity().getY();
        double z = ent.getVelocity().getZ();
        e.setCancelled(true);
        ent.setVelocity(new Vector().zero());
        Messenger.debug((Player) player, "&3Damage Modifier &f&l>> &6Canceled Sweep Attack: &a" + e.isCancelled() + " &6Velocity: X = &a" + x + " &6Y = &a" + y + " &6Z = &a" + z);
    }

    private void oldPickaxeDmg(EntityDamageByEntityEvent e, Entity player) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + oldPickaxeDamage;
        e.setDamage(newDmg);
        Messenger.debug((Player) player, "&3Damage Modifier &f&l>> &6Item: &aPickaxe &6Old Damage: &a" + damageDealt + " &6New Damage: &a" + newDmg);
    }

    private void oldAxeDmg(EntityDamageByEntityEvent e, Entity player) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + oldAxeDamage;
        e.setDamage(newDmg);
        Messenger.debug((Player) player, "&3Damage Modifier &f&l>> &6Item: &aAxe &6Old Damage: &a" + damageDealt + " &6New Damage: &a" + newDmg);
    }

    private void oldShovelDmg(EntityDamageByEntityEvent e, Entity player) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + oldShovelDamage;
        e.setDamage(newDmg);
        Messenger.debug((Player) player, "&3Damage Modifier &f&l>> &6Item: &aShovel &6Old Damage: &a" + damageDealt + " &6New Damage: &a" + newDmg);
    }

    private void oldSwordDmg(EntityDamageByEntityEvent e, Entity player) {
        double damageDealt = e.getDamage();
        double newDmg = damageDealt + oldSwordDamage;
        e.setDamage(newDmg);
        Messenger.debug((Player) player, "&3Damage Modifier &f&l>> &6Item: &aSword &6Old Damage: &a" + damageDealt + " &6New Damage: &a" + newDmg);
    }

    private void oldSharpDamage(EntityDamageByEntityEvent e, Entity player, ItemStack handItem) {
        if (handItem.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            double damageDealt = e.getDamage();
            double sharpLvl = handItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            double oldSharpDmg = sharpLvl >= 1 ? 1 + (sharpLvl - 1) * 0.5 : 0; //1.9+
            double newSharpDmg = sharpLvl >= 1 ? sharpLvl * 1.25 : 0; //1.8
            double total = damageDealt + newSharpDmg;
            e.setDamage(total);
            Messenger.debug((Player) player, "&3Damage Modifier &f&l>> &6Old Sharpness Damage: &a" + oldSharpDmg + " &6New Sharpness Damage: &a" + newSharpDmg);
        }
    }

    /*
     This Listener Changes the Damage Dealt to All Entities to the Old Values
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (worldUtils.combatDisabledWorlds((Player) e.getDamager())) return;
        Player player = (Player) e.getDamager();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        String isWeapon = handItem.getType().name();
        if (isEnabled("combat.settings.old_weapon_damage")) {
            if (handItem.getType().name().endsWith("_SWORD")) {
                oldSwordDmg(e, player);
            }
        }
        if (isEnabled("combat.settings.old_tool_damage")) {
            if (isWeapon.endsWith("_PICKAXE")) {
                oldPickaxeDmg(e, player);
            } else if (isWeapon.endsWith("_AXE")) {
                oldAxeDmg(e, player);
            } else if (isWeapon.endsWith("_SPADE") || isWeapon.endsWith("_SHOVEL")) {
                oldShovelDmg(e, player);
            }
        }

        /*
         This one disables Sweep Attacks and Reverts Sharpness Damage
         */

        if (isEnabled("combat.settings.disable_sweep_attacks.enabled")) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                disableSweep(e, player, handItem);
            }
        }
        if (isEnabled("combat.settings.old_sharpness")) {
            oldSharpDamage(e, player, handItem);
        }
    }

    private boolean isEnabled(String value) {
        return Config.get().getBoolean(value);
    }
}