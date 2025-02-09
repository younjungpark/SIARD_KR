/*======================================================================
Base class for simple dialogs.
Application : JavaFX Utilities.
Description : Dialog is a base class for simple dialogs.
Platform    : Java 8, JavaFX 8
------------------------------------------------------------------------
Copyright  : 2015, Enter AG, Rüti ZH, Switzerland
Created    : 22.12.2015, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.fx;

import ch.admin.bar.siard2.gui.MainMenuBar;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dialog
  extends Stage
{
  // "padding" inside the screen */
  protected static final double dSCREEN_PADDING = 10.0;
  // outer padding
  protected static final double dOUTER_PADDING = 10.0;
  // inner padding
  protected static final double dINNER_PADDING = 0.0;
  // vertical spacing of elements
  protected static final double dVSPACING = 10.0;
  // horizontal spacing of elements
  protected static final double dHSPACING = 10.0;

  protected Dialog(Stage stageOwner, String sTitle)
  {
    super();
    /* title */
    setTitle(sTitle);

    /* style */
//    initStyle(StageStyle.UTILITY);
	initStyle(StageStyle.DECORATED); /* IntraDIGM */

    /* owner */
    initOwner(stageOwner);
    /* modality */
    initModality(Modality.APPLICATION_MODAL);

    /* icon */ /* IntraDIGM */
	getIcons().add(new Image(MainMenuBar.class.getResourceAsStream("res/favicon.png"))); /* IntraDIGM */

    /* display on top */
    toFront();
  } /* constructor */

}
