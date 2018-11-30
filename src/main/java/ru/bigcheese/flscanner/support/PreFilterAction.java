package ru.bigcheese.flscanner.support;

import java.io.IOException;

/**
 * Created by BigCheese on 01.07.16.
 */
@Deprecated
public interface PreFilterAction {
    void applyFilter() throws IOException;
}
