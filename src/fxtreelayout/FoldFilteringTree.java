package fxtreelayout;

import java.util.Arrays;
import java.util.List;

import org.abego.treelayout.util.DefaultTreeForTreeLayout;

/**
 * An AbstractTreeForTreeLayout subclass which implements
 * {@link #getChildrenList(OvalTextNode)} as a simple way to prevent children of
 * folded nodes to be computed for layout
 */
class FoldFilteringTree extends DefaultTreeForTreeLayout<OvalTextNode>
{
	FoldFilteringTree(OvalTextNode root)
	{
		super(root);
	}

	@Override
	public List<OvalTextNode> getChildrenList(OvalTextNode node)
	{
		if (node.isFolded())
		{
			return Arrays.asList();
		}

		return super.getChildrenList(node);
	}

	Iterable<OvalTextNode> getUnfilteredChildren(OvalTextNode node)
	{
		return super.getChildrenList(node);
	}
}
