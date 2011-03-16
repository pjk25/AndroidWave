// 
//  TestUtils.java
//  tests
//  
//  Created by Philip Kuryloski on 2011-03-10.
//  Copyright 2011 Philip Kuryloski. All rights reserved.
// 

package edu.berkeley.androidwave;

import android.app.Instrumentation;
import android.content.Context;
//import android.test.MoreAsserts;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import junit.framework.Assert;

public class TestUtils {
    
    /**
     * copyAssetToInternal
     *
     * used for copying fixtures to app storage to simulate downloaded data
     * only tested with two component destination paths
     */
    public static File copyAssetToInternal(Instrumentation instrumentation, String source, String dest)
        throws IOException {
        
        File targetFile = null;
        
        InputStream is = instrumentation.getContext().getAssets().open(source);
        
        Context targetContext = instrumentation.getTargetContext();
        String[] destComponents = dest.split("/", 2);
        OutputStream os = null;
        if (destComponents.length == 0) {
            // System.out.println("copyAssetToInternal -> creating "+dest);
            os = targetContext.openFileOutput(dest, Context.MODE_PRIVATE);
        } else {
            File dir = targetContext.getDir(destComponents[0], Context.MODE_PRIVATE);
            // System.out.println("copyAssetToInternal -> created "+dir);
            destComponents = dest.split("/");
            // System.out.println("copyAssetToInternal -> destComponents "+Arrays.toString(destComponents));
            targetFile = new File(dir, destComponents[destComponents.length-1]);
            if (targetFile.exists()) {
                System.out.print("TestUtils: copyAssetToInternal->Deleting existing file at "+targetFile+"...");
                if (targetFile.delete()) {
                    System.out.println(" done.");
                } else {
                    System.out.println(" fail.");
                }
            }
            // System.out.println("copyAssetToInternal -> targetFile = "+targetFile);
            os = new FileOutputStream(targetFile);
        }
        
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        is.close();
        os.close();
        
        return targetFile;
    }
    
    public static String[] arrayToStringArray(Object[] objects) {
        String[] strings = new String[objects.length];
        for (int i=0; i<objects.length; i++) {
            strings[i] = objects[i].toString();
        }
        return strings;
    }
    
    /**
     * Asserts that a given object instance {@code actualObject} responds to
     * an expected method signature {@code expectedMethodSignature}
     * 
     * For method signature syntax, @see Method#toString
     * Use of Method#toGenericString, which might result in simpler syntax,
     * appears to cause a result similar to this known bug:
     * http://code.google.com/p/android/issues/detail?id=6636
     * present in the old API levels.
     */
    public static void assertHasMethod(String expectedMethodSignature, Object actualObject) {
        Method[] methods = actualObject.getClass().getMethods();
        String[] methodSignatures = TestUtils.arrayToStringArray(methods);
        /*
        String[] methodSignatures = new String[methods.length];
        for (int i=0; i<methods.length; i++) {
            methodSignatures[i] = methods[i].toGenericString();
        }
         */
        Assert.assertTrue("Expected "+expectedMethodSignature+" method in class "+actualObject.getClass(), Arrays.asList(methodSignatures).contains(expectedMethodSignature));
    }
}