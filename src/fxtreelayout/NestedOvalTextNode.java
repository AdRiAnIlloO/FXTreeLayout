package fxtreelayout;

import java.awt.geom.Rectangle2D.Double;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class NestedOvalTextNode extends OvalTextNode // NO_UCD
{
	private static double PARENT_ARC_STROKE_WIDTH = 1.5;

	private Line mParentArc;

	public NestedOvalTextNode(String idText, Font font, OvalTextNode parent)
	{
		super(idText, font);
		mParentArc = new Line();
		mParentArc.setStrokeWidth(PARENT_ARC_STROKE_WIDTH);
	}

	public NestedOvalTextNode(String idText, OvalTextNode parent)
	{
		this(idText, Font.getDefault(), parent);
	}

	@Override
	void addToParentPane(Pane parentPane)
	{
		super.addToParentPane(parentPane);
		parentPane.getChildren().add(mParentArc);
		mParentArc.toBack();
	}

	@Override
	void removeFromParentPane(Pane parentPane)
	{
		super.removeFromParentPane(parentPane);
		parentPane.getChildren().remove(mParentArc);
	}

	@Override
	void updateUIPosition(Double bounds, Double parentBounds)
	{
		super.updateUIPosition(bounds, parentBounds);
		mParentArc.setStartX(parentBounds.getCenterX());
		mParentArc.setStartY(parentBounds.getCenterY());
		mParentArc.setEndX(bounds.getCenterX());
		mParentArc.setEndY(bounds.getMinY());
	}
}
