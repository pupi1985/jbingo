package view.shells;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import controller.HistoryController;
import model.Bingo;
import net.miginfocom.swt.MigLayout;
import view.Messages;
import view.ViewUtils;

public class HistoryView {

    private Shell shell;
    private Shell parentShell;
    private Display display;

    private HistoryController historyController;
    private Bingo bingo;

    public HistoryView(Bingo bingo, HistoryController historyController, Display display, Shell parentShell) {
        this.bingo = bingo;
        this.historyController = historyController;
        this.display = display;
        this.parentShell = parentShell;
        createContents();
        shell.pack();
    }

    public void open() {
        shell.layout();
        shell.open();
        ViewUtils.centerShell(shell);
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createContents() {
        shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        shell.setLayout(new MigLayout("wrap 1,")); //$NON-NLS-1$
        shell.setText(Messages.getString("HistoryView.Title")); //$NON-NLS-1$

        int amountOfGridColumns = bingo.getSettingsManager().getAmountOfGridColumns();

        Composite mainPanel = new Composite(shell, SWT.NONE);
        mainPanel.setLayout(new MigLayout(String.format("wrap %d, fill, gapx 8, gapy 14", amountOfGridColumns + 1))); //$NON-NLS-1$
        mainPanel.setLayoutData("grow, push"); //$NON-NLS-1$

        List<Integer> pickedNumbers = new ArrayList<Integer>(bingo.getNumberBag().getPickedNumbers());

        // Make the font slightly smaller
        int fontHeight = (int) (bingo.getSettingsManager().getNumbersFontData().getHeight() * 0.9);
        FontDescriptor fontDescriptor = FontDescriptor.createFrom(bingo.getSettingsManager().getNumbersFontData());
        Font numberLabelFont = fontDescriptor.setHeight(fontHeight).createFont(display);

        Color fontColor = new Color(display, bingo.getSettingsManager().getPickedNumberColor());

        int amountOfPickedNumbers = pickedNumbers.size();
        int counter = 0;
        for (int pickedNumber : pickedNumbers) {
            boolean isLastNumber = counter == amountOfPickedNumbers - 1;
            if (counter % amountOfGridColumns == 0) {
                Composite mainLabelComposite = new Composite(mainPanel, SWT.NONE);
                mainLabelComposite.setLayout(new MigLayout("fill, insets 0")); //$NON-NLS-1$
                mainLabelComposite.setLayoutData("grow, push"); //$NON-NLS-1$
                String numberRangeText = isLastNumber
                        ? Messages.getString("HistoryView.NumberRangeOne", counter + 1) //$NON-NLS-1$
                        : Messages.getString("HistoryView.NumberRangeTwo", counter + 1, Math.min(counter + amountOfGridColumns, amountOfPickedNumbers)); //$NON-NLS-1$
                Label label = new Label(mainLabelComposite, SWT.NONE);
                label.setLayoutData("align right, gap 0"); //$NON-NLS-1$
                label.setText(numberRangeText);
            }
            Composite mainLabelComposite = new Composite(mainPanel, SWT.BORDER);
            mainLabelComposite.setLayout(new MigLayout("fill, insets 0")); //$NON-NLS-1$
            mainLabelComposite.setLayoutData("grow, push, sizegroup mainLabelComposite"); //$NON-NLS-1$
            Label label = new Label(mainLabelComposite, SWT.NONE);
            label.setLayoutData("align center, gap 0"); //$NON-NLS-1$
            label.setText(String.valueOf(pickedNumber));
            label.setFont(numberLabelFont);
            if (isLastNumber) {
                label.setForeground(new Color(display, bingo.getSettingsManager().getCurrentNumberColor()));
            } else {
                label.setForeground(fontColor);
            }
            counter++;
        }

        // Start bottom panel

        Label mainPanelSeparator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
        mainPanelSeparator.setLayoutData("grow"); //$NON-NLS-1$

        Composite bottomPanel = new Composite(shell, SWT.NONE);
        bottomPanel.setLayout(new MigLayout("wrap 1, fillx")); //$NON-NLS-1$
        bottomPanel.setLayoutData("growx, pushx"); //$NON-NLS-1$

        Composite spacer = new Composite(bottomPanel, SWT.NONE);
        spacer.setLayout(new MigLayout("insets 0")); //$NON-NLS-1$
        spacer.setLayoutData("growx, pushx"); //$NON-NLS-1$

        Button okButton = new Button(bottomPanel, SWT.PUSH);
        okButton.setLayoutData("wmin 100, align center"); //$NON-NLS-1$
        okButton.setText(Messages.getString("Application.ButtonOk")); //$NON-NLS-1$
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                historyController.okButtonAction();
            }
        });
        shell.setDefaultButton(okButton);
    }

    public Display getDisplay() {
        return display;
    }

    public Shell getShell() {
        return shell;
    }
}
