package com.padsurveysystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

//文件文件操作类
public class MapTilesView extends SurfaceView implements Runnable, Callback {

	Context mContext;
	int timeView = 0;
	private SurfaceHolder mHolder; // 用于控制SurfaceView
	private Thread t; // 声明一条线程
	private volatile boolean flag; // 线程运行的标识，用于控制线程
	Timer timer = new Timer();
	TimerTask task = null;
	private Path mPath;
	
	
	public boolean mShowArrow = false;
	public boolean mMoveFlag = false;
	
	
	
	public Paint mPaint = null;
	private int mBitmapWidth = 1024;// 绘图区宽度
	private int mBitmapHeight = 1024;// 绘图区高度
	private float mArrowLeft = 300;
	private float mArrowTop = 500;

	// 定义一个内存中的图片，该图片将作为缓冲区
	Bitmap cacheBitmap = null;
	Bitmap secondBitmap = null;
	// 定义cacheBitmap上的Canvas对象
	Canvas cacheCanvas = null;

	private double mResolutionX = 0;// 初始X轴分辨率
	private double mResolutionY = 0;// 初始Y轴分辨率
	private double mRotatingX = 0;// X轴旋转
	private double mRotatingY = 0;// X轴旋转
	private double mOriginX = 0;// 左上角原点X坐标
	private double mOriginY = 0;// 左上角原点Y坐标

	private double mStarX = 0;// 左上角实际X坐标
	private double mStarY = 0;// 左上角实际Y坐标

	private int mLevelCount = 0;// 总的分层数
	private int mCurrentLevelNumber = 0;// 当前层

	private float mCurrentLeftPixel = 0;
	private float mCurrentTopPixel = 0;
	
	//放样点和线数据
	private SurveyPoint mStakingPoint=null;
	private SurveyPoint mStakingLineStartPoint=null;
	private SurveyPoint mStakingLineEndPoint=null;
	
	private float mStakingoutPointTop = 500;	
	private float mStakingoutPointLeft = 500;	
	private float mStakingoutLineStartPointTop = 500;	
	private float mStakingoutLineStartPointLeft = 500;
	private float mStakingoutLineEndPointTop = 500;	
	private float mStakingoutLineEndPointLeft = 500;
	// 需要更新的文件区域
	private float mRereshLeftPixel = 0;
	private float mRereshTopPixel = 0;
	private float mRereshRightPixel = 0;
	private float mRereshBottomPixel = 0;

	private float mMovePixelX = 0;
	private float mMovePixelY = 0;

	private float mDownX = 0;
	private float mDownY = 0;
	private float mMoveLastX = 0;
	private float mMoveLastY = 0;

	private double mLastUserX = 0;
	private double mLastUserY = 0;
	
	private int mDrawSubLeft = 0;
	private int mDrawSubTop = 0;
	private Bitmap mDrawSubBitmap = null;

	private List<MapLevel> mMapLevelList;

	private File mFilePath;

	public MapTilesView(Context context) {
		super(context);
		mHolder = getHolder(); // 获得SurfaceHolder对象
		mHolder.addCallback(this); // 为SurfaceView添加状态监听
		setFocusable(true); // 设置焦点
		mContext = context;
		mPath = new Path();
		// 设置画笔的颜色
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setColor(Color.RED);
		// 设置画笔风格
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(1);
		// 反锯齿

	}

	public MapTilesView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHolder = getHolder(); // 获得SurfaceHolder对象
		mHolder.addCallback(this); // 为SurfaceView添加状态监听
		setFocusable(true); // 设置焦点

		mContext = context;
		mPath = new Path();
		// 设置画笔的颜色
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setColor(Color.RED);
		// 设置画笔风格
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(1);
		// 反锯齿
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);


	}

	public void initializeMap(String FilePath) {
		mCurrentLeftPixel = 0;
		mCurrentTopPixel = 0;
		mMovePixelX = 0;
		mMovePixelY = 0;

		mDownX = 0;
		mDownY = 0;

		mFilePath = new File(FilePath);

		// 获取实际高度,getHeight为获取组件高度
		// 创建一个与该View相同大小的缓存区
		mBitmapWidth = (int) Math.ceil((double) getWidth() / 256.0) * 256;
		mBitmapHeight = (int) Math.ceil((double) getHeight() / 256.0) * 256;

		mMapLevelList = new ArrayList<MapLevel>();
		String iniFile = FilePath + "/" + mFilePath.getName() + ".ini";
		initializeFromFile(iniFile);
		mLastUserX=mOriginX;
		mLastUserY=mOriginY;
		setLevelNumber(1);
	}

	// 初始化数据从配置文件
	private void initializeFromFile(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 0;
			int iLevel = 0;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				double dS = Double.valueOf(tempString);
				line++;
				switch (line) {
				case 1:
					mResolutionX = dS;
					break;
				case 2:
					mRotatingX = dS;
					break;
				case 3:
					mRotatingY = dS;
					break;
				case 4:
					mResolutionY = dS;
					break;
				case 5:
					mOriginX = dS;
					break;
				case 6:
					mOriginY = dS;
					break;
				case 7:
					mLevelCount = (int) dS;
					iLevel = mLevelCount;
					break;
				default:
					if (line % 2 == 0) {
						int r = Integer.valueOf(tempString);
						tempString = reader.readLine();
						int c = Integer.valueOf(tempString);
						MapLevel mP = new MapLevel(r, c);
						mP.resolutionX = mRotatingX * iLevel;
						iLevel--;
						mMapLevelList.add(mP);
						line++;
					}
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public void setLevelNumber(int levelNumber) {
		mCurrentLevelNumber = levelNumber;
	}

	public int getLevelNumber() {
		return mCurrentLevelNumber;
	}

	public int getLevelCount() {
		return mLevelCount;
	}

	// 缩放地图
	public void ZoomMap(int levelNumber) {

		double iCenterX = (mCurrentLeftPixel + getWidth() / 2)
				* (mLevelCount - mCurrentLevelNumber + 1) * mResolutionX;
		double iCenterY = (mCurrentTopPixel + getHeight() / 2)
				* (mLevelCount - mCurrentLevelNumber + 1) * mResolutionY;
		mCurrentLevelNumber = levelNumber;
		mCurrentLeftPixel = (float) (iCenterX
				/ ((mLevelCount - mCurrentLevelNumber + 1) * mResolutionX) - getWidth() / 2);
		mCurrentTopPixel = (float) (iCenterY
				/ ((mLevelCount - mCurrentLevelNumber + 1) * mResolutionY) - getHeight() / 2);
		float[] fScreen = getScreenCoordinates(mLastUserX, mLastUserY);
		mArrowLeft = fScreen[0]-64;
		mArrowTop = fScreen[1]-64;	
		if (mStakingPoint != null) {
			float fPoint[] = getScreenCoordinates(mStakingPoint.X,
					mStakingPoint.Y);
			mStakingoutPointLeft = fPoint[0]-15;
			mStakingoutPointTop = fPoint[1]-50;
		}
		creatMap();
	}

	// 设置视图中心
	public void setViewCenter(double x, double y) {

		double dX = x - mOriginX;
		double dY = y - mOriginY;
		mCurrentLeftPixel = (float) (dX
				/ ((mLevelCount - mCurrentLevelNumber + 1) * mResolutionX) - getWidth() / 2);
		mCurrentTopPixel = (float) (dY
				/ ((mLevelCount - mCurrentLevelNumber + 1) * mResolutionY) - getHeight() / 2);
    	Log.i("mOriginX", String.valueOf(mOriginX));
    	Log.i("mCurrentLeftPixel", String.valueOf(mCurrentLeftPixel));
    	Log.i("mCurrentTopPixel", String.valueOf(mCurrentTopPixel));
		// 获取在地图中的像素位置
		mShowArrow = true;
		mLastUserX=x;
		mLastUserY=y;	
		drawMap();

	}

	// 获取用户坐标,参数为地图坐标
	public double[] getUserCoordinates(float mapX, float mapY) {
		double A = mResolutionX * (mLevelCount - mCurrentLevelNumber + 1);
		double D = mRotatingX;
		double B = mRotatingY;
		double E = mResolutionY * (mLevelCount - mCurrentLevelNumber + 1);
		double C = mOriginX;
		double F = mOriginY;
		double[] UserXY = new double[2];
		UserXY[0] = A * mapX + B * mapY + C;
		UserXY[1] = D * mapX + E * mapY + F;
		return UserXY;
		/*
		 * x'=Ax+By+C y'=Dx+Ey+F 其中： x'=象素对应的地理X坐标 y'=象素对应的地理Y坐标 x=象素坐标【列号】
		 * y=象素坐标【行号】 A=X方向上的象素分辨率 D=X方向的旋转系数 B=Y方向的旋转系数 E=Y方向上的象素分辨素
		 * C=栅格地图左上角象素中心X坐标 F=栅格地图左上角象素中心Y坐标\
		 */
	}

	// 获取地图坐标，参数为用户坐标
	public float[] getMapCoordinates(double x, double y) {
		float[] mapXY = new float[2];

		double A = mResolutionX * (mLevelCount - mCurrentLevelNumber + 1);
		double D = mRotatingX;
		double B = mRotatingY;
		double E = mResolutionY * (mLevelCount - mCurrentLevelNumber + 1);
		double C = mOriginX;
		double F = mOriginY;
		mapXY[0] = (float) ((E * x - E * C - B * y + B * F) / (E * A - B * D));
		mapXY[1] = (float) ((D * x - C * D - A * y + A * F) / (D * B - A * E));
		return mapXY;
		/*
		 * x=(EX1-EC-BY1+BF)/EA-BD y=(DX1-CD-AY1+AF)/DB-AE 其中： x'=象素对应的地理X坐标 x
		 * y'=象素对应的地理Y坐标 y x=象素坐标【列号】 y=象素坐标【行号】 A=X方向上的象素分辨率 D=X方向的旋转系数
		 * mRotatingX B=Y方向的旋转系数 mRotatingY E=Y方向上的象素分辨素 C=栅格地图左上角象素中心X坐标
		 * F=栅格地图左上角象素中心Y坐标\
		 */
	}

	/**
	 * 当SurfaceView创建的时候，调用此函数
	 */
	// 获取屏幕坐标，参数为地图坐标
	public float[] getScreenCoordinates(float mapX, float mapY) {
		float[] screenXY = new float[2];

		screenXY[0] = mapX - mCurrentLeftPixel;
		screenXY[1] = mapY - mCurrentTopPixel;
		return screenXY;

	}

	// 获取屏幕坐标，参数为实际坐标
	public float[] getScreenCoordinates(double x, double y) {
		float[] mapXY = getMapCoordinates(x, y);
		float[] screenXY = getScreenCoordinates(mapXY[0], mapXY[1]);
		return screenXY;
	}



	public void drawMap() {
		creatMap();
		drawArrow();
	}
	public void drawPoint(SurveyPoint userpoint) {

		// 获取在地图中的像素位置
		mStakingPoint=new SurveyPoint();

		mStakingPoint.Name=userpoint.Name;
		mStakingPoint.X=userpoint.X;
		mStakingPoint.Y=userpoint.Y;		
	}
	public void drawLine(SurveyPoint startpoint,SurveyPoint endpoint) {

		mStakingLineStartPoint=new SurveyPoint();
		mStakingLineEndPoint=new SurveyPoint();
		// 获取在地图中的像素位置
		mStakingLineStartPoint.Name=startpoint.Name;
		mStakingLineStartPoint.X=startpoint.Y;
		mStakingLineStartPoint.Y=startpoint.X;
		
		mStakingLineEndPoint.Name=endpoint.Name;
		mStakingLineEndPoint.X=endpoint.Y;
		mStakingLineEndPoint.Y=endpoint.X;

        
	}
	//实时移动地图,只移动地图，光标显示在地图中心
public void MoveMap(double x,double y){

	float fEMx=getMapCoordinates(x, y)[0];
	float fEMy=getMapCoordinates(x, y)[1];	
	
	float fSMx=getMapCoordinates(mLastUserX, mLastUserY)[0];
	float fSMy=getMapCoordinates(mLastUserX, mLastUserY)[1];
	
	mLastUserX=x;
	mLastUserY=y;
	
	mMovePixelX = fEMx - fSMx;
	mMovePixelY = fEMy - fSMy;

	mCurrentLeftPixel = mCurrentLeftPixel + mMovePixelX;
	mCurrentTopPixel = mCurrentTopPixel + mMovePixelY;
	
	setViewCenter(x,y);

	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		drawMap();
	}

	/**
	 * 当SurfaceView的视图发生改变的时候，调用此函数
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 当SurfaceView销毁的时候，调用此函数
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		timer.cancel();
		mHolder.removeCallback(this);
		flag = false;

	}

	@Override
	public void run() {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 获取拖动事件的发生位置
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMoveFlag=true;
			mDownX = x;
			mDownY = y;
			mMoveLastX = x;
			mMoveLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:

			mMovePixelX = x - mDownX;
			mMovePixelY = y - mDownY;

			float fDx = x - mMoveLastX;
			float fDy = y - mMoveLastY;

			mArrowLeft = mArrowLeft + fDx;
			mArrowTop = mArrowTop + fDy;
			
			mStakingoutPointLeft =  mStakingoutPointLeft+ fDx;
			mStakingoutPointTop =  mStakingoutPointTop+ fDy;

			mStakingoutLineStartPointLeft = mStakingoutLineStartPointLeft + fDx;
			mStakingoutLineStartPointTop = mStakingoutLineStartPointTop + fDy;
			mStakingoutLineEndPointLeft =  mStakingoutLineEndPointLeft+ fDx;
			mStakingoutLineEndPointTop =mStakingoutLineEndPointTop + fDy;
			
			drawArrow();
			mMoveLastX = x;
			mMoveLastY = y;
			break;
		case MotionEvent.ACTION_UP:

			mMovePixelX = x - mDownX;
			mMovePixelY = y - mDownY;

			mCurrentLeftPixel = mCurrentLeftPixel - mMovePixelX;
			mCurrentTopPixel = mCurrentTopPixel - mMovePixelY;

			creatMap();
			mMovePixelX = 0;
			mMovePixelY = 0;
			mMoveFlag=false;
			break;
		}

		// 返回true表明处理方法已经处理该事件
		return true;
	}


	private void drawArrow() {

		if (!mMoveFlag) {
			// 获取在地图中的像素位置
			float[] fScreen = getScreenCoordinates(mLastUserX, mLastUserY);
			mArrowLeft = fScreen[0] - 64;
			mArrowTop = fScreen[1] - 64;
			if (mStakingPoint != null) {
				float fPoint[] = getScreenCoordinates(mStakingPoint.X,
						mStakingPoint.Y);
				mStakingoutPointLeft = fPoint[0]-15;
				mStakingoutPointTop = fPoint[1]-50;
			}
			if (mStakingLineStartPoint != null) {
				float fLinePoint1[] = getScreenCoordinates(mStakingLineStartPoint.X,
						mStakingLineStartPoint.Y);
				float fLinePoint2[] = getScreenCoordinates(mStakingLineEndPoint.X,
						mStakingLineEndPoint.Y);
				mStakingoutLineStartPointLeft = fLinePoint1[0];
				mStakingoutLineStartPointTop = fLinePoint1[1];
				mStakingoutLineEndPointLeft = fLinePoint2[0];
				mStakingoutLineEndPointTop = fLinePoint2[1];
			}
		}
		if (timer != null)
			timer.cancel();
		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				Bitmap bP1 = BitmapFactory.decodeResource(getResources(),
						R.drawable.vm_chevron_on);
				Bitmap bP2 = BitmapFactory.decodeResource(getResources(),
						R.drawable.vm_chevron_off);
				Bitmap bP3 = BitmapFactory.decodeResource(getResources(),
						R.drawable.stakingout_point);
				if (cacheBitmap == null)
					return;
				if (cacheBitmap.isRecycled())
					return;
				cacheCanvas = mHolder.lockCanvas();
				if (cacheCanvas != null) {
					cacheCanvas.drawColor(Color.BLACK);					
					cacheCanvas.drawBitmap(cacheBitmap, mMovePixelX,
							mMovePixelY, null);

					//绘制放样点
					if (mStakingPoint!=null) {
						cacheCanvas
								.drawBitmap(bP3, mStakingoutPointLeft,mStakingoutPointTop, null);
						mPaint.setTextAlign(Align.CENTER);
						mPaint.setTextSize(20);
						mPaint.setColor(Color.GREEN);
						cacheCanvas.drawText(mStakingPoint.Name, mStakingoutPointLeft+15, mStakingoutPointTop-5, mPaint);
					}
					//绘制放样线
					if (mStakingLineStartPoint != null) {
						cacheCanvas.drawBitmap(bP3,
								mStakingoutLineStartPointLeft-15,
								mStakingoutLineStartPointTop-50, null);
						mPaint.setTextAlign(Align.CENTER);
						mPaint.setTextSize(20);
						mPaint.setColor(Color.GREEN);
						cacheCanvas.drawText(mStakingLineStartPoint.Name,
								mStakingoutLineStartPointLeft,
								mStakingoutLineStartPointTop - 56, mPaint);

						cacheCanvas.drawBitmap(bP3,
								mStakingoutLineEndPointLeft-15,
								mStakingoutLineEndPointTop-50, null);
						mPaint.setTextAlign(Align.CENTER);
						mPaint.setTextSize(20);
						mPaint.setColor(Color.GREEN);
						cacheCanvas.drawText(mStakingLineEndPoint.Name,
								mStakingoutLineEndPointLeft,
								mStakingoutLineEndPointTop - 56, mPaint);

						mPaint.setColor(Color.BLUE);						
						mPaint.setStrokeWidth(3);
						cacheCanvas.drawLine(mStakingoutLineStartPointLeft,
								mStakingoutLineStartPointTop,
								mStakingoutLineEndPointLeft,
								mStakingoutLineEndPointTop, mPaint);
						Log.i("mStakingoutLineStartPointLeft",
								String.valueOf(mStakingoutLineStartPointLeft));
					}
					
					if (mShowArrow) {
						cacheCanvas
								.drawBitmap(bP1, mArrowLeft, mArrowTop, null);
					}
					mHolder.unlockCanvasAndPost(cacheCanvas);

				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cacheCanvas = mHolder.lockCanvas();
				if (cacheCanvas != null) {
					cacheCanvas.drawColor(Color.BLACK);
					cacheCanvas.drawBitmap(cacheBitmap, mMovePixelX,
							mMovePixelY, null);
					//绘制放样点
					if (mStakingPoint!=null) {
						cacheCanvas
								.drawBitmap(bP3, mStakingoutPointLeft,mStakingoutPointTop, null);
						mPaint.setTextAlign(Align.CENTER);
						mPaint.setTextSize(20);
						mPaint.setColor(Color.GREEN);
						cacheCanvas.drawText(mStakingPoint.Name, mStakingoutPointLeft+15, mStakingoutPointTop-5, mPaint);
					}
					//绘制放样线
					if (mStakingLineStartPoint!=null) {
						cacheCanvas.drawBitmap(bP3,
								mStakingoutLineStartPointLeft-15,
								mStakingoutLineStartPointTop-50, null);
						mPaint.setTextAlign(Align.CENTER);
						mPaint.setTextSize(20);
						mPaint.setColor(Color.GREEN);
						cacheCanvas.drawText(mStakingLineStartPoint.Name,
								mStakingoutLineStartPointLeft,
								mStakingoutLineStartPointTop - 56, mPaint);

						cacheCanvas.drawBitmap(bP3,
								mStakingoutLineEndPointLeft-15,
								mStakingoutLineEndPointTop-50, null);
						mPaint.setTextAlign(Align.CENTER);
						mPaint.setTextSize(20);
						mPaint.setColor(Color.GREEN);
						cacheCanvas.drawText(mStakingLineEndPoint.Name,
								mStakingoutLineEndPointLeft,
								mStakingoutLineEndPointTop - 56, mPaint);
						
						mPaint.setColor(Color.BLUE);
						mPaint.setStrokeWidth(2);
						cacheCanvas.drawLine(mStakingoutLineStartPointLeft,
								mStakingoutLineStartPointTop,
								mStakingoutLineEndPointLeft,
								mStakingoutLineEndPointTop, mPaint);
					}
					if (mShowArrow) {
						cacheCanvas
								.drawBitmap(bP2, mArrowLeft, mArrowTop, null);
					}
					mHolder.unlockCanvasAndPost(cacheCanvas);

				}
			}

		};
		timer.schedule(task, 0, 1);
	}

	private void creatMap() {

		if (mFilePath == null) {
			return;
		}
		timer.cancel();
		int iSc = (int) Math.ceil((double) mCurrentLeftPixel / 256.0);// 由当前像素点位置计算需要加载的图像开始行
		int iSr = (int) Math.ceil((double) mCurrentTopPixel / 256.0);// 由当前像素点位置计算需要加载的图像开始列
		int iEc = (int) Math
				.ceil((double) (mCurrentLeftPixel + getWidth()) / 256.0);// 由当前像素点位置计算需要加载的图像结束行
		int iEr = (int) Math
				.ceil((double) (mCurrentTopPixel + getHeight()) / 256.0);// 由当前像素点位置计算需要加载的图像结束列

		int iWC = (iEc - iSc + 1) * 256;
		int iHC = (iEr - iSr + 1) * 256;

		Canvas tC = new Canvas();
		Bitmap tMaxBitmap = Bitmap.createBitmap(iWC, iHC, Config.ARGB_8888);
		tC.setBitmap(tMaxBitmap);
		int iCnubmer = 0;
		for (int iC = iSc - 1; iC < iEc; iC++) {
			int iRnubmer = 0;
			for (int iR = iSr - 1; iR < iEr; iR++) {
				String sFilePath = mFilePath.getPath() + "/L_"
						+ mCurrentLevelNumber + "/" + iR + "_" + iC + ".png";
				File tF = new File(sFilePath);
				if (tF.exists()) {
					Bitmap tBmp = BitmapFactory.decodeFile(sFilePath);
					tC.drawBitmap(tBmp, iCnubmer * 256, iRnubmer * 256, mPaint);
					tBmp.recycle();
				} else {
					Canvas tNC = new Canvas();
					Bitmap tNoMapBitmap = Bitmap.createBitmap(256, 256,
							Config.ARGB_8888);
					tNC.setBitmap(tNoMapBitmap);
					mPaint.setTextAlign(Align.CENTER);
					mPaint.setTextSize(20);
					mPaint.setColor(Color.GRAY);
					tNC.drawText("本区域无图", 83, 118, mPaint);
					tC.drawBitmap(tNoMapBitmap, iCnubmer * 256, iRnubmer * 256,
							mPaint);
					tNoMapBitmap.recycle();

				}
				iRnubmer++;
			}
			iCnubmer++;

		}
		
		int iLeft = (int) (mCurrentLeftPixel - (iSc - 1) * 256);
		int iTop = (int) (mCurrentTopPixel - (iSr - 1) * 256);
		cacheBitmap = Bitmap.createBitmap(tMaxBitmap, iLeft, iTop,
				tMaxBitmap.getWidth() - iLeft, tMaxBitmap.getHeight() - iTop);
		
		tMaxBitmap.recycle();
		drawArrow();

		

	}

}

/**
 * 此类保存地图的层信息
 * 
 * @author ncpe
 * 
 */
class MapLevel {
	public int maxWidth = 0;// 当前缩放级别地图总宽度
	public int maxHeight = 0;// 当前缩放级别地图总高度
	public int rowCount = 0;
	public int columnCount = 0;
	public double resolutionX = 0;// X轴分辨率
	public double resolutionY = 0;// Y轴分辨率

	public MapLevel(int r, int c) {
		rowCount = r;
		columnCount = c;
		maxWidth = c * 256;
		maxHeight = r * 256;
	}
}
