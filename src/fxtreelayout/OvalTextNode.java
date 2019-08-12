package fxtreelayout;

import java.awt.geom.Rectangle2D;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class OvalTextNode // NO_UCD
{
	private static double RECT_SCALE_FROM_ID_TEXT_LAYOUT_HEIGHT = 1.3;

	private double mHeight;
	private Text mIdText;
	private boolean mIsFolded;
	private Path mPath;
	private StackPane mStackPane;
	private double mWidth;

	public OvalTextNode(String idText)
	{
		this(idText, Font.getDefault());
	}

	public OvalTextNode(String idText, Font font)
	{
		mIdText = new Text(idText);
		mIdText.setFont(font);
		mIdText.setMouseTransparent(true);
		MoveTo moveTo = new MoveTo();
		mPath = new Path(moveTo);
		mPath.setFill(Color.WHITE);

		// Set oval positioning variables
		double rectHeight = mIdText.getLayoutBounds().getHeight() * RECT_SCALE_FROM_ID_TEXT_LAYOUT_HEIGHT;
		double rectHalfHeight = rectHeight / 2;
		double radiusPow = Math.pow(rectHalfHeight, 2) * 2;
		double radius = Math.sqrt(radiusPow);
		double absControlsOffsetFromRect = (radius - rectHalfHeight) * 4 / 3;
		mHeight = rectHeight + absControlsOffsetFromRect * 2;

		// Restrict minimum width so that the oval is never narrower than a semicircle
		double rectWidth = Math.max(mIdText.getLayoutBounds().getWidth(), rectHeight);
		mWidth = rectWidth + absControlsOffsetFromRect * 2;

		// Build left cubic curve (bottom-up)
		buildRelativeCubicCurve(mPath, -absControlsOffsetFromRect, -absControlsOffsetFromRect,
				-absControlsOffsetFromRect, absControlsOffsetFromRect - rectHeight, 0, -rectHeight);

		// Build top cubic curve (left-right)
		buildRelativeCubicCurve(mPath, absControlsOffsetFromRect, -absControlsOffsetFromRect,
				rectWidth - absControlsOffsetFromRect, -absControlsOffsetFromRect, rectWidth, 0);

		// Build right cubic curve (top-down)
		buildRelativeCubicCurve(mPath, absControlsOffsetFromRect, absControlsOffsetFromRect, absControlsOffsetFromRect,
				rectHeight - absControlsOffsetFromRect, 0, rectHeight);

		// Build bottom cubic curve (right-left)
		buildRelativeCubicCurve(mPath, -absControlsOffsetFromRect, absControlsOffsetFromRect,
				absControlsOffsetFromRect - rectWidth, absControlsOffsetFromRect, -rectWidth, 0);

		mStackPane = new StackPane(mPath, mIdText);
	}

	void addToParentPane(Pane parentPane)
	{
		parentPane.getChildren().add(mStackPane);
	}

	private void buildRelativeCubicCurve(Path path, double controlX1, double controlY1, double controlX2,
			double controlY2, double x, double y)
	{
		CubicCurveTo cubicCurve = new CubicCurveTo(controlX1, controlY1, controlX2, controlY2, x, y);
		cubicCurve.setAbsolute(false);
		path.getElements().add(cubicCurve);
	}

	double getHeight()
	{
		return mHeight;
	}

	public Text getIdText()
	{
		return mIdText;
	}

	public Path getPath()
	{
		return mPath;
	}

	public StackPane getStackPane()
	{
		return mStackPane;
	}

	double getWidth()
	{
		return mWidth;
	}

	public boolean isFolded()
	{
		return mIsFolded;
	}

	void removeFromParentPane(Pane parentPane)
	{
		parentPane.getChildren().remove(mStackPane);
	}

	void setFold(boolean fold)
	{
		mIsFolded = fold;
	}

	@Override
	public String toString()
	{
		return mIdText.getText();
	}

	void updateUIPosition(Rectangle2D.Double bounds, Rectangle2D.Double parentBounds)
	{
		mStackPane.relocate(bounds.getX(), bounds.getY());
	}
}
