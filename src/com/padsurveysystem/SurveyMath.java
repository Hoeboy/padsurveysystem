package com.padsurveysystem;

import java.text.DecimalFormat;

import android.util.Log;

public class SurveyMath {
	final static double ZERO = 0.00000001;// 定义最小值

	// 将60进度角度转换为十进制
	public static double DMSToDEG(double DMS) {
		double AngleDMS = Math.abs(DMS);
		double AngleDEG;
		double Degree, Minute, Second;
		Degree = (int) AngleDMS;
		Minute = ((int) ((AngleDMS - Degree) * 100.0));
		Second = (((AngleDMS - Degree) * 100.0 - Minute) * 100.0);
		AngleDEG = Degree + Minute / 60.0 + Second / 3600.0;
		if (DMS < 0) {
			return 0 - AngleDEG;
		} else {
			return AngleDEG;
		}

	}

	// 将十进制角度转换为六十进制角度
	public static double DEGToDMS(double DEG) {
		double AngleDEG = Math.abs(DEG);
		double AngleDMS;
		double Degree, Minute, Second;
		Degree = (int) AngleDEG;
		Minute = (int) ((AngleDEG - Degree) * 60.0);
		Second = ((AngleDEG - Degree) * 60.0 - Minute) * 60;
		AngleDMS = Degree + Minute / 100.0 + Second / 10000.0;
		if (DEG < 0) {
			return 0 - AngleDMS;
		} else {
			return AngleDMS;
		}

	}

	/** 将弧度转换为度分秒格式的角度 */
	public static double RadianToDMS(double AngleRadian) {
		double AngleDEG;
		AngleDEG = AngleRadian * 180.0 / Math.PI;
		return DEGToDMS(AngleDEG);
	}

	/** 将弧度转换为以度为单位的角度 */
	public static double RadianToDEG(double AngleRadian) {
		double AngleDEG;
		AngleDEG = AngleRadian * 180.0 / Math.PI;
		return AngleDEG;
	}

	/** 将度分秒格式的角度转换为弧度 */
	public static double DMSToRadian(double DMS) {
		double AngleDMS = Math.abs(DMS);
		double RadianAngle;
		double Degree, Minute, Second;
		Degree = (int) AngleDMS;
		Minute = ((int) ((AngleDMS - Degree) * 100.0));
		Second = (((AngleDMS - Degree) * 100.0 - Minute) * 100.0);
		RadianAngle = (Degree + Minute / 60.0 + Second / 3600.0) * Math.PI
				/ 180.0;
		if (DMS < 0) {
			return 0 - RadianAngle;
		} else {
			return RadianAngle;
		}

	}

	/** 将以度为单位的角度转换为弧度 */
	public static double DEGToRadian(double AngleDEG) {
		return AngleDEG * Math.PI / 180.0;
	}
    /**计算平距*/
    public static double GetDistance(SurveyPoint StartPoint, SurveyPoint EndPoint)
    {
        return Math.sqrt((StartPoint.X - EndPoint.X) * (StartPoint.X - EndPoint.X)
               + (StartPoint.Y - EndPoint.Y) * (StartPoint.Y - EndPoint.Y));
    }
    /**计算方位角,角度单位为弧度*/
    public static double GetAzimuth(SurveyPoint StartPoint, SurveyPoint EndPoint)
    {
        double doubAng01, E0, N0, E1, N1;
        E0 = StartPoint.X;
        N0 = StartPoint.Y;
        E1 = EndPoint.X;
        N1 = EndPoint.Y;
        doubAng01 = 0;
        if ((E1 - E0) > 0 && (N1 - N0) > 0)
        {
            doubAng01 = Math.atan((Math.abs((E1 - E0) / (N1 - N0))));
        }
        if ((E1 - E0) > 0 && (N1 - N0) < 0)
        {
            doubAng01 = Math.PI - Math.atan(Math.abs((E1 - E0) / (N1 - N0)));
        }
        if ((E1 - E0) < 0 && (N1 - N0) < 0)
        {
            doubAng01 = Math.PI + Math.atan(Math.abs((E1 - E0) / (N1 - N0)));
        }
        if ((E1 - E0) < 0 && (N1 - N0) > 0)
        {
            doubAng01 = 2 * Math.PI - Math.atan(Math.abs((E1 - E0) / (N1 - N0)));
        }
        if (Math.abs(E1 - E0) < ZERO && N1 > N0)
        {
            doubAng01 = 0;
        }
        if (Math.abs(E1 - E0) < ZERO && N1 < N0)
        {
            doubAng01 = Math.PI;
        }
        if (Math.abs(N1 - N0) < ZERO && E1 > E0)
        {
            doubAng01 = Math.PI / 2;
        }
        if (Math.abs(N1 - N0) < ZERO && E1 < E0)
        {
            doubAng01 = 1.5 * Math.PI;
        }
        return doubAng01;
    }
/**计算偏距里程*/
public static LinePoint GetStationOffset(LinePoint CeZhan, LinePoint HouShi, LinePoint CeDian)
{
    double douJ0, douJ1, douJ2, douPJ;
    douPJ = GetDistance((SurveyPoint)CeZhan, (SurveyPoint)CeDian);//计算两点间平距
    douJ0 = GetAzimuth((SurveyPoint)CeZhan, (SurveyPoint)HouShi); //计算测站与后视的方位角
    douJ1 = GetAzimuth((SurveyPoint)CeZhan, (SurveyPoint)CeDian); //计算测点与测站方位角	     

    douJ2 = douJ0 - douJ1;//计算相对夹角
    //计算测点的偏距里程
    CeDian.Station= CeZhan.Station + douPJ * Math.cos(douJ2);
    CeDian.Offset = douPJ * Math.sin(douJ2);

    Log.i("douPJ",String.valueOf(douPJ));
    Log.i("CeDian.X",String.valueOf(CeDian.X));
    Log.i("CeDian.Y",String.valueOf(CeDian.Y));
    Log.i("CeZhan.X",String.valueOf(CeZhan.X));
    Log.i("CeZhan.Y",String.valueOf(CeZhan.Y));
    return CeDian;
}
/**得到线路左转或右转的角度格式*/
public static String GetTurnAngle(LinePoint CeZhan, LinePoint HouShi, LinePoint QianShi)
{
    double douJ0, douJ1, douJ2;
    String SAngle = new String();
    SurveyAngle  sA=new SurveyAngle();

    douJ0 = GetAzimuth(CeZhan, HouShi);
    douJ1 = GetAzimuth(CeZhan, QianShi);
    douJ2 = douJ1 - douJ0;


    if (douJ2 < 0) { douJ2 = 2 * Math.PI + douJ2; }
    if (douJ2 < Math.PI)
    {
        douJ2 = Math.PI - douJ2;
        sA.valueOfRadian(douJ2);
        SAngle = "左转 " + sA.GetSubDegree()+"度" + sA.GetSubMinute()+"分" + sA.GetSubSecond(0)+"秒";
    }
    if (douJ2 >= Math.PI)
    {
        douJ2 = douJ2 - Math.PI;
        sA.valueOfRadian(douJ2);
        SAngle = "右转 " + sA.GetSubDegree()+"度" + sA.GetSubMinute()+"分" + sA.GetSubSecond(0)+"秒";
    }
    return SAngle;
}
public static String setDigitsToString(double number,int digits) {

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
	return df.format(number);
}
public static double setDigitsToDouble(double number,int digits) {

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
	return Double.valueOf(df.format(number));
}
}
