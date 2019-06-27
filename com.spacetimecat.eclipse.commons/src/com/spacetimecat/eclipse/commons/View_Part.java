package com.spacetimecat.eclipse.commons;

import java.util.concurrent.Future;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;

public final class View_Part implements Scheduler {

    private final IViewSite site;
    private final Eclipse_Plugin plugin;
    private final FUI2 fui;

    public View_Part (IViewSite site, Eclipse_Plugin plugin) {
        this.site = site;
        this.plugin = plugin;
        this.fui = new FUI2(plugin);

    }

    @Override
    public Future<?> schedule (Consumer0<Progress_Monitor> task) {
        return Schedulers.schedule(plugin, site, task);
    }

    public Button button (Composite parent, String text, Consumer0<SelectionEvent> onSelect) {
        return fui.button(parent, text, onSelect);
    }

}
