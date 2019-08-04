package fxtreelayout;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class SampleController // NO_UCD
{
	@FXML
	private Pane mPane;

	@FXML
	private void initialize()
	{
		FXTreeLayout layout = new FXTreeLayout(mPane, "1", 50, 25);
		OvalTextNode node = layout.addChild(layout.getTree().getRoot(), "1.1");
		layout.addChild(node, "1.1.1");
		node = layout.addChild(layout.getTree().getRoot(), "1.2");
		layout.addChild(node, "1.2.1");
		node = layout.addChild(layout.getTree().getRoot(), "1.3");
		layout.addChild(node, "1.3.1");
	}
}
