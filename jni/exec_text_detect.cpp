#include "text_detect.h"
#include <jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>

//#define APPNAME "TranslateText"

//namespace {
//	DetectText* toDetectTextNative(jlong detectPtr) {
//		return reinterpret_cast<DetectText*>(detectPtr);
//	}
//}

extern "C" {

//	JNIEXPORT jlong JNICALL Java_com_detecttext_DetectTextNative_create(JNIEnv* env, jobject jobj) {
//		DetectText* dt = new DetectText();
//		return reinterpret_cast<jlong>(dt);
//	}
//
//	JNIEXPORT void JNICALL Java_com_detecttext_DetectTextNative_destroy(JNIEnv* env, jobject jobj, jlong detectPtr) {
//		delete toDetectTextNative(detectPtr);
//	}

	JNIEXPORT jintArray JNICALL Java_com_texttranslator_core_DetectText_swtBoundingBoxes(
				JNIEnv* env, jobject jobj, jstring filePathOriginalStr, jstring filePathBWStr) {
			const char *filePathOriginal = env->GetStringUTFChars(filePathOriginalStr, 0);
			const char *filePathBW = env->GetStringUTFChars(filePathBWStr, 0);
			DetectText* dt = new DetectText();
			Mat nativeMat = imread(filePathOriginal, CV_LOAD_IMAGE_COLOR);
			vector<Rect> boundingBoxes = dt->getBoundingBoxes(nativeMat);

			jintArray result = env->NewIntArray(boundingBoxes.size() * 4);

			dt->createImgBW(filePathBW);

			if (result == NULL) {
				return NULL;
			}

			jint tmp_arr[boundingBoxes.size() * 4];

			int idx = 0;

			for (int i = 0; i < boundingBoxes.size(); i++) {
				tmp_arr[idx++] = boundingBoxes[i].x;
				tmp_arr[idx++] = boundingBoxes[i].y;
				tmp_arr[idx++] = boundingBoxes[i].width;
				tmp_arr[idx++] = boundingBoxes[i].height;
			}

			env->SetIntArrayRegion(result, 0, boundingBoxes.size() * 4, tmp_arr);

			return result;
		}
}
