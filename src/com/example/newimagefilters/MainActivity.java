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
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity 
{
	
	ImageView imgView;
	Bitmap bmptobeManipulated=null,bmpmutable=null,bmpmutable_bgLayer=null,bmpmutable_fgLayer=null;
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
//		implementXPro2Filter();
		
		
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
				
				
			}
			
			else if(v.getId()==127)
			{			
				activeFilter=(activeFilter+1)%numFilters;
				
				
			}
			
			else if(v.getId()==126)
			{
				
				activeFilter=(activeFilter-1)%numFilters;
				if(activeFilter<0)
					activeFilter=0;
				
			}
			
			if(activeFilter==0)
					filterName.setText("Vintage");
			else if(activeFilter==1)
					filterName.setText("Calm Breeze");
//			index=index%resIds.size();
			
			bmptobeManipulated=null;
			bmptobeManipulated=BitmapFactory.decodeResource(activityContext.getResources(), resIds.get(index%resIds.size()));

			Log.d("Ids "+v.getId(),"index"+index);
			//imgView.setImageBitmap(null);
			
			if(!showOriginalImage)
			{
				if(activeFilter==0)
				   implementVintageFilter();
				else if(activeFilter==1)
				   implementXPro2Filter();
				
				 
				imgView.setImageBitmap(null);
				imgView.setImageBitmap(bmpmutable);
			}
			else imgView.setImageBitmap(bmptobeManipulated);
			
			
			
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

	
	
	
}