package com.padsurveysystem;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class StringConver {
	// 按指定的分隔符分隔字符串
	public static List<String> GetSubString(String InputString, String Separator) {
		// Log.d("Line", InputString);///log debug
		List<String> ListString = new ArrayList();
		int StartIndex = 0;
		int EndIndex = 0;
		int iL = InputString.length();
		for (int i = 0; i < iL; i++) {
			if (InputString.substring(i, i + 1) == Separator) {

			}
		}
		while (true) {
			EndIndex = InputString.indexOf(Separator, StartIndex);

			if (EndIndex == -1) {
				break;
			}
			ListString.add(InputString.substring(StartIndex, EndIndex));
			StartIndex = EndIndex + 1;
		}
		ListString.add(InputString.substring(StartIndex, InputString.length()));

		return ListString;
	}
}
