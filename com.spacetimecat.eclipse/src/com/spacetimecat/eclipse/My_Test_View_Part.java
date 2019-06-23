package com.spacetimecat.eclipse;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import com.spacetimecat.eclipse.commons.FUI;

public class My_Test_View_Part extends ViewPart {

    Tree a;

    @Override
    public void createPartControl (Composite parent) {
        a = new Tree(parent, SWT.MULTI);
        {
            TreeItem a_0 = new TreeItem(a, 0);
            a_0.setText("0");
            a_0.setExpanded(true);
            {
                TreeItem a_0_0 = new TreeItem(a_0, 0);
                a_0_0.setText("0.0");
                a_0_0.setExpanded(true);
                {
                    TreeItem a_0_0_0 = new TreeItem(a_0_0, 0);
                    a_0_0_0.setText("0.0.0");
                }
            }
        }
        Button b = FUI.button(parent, "Test P2/PDE", (e) -> {
            try {
                new My_PDE_P2_Test_Command().execute(new ExecutionEvent());
            } catch (ExecutionException x) {
                My_Plugin.get_instance().handle_status(x);
            }
        });
    }

    @Override
    public void setFocus () {
        a.setFocus();
    }

}
