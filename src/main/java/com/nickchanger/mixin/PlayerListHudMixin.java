package com.nickchanger.mixin;

import com.nickchanger.NameReplacer;
import com.nickchanger.NickContext;
import com.nickchanger.NickMod;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Tab list (the list you see when holding TAB). */
@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Inject(method = "getPlayerName", at = @At("HEAD"))
    private void nickchanger$start(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        NickContext.inTab = true;
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void nickchanger$end(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        NickContext.inTab = false;
        if (NickMod.config != null && NickMod.config.active() && NickMod.config.tabList) {
            cir.setReturnValue(NameReplacer.replaceText(cir.getReturnValue()));
        }
    }
}
