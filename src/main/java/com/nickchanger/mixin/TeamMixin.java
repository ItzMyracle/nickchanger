package com.nickchanger.mixin;

import com.nickchanger.NameReplacer;
import com.nickchanger.NickContext;
import com.nickchanger.NickMod;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Almost every displayed name (name tags, side scoreboard lines, ...) goes through Team.decorateName,
 * so this one hook covers them. The tab list is handled in PlayerListHudMixin to avoid replacing twice.
 */
@Mixin(Team.class)
public class TeamMixin {
    @Inject(method = "decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;",
            at = @At("RETURN"), cancellable = true)
    private static void nickchanger$decorate(AbstractTeam team, Text name, CallbackInfoReturnable<MutableText> cir) {
        if (NickMod.config == null || !NickMod.config.active() || NickContext.inTab) return;
        boolean allowed = NickContext.inSidebar ? NickMod.config.scoreboard : NickMod.config.nametag;
        if (allowed) cir.setReturnValue(NameReplacer.replaceMutable(cir.getReturnValue()));
    }
}
