package com.nickchanger.mixin;

import com.nickchanger.NickContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Marks the time while the side scoreboard is drawn, so its lines can be treated separately. */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At("HEAD"))
    private void nickchanger$sidebarStart(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        NickContext.inSidebar = true;
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At("RETURN"))
    private void nickchanger$sidebarEnd(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        NickContext.inSidebar = false;
    }
}
