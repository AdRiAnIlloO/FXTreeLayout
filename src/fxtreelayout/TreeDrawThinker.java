package fxtreelayout;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;

class TreeDrawThinker extends AnimationTimer
{
	static TreeDrawThinker sInstance;

	static
	{
		sInstance = new TreeDrawThinker();
		sInstance.start();
	}

	List<FXTreeLayout> mTreeLayouts;

	TreeDrawThinker()
	{
		mTreeLayouts = new ArrayList<>();
	}

	@Override
	public void handle(long now)
	{
		for (FXTreeLayout layout : mTreeLayouts)
		{
			layout.updateUI();
		}
	}
}
