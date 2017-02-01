package com.icil.camera1_2_compare;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SizeF;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by BRB_LAB on 2017-02-01.
 * Packaged : icil.com.camera2fov
 * Web : http://myandroidarchive.tistory.com
 */

public class MainActivity extends AppCompatActivity {
    private TextureView mCameraTextureView2;
    private SurfaceView mCameraSurfaceView1;
    private PreviewCam2 mPreviewCam2;
    private PreviewCam1 mPreviewCam1;
    private TextView mResult2;
    private TextView mResult1;

    Activity mainActivity = this;

    private static final String TAG = "MAINACTIVITY";

    static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mResult1 = (TextView)findViewById(R.id.result1);
        mResult2 = (TextView)findViewById(R.id.result2);

//        mCameraTextureView2 = (TextureView) findViewById(R.id.cameraTextureView2);
        mCameraSurfaceView1 = (SurfaceView) findViewById(R.id.cameraSurfaceView1);
//        mPreviewCam2 = new PreviewCam2(this, mCameraTextureView2);

        int permissionCamera = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permissionCamera = checkSelfPermission(Manifest.permission.CAMERA);
            if(permissionCamera == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.REQUEST_CAMERA);
            } else {
                mPreviewCam1 = new PreviewCam1(mCameraSurfaceView1,mResult1);
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                calculateFOV(manager);
                Log.d(TAG, "h : " + horizontalAngle + "      v : " + verticalAngle);
            }
        }
    }

    float horizontalAngle;
    float verticalAngle;

    private void calculateFOV(CameraManager cManager) {
        try {
            for (final String cameraId : cManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) {
                    Float maxFocus = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
                    if(maxFocus == null)
                        maxFocus = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];
                    SizeF size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                    float w = size.getWidth();
                    float h = size.getHeight();
                    horizontalAngle = (float) (2*Math.atan(w/(maxFocus*2)));
                    verticalAngle = (float) (2*Math.atan(h/(maxFocus*2)));
                    //because of landscape orientaion horizontal angle is larger than vertical angle
                    mResult2.setText("Camera2 \nResult = " + "h : " + horizontalAngle + "      w : " + verticalAngle);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            mPreviewCam1 = new PreviewCam1(mCameraSurfaceView1,mResult1);
                            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                            calculateFOV(manager);
                            Log.d(TAG, "h : " + horizontalAngle + "      v : " + verticalAngle);
                            Log.d(TAG, "mPreview set");
                        } else {
                            Toast.makeText(this, "Should have camera permission to run", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPreviewCam2.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mPreviewCam2.onPause();
    }
}
