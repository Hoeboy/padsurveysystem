package com.padsurveysystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.widget.Toast;

//文件文件操作类
public class TextFile {

	public void Write(String FilePath, String Content) {
		// 如果filePath是传递过来的参数，可以做一个后缀名称判断； 没有指定的文件名没有后缀，则自动保存为.txt格式
		if (!FilePath.endsWith(".csv") && !FilePath.endsWith(".log"))
			FilePath += ".csv";
		// 保存文件
		File file = new File(FilePath);

		try {
			OutputStream outstream = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(Content);
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

}
