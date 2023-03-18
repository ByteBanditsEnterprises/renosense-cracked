//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package fr.litarvan.openauth.microsoft;

import javax.swing.*;
import java.util.concurrent.*;
import javafx.embed.swing.*;
import java.awt.*;
import java.awt.event.*;
import javafx.application.*;
import java.net.*;
import javafx.scene.web.*;
import javafx.scene.*;
import javafx.beans.value.*;

public class LoginFrame extends JFrame
{
    private CompletableFuture<String> future;
    
    public LoginFrame() {
        this.setTitle("Microsoft Authentication");
        this.setSize(750, 750);
        this.setLocationRelativeTo(null);
        this.setContentPane(new JFXPanel());
    }
    
    public CompletableFuture<String> start(final String url) {
        if (this.future != null) {
            return this.future;
        }
        this.future = new CompletableFuture<String>();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                LoginFrame.this.future.completeExceptionally(new MicrosoftAuthenticationException("User closed the authentication window"));
            }
        });
        Platform.runLater(() -> this.init(url));
        return this.future;
    }
    
    protected void init(final String url) {
        final CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        manager.getCookieStore().removeAll();
        final WebView webView = new WebView();
        final JFXPanel content = (JFXPanel)this.getContentPane();
        content.setScene(new Scene(webView, this.getWidth(), this.getHeight()));
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("access_token")) {
                this.setVisible(false);
                this.future.complete(newValue);
            }
            return;
        });
        webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        webView.getEngine().load(url);
        this.setVisible(true);
    }
}
