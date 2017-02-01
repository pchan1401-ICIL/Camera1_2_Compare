package com.icil.camera1_2_compare;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by BRB_LAB on 2017-02-01.
 * Packaged : icil.com.camera2fov
 * Web : http://myandroidarchive.tistory.com
 */

public class PreviewCam1  implements SurfaceHolder.Callback {
    private String TAG = "Preview : ";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private float horizontalAngle;
    private float verticalAngle;
    private TextView mResult;


    PreviewCam1(SurfaceView surfaceView, TextView result) {
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        horizontalAngle = 0;
        verticalAngle = 0;
        mResult = result;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = openBackFacingCamera();
        mCamera.setDisplayOrientation(0);
        Camera.Parameters cameraParameter = mCamera.getParameters();
        horizontalAngle = (float) Math.toRadians(cameraParameter.getHorizontalViewAngle());
        verticalAngle = (float) Math.toRadians(cameraParameter.getVerticalViewAngle());
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        mCamera.startPreview();
        mResult.setText("Camera1 \nResult = " + "h : " + horizontalAngle + "      v : " + verticalAngle);
    }

    private Camera openBackFacingCamera() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        mCamera = camera;
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    public float getHorizontalAngle() {
        return horizontalAngle;
    }

    public float getVerticalAngle() {
        return verticalAngle;
    }
}
