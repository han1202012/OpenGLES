package kim.hsl.opengl.projection;
import kim.hsl.opengl.R;
import kim.hsl.opengl.utils.L;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Activity 显示 OpenGL 流程
 * ① 设置屏幕参数
 * ② 初始化 GLSurfaceView
 * ③ 设置显示 GLSurface
 * 
 * 在onResume 和 onPause 中分别调用 GLSurfaceView 的 onResume 和 onPause 方法
 * @author octopus
 *
 */
public class OrthogonalProjectionActivity extends Activity {
	
	public static final String TAG = "octopus.OrthogonalProjectionActivity";
	
	private ProjectionGLSurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);         
        //① 设置屏幕参数
        requestWindowFeature(Window.FEATURE_NO_TITLE); 						//设置无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  	//设置全屏充满
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	//设置屏幕为竖屏
		
		//② 初始化 GLSurfaceView
        mGLSurfaceView = new ProjectionGLSurfaceView(this);
        
		//③ 设置显示 GLSurfaceView
		setContentView(mGLSurfaceView);					//设置界面显示该 GLSurfaceView
        mGLSurfaceView.requestFocus();					//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);	//设置为可触控  
        
    }

    public void onClick(View view) {
    	L.i(TAG, "点击了按钮");
    	int id = view.getId();
    	switch (id) {
		case R.id.bt_switch_orth:
			ProjectionGLSurfaceView.isOrth = true;
			break;
		case R.id.bt_switch_flu:
			ProjectionGLSurfaceView.isOrth = false;
			break;

		default:
			break;
		}
	}
    
    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();	// GLSurfaceView 根据 Acivity 周期变化
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();	// GLSurfaceView 根据 Acivity 周期变化
    }    
}



