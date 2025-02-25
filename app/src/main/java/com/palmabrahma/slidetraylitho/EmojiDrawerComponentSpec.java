package com.palmabrahma.slidetraylitho;

import android.graphics.Color;
import android.text.Layout;
import android.view.MotionEvent;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.DynamicValue;
import com.facebook.litho.EventHandler;
import com.facebook.litho.Row;
import com.facebook.litho.StateValue;
import com.facebook.litho.TouchEvent;
import com.facebook.litho.Transition;
import com.facebook.litho.animation.AnimatedProperties;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnCreateTransition;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaPositionType;

import java.util.Arrays;
import java.util.List;

/**
 * EmojiDrawerComponent - A Litho component that creates a drawer sliding from the right
 * with emojis that have a macOS dock-like effect when touched.
 */
@LayoutSpec(events = {EmojiSelectedEvent.class})
class EmojiDrawerComponentSpec {

    // Sample emoji data
    private static final List<EmojiItem> EMOJI_LIST = Arrays.asList(
            new EmojiItem("😀", "Smile"),
            new EmojiItem("😂", "Laugh"),
            new EmojiItem("😍", "Love"),
            new EmojiItem("🎉", "Party"),
            new EmojiItem("🔥", "Fire"),
            new EmojiItem("💯", "100"),
            new EmojiItem("🚀", "Rocket"),
            new EmojiItem("🌈", "Rainbow"),
            new EmojiItem("👍", "Thumbs Up"),
            new EmojiItem("🤔", "Think")
    );

    @OnCreateInitialState
    static void createInitialState(
            ComponentContext c,
            StateValue<Boolean> isVisible,
            StateValue<Integer> hoveredEmojiIndex,
            @Prop(optional = true) EmojiSelectedEventHandlerInterface emojiSelEventHandler) {
        isVisible.set(false);
        hoveredEmojiIndex.set(-1);
    }


    @OnCreateLayout
    static Component onCreateLayout(
            ComponentContext c,
            @State boolean isVisible,
            @State int hoveredEmojiIndex) {

        // Drawer position for slide effect
        int drawerPosition = isVisible ? 0 : -300;

        return Column.create(c)
                .positionType(YogaPositionType.ABSOLUTE)
                .positionPx(YogaEdge.RIGHT, drawerPosition)
                .widthPx(300)
                .heightPercent(100)
                .backgroundColor(Color.WHITE)
                .paddingDip(YogaEdge.ALL, 16)
                //.shadow(Shadow.builder()
                        //.radiusDip(10)
                        //.colorRgba(0x55000000)
                        //.offsetXDip(-5)
                        //.offsetYDip(0)
                        //.build())
                .child(
                        // Emoji section title
                        Text.create(c)
                                .text("Emoji Selector")
                                .textSizeSp(20)
                                //.textStyle(Text.Style.BOLD)
                                .marginDip(YogaEdge.BOTTOM, 20)
                                .build())
                .child(
                        // Emoji list container with touch handler for tracking finger movement
                        Row.create(c)
                                .heightDip(100)
                                .touchHandler(EmojiDrawerComponent.onDrawerTouch(c))
                                .child(buildEmojiList(c, hoveredEmojiIndex))
                                .build())
                .transitionKey("drawer")
                .build();
    }

    // Build emoji list UI
    private static Component buildEmojiList(
            ComponentContext c,
            int hoveredEmojiIndex) {

        Row.Builder builder = Row.create(c)
                .justifyContent(YogaJustify.SPACE_AROUND)
                .alignItems(YogaAlign.CENTER);

        for (int i = 0; i < EMOJI_LIST.size(); i++) {
            EmojiItem item = EMOJI_LIST.get(i);

            // Scale factor for dock-like effect - make it larger when hovered
            float scale = (i == hoveredEmojiIndex) ? 1.5f : 1.0f;
            DynamicValue<Float> scaleDynamicValue = new DynamicValue<>(scale);

            builder.child(
                    Column.create(c)
                            .paddingDip(YogaEdge.HORIZONTAL, 4)
                            .alignItems(YogaAlign.CENTER)
                            .child(Text.create(c)
                                            .text(item.emoji)
                                            .textSizeSp(30)
                                            .scaleX(scaleDynamicValue)
                                            .scaleY(scaleDynamicValue)
                                            .transitionKey("emoji_" + i)
                                            .build())
                            .child(Text.create(c)
                                            .text(item.name)
                                            .textSizeSp(12)
                                            .textAlignment(Layout.Alignment.ALIGN_CENTER)
                                            .marginDip(YogaEdge.TOP, 4)
                                            .build())
                            .clickHandler(EmojiDrawerComponent.onEmojiClick(c, i))
                            .build());
        }

        return builder.build();
    }

    // Handle touch events for dock-like effect
    @OnEvent(TouchEvent.class)
    static boolean onDrawerTouch(
            ComponentContext c,
            @FromEvent MotionEvent motionEvent,
            @State int hoveredEmojiIndex) {

        float x = motionEvent.getX();

        // Calculate which emoji is being touched
        int containerWidth = 300 - 32; // Width minus padding
        int emojiWidth = containerWidth / EMOJI_LIST.size();
        int touchedIndex = (int) (x / emojiWidth);

        // Ensure index is within bounds
        if (touchedIndex < 0) touchedIndex = 0;
        if (touchedIndex >= EMOJI_LIST.size()) touchedIndex = EMOJI_LIST.size() - 1;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Update hovered emoji if changed
                if (hoveredEmojiIndex != touchedIndex) {
                    EmojiDrawerComponent.updateHoveredEmojiIndexSync(c, touchedIndex);
                }
                return true;

            case MotionEvent.ACTION_UP:
                // Reset hovered state
                EmojiDrawerComponent.updateHoveredEmojiIndexSync(c, -1);
                return false; // Allow click to propagate

            case MotionEvent.ACTION_CANCEL:
                // Reset hovered state
                EmojiDrawerComponent.updateHoveredEmojiIndexSync(c, -1);
                return true;
        }

        return false;
    }


    // Handle emoji clicks
    @OnEvent(ClickEvent.class)
    static void onEmojiClick(
            ComponentContext c,
            @Param int index,
            @State boolean isVisible) {

        // When emoji is clicked, close the drawer
        if (isVisible) {
            EmojiDrawerComponent.updateVisibilityStateSync(c, false);
            EventHandler handler  = EmojiDrawerComponent.getEmojiSelectedEventHandler(c);

            // Dispatch emoji selected event
            if (handler != null)
                EmojiDrawerComponent.dispatchEmojiSelectedEvent(
                        handler,
                        EMOJI_LIST.get(index).emoji,
                        EMOJI_LIST.get(index).name);
            //}
        }
    }


    // Event handler interface
    public interface EmojiSelectedEventHandlerInterface {
        void onEmojiSelected(String emoji, String name);
    }

    // Public API to show the drawer
    public static void openIt(ComponentContext c) {
        EmojiDrawerComponent.updateVisibilityStateSync(c, true);
    }

    // Public API to hide the drawer
    public static void hideIt(ComponentContext c) {
        EmojiDrawerComponent.updateVisibilityStateSync(c, false);
    }

    // State update methods
    @OnUpdateState
    static void updateHoveredEmojiIndex(StateValue<Integer> hoveredEmojiIndex, @Param int newIndex) {
        hoveredEmojiIndex.set(newIndex);
    }

    @OnUpdateState
    static void updateVisibilityState(StateValue<Boolean> isVisible, @Param boolean visible) {
        isVisible.set(visible);
    }

    // Define transitions for animations
    @OnCreateTransition
    static Transition onCreateTransition(ComponentContext c) {
        return Transition.parallel(
                // Drawer slide animation
                Transition.create("drawer")
                        .animate(AnimatedProperties.X)
                        .animator(Transition.timing(300)),

                // Emoji scale animation for dock effect
                Transition.create("emoji_.*")
                        .animate(AnimatedProperties.SCALE)
                        .animator(Transition.timing(150))
        );
    }

    // Data class for emojis
    static class EmojiItem {
        final String emoji;
        final String name;

        EmojiItem(String emoji, String name) {
            this.emoji = emoji;
            this.name = name;
        }
    }
}

// Usage example in a parent component:
//
// @OnEvent(ClickEvent.class)
// static void onShowEmojiDrawer(ComponentContext c) {
//     EmojiDrawerComponent.show(c);
// }
//
// @OnEvent(EmojiDrawerComponent.EmojiSelectedEvent.class)
// static void onEmojiSelected(
//         ComponentContext c,
//         @FromEvent String emoji,
//         @FromEvent String name) {
//     // Handle selected emoji
// }