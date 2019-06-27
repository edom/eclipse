package com.spacetimecat.eclipse.commons;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Functional User Interface.
 */
public final class FUI2 {

    private final Eclipse_Plugin plugin;

    public FUI2 (Eclipse_Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    public Button button (Composite parent, String text, Consumer0<SelectionEvent> onSelect) {
        Button a = new Button(parent, SWT.PUSH);
        a.setText(text);
        a.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected (SelectionEvent e) {
                try {
                    onSelect.accept(e);
                } catch (Exception t) {
                    plugin.handle_status(t);
                }
            }
        });
        return a;
    }

    public Label label (Composite parent, String text) {
        Label a = new Label(parent, SWT.HORIZONTAL);
        a.setText(text);
        return a;
    }

}
