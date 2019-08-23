package com.base.engine.components;

import com.base.engine.core.Input;
import com.base.engine.core.Vector3f;

public class FreeMove extends GameComponents
{
	private float speed;
	private int forwardKey;
	private int backKey;
	private int leftKey;
	private int rightKey;

	public FreeMove(float speed)
	{
		this(speed, Input.KEY_W, Input.KEY_S, Input.KEY_A, Input.KEY_D);
	}

	public FreeMove(float speed, int forwardKey, int backKey, int leftKey, int rightKey)
	{
		this.speed = speed;
		this.forwardKey = forwardKey;
		this.backKey = backKey;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
	}

	@Override
	public void input(float delta)
	{
		float movAmt = speed * delta;

		if(Input.getKey(forwardKey))
			move(getTransform().getRotation().getForward(), movAmt);
		if(Input.getKey(backKey))
			move(getTransform().getRotation().getForward(), -movAmt);
		if(Input.getKey(leftKey))
			move(getTransform().getRotation().getLeft(), movAmt);
		if(Input.getKey(rightKey))
			move(getTransform().getRotation().getRight(), movAmt);
	}

	private void move(Vector3f dir, float amt)
	{
		getTransform().setTranslation(getTransform().getTranslation().add(dir.mul(amt)));
	}
}