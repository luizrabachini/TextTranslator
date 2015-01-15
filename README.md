TextTranslator Android App
==========================

Android application able to automatically translate text pictures captured by smartphone camera. The result image of process is similar to original, but contain the translation result in the same location of original text.


Third Party Resources
---------------------

- Text Detection:
Is based in work of Menglong Zhu on Literate PR2 architecture. A Android application, with contribution of Andrei Nagornyi, is available [here](https://github.com/dreamdragon/text-detection).

- Text Extraction:
The character recognition is executed by [Tesseract OCR](https://code.google.com/p/tesseract-ocr/), created by Ray Smith. To embed it in the application was used [tess-two](https://github.com/rmtheis/tess-two), developed by Robert Theis.

- Text Translation:
To translate text is used MyMemory API, property of Translated S.r.l, available [here](http://mymemory.translated.net/doc/spec.php) (free and anonymous usage is limited to 100 requests per day. Verify the [Service Terms and Conditions of Use](http://mymemory.translated.net/doc/en/tos.php)).

Another resources documented in source code.


Installation
------------

1. Install [Eclipse IDE and ADT](http://developer.android.com/sdk/installing/installing-adt.html);

2. Install [Android NDK](http://developer.android.com/tools/sdk/ndk/index.html);

3. Download [OpenCV Android SDK](http://sourceforge.net/projects/opencvlibrary/files/opencv-android/2.4.9/OpenCV-2.4.9-android-sdk.zip/download) and [configure](http://docs.opencv.org/doc/tutorials/introduction/android_binary_package/android_dev_intro.html);

4. Check paths in Android.mk (jni > Android.mk);

5. Clone [tess-two](https://github.com/dreamdragon/text-detection) and check the project dependency (Project Properties > Android > Library);

A Nexus 4 device has been utilized in the tests to verify system functionalities. This [article](http://developer.android.com/tools/device.html) describes how to configure hardware devices to run Android applications.

The current minimum SDK required is 14 (Android 4.0 Ice Cream Sandwich).


Some Images of App
------------------

<p align="center">
  <img src="http://luizrabachini.com/media/projects/TextTranslator/app_01.png" alt="Image capture interface"/><br><br>
  <img src="http://luizrabachini.com/media/projects/TextTranslator/app_02.png" alt="Result of translation"/>
</p>