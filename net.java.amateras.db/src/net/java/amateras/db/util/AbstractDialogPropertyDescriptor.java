package net.java.amateras.db.util;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * The base class for selection dialog property descriptors.
 *
 * @author Naoki Takezoe
 */
public abstract class AbstractDialogPropertyDescriptor extends PropertyDescriptor {

	public AbstractDialogPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	@Override public CellEditor createPropertyEditor(Composite parent) {
		ValueCellEditor editor = new ValueCellEditor(parent);
		if (getValidator() != null){
			editor.setValidator(getValidator());
		}
		return editor;
	}

	protected abstract Object openDialogBox(Object value, Control cellEditorWindow);

	protected class ValueCellEditor extends CellEditor {

		private Text text;
		private Composite editor;
		private Button button;
		private Object value = null;

		private class DialogCellLayout extends Layout {
			public void layout(Composite editor, boolean force) {
				Rectangle bounds = editor.getClientArea();
				Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				if (text != null){
					text.setBounds(0, 0, bounds.width-size.x, bounds.height);
				}
				button.setBounds(bounds.width-size.x, 0, size.x, bounds.height);
			}
			public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
				if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT){
					return new Point(wHint, hHint);
				}
				Point contentsSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				Point buttonSize   = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);

				Point result = new Point(buttonSize.x,Math.max(contentsSize.y, buttonSize.y));
				return result;
			}
		}

		public ValueCellEditor(Composite parent) {
			super(parent, SWT.NONE);
		}

		protected Control createControl(Composite parent) {

			Font font = parent.getFont();
			Color bg  = parent.getBackground();

			editor = new Composite(parent, getStyle());
			editor.setFont(font);
			editor.setBackground(bg);
			editor.setLayout(new DialogCellLayout());

			text = new Text(editor,SWT.NULL|SWT.READ_ONLY);
//			text.setEditable(false);
			text.setFont(parent.getFont());
			text.setBackground(parent.getBackground());

			button = new Button(editor, SWT.DOWN);
			button.setText("...");
			button.setFont(font);
			button.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					if (e.character == '\u001b') { // Escape
						fireCancelEditor();
					}
				}
			});
			button.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent event) {
					Object newValue = openDialogBox(editor);
					if (newValue != null) {
						updateValue(newValue);
					}
				}
			});

			setValueValid(true);
			updateContents(value);

			return editor;
		}

		private void updateValue(Object newValue){
			if (newValue != null) {
				boolean newValidState = isCorrect(newValue);
				if (newValidState) {
					markDirty();
					doSetValue(newValue);
				} else {
					setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { newValue.toString()}));
				}
				fireApplyEditorValue();
			}
		}

		protected Object doGetValue() {
			return value;
		}

		protected void doSetFocus() {
		    button.setFocus();
		}

		protected void doSetValue(Object value) {
			this.value = value;
			updateContents(value);
		}

		@Override public boolean isCopyEnabled() {
			return false;
		}

		@Override public boolean isCutEnabled() {
			return false;
		}

		@Override public boolean isDeleteEnabled() {
			return false;
		}

		@Override public boolean isPasteEnabled() {
			return false;
		}

		@Override public boolean isSelectAllEnabled() {
			return false;
		}

		@Override public void performCopy() {
		}

		@Override public void performCut() {
		}

		@Override public void performDelete() {
		}

		@Override public void performPaste() {
		}

		@Override public void performSelectAll() {
		}

		protected Object openDialogBox(Control cellEditorWindow){
			return AbstractDialogPropertyDescriptor.this.openDialogBox(value, cellEditorWindow);
		}

		protected void updateContents(Object value) {
			if (this.text == null){
				return;
			}
			String text = "";//$NON-NLS-1$
			if (value != null){
				text = getDisplayText(value);
			}
			this.text.setText(text);
		}
	}

	protected abstract String getDisplayText(Object value);

}
