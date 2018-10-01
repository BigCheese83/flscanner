package ru.bigcheese.flscanner.event;

import java.util.EventListener;

public interface ParseTaskEventListener extends EventListener {

    void handleEvent(ParseTaskEvent event);
}
