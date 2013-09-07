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
import android.widget.ImageView;


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
		
		implementVintageFilter();
		imgView.setImageBitmap(bmpmutable);
		
		origButton.setText("Original");
	}
	
	
	
	public OnClickListener filtersLayerClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) 
		{
			
			if(v.getId()==123)
				index=index+1;
			if(v.getId()==124&&index!=0)
				index=index-1;
			if(v.getId()==125)
			{
				showOriginalImage=!showOriginalImage;
				
				if(showOriginalImage)  
				origButton.setText("Filtered");
				else origButton.setText("Original");
				origButton.invalidate();
				
				
			}
//			index=index%resIds.size();
			
			bmptobeManipulated=null;
			bmptobeManipulated=BitmapFactory.decodeResource(activityContext.getResources(), resIds.get(index%resIds.size()));

			Log.d("Ids "+v.getId(),"index"+index);
			//imgView.setImageBitmap(null);
			
			if(!showOriginalImage)
			{
				implementVintageFilter();
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