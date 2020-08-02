package kim.hsl.opengl.rotate_triangle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class RotateTriangleActivity extends Activity {

	private TriangleView triangleView;
	
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置界面显示为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //创建OpenGL的显示界面
        triangleView = new TriangleView(this);
        triangleView.requestFocus();
        triangleView.setFocusableInTouchMode(true);
        //将OpenGL显示界面设置给Activity
        setContentView(triangleView);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	triangleView.onResume();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	triangleView.onPause();
    }
    
}
