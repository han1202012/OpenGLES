package kim.hsl.opengl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kim.hsl.opengl.projection.OrthogonalProjectionActivity;
import kim.hsl.opengl.projection.ProjectionGLSurfaceView;
import kim.hsl.opengl.rotate_triangle.RotateTriangleActivity;

public class HomeActivity extends Activity {

	public static final String TAG = "octopus.MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onClick(View view) {
		int id = view.getId();

		switch (id) {
		case R.id.orhthogonal_projection:
			ProjectionGLSurfaceView.isOrth = true;
			Intent intent = new Intent(this, OrthogonalProjectionActivity.class);
			startActivity(intent);
			break;
		case R.id.rotate_triangle:
			intent = new Intent(this, RotateTriangleActivity.class);
			startActivity(intent);
			break;
		case R.id.flu_projection:
			ProjectionGLSurfaceView.isOrth = false;
			intent = new Intent(this, OrthogonalProjectionActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
