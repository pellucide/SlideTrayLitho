package com.palmabrahma.slidetraylitho;

import com.facebook.litho.annotations.Event;

// Custom event for emoji selection
@Event
public class EmojiSelectedEvent {
    public String emoji;
    public String name;
}

