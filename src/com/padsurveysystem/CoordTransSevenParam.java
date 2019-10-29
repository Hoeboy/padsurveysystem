package com.padsurveysystem;

import android.util.Log;
import Jama.Matrix;

/*求七参数方法
 * 矩阵的右边为 x84，y84,z84
 * 矩阵的左边为
 * １ ０ ０ ｘ84 ０ －ｚ84 ｙ84
 * ０ １ ０ ｙ84 ｚ84 ０ －ｘ84
 * ０ ０ １ ｚ84 －ｙ84 ｘ84
 * 矩阵要求的参数为Δｘ，Δｙ, Δｚ, ｋ, εｘ, εｙ,εz
 * 左边乘参数得到右边的值
 * 程序实现方法
 * 1，根据用户提供的数据计算右边数据的个数为N*3，N为数据点的个数
 *    建立一个N*3行，1列的矩阵B
 * 2，根据用户提供的数据计算左边数据的个数为N*3，N为数据点的个数
 *    建立一个N*3行，7列的矩阵A
 * 3，利用最小二乘求出7参数,矩阵X
 * */
public class CoordTransSevenParam {

	public double WDX,WDY,WDZ,WRX,WRY,WRZ,WK;
	public double CDX,CDY,CDZ,CRX,CRY,CRZ,CK;
	private Matrix WGS84TransCartesianSevenParameter = new Matrix(7, 1);// WGS84转网格七参数
	private Matrix CartesianTransWGS84SevenParameter = new Matrix(7, 1);// 网格转WGS84七参数

	private double MiddleLongitude = 0;

	public CoordTransSevenParam() {

	}

	private Matrix CalculateSevenParameter(double A[][], double B[][]) {
		Matrix ArrayA = new Matrix(A.length * 3, 7);
		Matrix ArrayB = new Matrix(A.length * 3, 1);
		Matrix SevenParam = new Matrix(7, 1);        
		for (int i = 0; i < A.length; i++) {
			ArrayA.set(3 * i, 0, 1.0);
			ArrayA.set(3 * i, 1, 0.0);
			ArrayA.set(3 * i, 2, 0.0);
			ArrayA.set(3 * i, 3, A[i][0]);
			ArrayA.set(3 * i, 4, 0.0);
			ArrayA.set(3 * i, 5, 0.0 - A[i][2]);
			ArrayA.set(3 * i, 6, A[i][1]);
			ArrayB.set(3 * i, 0, B[i][0]);

			ArrayA.set(3 * i + 1, 0, 0.0);
			ArrayA.set(3 * i + 1, 1, 1.0);
			ArrayA.set(3 * i + 1, 2, 0.0);
			ArrayA.set(3 * i + 1, 3, A[i][1]);
			ArrayA.set(3 * i + 1, 4, A[i][2]);
			ArrayA.set(3 * i + 1, 5, 0.0);
			ArrayA.set(3 * i + 1, 6, 0.0 - A[i][0]);
			ArrayB.set(3 * i + 1, 0, B[i][1]);

			ArrayA.set(3 * i + 2, 0, 0.0);
			ArrayA.set(3 * i + 2, 1, 0.0);
			ArrayA.set(3 * i + 2, 2, 1.0);
			ArrayA.set(3 * i + 2, 3, A[i][2]);
			ArrayA.set(3 * i + 2, 4, 0.0 - A[i][1]);
			ArrayA.set(3 * i + 2, 5, A[i][0]);
			ArrayA.set(3 * i + 2, 6, 0.0);
			ArrayB.set(3 * i + 2, 0, B[i][2]);
		}
		SevenParam =ArrayA.solve(ArrayB);

		return SevenParam;

	}

	public CoordTransSevenParam(double WGS84Coordinate[][],
			double CartesianCoordinate[][]) {

		double[][] ArrayW = new double[WGS84Coordinate.length][WGS84Coordinate[0].length];

		double[] GeodeticCoordinate = new double[3];
		double[] UserCoordinate = new double[3];
		int CoordCount = WGS84Coordinate.length;// 参加坐标转换点的个数
		for (int i = 0; i < CoordCount; i++) {
			MiddleLongitude = MiddleLongitude + WGS84Coordinate[i][1];
		}
		MiddleLongitude = MiddleLongitude / CoordCount;
		for (int i = 0; i < CoordCount; i++) {
			GeodeticCoordinate[0] = WGS84Coordinate[i][0];
			GeodeticCoordinate[1] = WGS84Coordinate[i][1];
			GeodeticCoordinate[2] = WGS84Coordinate[i][2];
			GeodeticTransCartesian(GeodeticCoordinate, UserCoordinate);

			ArrayW[i][0] = UserCoordinate[0];
			ArrayW[i][1] = UserCoordinate[1];
			ArrayW[i][2] = UserCoordinate[2];

		}

		WGS84TransCartesianSevenParameter = CalculateSevenParameter(ArrayW,
				CartesianCoordinate);// 利用矩阵最小二乘求七参数
		CartesianTransWGS84SevenParameter = CalculateSevenParameter(CartesianCoordinate,
				ArrayW);// 利用矩阵最小二乘求七参数

	
		WDX=WGS84TransCartesianSevenParameter.get(0, 0);
		WDY=WGS84TransCartesianSevenParameter.get(1, 0);
		WDZ=WGS84TransCartesianSevenParameter.get(2, 0);
		WRX=WGS84TransCartesianSevenParameter.get(3, 0);
		WRY=WGS84TransCartesianSevenParameter.get(4, 0);
		WRZ=WGS84TransCartesianSevenParameter.get(5, 0);
		WK=WGS84TransCartesianSevenParameter.get(6, 0);

		CDX=CartesianTransWGS84SevenParameter.get(0, 0);
		CDY=CartesianTransWGS84SevenParameter.get(1, 0);
		CDZ=CartesianTransWGS84SevenParameter.get(2, 0);
		CRX=CartesianTransWGS84SevenParameter.get(3, 0);
		CRY=CartesianTransWGS84SevenParameter.get(4, 0);
		CRZ=CartesianTransWGS84SevenParameter.get(5, 0);
		CK=CartesianTransWGS84SevenParameter.get(6, 0);

	}
    public void setWGS84TransCartesianSevenParameter(double wdx,double wdy,double wdz,double wrx,double wry,double wrz,double wk){
		 WGS84TransCartesianSevenParameter.set(0, 0,wdx);
		 WGS84TransCartesianSevenParameter.set(1, 0,wdy);
		 WGS84TransCartesianSevenParameter.set(2, 0,wdz);
		 WGS84TransCartesianSevenParameter.set(3, 0,wrx);
		 WGS84TransCartesianSevenParameter.set(4, 0,wry);
		 WGS84TransCartesianSevenParameter.set(5, 0,wrz);
		 WGS84TransCartesianSevenParameter.set(6, 0,wk);
    }
    public void setCartesianTransWGS84SevenParameter(double cdx,double cdy,double cdz,double crx,double cry,double crz,double ck){
    	CartesianTransWGS84SevenParameter.set(0, 0,cdx);
    	CartesianTransWGS84SevenParameter.set(1, 0,cdy);
    	CartesianTransWGS84SevenParameter.set(2, 0,cdz);
    	CartesianTransWGS84SevenParameter.set(3, 0,crx);
    	CartesianTransWGS84SevenParameter.set(4, 0,cry);
    	CartesianTransWGS84SevenParameter.set(5, 0,crz);
    	CartesianTransWGS84SevenParameter.set(6, 0,ck);
    	
    }
	// 大地坐标转换成同一椭球下的高斯坐标
	private void GeodeticTransCartesian(double GeodeticCoordinate[],
			double CartesianCoordinate[]) {
		CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();// 网格坐标
		GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();// 大地坐标
		CoordinateTransform ct = new CoordinateTransform();// 坐标转换类

		ct.WGS84.MiddleLongitude = MiddleLongitude;

		pgc.Latitude = GeodeticCoordinate[0];
		pgc.Longitude = GeodeticCoordinate[1];
		pgc.Height = GeodeticCoordinate[2];
		ct.GeodeticToCartesian(pcc, pgc, ct.WGS84);
		CartesianCoordinate[0] = pcc.X;
		CartesianCoordinate[1] = pcc.Y;
		CartesianCoordinate[2] = pcc.H;
	}

	/**
	 * 将WGS84大地坐标转换为指定坐标系统的网格坐标
	 * 
	 * @param WGS84Coordinate
	 *            输入的WGS84大地坐标
	 * @param UserCoordinate
	 *            输出的网格坐标
	 * @return 长度为3 的网格坐标数组 X,Y,Z
	 */
	public double[] WGS84TransCartesian(double WGS84Coordinate[],
			double UserCoordinate[]) {
		Matrix ArrayA = new Matrix(3, 7);
		Matrix ArrayB = new Matrix(3, 1);
		double[] CartesianCoordinate = new double[3];
		GeodeticTransCartesian(WGS84Coordinate, CartesianCoordinate);
		ArrayA.set(0, 0, 1);
		ArrayA.set(0, 1, 0);
		ArrayA.set(0, 2, 0);
		ArrayA.set(0, 3, CartesianCoordinate[0]);
		ArrayA.set(0, 4, 0);
		ArrayA.set(0, 5, 0.0 - (CartesianCoordinate[2]));
		ArrayA.set(0, 6, CartesianCoordinate[1]);
		ArrayA.set(1, 0, 0);
		ArrayA.set(1, 1, 1);
		ArrayA.set(1, 2, 0);
		ArrayA.set(1, 3, CartesianCoordinate[1]);
		ArrayA.set(1, 4, CartesianCoordinate[2]);
		ArrayA.set(1, 5, 0);
		ArrayA.set(1, 6, 0 - (CartesianCoordinate[0]));
		ArrayA.set(2, 0, 0);
		ArrayA.set(2, 1, 0);
		ArrayA.set(2, 2, 1);
		ArrayA.set(2, 3, CartesianCoordinate[2]);
		ArrayA.set(2, 4, 0 - (CartesianCoordinate[1]));
		ArrayA.set(2, 5, CartesianCoordinate[0]);
		ArrayA.set(2, 6, 0);
		ArrayB = ArrayA.times(WGS84TransCartesianSevenParameter);
		UserCoordinate[0] = ArrayB.get(0, 0);
		UserCoordinate[1] = ArrayB.get(1, 0);
		UserCoordinate[2] = ArrayB.get(2, 0);
		return UserCoordinate;
	}

	// 高斯坐标转换成同一椭球下的大地坐标
	private void CartesianTransGeodetic(double CartesianCoordinate[],
			double GeodeticCoordinate[]) {
		CartesianCoordinatePoint pcc = new CartesianCoordinatePoint();// 网格坐标
		GeodeticCoordinatePoint pgc = new GeodeticCoordinatePoint();// 大地坐标
		CoordinateTransform ct = new CoordinateTransform();// 坐标转换类

		ct.WGS84.MiddleLongitude = MiddleLongitude;
		pcc.X = CartesianCoordinate[0];
		pcc.Y = CartesianCoordinate[1];
		pcc.H = CartesianCoordinate[2];

		ct.CartesianToGeodetic(pgc, pcc, ct.WGS84);

		GeodeticCoordinate[0] = pgc.Latitude;
		GeodeticCoordinate[1] = pgc.Longitude;
		GeodeticCoordinate[2] = pcc.H;
	}

	/**
	 * 将指定坐标系统的网格坐标转换为WGS84大地坐标
	 * 
	 * @param WGS84Coordinate
	 *            输入的WGS84大地坐标
	 * @param UserCoordinate
	 *            输出的网格坐标
	 * @return 长度为3 的网格坐标数组 X,Y,Z
	 */
	public void CartesianTransWGS84(double UserCoordinate[],
			double WGS84Coordinate[]) {
		Matrix ArrayA = new Matrix(3, 7);
		Matrix ArrayB = new Matrix(3, 1);
		double[] CartesianCoordinate = new double[3];

		ArrayA.set(0, 0, 1);
		ArrayA.set(0, 1, 0);
		ArrayA.set(0, 2, 0);
		ArrayA.set(0, 3, UserCoordinate[0]);
		ArrayA.set(0, 4, 0);
		ArrayA.set(0, 5, 0.0 - (UserCoordinate[2]));
		ArrayA.set(0, 6, UserCoordinate[1]);
		ArrayA.set(1, 0, 0);
		ArrayA.set(1, 1, 1);
		ArrayA.set(1, 2, 0);
		ArrayA.set(1, 3, UserCoordinate[1]);
		ArrayA.set(1, 4, UserCoordinate[2]);
		ArrayA.set(1, 5, 0);
		ArrayA.set(1, 6, 0 - (UserCoordinate[0]));
		ArrayA.set(2, 0, 0);
		ArrayA.set(2, 1, 0);
		ArrayA.set(2, 2, 1);
		ArrayA.set(2, 3, UserCoordinate[2]);
		ArrayA.set(2, 4, 0 - (UserCoordinate[1]));
		ArrayA.set(2, 5, UserCoordinate[0]);
		ArrayA.set(2, 6, 0);
		ArrayB = ArrayA.times(CartesianTransWGS84SevenParameter);
		CartesianCoordinate[0] = ArrayB.get(0, 0);
		CartesianCoordinate[1] = ArrayB.get(1, 0);
		CartesianCoordinate[2] = ArrayB.get(2, 0);

		CartesianTransGeodetic(CartesianCoordinate, WGS84Coordinate);
	}
}
