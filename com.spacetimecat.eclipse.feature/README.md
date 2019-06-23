# Erik's Eclipse Enhancements

- WorkbenchPage:
    - New tabs open *next to the active tab*.
    (In Eclipse, new tabs open at the *end of the tab list*.)
    - Move tab left or right with Ctrl+Shift+PageUp or Ctrl+Shift+PageDown.
    (Known problem: It only works for editor parts and not view parts)
- SWT Tree:
    - Left Arrow folds the selected item or folds its parent.
    - Right Arrow expands the selected item and selects its first child.

The SWT Tree Arrow keys behavior could be achieved with GTK config files,
but I do not want to maintain GTK config files because
they will break in the next major GTK release.
