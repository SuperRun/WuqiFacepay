package com.wuqi.facepay.manager;

import android.util.Log;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.wuqi.facepay.callback.FaceDetectCallBack;
import com.wuqi.facepay.model.LivenessModel;
import com.wuqi.facepay.model.SingleBaseConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Time: 2019/1/25
 * Author: v_chaixiaogang
 * rgb单目检测管理类
 */
public class FaceTrackManager {

    private String TAG = "FaceTrackManager";

    private static volatile FaceTrackManager instance = null;

    private ExecutorService es;
    private Future future;
    private boolean isAliving;
    private boolean isQualityControl = false;
    private boolean isDetect = true;

    private FaceDetectCallBack mFaceDetectCallBack;

    public FaceDetectCallBack getFaceDetectCallBack() {
        return mFaceDetectCallBack;
    }

    public void setFaceDetectCallBack(FaceDetectCallBack faceDetectCallBack) {
        mFaceDetectCallBack = faceDetectCallBack;
    }

    public boolean isQualityControl() {
        return isQualityControl;
    }

    public void setQualityControl(boolean qualityControl) {
        isQualityControl = qualityControl;
    }

    public boolean isAliving() {
        return isAliving;
    }

    public void setAliving(boolean aliving) {
        isAliving = aliving;
    }

    public boolean isDetect() {
        return isDetect;
    }

    public void setDetect(boolean detect) {
        isDetect = detect;
    }

    public FaceTrackManager() {
        es = Executors.newSingleThreadExecutor();
    }

    public static FaceTrackManager getInstance() {
        synchronized (FaceTrackManager.class) {
            if (instance == null) {
                instance = new FaceTrackManager();
            }
        }
        return instance;
    }

    public void faceTrack(final byte[] argb, final int width, final int height,
                          final FaceDetectCallBack faceDetectCallBack) {

        if (future != null && !future.isDone()) {
            Log.d(TAG, "faceTrack: DONE");
            return;
        }
        Log.d(TAG, "faceTrack: NULL");
        future = es.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "faceDataDetect");
                faceDataDetect(argb, width, height, faceDetectCallBack);
            }
        });
    }

    /**
     * 人脸检测
     *
     * @param argb
     * @param width
     * @param height
     * @param faceDetectCallBack
     */
    private void faceDataDetect(final byte[] argb, int width, int height, FaceDetectCallBack faceDetectCallBack) {
        Log.d(TAG, "人脸检测1");
        LivenessModel livenessModel = new LivenessModel();

        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(argb, height, width,
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_420,
                SingleBaseConfig.getBaseConfig().getDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorRGB());
        Log.d(TAG, "人脸检测2");
        livenessModel.setBdFaceImageInstance(rgbInstance);
        long startTime = System.currentTimeMillis();
        Log.d(TAG, String.valueOf(rgbInstance));
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect()
                .track(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
        Log.d(TAG, String.valueOf(faceInfos));
        livenessModel.setRgbDetectDuration(System.currentTimeMillis() - startTime);
        // getImage() 获取送检图片
        livenessModel.setBdFaceImageInstance(rgbInstance.getImage());

        Log.d(TAG, faceInfos.toString());

        if (faceInfos != null && faceInfos.length > 0) {
            Log.d(TAG, "faceDataDetect: if");
            livenessModel.setTrackFaceInfo(faceInfos);
            FaceInfo faceInfo = faceInfos[0];
            livenessModel.setFaceInfo(faceInfo);
            livenessModel.setLandmarks(faceInfo.landmarks);
            // 质量检测，针对模糊度、遮挡、角度
            if (onQualityCheck(livenessModel, faceDetectCallBack)) {
                Log.d(TAG, "faceDataDetect: if if");
                // 活体检测
                if (isAliving) {
                    startTime = System.currentTimeMillis();
                    float rgbScore = FaceSDKManager.getInstance().getFaceLiveness().silentLive(
                            BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB,
                            rgbInstance, faceInfo.landmarks);
                    livenessModel.setRgbLivenessScore(rgbScore);
                    livenessModel.setRgbLivenessDuration(System.currentTimeMillis() - startTime);
                }
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                rgbInstance.destory();
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(livenessModel);
                }
            } else {
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                rgbInstance.destory();
            }

        } else {
            Log.d(TAG, "faceDataDetect: else");
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            rgbInstance.destory();
            if (faceDetectCallBack != null) {
                Log.d(TAG, "faceDataDetect: else if");
                faceDetectCallBack.onTip(0, "未检测到人脸");
                faceDetectCallBack.onFaceDetectCallback(null);
            }
        }
    }


    /**
     * 质量检测
     *
     * @param livenessModel
     * @param faceDetectCallBack
     * @return
     */
    public boolean onQualityCheck(final LivenessModel livenessModel,
                                  final FaceDetectCallBack faceDetectCallBack) {

        if (!SingleBaseConfig.getBaseConfig().isQualityControl()) {
            return true;
        }

        if (livenessModel != null && livenessModel.getFaceInfo() != null) {

            // 角度过滤
            if (Math.abs(livenessModel.getFaceInfo().yaw) > SingleBaseConfig.getBaseConfig().getYaw()) {
                faceDetectCallBack.onTip(-1, "人脸左右偏转角超出限制");
                return false;
            } else if (Math.abs(livenessModel.getFaceInfo().roll) > SingleBaseConfig.getBaseConfig().getRoll()) {
                faceDetectCallBack.onTip(-1, "人脸平行平面内的头部旋转角超出限制");
                return false;
            } else if (Math.abs(livenessModel.getFaceInfo().pitch) > SingleBaseConfig.getBaseConfig().getPitch()) {
                faceDetectCallBack.onTip(-1, "人脸上下偏转角超出限制");
                return false;
            }


            // 模糊结果过滤
            float blur = livenessModel.getFaceInfo().bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                faceDetectCallBack.onTip(-1, "图片模糊");
                return false;
            }

            // 光照结果过滤
            float illum = livenessModel.getFaceInfo().illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                faceDetectCallBack.onTip(-1, "图片光照不通过");
                return false;
            }

            // 遮挡结果过滤
            if (livenessModel.getFaceInfo().occlusion != null) {
                BDFaceOcclusion occlusion = livenessModel.getFaceInfo().occlusion;

                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye()) {
                    // 左眼遮挡置信度
                    faceDetectCallBack.onTip(-1, "左眼遮挡");
                } else if (occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    // 右眼遮挡置信度
                    faceDetectCallBack.onTip(-1, "右眼遮挡");
                } else if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    // 鼻子遮挡置信度
                    faceDetectCallBack.onTip(-1, "鼻子遮挡");
                } else if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    // 嘴巴遮挡置信度
                    faceDetectCallBack.onTip(-1, "嘴巴遮挡");
                } else if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek()) {
                    // 左脸遮挡置信度
                    faceDetectCallBack.onTip(-1, "左脸遮挡");
                } else if (occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    // 右脸遮挡置信度
                    faceDetectCallBack.onTip(-1, "右脸遮挡");
                } else if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    // 下巴遮挡置信度
                    faceDetectCallBack.onTip(-1, "下巴遮挡");
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
