/*
 * ====================================================================== OutErrTabPane is a tab
 * pane with two console tabs for stdout and stderr. Application : Siard2 Description :
 * OutErrTabPane is a tab pane with two console tabs for stdout and stderr. Platform : Java 7,
 * JavaFX 2.2 ------------------------------------------------------------------------ Copyright :
 * Swiss Federal Archives, Berne, Switzerland, 2017 Created : 28.06.2017, Hartwig Thomas, Enter AG,
 * Rüti ZH ======================================================================
 */
package ch.enterag.utils.fx.controls;

import ch.enterag.utils.fx.FxSizes;
import ch.enterag.utils.fx.FxStyles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

/* ==================================================================== */
/**
 * OutErrTabPane implements a tab pane with a console tab for stdout and a console tab for stderr.
 * N.B.: System.err and System.out must be saved before this is created and restored after the tab
 * pane is not used any more!
 *
 * @author Hartwig
 */
public class OutErrTabPane extends TabPane
{
	/** preferred number of columns of the console */
	private static final int iPREF_COLUMNS = 80;
	/** preferred number of rows of the console */
	private static final int iPREF_ROWS = 25;
	/** stdout tab */
	Tab _tabOut = null;
	/** stderr tab */
	Tab _tabError = null;

	/* ================================================================== */
	/**
	 * a change of the text content of the stdout console selects its tab.
	 */
	private class OutChangeListener implements ChangeListener<String>
	{
		@Override
		public void changed(ObservableValue<? extends String> ov,
			String sOld, String sNew)
		{
			OutErrTabPane.this.getSelectionModel().select(_tabOut);
		}
	}

	OutChangeListener ocl = new OutChangeListener();

	/* ================================================================== */
	/**
	 * a change of the text content of the stderr console selects its tab.
	 */
	private class ErrChangeListener implements ChangeListener<String>
	{
		@Override
		public void changed(ObservableValue<? extends String> ov,
			String sOld, String sNew)
		{
			OutErrTabPane.this.getSelectionModel().select(_tabError);
		}
	}

	ErrChangeListener ecl = new ErrChangeListener();

	/*------------------------------------------------------------------*/
	/**
	 * create a tab for the tab pane containing a scrollable console text area.
	 *
	 * @param sTitle title of tab.
	 * @param taConsole console text area.
	 * @return tab for tab pane.
	 */
	private Tab createTab(String sTitle, TextArea taConsole)
	{
		taConsole.setEditable(false);
		taConsole.setWrapText(false);
		taConsole.setPrefColumnCount(iPREF_COLUMNS);
		taConsole.setPrefRowCount(iPREF_ROWS);

		ScrollPane sp = new ScrollPane();
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		sp.setContent(taConsole);
		sp.setPrefViewportWidth(taConsole.getPrefColumnCount() * FxSizes.getEm());
		sp.setPrefViewportHeight(taConsole.getPrefRowCount() * FxSizes.getEx());
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);

		Tab tab = new Tab();
		tab.setText(sTitle);
		tab.setContent(sp);
		return tab;
	} /* createTab */

	/*------------------------------------------------------------------*/
	/**
	 * constructor creates a tab pane containing two console tabs for the output and the error stream
	 * and redirects System.out and System.err to them.
	 */
	public OutErrTabPane()
	{
		super();
		setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		setStyle(FxStyles.sSTYLE_SOLID_BORDER);
		double dMinWidth = 0.0;
		double dMinHeight = 0.0;
		TextArea taOut = new TextArea();
		taOut.textProperty().addListener(ocl);
		ConsolePrintStream cpsOut = ConsolePrintStream.newConsolePrintStream(taOut);
		TextArea taError = new TextArea();
		taError.textProperty().addListener(ecl);
		taError.setStyle(FxStyles.sSTYLE_ERROR);
		ConsolePrintStream cpsError = ConsolePrintStream.newConsolePrintStream(taError);
		_tabOut = createTab("Out", taOut);
		ScrollPane spOut = (ScrollPane) _tabOut.getContent();
		if (dMinWidth < spOut.getPrefViewportWidth())
			dMinWidth = spOut.getPrefViewportWidth();
		if (dMinHeight < spOut.getPrefViewportHeight())
			dMinHeight = spOut.getPrefViewportHeight();
		_tabError = createTab("Error", taError);
		ScrollPane spErr = (ScrollPane) _tabError.getContent();
		if (dMinWidth < spErr.getMinWidth())
			dMinWidth = spErr.getMinWidth();
		if (dMinHeight < spErr.getMinHeight())
			dMinHeight = spErr.getMinHeight();
		getTabs().add(_tabOut);
		getTabs().add(_tabError);
		setMinWidth(dMinWidth);
		setMinHeight(dMinHeight);
		System.setOut(cpsOut);

		/* S: err탭에 로그 출력 */
		// 파일다운로드 내역이 err창에 출력되서 주석처리
		// System.setOut(cpsErr);
		/* E: err탭에 로그 출력 */

		System.setErr(cpsError);
		getSelectionModel().select(_tabOut);
	} /* constructor */

} /* OutErrTabPane */
