package com.spacetimecat.eclipse.commons;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Functional User Interface.
 */
public final class FUI {

    private FUI () {}

    public static Button button (Composite parent, String text, Consumer<SelectionEvent> onSelect) {
        Button a = new Button(parent, SWT.PUSH);
        a.setText(text);
        a.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected (SelectionEvent e) {
                onSelect.accept(e);
            }
        });
        return a;
    }

    public static Label label (Composite parent, String text) {
        Label a = new Label(parent, SWT.HORIZONTAL);
        a.setText(text);
        return a;
    }

}
