package com.padsurveysystem;

//测量点类
public class SurveyPoint {
	public int PointFlag; // 字段点类型
	public String Name; // 字段点名
	public String Code; // 字段代码
	public double X; // 字段X
	public double Y; // 字段Y
	public double Altitude; // 字段海 跋高
	public double Latitude; // 字段纬度
	public double Longitude;// 字段经度
	public double Elevation; // 字段GPS高

	public SurveyPoint() {
		PointFlag = 0;
		Name = "0";
		Code = "0";
		X = 0;
		Y = 0;
		Altitude = 0;
		Latitude = 0;
		Longitude = 0;
		Elevation = 0;
	}

	public SurveyPoint(int pointFlag, String pointName, String pointCode,
			double x, double y, double altitude, double latitude,
			double longitude, double elevation) {
		PointFlag = pointFlag;
		Name = pointName;
		Code = pointCode;
		X = x;
		Y = y;
		Altitude = altitude;
		Latitude = latitude;
		Longitude = longitude;
		Elevation = elevation;
	}

	public SurveyPoint(int pointFlag, String pointName, String pointCode,
			double x, double y, double z) {
		PointFlag = pointFlag;
		Name = pointName;
		Code = pointCode;
		if (pointFlag == 0) {
			X = x;
			Y = y;
			Altitude = z;
		}
		if (pointFlag == 1) {
			Latitude = x;
			Longitude = y;
			Elevation = z;
		}
	}
}
//线路测量点类
class LinePoint extends SurveyPoint{	
	public double Station; // 字段偏距
	public double Offset; // 字段里程
	public double Angle; // 字段转角度数
}