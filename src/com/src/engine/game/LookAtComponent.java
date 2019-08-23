package com.src.engine.game;

import com.base.engine.components.GameComponents;
import com.base.engine.core.Quaternion;
import com.base.engine.core.RenderingEngine;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;

public class LookAtComponent extends GameComponents{
	RenderingEngine renderingEngine;

	@Override
	public void update(float delta)
	{
		if(renderingEngine != null)
		{
			Quaternion newRot = getTransform().getLookAtRotation(renderingEngine.getMainCamera().getTransform().getTransformedPos(),
					new Vector3f(0,1,0));
					//getTransform().getRot().getUp());

			getTransform().setRotation(getTransform().getRotation().nlerp(newRot, delta * 5.0f, true));//this is less expensive..
			//getTransform().setRot(getTransform().getRot().slerp(newRot, delta * 5.0f, true)); //but this gives guranteed linear rotation...
		}
	}

	@Override
	public void render(Shader shader, RenderingEngine renderingEngine)
	{
		this.renderingEngine = renderingEngine;
	}
}