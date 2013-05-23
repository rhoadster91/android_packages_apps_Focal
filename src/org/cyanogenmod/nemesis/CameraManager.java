/*
 * Copyright (C) 2013 The CyanogenMod Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.nemesis;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class is responible for interacting with the Camera HAL.
 * It provides easy open/close, helper methods to set parameters or 
 * toggle features, etc. in an asynchronous fashion.
 */
public class CameraManager {
    private final static String TAG = "CameraManager";
    
    public CameraPreview mPreviewFront;
    public CameraPreview mPreviewBack;
    private Camera mCameraFront;
    private Camera mCameraBack;
    
    public CameraManager(Context context) {
        mPreviewFront = new CameraPreview(context);
        mPreviewBack = new CameraPreview(context);
    }
    
    /**
     * Opens the camera and show its preview in the preview
     * @param facing
     * @return
     */
    public boolean open(int facing) {
        /*if (mCamera != null) {
            // Close the previous camera
            mPreview.notifyCameraChanged(null);
            mCamera.release();
            mCamera = null;
        }
        */
        try {
            mCameraFront = Camera.open(facing);
            //mCameraBack = Camera.open(1);
        }
        catch (Exception e) {
            Log.e(TAG, "Error while opening cameras: " + e.getMessage());
            return false;
        }
        
        mPreviewFront.notifyCameraChanged(mCameraFront);
        //mPreviewBack.notifyCameraChanged(mCameraBack);
        
        return true;
    }
    
    
    /**
     * The CameraPreview class handles the Camera preview feed
     * and setting the surface holder.
     */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private final static String TAG = "CameraManager.CameraPreview";
        
        private SurfaceHolder mHolder;
        private Camera mCamera;
        

        public CameraPreview(Context context) {
            super(context);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
        }
        
        public void notifyCameraChanged(Camera camera) {
            mCamera = camera;
            
            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    Log.e(TAG, "Error setting camera preview: " + e.getMessage());
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            if (mCamera == null)
                return;
            
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.e(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
              // preview surface does not exist
              return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
              // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}