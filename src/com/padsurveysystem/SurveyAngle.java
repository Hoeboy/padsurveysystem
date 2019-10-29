package com.padsurveysystem;

import java.text.DecimalFormat;

import android.util.Log;

public class SurveyAngle {
	private double mAngle;

	public SurveyAngle() {
		mAngle = 0;
	}

	public SurveyAngle(double Radian) {
		mAngle = Radian;
	}

	public void valueOfRadian(double Radian) {
		mAngle = Radian;
	}
	public void valueOfRadian(String Radian) {
		mAngle = Double.valueOf(Radian);
	}
	public void valueOfDMS(double DMS) {
		double AngleDMS = Math.abs(DMS);
		double RadianAngle;
		double Degree, Minute, Second;
		Degree = (int) AngleDMS;
		Minute = ((int) ((AngleDMS - Degree) * 100.0));
		Second = (((AngleDMS - Degree) * 100.0 - Minute) * 100.0);
		RadianAngle = (Degree + Minute / 60.0 + Second / 3600.0) * Math.PI
				/ 180.0;
		if (DMS < 0) {
			mAngle = 0 - RadianAngle;
		} else {
			mAngle = RadianAngle;
		}
	}
	public void valueOfDMS(String Angle) {
		
		double DMS=Double.valueOf(Angle);
		double AngleDMS = Math.abs(DMS);
		double RadianAngle;
		double Degree, Minute, Second;
		Degree = (int) AngleDMS;
		Minute = ((int) ((AngleDMS - Degree) * 100.0));
		Second = (((AngleDMS - Degree) * 100.0 - Minute) * 100.0);
		RadianAngle = (Degree + Minute / 60.0 + Second / 3600.0) * Math.PI
				/ 180.0;
		if (DMS < 0) {
			mAngle = 0 - RadianAngle;
		} else {
			mAngle = RadianAngle;
		}
	}
	public void valueOfDEG(double DEG) {
		mAngle = DEG * Math.PI / 180.0;
	}
	public void valueOfDEG(String Angle) {
		double DEG=Double.valueOf(Angle);
		mAngle = DEG * Math.PI / 180.0;
	}
	public double GetRadian() {
		return mAngle;
	}

	public double GetDMS() {

		return SurveyMath.RadianToDMS(mAngle);
	}

	public double GetDEG() {
		return SurveyMath.RadianToDEG(mAngle);
	}

	public int GetSubDegree() {
		double DEG = SurveyMath.RadianToDEG(mAngle);
		double AngleDEG = Math.abs(DEG);
		int Degree = (int) AngleDEG;
		if (DEG < 0) {
			return 0 - Degree;
		} else {
			return Degree;
		}

	}

	public int GetSubMinute() {
		double DEG = SurveyMath.RadianToDEG(mAngle);
		double AngleDEG = Math.abs(DEG);
		int Degree, Minute;
		Degree = (int) AngleDEG;
		Minute = (int) ((AngleDEG - Degree) * 60);
		return Minute;
	}

	public String GetSubSecond(int digits) {

		String d = "0";
		if (digits == 0) {
			d = "0";
		} else {
			d="0.";
			for (int i = 0; i < digits; i++) {
				d = d + "0";
			}
		}
		
		DecimalFormat df = new DecimalFormat(d); // 创建一个格式化类f
		double DEG = SurveyMath.RadianToDEG(mAngle);
		double AngleDEG = Math.abs(DEG);
		double Degree, Minute, Second;
		Degree = (int) AngleDEG;
		Minute = (int) ((AngleDEG - Degree) * 60.0);
		Second = ((AngleDEG - Degree) * 60.0 - Minute) * 60;
		return df.format(Second);
	}

}