package kim.hsl.opengl.rotate_triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class TriangleView extends GLSurfaceView {

	private final float ANGLE_SPAN = 0.375f;		//三角形每次旋转的角度
	
	private RotateThread mRotateThread;		//该线程用来改变图形角度
	private SceneRenderer mSceneRender;		//渲染器
	
	public TriangleView(Context context) {
		super(context);
		//设置OpenGLES版本为2.0
		this.setEGLContextClientVersion(2);	
		
		//设置渲染器 渲染模式
		mSceneRender = new SceneRenderer();
		this.setRenderer(mSceneRender);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	/**
	 * 渲染器
	 * 实现了下面三个方法 : 
	 * 		界面创建 : 
	 * 		界面改变 : 
	 * 		界面绘制 : 
	 * @author HanShuliang
	 *
	 */
	private class SceneRenderer implements Renderer{
		Triangle triangle;
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			//设置屏幕背景色
			GLES20.glClearColor(0, 0, 0, 1.0f);
			//创建三角形对象
			triangle = new Triangle(TriangleView.this);
			//打开深度检测
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			mRotateThread = new RotateThread();
			mRotateThread.start();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			//设置视窗大小及位置
			GLES20.glViewport(0, 0, width, height);
			//计算GLSurfaceView的宽高比
			float ratio = (float)width/height;
			/*
			 * 产生透视矩阵
			 * 参数介绍 : 
			 * ① 4 * 4 投影矩阵
			 * ② 投影矩阵的起始位置
			 * 后面的四个参数分别是 左 右 下 上 的距离
			 * 最后两个参数是 近视点 和 远视点 距离
			 */
			Matrix.frustumM(Triangle.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
			/*
			 * 设置摄像机参数矩阵
			 * 参数介绍 : 
			 * 前两个参数是摄像机参数矩阵 和 矩阵数组的起始位置
			 * 后面三个一组是三个空间坐标 先后依次是 摄像机的位置  看的方向 摄像机上方朝向
			 */
			Matrix.setLookAtM(Triangle.mVMatrix, 0, 0f,0f,3f,0f,0f,0f,0f,1.0f,0.0f);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			//清除深度缓冲与颜色缓冲
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			//绘制三角形
			triangle.drawSelf();
		}
	}
	
	
	/**
	 * 这个线程是用来改变三角形角度用的
	 */
	public class RotateThread extends Thread{
		
		public boolean flag = true;
		
		@Override
		public void run() {
			while(flag){
				mSceneRender.triangle.xAngle = mSceneRender.triangle.xAngle + ANGLE_SPAN;
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	} 
	
}
