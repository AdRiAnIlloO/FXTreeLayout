package fxtreelayout;

import java.awt.geom.Rectangle2D.Double;
import java.util.Iterator;
import org.abego.treelayout.Configuration;
import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class FXTreeLayout implements NodeExtentProvider<OvalTextNode> // NO_UCD
{
	/**
	 * The Pane which minimally surrounds the tree. It exists apart from
	 * {@link #mOuterRegion} to easily center the tree within the screen, since the
	 * desired viewport may be wider than the tree.
	 */
	private Pane mInnerPane;

	/**
	 * We have two cases where the tree must be fully recomputed and updated in UI:
	 * node addition and node folding/unfolding. Since TreeLayout computes the
	 * layout only during initialization, we state a need for reinitialization by
	 * setting this condition to true, so that multiple change requests are only
	 * attended by recreating it on the next UI update in order to save performance.
	 */
	private boolean mNeedsRefresh;

	/**
	 * The Region where to draw {@link #mInnerPane} the inner tree Pane at
	 */
	private Region mOuterRegion;

	private TreeLayout<OvalTextNode> mTreeLayout;

	public FXTreeLayout(Pane outerPane, OvalTextNode root, double gapBetweenLevels, double gapBetweenNodes)
	{
		Pane innerPane = new Pane();
		outerPane.getChildren().add(innerPane);
		init(outerPane, innerPane, root, gapBetweenLevels, gapBetweenNodes);
	}

	public FXTreeLayout(Pane outerPane, String rootNodeIdText, double gapBetweenLevels, double gapBetweenNodes)
	{
		this(outerPane, new OvalTextNode(rootNodeIdText), gapBetweenLevels, gapBetweenNodes);
	}

	public FXTreeLayout(Region outerRegion, Pane innerPane, OvalTextNode root, double gapBetweenLevels,
			double gapBetweenNodes)
	{
		init(outerRegion, innerPane, root, gapBetweenLevels, gapBetweenNodes);
	}

	public FXTreeLayout(Region outerRegion, Pane innerPane, String rootNodeIdText, double gapBetweenLevels,
			double gapBetweenNodes)
	{
		this(outerRegion, innerPane, new OvalTextNode(rootNodeIdText), gapBetweenLevels, gapBetweenNodes);
	}

	public OvalTextNode addChild(OvalTextNode parent, OvalTextNode child)
	{
		getTree().addChild(parent, child);
		OvalTextNode ancestor = parent;

		// Search folded ancestors
		do
		{
			if (ancestor.isFolded())
			{
				return child;
			}

			ancestor = getTree().getParent(ancestor);
		}
		while (ancestor != null);

		child.addToParentPane(mInnerPane);
		mNeedsRefresh = true;
		return child;
	}

	public OvalTextNode addChild(OvalTextNode parent, String childNodeIdText)
	{
		OvalTextNode child = new NestedOvalTextNode(childNodeIdText, parent);
		return addChild(parent, child);
	}

	private void applyUnfoldingRecursively(OvalTextNode parent)
	{
		for (OvalTextNode child : getTree().getUnfilteredChildren(parent))
		{
			child.addToParentPane(mInnerPane);

			if (!child.isFolded())
			{
				applyUnfoldingRecursively(child);
			}
		}
	}

	public void fold(OvalTextNode node)
	{
		if (!node.isFolded())
		{
			node.setFold(true);
			removeChildrenNodesFromUIRecursively(node);
			mNeedsRefresh = true;
		}
	}

	@Override
	public double getHeight(OvalTextNode node)
	{
		return node.getHeight();
	}

	public Pane getInnerPane()
	{
		return mInnerPane;
	}

	public Region getOuterRegion()
	{
		return mOuterRegion;
	}

	FoldFilteringTree getTree()
	{
		return ((FoldFilteringTree) mTreeLayout.getTree());
	}

	@Override
	public double getWidth(OvalTextNode node)
	{
		return node.getWidth();
	}

	private void init(Region outerRegion, Pane innerPane, OvalTextNode root, double gapBetweenLevels,
			double gapBetweenNodes)
	{
		FoldFilteringTree tree = new FoldFilteringTree(root);
		Configuration<OvalTextNode> configuration = new DefaultConfiguration<>(gapBetweenLevels, gapBetweenNodes);
		mTreeLayout = new TreeLayout<>(tree, this, configuration, true);
		mOuterRegion = outerRegion;
		mInnerPane = innerPane;
		root.addToParentPane(innerPane);
		updateUIChildrenRecursively(root, null);
		TreeDrawThinker.sInstance.mTreeLayouts.add(this);
	}

	public void remove()
	{
		removeNodesRecursively(getTree().getRoot());
		TreeDrawThinker.sInstance.mTreeLayouts.remove(this);
	}

	private void removeChildrenNodesFromUIRecursively(OvalTextNode parent)
	{
		for (OvalTextNode child : getTree().getUnfilteredChildren(parent))
		{
			child.removeFromParentPane(mInnerPane);
			removeChildrenNodesFromUIRecursively(child);
		}
	}

	public void removeNodesRecursively(OvalTextNode parent)
	{
		parent.removeFromParentPane(mInnerPane);
		parent.mIsPurged = true;
		mNeedsRefresh = true;

		for (Iterator<OvalTextNode> iterator = getTree().getUnfilteredChildren(parent).iterator(); iterator
				.hasNext(); iterator.remove())
		{
			removeNodesRecursively(iterator.next());
		}
	}

	public void unfold(OvalTextNode node)
	{
		if (node.isFolded())
		{
			node.setFold(false);
			applyUnfoldingRecursively(node);
			mNeedsRefresh = true;
		}
	}

	void updateUI()
	{
		if (mNeedsRefresh)
		{
			mTreeLayout = new TreeLayout<>(getTree(), this, mTreeLayout.getConfiguration(), true);
			updateUIChildrenRecursively(getTree().getRoot(), null);
			mNeedsRefresh = false;
		}
	}

	private void updateUIChildrenRecursively(OvalTextNode node, OvalTextNode parent)
	{
		Double bounds = mTreeLayout.getNodeBounds().get(node);
		node.updateUIPosition(bounds, mTreeLayout.getNodeBounds().get(parent));

		for (OvalTextNode child : getTree().getChildren(node))
		{
			updateUIChildrenRecursively(child, node);
		}
	}
}
