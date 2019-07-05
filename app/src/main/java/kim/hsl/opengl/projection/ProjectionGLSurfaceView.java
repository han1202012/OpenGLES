package kim.hsl.opengl.projection;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * 自定义显示 OpenGL 图形的 SurfaceView
 * 
 * ① 初始化 SurfaceView
 * 		a. 设置 OpenGL ES 版本
 * 		b. 创建场景渲染器
 * 		c. 设置场景渲染器
 * 		d. 设置场景渲染器模式 
 * ② 自定义场景渲染器
 * 		a. 创建时 设置背景 -> 创建绘制元素 -> 打开深度检测
 * 		b. 场景改变时 设置视口参数 -> 设置投影参数 -> 设置摄像机参数
 * 		c. 绘制时 清楚颜色,深度缓冲 -> 绘制元素
 * @author octopus
 *
 */
public class ProjectionGLSurfaceView extends GLSurfaceView {
	public static boolean isOrth = true;
	
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320; 	// 角度缩放比例
	private SceneRenderer mRenderer; 						// 场景渲染器

	private float mPreviousY;								//上次触摸位置的Y坐标
	private float mPreviousX;								//上次触摸位置的X坐标

	/**
	 * 初始化 GLSurfaceView
	 * ① 设置 OpenGL ES 的版本
	 * ② 创建场景渲染器
	 * ③ 设置场景渲染器
	 * ④ 设置场景渲染模式
	 * @param context
	 */
	public ProjectionGLSurfaceView(Context context) {
		super(context);
		this.setEGLContextClientVersion(2); 					// 设置OpenGL ES 版本为 2.0
		mRenderer = new SceneRenderer(); 						// 创建场景渲染器
		setRenderer(mRenderer); 								// 设置场景渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);	// 设置场景渲染模式
	}

	// 触摸方法
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float y = e.getY();	//获取当前触摸的 y 坐标
		float x = e.getX();	//获取当前触摸的 x 坐标
		switch (e.getAction()) {	//获取触摸类型
		case MotionEvent.ACTION_MOVE:
			float dy = y - mPreviousY;// 计算 y 方向的位移
			float dx = x - mPreviousX;// 计算 x 方向的位移
			for (SixPointedStar h : mRenderer.ha) {
				h.yAngle += dx * TOUCH_SCALE_FACTOR;// 设置六角星绕 x 轴旋转角度
				h.xAngle += dy * TOUCH_SCALE_FACTOR;// 设置六角星绕 y 轴旋转角度
			}
		}
		mPreviousY = y;// 将本次触摸的 y 坐标记录为历史坐标
		mPreviousX = x;// 将本次触摸的 x 坐标记录为历史坐标
		return true;
	}

	/**
	 * 场景渲染器
	 * 创建六角星数组中得六角星对象, 将六角星显示在屏幕中
	 * @author octopus
	 *
	 */
	private class SceneRenderer implements GLSurfaceView.Renderer {
		SixPointedStar[] ha = new SixPointedStar[6];// 六角星数组

		/**
		 * ① 清楚深度缓冲 与 颜色缓冲
		 * ② 重新绘制各个元素
		 */
		public void onDrawFrame(GL10 gl) {
			// 清除深度缓冲与颜色缓冲
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
					| GLES20.GL_COLOR_BUFFER_BIT);
			// 循环绘制各个六角星
			for (SixPointedStar h : ha) {
				h.drawSelf();
			}
		}

		/**
		 * Surface 改变时
		 * ① 设置视口参数
		 * ② 设置投影参数
		 * ③ 设置摄像机参数
		 */
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// 设置视口的大小及位置
			GLES20.glViewport(0, 0, width, height);
			// 设置视口的宽高比, 注意视口的长宽比与近平面的长宽比需要相同, 否则显示内容会变形
			float ratio = (float) width / height;
			// 设置正交投影, 如果是透视投影, 就在这里使用透视投影
			if(isOrth){
				//设置正交投影
				MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);
			}else{
				//设置透视投影
	        	MatrixState.setProjectFrustum(-ratio*0.4f, ratio*0.4f, -1*0.4f, 1*0.4f, 1, 50);
			}

			// 设置摄像机位置
			MatrixState.setCamera(0, 0, 3f, 0, 0, 0f, 0f, 1.0f, 0.0f);
		}

		/**
		 * 创建时回调
		 * ① 设置北京颜色
		 * ② 创建绘制元素
		 * ③ 打开深度检测
		 */
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// 设置屏幕的背景颜色
			GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
			
			float distance = 0f;
			if(isOrth){
				distance = -1.0f;
			}else{
				distance = -1.0f;
			}
			
			// 创建六角星数组中得各个六角星
			for (int i = 0; i < ha.length; i++) {
				ha[i] = new SixPointedStar(ProjectionGLSurfaceView.this, 0.2f, 0.5f,
						distance * i);
			}
			// 打开深度检测
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		}
	}
}
