package com.spacetimecat.eclipse.commons;

final class Meager_Progress_Monitor implements Progress_Monitor {

    private void printf (String format, Object... args) {
        System.out.printf("Meager_Progress_Monitor: " + format, args);
    }

    @Override
    public void set_caption (String text) {
        printf("set_caption: %s\n", text);
    }

    @Override
    public boolean is_canceled () {
        return false;
    }

    @Override
    public void set_progress (int done, int total) {
        printf("set_progress: %d / %d\n", done, total);
    }

}
