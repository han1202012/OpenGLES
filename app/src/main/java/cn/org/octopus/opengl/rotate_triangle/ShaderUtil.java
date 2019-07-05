package cn.org.octopus.opengl.rotate_triangle;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

/*
 * 这个工具类用来加载定点着色器与片元着色器
 */
public class ShaderUtil {
	
	/**
	 * 加载着色器方法
	 * 
	 * 流程 : 
	 * 
	 * ① 创建着色器
	 * ② 加载着色器脚本
	 * ③ 编译着色器
	 * ④ 获取着色器编译结果
	 * 
	 * @param shaderType 着色器类型,顶点着色器(GLES20.GL_FRAGMENT_SHADER), 片元着色器(GLES20.GL_FRAGMENT_SHADER)
	 * @param source 着色脚本字符串
	 * @return 返回的是着色器的引用, 返回值可以代表加载的着色器
	 */
	public static int loadShader(int shaderType , String source){
		//1.创建一个着色器, 并记录所创建的着色器的id, 如果id==0, 那么创建失败
		int shader = GLES20.glCreateShader(shaderType);
		if(shader != 0){
			//2.如果着色器创建成功, 为创建的着色器加载脚本代码
			GLES20.glShaderSource(shader, source);
			//3.编译已经加载脚本代码的着色器
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			//4.获取着色器的编译情况, 如果结果为0, 说明编译失败
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if(compiled[0] == 0){
				 Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
	             Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
	             //编译失败的话, 删除着色器, 并显示log
	             GLES20.glDeleteShader(shader);
	             shader = 0;
			}
		}
		return shader;
	}
	
	/**
	 * 检查每一步的操作是否正确
	 * 
	 * 使用GLES20.glGetError()方法可以获取错误代码, 如果错误代码为0, 那么就没有错误
	 * 
	 * @param op 具体执行的方法名, 比如执行向着色程序中加入着色器, 
	 * 		使glAttachShader()方法, 那么这个参数就是"glAttachShader"
	 */
	public static void checkGLError(String op){
		int error;
		//错误代码不为0, 就打印错误日志, 并抛出异常
		while( (error = GLES20.glGetError()) != GLES20.GL_NO_ERROR ){
			 Log.e("ES20_ERROR", op + ": glError " + error);
	         throw new RuntimeException(op + ": glError " + error);
		}
	}
	
	/**
	 * 创建着色程序
	 * 
	 * ① 加载顶点着色器
	 * ② 加载片元着色器
	 * ③ 创建着色程序
	 * ④ 向着色程序中加入顶点着色器
	 * ⑤ 向着色程序中加入片元着色器
	 * ⑥ 链接程序
	 * ⑦ 获取链接程序结果
	 * 
	 * @param vertexSource		定点着色器脚本字符串
	 * @param fragmentSource	片元着色器脚本字符串
	 * @return
	 */
	public static int createProgram(String vertexSource , String fragmentSource){
		//1. 加载顶点着色器, 返回0说明加载失败
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if(vertexShader == 0)
			return 0;
		//2. 加载片元着色器, 返回0说明加载失败
		int fragShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if(fragShader == 0)
			return 0;
		//3. 创建着色程序, 返回0说明创建失败
		int program = GLES20.glCreateProgram();
		if(program != 0){
			//4. 向着色程序中加入顶点着色器
			GLES20.glAttachShader(program, vertexShader);
			checkGLError("glAttachShader");
			//5. 向着色程序中加入片元着色器
			GLES20.glAttachShader(program, fragShader);
			checkGLError("glAttachShader");
			
			//6. 链接程序
			GLES20.glLinkProgram(program);
			int[] linkStatus = new int[1];
			//获取链接程序结果
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if(linkStatus[0] != GLES20.GL_TRUE){
				Log.e("ES20.ERROR", "链接程序失败 : ");
				Log.e("ES20.ERROR", GLES20.glGetProgramInfoLog(program));
				//如果链接程序失败删除程序
				GLES20.glDeleteProgram(program);
				program = 0;
			}			
		}
		return program;
	}
	
	/**
	 * 从assets中加载着色脚本
	 * 
	 * ① 打开assets目录中的文件输入流
	 * ② 创建带缓冲区的输出流
	 * ③ 逐个字节读取文件数据, 放入缓冲区
	 * ④ 将缓冲区中的数据转为字符串
	 * 
	 * @param fileName assets目录中的着色脚本文件名
	 * @param resources	应用的资源
	 * @return
	 */
	public static String loadFromAssetsFile(String fileName, Resources resources){
		String result = null;
		try {
			//1. 打开assets目录中读取文件的输入流, 相当于创建了一个文件的字节输入流
			InputStream is = resources.getAssets().open(fileName);
			int ch = 0;
			//2. 创建一个带缓冲区的输出流, 每次读取一个字节, 注意这里字节读取用的是int类型
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//3. 逐个字节读取数据, 并将读取的数据放入缓冲器中
			while((ch = is.read()) != -1){
				baos.write(ch);
			}
			//4. 将缓冲区中的数据转为字节数组, 并将字节数组转换为字符串
			byte[] buffer = baos.toByteArray();
			baos.close();
			is.close();
			result = new String(buffer, "UTF-8");
			result = result.replaceAll("\\r\\n", "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
