package kim.hsl.opengl.projection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * 单个六角星元素
 * 
 * @author octopus
 *
 */
public class SixPointedStar {
	int mProgram; 								// 自定义渲染管线着色器程序id

	static float[] mMMatrix = new float[16]; 	// 具体物体的3D变换矩阵，包括旋转、平移、缩放
	int muMVPMatrixHandle; 						// 总变换矩阵引用
	
	int maPositionHandle; 						// 顶点位置属性引用
	int maColorHandle; 							// 顶点颜色属性引用
	
	String mVertexShader; 						// 顶点着色器代码脚本
	String mFragmentShader; 					// 片元着色器代码脚本
	
	FloatBuffer mVertexBuffer; 					// 顶点坐标数据缓冲
	FloatBuffer mColorBuffer; 					// 顶点着色数据缓冲
	
	int vCount = 0;								// 顶点个数
	public float yAngle = 0; 							// 绕y轴旋转的角度
	public float xAngle = 0; 							// 绕z轴旋转的角度
	final float UNIT_SIZE = 1;

	public SixPointedStar(ProjectionGLSurfaceView mv, float r, float R, float z) {
		// 调用初始化顶点数据的initVertexData方法
		initVertexData(R, r, z);
		// 调用初始化着色器的intShader方法
		initShader(mv);
	}

	/**
	 * 自定义初始化顶点数据的initVertexData方法
	 * @param R 外圆半径, 最外面6个点组成的圆
	 * @param r 内圆半径, 最里面6个点组成的圆, 6个凹槽处的点
	 * @param z 深度
	 */
	public void initVertexData(float R, float r, float z) {
		List<Float> flist = new ArrayList<Float>();
		float tempAngle = 360 / 6;
		// 每 60 度绘制一个四边形, 每个四边形由 2 个三角形组成, 箭头形的平行四边形
		for (float angle = 0; angle < 360; angle += tempAngle) {
			// 第一个三角形, (angle = 60度时, 这是处于 60 ~ 90度的三角形)
			// 第一个中心点, 正中心的点
			flist.add(0f);	//屏幕中心
			flist.add(0f);	//屏幕中心
			flist.add(z);	//深度, z轴, 垂直于屏幕
			// 第二个点, (angle = 60度时 第一象限 60度 右上的点)
			flist.add((float) (R * UNIT_SIZE * Math.cos(Math.toRadians(angle))));	// 公式 : R / x = cos60, x = R * cos60
			flist.add((float) (R * UNIT_SIZE * Math.sin(Math.toRadians(angle))));	// 公式 : R / y = cos60, y = R * sin60
			flist.add(z);	//深度
			// 第三个点, 顺时针方向的三角形的另一个点
			flist.add((float) (r * UNIT_SIZE * Math.cos(Math.toRadians(angle		
					+ tempAngle / 2))));
			flist.add((float) (r * UNIT_SIZE * Math.sin(Math.toRadians(angle
					+ tempAngle / 2))));
			flist.add(z);

			// 第二个三角形
			// 第一个中心点, 最中心的点
			flist.add(0f);
			flist.add(0f);
			flist.add(z);
			// 第二个点, (angle = 60度时, 这是处于 90 ~ 120 的三角形)
			flist.add((float) (r * UNIT_SIZE * Math.cos(Math.toRadians(angle
					+ tempAngle / 2))));
			flist.add((float) (r * UNIT_SIZE * Math.sin(Math.toRadians(angle
					+ tempAngle / 2))));
			flist.add(z);
			// 第三个点
			flist.add((float) (R * UNIT_SIZE * Math.cos(Math.toRadians(angle
					+ tempAngle))));
			flist.add((float) (R * UNIT_SIZE * Math.sin(Math.toRadians(angle
					+ tempAngle))));
			flist.add(z);
		}
		
		//顶点个数, 集合个数 / 3
		vCount = flist.size() / 3;
		//创建一个顶点数组, 大小为顶点集合的大小, 将顶点数组的元素拷贝到顶点集合中
		float[] vertexArray = new float[flist.size()];
		for (int i = 0; i < vCount; i++) {
			vertexArray[i * 3] = flist.get(i * 3);
			vertexArray[i * 3 + 1] = flist.get(i * 3 + 1);
			vertexArray[i * 3 + 2] = flist.get(i * 3 + 2);
		}
		
		//创建一个字节数组缓冲, 大小为 顶点个数 * 4
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * 4);
		// 设置字节顺序为本地操作系统顺序
		vbb.order(ByteOrder.nativeOrder()); 
		//将 byte 缓冲 转为 float 缓冲, 赋值给 顶点数据缓冲
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertexArray);
		//设置缓冲区的起始位置
		mVertexBuffer.position(0);

		/*
		 * 下面是初始化顶点颜色数据
		 */
		
		//共有 vCount 个顶点, 每个顶点颜色值是 4个分别是 RGBA
		float[] colorArray = new float[vCount * 4];
		//中心点设置一个颜色, 其它点设置一个颜色
		for (int i = 0; i < vCount; i++) {
			if (i % 3 == 0) {// 中心点为白色
				colorArray[i * 4] = 1;
				colorArray[i * 4 + 1] = 1;
				colorArray[i * 4 + 2] = 1;
				colorArray[i * 4 + 3] = 0;
			} else {// 边上的点为淡蓝色
				colorArray[i * 4] = 0.45f;
				colorArray[i * 4 + 1] = 0.75f;
				colorArray[i * 4 + 2] = 0.75f;
				colorArray[i * 4 + 3] = 0;
			}
		}
		
		ByteBuffer cbb = ByteBuffer.allocateDirect(colorArray.length * 4);
		cbb.order(ByteOrder.nativeOrder()); // 设置字节顺序为本地操作系统顺序
		//将颜色Byte缓冲转为 Float缓冲
		mColorBuffer = cbb.asFloatBuffer();
		//将颜色缓冲数据放入 颜色数据缓冲成员变量中
		mColorBuffer.put(colorArray);
		mColorBuffer.position(0);
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

	}

	/**
	 * 初始化着色器
	 * ① 加载顶点着色器与片元着色器脚本
	 * ② 基于加载的着色器创建着色程序
	 * ③ 根据着色程序获取 顶点属性引用 顶点颜色引用 总变换矩阵引用
	 * @param mv
	 */
	public void initShader(ProjectionGLSurfaceView mv) {
		/* 
         * mVertextShader是顶点着色器脚本代码 
         * 调用工具类方法获取着色器脚本代码, 着色器脚本代码放在assets目录中 
         * 传入的两个参数是 脚本名称 和 应用的资源 
         * 应用资源Resources就是res目录下的那写文件 
         */ 
		
		//① 加载顶点着色器的脚本内容
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_projection.sh",
				mv.getResources());
		//② 加载片元着色器的脚本内容
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_projection.sh",
				mv.getResources());
		//③ 基于顶点着色器与片元着色器创建程序, 传入顶点着色器脚本 和 片元着色器脚本 注意顺序不要错
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		
		/* 
         * 从着色程序中获取 属性变量 顶点坐标(颜色)数据的引用 
         * 其中的"aPosition"是顶点着色器中的顶点位置信息 
         * 其中的"aColor"是顶点着色器的颜色信息 
         */  
		
		//④ 获取程序中顶点位置属性引用id
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		//⑤ 获取程序中顶点颜色属性引用id
		maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
		//⑥ 获取程序中总变换矩阵引用id
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	/**
	 * 六角星绘制自身方法
	 * 
	 * ① 设置绘制使用的着色程序
	 * ② 初始化总变换矩阵
	 * ③ 设置位移
	 * ④ 设置旋转
	 * ⑤ 应用最终变换矩阵
	 * ⑥ 指定顶点与颜色位置缓冲数据
	 * ⑦ 开始绘制
	 */
	public void drawSelf() {
		// 制定使用某套shader程序
		GLES20.glUseProgram(mProgram);
		// 初始化变换矩阵, 第二参数是矩阵起始位, 第三参数 旋转的角度, 四五六参数 旋转的轴
		Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
		// 设置沿Z轴正向位移1
		Matrix.translateM(mMMatrix, 0, 0, 0, 1);
		// 设置绕y轴旋转
		Matrix.rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
		// 设置绕z轴旋转
		Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
		// 将最终变换矩阵传入shader程序
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(mMMatrix), 0);
		// 为画笔指定顶点位置数据
		GLES20.glVertexAttribPointer(maPositionHandle, 	// 顶点位置数据引用
				3, 										// 每 3 个元素代表一个坐标
				GLES20.GL_FLOAT,						// 坐标的单位是浮点型
				false, 									// 
				3 * 4, 									// 每组数据有多少字节
				mVertexBuffer);							// 顶点数据缓冲区
		// 为画笔指定顶点着色数据
		GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
				4 * 4, mColorBuffer);
		// 允许顶点位置数据数组
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maColorHandle);
		// 绘制六角星
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}
}
