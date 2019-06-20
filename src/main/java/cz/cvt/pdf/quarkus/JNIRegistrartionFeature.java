package cz.cvt.pdf.quarkus;

import org.graalvm.nativeimage.Feature;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.jni.JNIRuntimeAccess;

import sun.java2d.DefaultDisposerRecord;

@AutomaticFeature
class JNIRegistrationFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        try {
            JNIRuntimeAccess.register(
                    DefaultDisposerRecord.class.getDeclaredMethod("invokeNativeDispose", long.class, long.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}