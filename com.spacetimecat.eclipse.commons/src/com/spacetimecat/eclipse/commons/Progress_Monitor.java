package com.spacetimecat.eclipse.commons;

public interface Progress_Monitor {

    boolean is_canceled ();

    void set_caption (String text);

    void set_progress (int done, int total);

}
