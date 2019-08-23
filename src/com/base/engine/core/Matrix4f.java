package com.base.engine.core;

public class Matrix4f {
	
	private float[][] m;
	
	public Matrix4f() {
		m = new float[4][4];
	}
	
	public Matrix4f initIdentity() {
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 3; j++) {
				if(i == j)
					m[i][j] = 1;
				else
					m[i][j] = 0;
			}
		}
		/*m[0][0] = 1; m[0][1] = 0; m[0][2] = 0; m[0][3] = 0; 
		m[1][0] = 0; m[1][1] = 1; m[1][2] = 0; m[1][3] = 0; 
		m[2][0] = 0; m[2][1] = 0; m[2][2] = 1; m[2][3] = 0; 
		m[3][0] = 0; m[3][1] = 0; m[3][2] = 0; m[3][3] = 1;*/ 
		return this;
	}
	
	
	// Here scaled all of the points in a 1x1 box of x and y coordinates 
	// It squish in the correct proportion of aspect ratio because without it 
	// when it rotates it gets bigger like comming towards screen.
	public Matrix4f initPerspective(float fov, float aspectRatio, float zNear, float zFar) {
		 // This is aspect ratio...
		float tanHalFov = (float)(Math.tan(fov/2)); //Trignomatry is Happening here
		float zRange = zNear - zFar; 
		m[0][0] = 1.0f / (tanHalFov * aspectRatio);  m[0][1] = 0; 				m[0][2] = 0; 					    m[0][3] = 0; 
		m[1][0] = 0; 						m[1][1] = 1.0f / tanHalFov;    		m[1][2] = 0; 		 		     	m[1][3] = 0; 
		m[2][0] = 0; 						m[2][1] = 0;		    			m[2][2] = (-zNear - zFar)/zRange;   m[2][3] = (2 * zFar * zNear) / zRange; 
		m[3][0] = 0; 						m[3][1] = 0; 						m[3][2] = 1; 					    m[3][3] = 0; 
		return this;
	}
	
	//Initializing Orthographic projection...
	public Matrix4f initOrthographic(float left, float right, float bottom, float top, float near, float far) {
		//We project this rectangular prism in 2 steps:
		//First we scale all the things in the scene so it fits in float variable below...
		//Second we translate everything so it fits in the prism...
		float width = right - left;
		float height = top - bottom;
		float depth = far - near;
																													//-ve sing is used because we are going from the end position to the negative -1 and +1...	
		m[0][0] = /*1st Step*/ 2/width; 		m[0][1] = 0; 						m[0][2] = 0; 					 m[0][3] = -(right + left)/width/*2nd Step*/; 
		m[1][0] = 0;		  					m[1][1] = 2/height; /*1st Step*/	m[1][2] = 0; 					 m[1][3] = -(top + bottom)/height/*2nd Step*/; 
		m[2][0] = 0; 							m[2][1] = 0; 						m[2][2] = /*1st Step*/ -2/depth; m[2][3] = -(far + near)/depth/*2nd Step*/; 
		m[3][0] = 0; 							m[3][1] = 0; 						m[3][2] = 0; 					 m[3][3] = 1; 
		return this;
	}
	
	public Matrix4f initRotation(Vector3f forward, Vector3f up) {
		Vector3f f = forward;
		f.normalized();
		
		Vector3f r = up;

		r.normalized();
		r = r.cross(f);
		
		Vector3f u = f.cross(r);
		
		//Goes to it's overloading function below and do matrix calculation and retunrs values...
		return new Matrix4f().initRotation(f, u, r);
	}
	
	public Matrix4f initRotation(Vector3f forward, Vector3f up, Vector3f right) {
		Vector3f f = forward;
		Vector3f r = right;
		Vector3f u = up;
		
		m[0][0] = r.getX();   	m[0][1] = r.getY(); 		m[0][2] = r.getZ();         m[0][3] = 0; 
		m[1][0] = u.getX();   	m[1][1] = u.getY();		    m[1][2] = u.getZ();	     	m[1][3] = 0; 
		m[2][0] = f.getX();   	m[2][1] = f.getY();		    m[2][2] = f.getZ();		    m[2][3] = 0; 
		m[3][0] = 0;   			m[3][1] = 0; 				m[3][2] = 0; 				m[3][3] = 1; 
		return this;
	}
	
	public Matrix4f initScale(float x, float y, float z) {
		
		m[0][0] = x; m[0][1] = 0; m[0][2] = 0; m[0][3] = 0; 
		m[1][0] = 0; m[1][1] = y; m[1][2] = 0; m[1][3] = 0; 
		m[2][0] = 0; m[2][1] = 0; m[2][2] = z; m[2][3] = 0; 
		m[3][0] = 0; m[3][1] = 0; m[3][2] = 0; m[3][3] = 1; 
		return this;
	}
	
	public Matrix4f initRotation(float x, float y, float z) {
		Matrix4f rx = new Matrix4f();
		Matrix4f ry = new Matrix4f();
		Matrix4f rz = new Matrix4f();
		
		x = (float)Math.toRadians(x);
		y = (float)Math.toRadians(y);
		z = (float)Math.toRadians(z);

		rz.m[0][0] = (float)Math.cos(z);   rz.m[0][1] = -(float)Math.sin(z); rz.m[0][2] = 0; rz.m[0][3] = 0; 
		rz.m[1][0] = (float)Math.sin(z);   rz.m[1][1] = (float)Math.cos(z);   rz.m[1][2] = 0; rz.m[1][3] = 0; 
		rz.m[2][0] = 0;                    rz.m[2][1] = 0;                   rz.m[2][2] = 1; rz.m[2][3] = 0; 
		rz.m[3][0] = 0;                    rz.m[3][1] = 0;                   rz.m[3][2] = 0; rz.m[3][3] = 1;
		
		rx.m[0][0] = 1;                    rx.m[0][1] = 0; 				    rx.m[0][2] = 0;  				  rx.m[0][3] = 0; 
		rx.m[1][0] = 0; 				   rx.m[1][1] = (float)Math.cos(x); rx.m[1][2] = -(float)Math.sin(x); rx.m[1][3] = 0; 
		rx.m[2][0] = 0; 				   rx.m[2][1] = (float)Math.sin(x); rx.m[2][2] = (float)Math.cos(x);  rx.m[2][3] = 0; 
		rx.m[3][0] = 0; 				   rx.m[3][1] = 0; 					rx.m[3][2] = 0;    				  rx.m[3][3] = 1; 
		
		ry.m[0][0] = (float)Math.cos(y);   ry.m[0][1] = 0;     				ry.m[0][2] = -(float)Math.sin(y); ry.m[0][3] = 0; 
		ry.m[1][0] = 0; 				   ry.m[1][1] = 1;   				ry.m[1][2] = 0;      			  ry.m[1][3] = 0; 
		ry.m[2][0] = (float)Math.sin(y);   ry.m[2][1] = 0; 					ry.m[2][2] = (float)Math.cos(y);  ry.m[2][3] = 0; 
		ry.m[3][0] = 0;                    ry.m[3][1] = 0;    				ry.m[3][2] = 0; 				  ry.m[3][3] = 1; 
		
		m = rz.mul(ry.mul(rx)).getM();
		return this;
	}
	
	public Matrix4f initTranslation(float x, float y, float z) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if(i == 0 && j == 3)
					m[i][j] = x;
				else if(i == 1 && j == 3)
					m[i][j] = y;
				else if(i == 2 && j == 3)
					m[i][j] = z;
				else if(i == j)
					m[i][j] = 1;
				else
					m[i][j] = 0;
			}
		}
		// This means only those components will appear in last 
		// last which are 1 e.g. first row is x because x([0][0]) is 1
		// in second row y is 1 [1][1] and in third z is 1... in last W
		// component is 0 so only W is one W is last index value...
	/*	m[0][0] = 1; m[0][1] = 0; m[0][2] = 0; m[0][3] = x; 
		m[1][0] = 0; m[1][1] = 1; m[1][2] = 0; m[1][3] = y; 
		m[2][0] = 0; m[2][1] = 0; m[2][2] = 1; m[2][3] = z; 
		m[3][0] = 0; m[3][1] = 0; m[3][2] = 0; m[3][3] = 1;*/
		return this;
	}
	
	public Matrix4f mul(Matrix4f r) {
		Matrix4f res = new Matrix4f();
		float[][] t = new float[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				t[i][j] = 0;
				for (int k = 0; k < 4; k++) {
					t[i][j] += m[i][k] * r.get(k, j);
					res.set(i, j, t[i][j]);					
				}
			}
		}
	/*	for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				res.set(i, j, m[i][0] * r.get(0, j) +
						    m[i][1] * r.get(1, j) +
						    m[i][2] * r.get(2, j) +
						    m[i][3] * r.get(3, j));
			}
		}*/
		
		return res;
	}
	
	public Vector3f transform(Vector3f r) { //Passing another parameter like float srcW and multiply with this will change our spawn position...
		return new Vector3f(m[0][0] * r.getX() + m[0][1] * r.getY() + m[0][2] * r.getZ() + m[0][3],
							m[1][0] * r.getX() + m[1][1] * r.getY() + m[1][2] * r.getZ() + m[1][3],
							m[2][0] * r.getX() + m[2][1] * r.getY() + m[2][2] * r.getZ() + m[2][3]) ;
	}
	
	public float[][] getM() {
		float[][] res = new float[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				res[i][j] = m[i][j];
			}	
		}
		return res;
	}
	
	public float get(int x, int y) {
		return m[x][y];
	}
	
	public void setM(float[][] m) {
		this.m = m;
	}
	
	public void set(int x, int y, float value) {
		m[x][y] = value;
	}
}
