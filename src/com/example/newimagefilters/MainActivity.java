package com.example.newimagefilters;

import java.lang.reflect.Field;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainActivity extends Activity 
{
	
	ImageView imgView;
	Bitmap bmptobeManipulated=null,bmpmutable=null,bmpmutable_bgLayer=null,bmpmutable_fgLayer=null,bmpmutable_upperfgLayer=null;
	Paint multiplyPaint;
	Field[] drawables;
	Vector <Integer> resIds;
	static int index=0;
	Button backButton,frontButton,origButton;
	Context activityContext;
	boolean showOriginalImage=false;
	 
	ImageButton filter_backButton,filter_frontButton;
	
	int activeFilter=0;
	int numFilters=2;
	TextView filterName;
	
	Spinner selectedFilter;
	
	int spinnerPosition;
	
	
	public void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		activityContext=this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		
		drawables = android.R.drawable.class.getFields();
		
		final R.drawable drawableResources = new R.drawable();
		final Class<R.drawable> c = R.drawable.class;
		final Field[] fields = c.getDeclaredFields();

		resIds=new Vector<Integer>();
		
		for (int i = 0, max = fields.length; i < max; i++) {
		    final int resourceId;
		    try {
		        resourceId = fields[i].getInt(drawableResources);
		        resIds.add(i, resourceId);
		        Log.d("ID","id : value "+resourceId);
		    } catch (Exception e) {
		        continue;
		    }
		    /* make use of resourceId for accessing Drawables here */
		}
		
		index=1000*fields.length;
		
		Log.d("index","ffdf"+index);
		imgView=(ImageView)findViewById(R.id.imgView);

		bmptobeManipulated=BitmapFactory.decodeResource(this.getResources(), resIds.get(index%fields.length));

		imgView.invalidate();
		
		frontButton=(Button)findViewById(R.id.frontButton);
		frontButton.setId(123);
		frontButton.setOnClickListener(filtersLayerClickListener);
		
		backButton=(Button)findViewById(R.id.backButton);
		backButton.setId(124);
		backButton.setOnClickListener(filtersLayerClickListener);
		
		origButton=(Button)findViewById(R.id.origButton);
		origButton.setId(125);
		origButton.setOnClickListener(filtersLayerClickListener);
		
		filter_backButton=(ImageButton)findViewById(R.id.filter_backButton);
		filter_backButton.setId(126);
		filter_backButton.setOnClickListener(filtersLayerClickListener);
		
		filter_frontButton=(ImageButton)findViewById(R.id.filter_frontButton);
		filter_frontButton.setId(127);
		filter_frontButton.setOnClickListener(filtersLayerClickListener);
		
		filterName=(TextView)findViewById(R.id.filterName);
				
		implementVintageFilter();

		
		selectedFilter=(Spinner)findViewById(R.id.filter_dropdown);
		selectedFilter.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	
		imgView.setImageBitmap(bmpmutable);
		
		origButton.setText("Original");
	}
	
	
	
	public OnClickListener filtersLayerClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) 
		{
			
			if(v.getId()==123)
				index=index+1;
			else if(v.getId()==124&&index!=0)
				index=index-1;
			else if(v.getId()==125)
			{
				showOriginalImage=!showOriginalImage;
				
				if(showOriginalImage)  
				origButton.setText("Filtered");
				else origButton.setText("Original");
				origButton.invalidate();
				
				if(!showOriginalImage)
				{
					
					if(spinnerPosition==0)
						   implementVintageFilter();
						else if(spinnerPosition==1)
						   implementXPro2Filter();
						else if (spinnerPosition==2)
						   implementVividFilter();
						else if(spinnerPosition==3)
						   implementEarlyBirdFilter();
						 
						imgView.setImageBitmap(null);
						imgView.setImageBitmap(bmpmutable);
					
				}
				
			}
			
			
			
			bmptobeManipulated=null;
			bmptobeManipulated=BitmapFactory.decodeResource(activityContext.getResources(), resIds.get(index%resIds.size()));
			
			if(showOriginalImage)
			imgView.setImageBitmap(bmptobeManipulated);
			
			
			
			
			
//			
//			else if(v.getId()==127)
//			{			
//				activeFilter=(activeFilter+1)%numFilters;
//				
//				
//			}
//			
//			else if(v.getId()==126)
//			{
//				
//				activeFilter=(activeFilter-1)%numFilters;
//				if(activeFilter<0)
//					activeFilter=0;
//				
//			}
//			
//			if(activeFilter==0)
//					filterName.setText("Vintage");
//			else if(activeFilter==1)
//					filterName.setText("Calm Breeze");
////			index=index%resIds.size();
//			
//			bmptobeManipulated=null;
//			bmptobeManipulated=BitmapFactory.decodeResource(activityContext.getResources(), resIds.get(index%resIds.size()));
//
//			Log.d("Ids "+v.getId(),"index"+index);
//			//imgView.setImageBitmap(null);
//			
//			if(!showOriginalImage)
//			{
//				if(activeFilter==0)
//				   implementVintageFilter();
//				else if(activeFilter==1)
//				   //implementXPro2Filter();
//				implementEarlyBirdFilter();
//				 
//				imgView.setImageBitmap(null);
//				imgView.setImageBitmap(bmpmutable);
//			}
//			else imgView.setImageBitmap(bmptobeManipulated);
			
			
			
		}
		};
	
	public void onStart()
	{
		super.onStart();
	
	}
	

	
	private void implementXPro2Filter()
	{
		bmpmutable=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_bgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_fgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		
		int rgbval[]=new int[bmpmutable.getWidth()*bmpmutable.getHeight()];
		bmpmutable.getPixels(rgbval, 0, bmpmutable.getWidth(), 0, 0,  bmpmutable.getWidth(), bmpmutable.getHeight());
	
		int A, R, G, B;
	    int pixel;
	    
	    double contrast_value=20;
	    double contrast = Math.pow((100 + contrast_value) / 100, 2);

		 for(int x = 0; x < bmpmutable.getWidth(); ++x) {
		      for(int y = 0; y < bmpmutable.getHeight(); ++y) {
		        // get pixel color
		        pixel = bmpmutable.getPixel(x, y);
		        // apply filtering on each channel R, G, B
		        
		        // Step 1 : Modifying input and output channels 
		        A = Color.alpha(pixel);
		        R = (int)    (((Color.red(pixel)+28) *0.77*1.09)  ) ;
		        G = (int)((Color.green(pixel)*0.77*1.06));
		        B = (int)((Color.blue(pixel)*0.77*1.12))+45;
		       
		        // Step 2: Modifying brightness and contrast ..
		        
		        R+=12;G+=12;B+=12;
		        
		        R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		        G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		        B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		        
		        
		        // Step 3: Levels again ..
		        
		        R=(int)((R+3)*1.03/2)       -23+11 ;
		        G=(int)((G+3)*1.03/1.12) -5;
		        B=(int)((B+3)*1.03*0.56) -17+15;
		        
		        R+=20;B+=20;G+=20;
//		        
//		        // Step 4: Adjust Brightness and Contrast ..
//		        contrast_value=26;
//		        contrast = Math.pow((100 + contrast_value) / 100, 2);
//		        
//		        R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//		        G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//		        B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//		       
//		        
		        
		        // Step 5:Adjust levels again ..
		        //    B-=14;
		        // Step 6:Adjust levels again ..
		           // B+=14;
		        
		        if(R > 255) { R = 255; }
			              else if(R < 0) { R = 0; }
			         
			            
			              if(G > 255) { G = 255; }
			              else if(G < 0) { G = 0; }
			         
			             
			              if(B > 255) { B = 255; }
			              else if(B < 0) { B = 0; }
			        // set new color pixel to output bitmap
			        bmpmutable_bgLayer.setPixel(x, y, Color.argb(A, R, G, B));
		      }
		 }
		 
		 Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
			
			p.setARGB(255, 252,255,235);
			p.setStyle(Style.FILL);
			
	        p.setFilterBitmap(false);
	        
	        multiplyPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
			multiplyPaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
			multiplyPaint.setFilterBitmap(false);
	
			Canvas fgCanvas=new Canvas(bmpmutable_fgLayer);

			// set the blending mode to multiply ..
			
			fgCanvas.drawRect(new Rect(0,0,bmpmutable.getWidth(),bmpmutable.getHeight()), p);
			
			
				
			//fgCanvas.drawBitmap(bmptobeManipulated,0,0, multiplyPaint);
			
			Canvas combinedCanvas=new Canvas(bmpmutable);
			
			combinedCanvas.drawBitmap(bmpmutable_fgLayer,0,0,null);
			
			combinedCanvas.drawBitmap(bmpmutable_bgLayer,0,0,multiplyPaint);
			
		 
		 
	}
	
	private void implementVividFilter()
	{
		
		
		bmpmutable=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable=sharpen(bmpmutable,11);
		
		bmpmutable_bgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_fgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		
		int rgbval[]=new int[bmpmutable.getWidth()*bmpmutable.getHeight()];
		bmpmutable.getPixels(rgbval, 0, bmpmutable.getWidth(), 0, 0,  bmpmutable.getWidth(), bmpmutable.getHeight());
		
		
		int A, R, G, B;
	    int pixel;
	    
	    
	    double contrast_value=40;
	    double contrast = Math.pow((100 + contrast_value) / 100, 2);

		 for(int x = 0; x < bmpmutable.getWidth(); ++x) {
		      for(int y = 0; y < bmpmutable.getHeight(); ++y) {
		        // get pixel color
		        pixel = bmpmutable.getPixel(x, y);
		        // apply filtering on each channel R, G, B
		        
		        A=(int)Color.alpha(pixel);
		        R = (int)(Color.red(pixel));
		        G = (int)(Color.green(pixel));
		        B = (int)(Color.blue(pixel));
		        
		        contrast_value=20;
		        contrast = Math.pow((100 + contrast_value) / 100, 2);
		        R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		        G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		       // B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//		       
		        R-=5;G-=5;B-=5;
		        
		        contrast_value=5;
		        contrast = Math.pow((100 + contrast_value) / 100, 2);
		        
		        R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		        G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		        B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
//			     
		        
		        
		        
		        if(R > 255) { R = 255; }
	              else if(R < 0) { R = 0; }
	         
	            
	              if(G > 255) { G = 255; }
	              else if(G < 0) { G = 0; }
	         
	             
	              if(B > 255) { B = 255; }
	              else if(B < 0) { B = 0; }
		        
	              bmpmutable.setPixel(x, y, Color.argb(A, R, G, B));
	               
		      }
		 }
		 
		 Canvas combinedCanvas=new Canvas(bmpmutable);
			
			combinedCanvas.drawBitmap(bmpmutable,0,0,null);
			
	}
	
	private void implementEarlyBirdFilter()
	{
		bmpmutable=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_bgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_fgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_upperfgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		
		//bmpmutable=applySaturationFilter(bmpmutable,32);
		
		int rgbval[]=new int[bmpmutable.getWidth()*bmpmutable.getHeight()];
		bmpmutable.getPixels(rgbval, 0, bmpmutable.getWidth(), 0, 0,  bmpmutable.getWidth(), bmpmutable.getHeight());
		
		
		int A, R, G, B;
	    int pixel;
	 
	    float[] pixelHSV = new float[3];
	    double contrast_value=36;
	    double contrast = Math.pow((100 + contrast_value) / 100, 2);
	    
	    int tempColor=0;
//	    
	    // scan through all pixels
	    for(int x = 0; x < bmpmutable.getWidth(); ++x) {
	      for(int y = 0; y < bmpmutable.getHeight(); ++y) {
	        // get pixel color
	        pixel = bmpmutable.getPixel(x, y);
	        
	        Color.colorToHSV(pixel, pixelHSV);
	        
	      pixelHSV[1]-=0.15;
	      
	      if(pixelHSV[1]<=0)
	    	  pixelHSV[1]=0;
	    	  
	      pixel=Color.HSVToColor(Color.alpha(pixel),pixelHSV);
	        
	        
	        
	        // apply filtering on each channel R, G, B
	        A = Color.alpha(pixel);
	        R = (int)    ((Color.red(pixel) *1.19)  )+42 ;
	        G = (int)((Color.green(pixel)*1.19));
	        B = (int)((Color.blue(pixel)*1.19));
	        
	       
	        R-=8;G-=8;B-=8;
	        
	        contrast_value=4;
	        contrast = Math.pow((100 + contrast_value) / 100, 2);
	        
	        R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
	        G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
	        B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
		     
	        
	        
	        
	        	  if(R > 255) { R = 255; }
	              else if(R < 0) { R = 0; }
	         
	            
	              if(G > 255) { G = 255; }
	              else if(G < 0) { G = 0; }
	         
	             
	              if(B > 255) { B = 255; }
	              else if(B < 0) { B = 0; }
	              
	              
	              tempColor=Color.argb(A, R, G, B);
	              
	              Color.colorToHSV(tempColor, pixelHSV);
	              
	              pixelHSV[1]-=0.17;
	    	      
	    	      if(pixelHSV[1]<=0)
	    	    	  pixelHSV[1]=0;
	    	    	  
	    	      pixel=Color.HSVToColor(Color.alpha(tempColor),pixelHSV);
//	    	        
	    	      A = Color.alpha(pixel);
	  	        R = (int)    ((Color.red(pixel) )  )-18 ;
	  	        G = (int)((Color.green(pixel)))-20;
	  	        B = (int)((Color.blue(pixel)))-20;
	              
	              

	        	  if(R > 255) { R = 255; }
	              else if(R < 0) { R = 0; }
	         
	            
	              if(G > 255) { G = 255; }
	              else if(G < 0) { G = 0; }
	         
	             
	              if(B > 255) { B = 255; }
	              else if(B < 0) { B = 0; }
	              
	              
	        // set new color pixel to output bitmap
	        bmpmutable_bgLayer.setPixel(x, y, Color.argb(A, R, G, B));
	      }
	    }
		
		Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
		
		p.setARGB(255,252,243,214);
		p.setStyle(Style.FILL);
		
        p.setFilterBitmap(false);
        
        
        multiplyPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		multiplyPaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
//		multiplyPaint.setStyle(Style.FILL);
		multiplyPaint.setFilterBitmap(false);
	//	multiplyPaint.setColorFilter(new LightingColorFilter(0xffffff, 0x880000));
		
		

		/* New foreground layer*/
		/*		
		Canvas upperfgCanvas=new Canvas(bmpmutable_upperfgLayer);
		Paint tempPaint=new Paint();
		
		tempPaint.setARGB(255, 184, 184, 184);
		tempPaint.setStyle(Style.FILL);
		tempPaint.setFilterBitmap(false);
		
		upperfgCanvas.drawRect(new Rect(0,0,bmpmutable.getWidth(),bmpmutable.getHeight()), tempPaint);
		
		// Draw transparent circle into tempBitmap
	    Paint p2 = new Paint();
	   
	    p2.setColor(0xFFFFFFFF);

	    p2.setDither(true);
	    upperfgCanvas.drawCircle(bmpmutable.getWidth()/2,bmpmutable.getHeight()/2, bmpmutable.getWidth()/2-40, p2);
		
	
		
//		int topPixel,bottomPixel;
//		  // scan through all pixels
//	    for(int x = 0; x < bmpmutable_upperfgLayer.getWidth(); ++x) {
//	      for(int y = 0; y < bmpmutable_upperfgLayer.getHeight(); ++y) {
//	        // get pixel color
//	    	  
//	    	  topPixel = bmpmutable_upperfgLayer.getPixel(x, y);
//	          bottomPixel= bmpmutable_fgLayer.getPixel(x,y);
//	        
//	        
//	        bmpmutable_fgLayer.setPixel(x, y, Color.argb(channelColorBurn(Color.alpha(topPixel),Color.alpha(bottomPixel)), channelColorBurn(Color.red(topPixel),Color.red(bottomPixel)), channelColorBurn(Color.green(topPixel),Color.green(bottomPixel)), channelColorBurn(Color.blue(topPixel),Color.blue(bottomPixel))));
//	      }
//	      }
//		
		*/
		//fgCanvas.drawBitmap(bmptobeManipulated,0,0, multiplyPaint);
		
	    
		Canvas fgCanvas=new Canvas(bmpmutable_fgLayer);

		// set the blending mode to multiply ..
		
		
		fgCanvas.drawRect(new Rect(0,0,bmpmutable.getWidth(),bmpmutable.getHeight()), p);
		Canvas combinedCanvas=new Canvas(bmpmutable);
		
//		combinedCanvas.drawBitmap(bmpmutable_upperfgLayer,0,0,null); 
		
		combinedCanvas.drawBitmap(bmpmutable_fgLayer,0,0,null);
//		
		combinedCanvas.drawBitmap(bmpmutable_bgLayer,0,0,multiplyPaint);
		
		
		
		
		
	}
	
	private void implementVintageFilter() {
		// TODO Auto-generated method stub
		bmpmutable=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_bgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		bmpmutable_fgLayer=bmptobeManipulated.copy(Bitmap.Config.ARGB_8888, true);
		
		int rgbval[]=new int[bmpmutable.getWidth()*bmpmutable.getHeight()];
		bmpmutable.getPixels(rgbval, 0, bmpmutable.getWidth(), 0, 0,  bmpmutable.getWidth(), bmpmutable.getHeight());
		
		
		int A, R, G, B;
	    int pixel;
	 
	    // scan through all pixels
	    for(int x = 0; x < bmpmutable.getWidth(); ++x) {
	      for(int y = 0; y < bmpmutable.getHeight(); ++y) {
	        // get pixel color
	        pixel = bmpmutable.getPixel(x, y);
	        // apply filtering on each channel R, G, B
	        A = Color.alpha(pixel);
	        R = (int)    ((Color.red(pixel) *1.5)  ) ;
	        G = (int)((Color.green(pixel)*1.5))+10;
	        B = (int)((Color.blue(pixel)*1.5))+45;
	        
	       
	        	  if(R > 255) { R = 255; }
	              else if(R < 0) { R = 0; }
	         
	            
	              if(G > 255) { G = 255; }
	              else if(G < 0) { G = 0; }
	         
	             
	              if(B > 255) { B = 255; }
	              else if(B < 0) { B = 0; }
	        // set new color pixel to output bitmap
	        bmpmutable_bgLayer.setPixel(x, y, Color.argb(A, R, G, B));
	      }
	    }
		
		Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
		
		p.setARGB(255, 250,220,175);
		p.setStyle(Style.FILL);
		
        p.setFilterBitmap(false);
        
        
        multiplyPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		multiplyPaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
//		multiplyPaint.setStyle(Style.FILL);
		multiplyPaint.setFilterBitmap(false);
	//	multiplyPaint.setColorFilter(new LightingColorFilter(0xffffff, 0x880000));
		
		
		Canvas fgCanvas=new Canvas(bmpmutable_fgLayer);

		// set the blending mode to multiply ..
		
		fgCanvas.drawRect(new Rect(0,0,bmpmutable.getWidth(),bmpmutable.getHeight()), p);
		
		
		
		//fgCanvas.drawBitmap(bmptobeManipulated,0,0, multiplyPaint);
		
		Canvas combinedCanvas=new Canvas(bmpmutable);
		
		combinedCanvas.drawBitmap(bmpmutable_fgLayer,0,0,null);
		
		combinedCanvas.drawBitmap(bmpmutable_bgLayer,0,0,multiplyPaint);
		
		//imgView.setImageBitmap(null);
		
		
		
	}

	
	public static Bitmap sharpen(Bitmap src, double weight) {
		  double[][] SharpConfig = new double[][] {
		    { 0 , -2    , 0  },
		    { -2, weight, -2 },
		    { 0 , -2    , 0  }
		  };
		  ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		  convMatrix.applyConfig(SharpConfig);
		  convMatrix.Factor = weight - 8;
		  return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
		}
	
	public static Bitmap applySaturationFilter(Bitmap source, int level) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[3];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
       
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                
                HSV[1] *= level;
                HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
                // take color back
                pixels[index] |= Color.HSVToColor(HSV);
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

	public static double normalize ( int col ) 
	{
		return col / 255.0;
	}
	
	public static int channelColorBurn ( int top, int bottom ) {
	    if ( top == 0 ) return 0; // We don't want to divide by zero
	    int col = (int) ( 255 * ( 1 - ( 1 - normalize( bottom ) ) / normalize( top ) ) );
	    return ( col <0 ) ? 0 : col;
	}
	
	public class CustomOnItemSelectedListener implements OnItemSelectedListener {
		 
		  public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
//		  Toast.makeText(parent.getContext(), 
//		    "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//		    Toast.LENGTH_SHORT).show();
		 
			    bmptobeManipulated=null;
				bmptobeManipulated=BitmapFactory.decodeResource(activityContext.getResources(), resIds.get(index%resIds.size()));
				filterName.setVisibility(View.VISIBLE);
				filterName.setText("Loading ...");
//				Log.d("Ids "+v.getId(),"index"+index);
				//imgView.setImageBitmap(null);
		
				if(!showOriginalImage)
				{
					if(pos==0)
					   implementVintageFilter();
					else if(pos==1)
					   implementXPro2Filter();
					else if (pos==2)
					   implementVividFilter();
					else if(pos==3)
					   implementEarlyBirdFilter();
					 
					imgView.setImageBitmap(null);
					imgView.setImageBitmap(bmpmutable);
				}
				else imgView.setImageBitmap(bmptobeManipulated);
				
				spinnerPosition=pos;
				filterName.setVisibility(View.INVISIBLE);
			  
		  }
		 
		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {
		  // TODO Auto-generated method stub
		  }
		 
		}
}