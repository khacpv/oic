package com.oic.sdk.io.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class ZipUtil {
	public static final String TAG = "ZipUtil";
	public static final int BUFFER_SIZE = 1024;

	public static void zip(String[] files, String zipFile, OnZipListener listener) throws IOException {
		BufferedInputStream origin = null;
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
		int size = 0;
		try {
			byte data[] = new byte[BUFFER_SIZE];

			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				try {
					ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
						out.write(data, 0, count);
						size += BUFFER_SIZE;
					}
				} finally {
					origin.close();
				}
			}

		} finally {
			out.close();
			if (listener != null) {
				listener.onZipComplete(size);
			}
		}
	}
	
	/**
	 * un-zip file in another Thread
	 * */
	public static void unzip(final InputStream zipFile,final String destination,final OnZipListener listener) {
		new Thread(){
			@Override
			public void run() {
				try {
			        ZipInputStream zipStream = new ZipInputStream(zipFile);
			        ZipEntry zEntry = null;
			        int unzipByte = 0;
			        while ((zEntry = zipStream.getNextEntry()) != null) {

			            if (zEntry.isDirectory()) {
			            	File unzipFile = new File(destination+File.separator+zEntry.getName());
							if (!unzipFile.isDirectory()) {
								unzipFile.mkdirs();
							}
			            } else {
			                FileOutputStream fout = new FileOutputStream(
			                        destination + File.separator + zEntry.getName());
			                BufferedOutputStream bufout = new BufferedOutputStream(fout);
			                byte[] buffer = new byte[1024];
			                int read = 0;
			                while ((read = zipStream.read(buffer)) != -1) {
			                    bufout.write(buffer, 0, read);
			                    unzipByte++;
								if (listener != null) {
									listener.onProgress(unzipByte);
								}
			                }

			                zipStream.closeEntry();
			                bufout.close();
			                fout.close();
			            }
			        }
			        zipStream.close();
			        Log.d("Unzip", "Unzipping complete. path :  " + destination);
			        if (listener != null) {
						listener.onZipComplete(unzipByte);
					}
			        
			    } catch (Exception e) {
			        Log.d("Unzip", "Unzipping failed");
			        e.printStackTrace();
			    }
			}
		}.start();
	}

	public static interface OnZipListener {
		public void onProgress(int unzipByte);

		public void onZipComplete(int totalSize);
	}
}
