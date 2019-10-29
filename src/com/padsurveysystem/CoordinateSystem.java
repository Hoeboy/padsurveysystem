package com.padsurveysystem;

/** 参考椭球 */
class ReferenceEllipsoid {
	public String Name;
	private double A;// 椭球长半轴
	private double B;// 椭球短半轴
	private double F;// 椭球扁率
	private double E1;// 第一偏心率e
	private double E2;// 第二偏心率e'

	public ReferenceEllipsoid(String name,double a, double f) {
		Name=name;
		A = a;
		F = f;
		B = A - A * F;
		E1 = Math.sqrt(2.0 * F - F * F);
		E2 = Math.sqrt(A * A - B * B) / B;
	}

	public double a() {
		return A;
	}

	public double b() {
		return B;
	}

	public double f() {
		return F;
	}

	public double e1() {
		return E1;
	}

	public double e2() {
		return E2;
	}

}

/** 坐标系统 */
public class CoordinateSystem {
	public ReferenceEllipsoid ReferenceEllipsoid;
	private ReferenceEllipsoid re;
	public double MiddleLongitude = 0.0;// 中央了午线
	public double OffsetNorth = 0.0;// 北坐标偏移量
	public double OffsetEast = 500000.0;// 东坐标偏移量
	public double CorrectNorth = 0.0;// 北坐标改正数
	public double CorrectEast = 0;// 东坐标改正数

	public static final ReferenceEllipsoid WGS84 = new ReferenceEllipsoid("WGS84坐标系",
			6378137.0, 1 / 298.257223563);
	public static final ReferenceEllipsoid BEIJIN54 = new ReferenceEllipsoid("1954年北京坐标系",
			6378245.0, 1 / 298.3);
	public static final ReferenceEllipsoid XIAN80 = new ReferenceEllipsoid("1980西安坐标系",
			6378140.0, 1 / 298.257);
	public static final ReferenceEllipsoid NATIONAL2000 = new ReferenceEllipsoid("国家2000坐标系",
			6378137.0, 1 / 298.257222101);

	public static ReferenceEllipsoid GetReferenceEllipsoid(int Index) {
		switch (Index) {
		case 0:
			return WGS84;
		case 1:
			return BEIJIN54;
		case 2:
			return XIAN80;
		case 3:
			return NATIONAL2000;
		default:
			return WGS84;
		}
	}

	public CoordinateSystem(String Name, double a, double f, double middlelongitude) {
		ReferenceEllipsoid = new ReferenceEllipsoid(Name,a, f);
		MiddleLongitude = middlelongitude;
	}

	public CoordinateSystem() {
	}

}