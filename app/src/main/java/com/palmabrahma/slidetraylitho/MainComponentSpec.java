package com.palmabrahma.slidetraylitho;

import android.graphics.Color;
import android.widget.Toast;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.EventHandler;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.widget.Button;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaJustify;

/**
 * Main component that hosts the EmojiDrawer and controls to show it.
 */
@LayoutSpec
public class MainComponentSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c) {
        return Column.create(c)
                .paddingDip(YogaEdge.ALL, 16)
                .backgroundColor(Color.WHITE)
                .child(
                        Text.create(c)
                                .text("Emoji Drawer Demo")
                                .textSizeSp(24)
                                .textColor(Color.BLACK)
                                .marginDip(YogaEdge.BOTTOM, 16)
                )
                .child(
                        Text.create(c)
                                .text("This demo shows a sliding emoji drawer with a macOS dock-like effect.")
                                .textSizeSp(16)
                                .textColor(Color.DKGRAY)
                                .marginDip(YogaEdge.BOTTOM, 24)
                )
                .child(
                        Button.create(c)
                                .text("Open Emoji Drawer")
                                //.textSizeSp(16)
                                .alignSelf(YogaAlign.CENTER)
                                .paddingDip(YogaEdge.HORIZONTAL, 16)
                                .paddingDip(YogaEdge.VERTICAL, 8)
                                .clickHandler(MainComponent.onShowDrawerClick(c))
                )
                .child(
                        // Add the emoji drawer component (initially hidden)
                        EmojiDrawerComponent.create(c)
                                .emojiSelEventHandler((emoji, name) -> {
                                    //// Show toast when emoji is selected
                                    Toast.makeText(
                                            c.getAndroidContext(),
                                            "Selected: " + emoji + " (" + name + ")",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                })
                                .build()
                )
                .build();
    }

    @OnEvent(ClickEvent.class)
    static void onShowDrawerClick(ComponentContext c) {
        // Show the emoji drawer when button is clicked
        EmojiDrawerComponentSpec.openIt(c);
    }
}