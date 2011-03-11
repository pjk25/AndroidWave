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
import java.io.*;

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
    
}