package ru.bigcheese.flscanner.event;

import java.util.EventObject;

public class ParseTaskEvent extends EventObject {

    private final ParseTaskEventType type;
    private Object payload;

    public ParseTaskEvent(Object source, ParseTaskEventType type) {
        this(source, type, null);
    }

    public ParseTaskEvent(Object source, ParseTaskEventType type, Object payload) {
        super(source);
        this.type = type;
        this.payload = payload;
    }

    public ParseTaskEventType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
