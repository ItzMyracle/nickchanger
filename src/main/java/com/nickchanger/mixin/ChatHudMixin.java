package com.nickchanger.mixin;

import com.nickchanger.NameReplacer;
import com.nickchanger.NickMod;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Chat: your real name inside incoming messages is shown as the fake one. */
@Mixin(ChatHud.class)
public class ChatHudMixin {
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"), argsOnly = true)
    private Text nickchanger$chat(Text message) {
        if (NickMod.config != null && NickMod.config.active() && NickMod.config.chat) {
            return NameReplacer.replaceText(message);
        }
        return message;
    }
}
