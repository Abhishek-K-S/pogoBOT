package pogoBot1;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class AllProcesses{
	BufferedImage loaded;
	BufferedImage mainScrCroped;
	
	Runtime r= Runtime.getRuntime();
	
	
	boolean inscenced=true;
	
	//setup
	int scrHeight=1920;
	int scrWidth=1080;
	int notchStartY;
	int charStartX,charStartY,charEndX,charEndY;//setup
	
	//setup
	int cropX,cropY,cropW,cropH;
	int objectX,objectY;
	
	int scrTapX,scrTapY;
	int menuCordX,menuCordY;	
	int exitCordX,exitCordY;
	int firstEggX,firstEggY;
	int firstIncubatorX,firstIncubatorY;
	int incubateX,incubateY;
	int transferYesX,transferYesY;
	int transferX,transferY;
	int startSwipeY;  //pokeballthrow
	int shinyNoX,shinyNoY;
	int toeX,toeY;
	//setup
	
	int objectLen=120;	//setup
	int hatchCount=0;
	
	String monCamDist="D:/PogoProcess/monCamDist.txt";
	String scrText=null;
	
	Scanner sc;

	void beginProcess(){
		System.out.println("Started");
		screenCap();
		this.loaded=openBuffImage();
		testDisplay();
		openMonDataFile();
		System.out.println("entering the loop");
		while(true) {
			Sleep(2000);
			screenCap();
			this.loaded=openBuffImage();
			if(isMainScr()) {
				System.out.println("main");
				mainScrCroped=cropImage(loaded,cropX,cropY,cropW,cropH);
				mainScrCroped=mainScrFilter(mainScrCroped);
				mainScrCroped=removeNoise(mainScrCroped);
				if(findObject()) {
					calcScrCords();
					makeTap(scrTapX,scrTapY);
				}
				else {
					System.out.println("NO mons detected, Clicking toe");
					tapToe();
				}
				Sleep(4000);
				continue;
			}
			readText();
			if(scrText.contains("HP")||scrText.contains("GYMS&RAIDS")) {
				//POKE stats
				System.out.println("Stats");
				tapMenu();
				continue;
			}
			if(scrText.contains("Gotcha")||scrText.contains("CAUGHT")&&scrText.contains("TOTAL")) {
				//CAUGHT SCREEN
				System.out.println("Caught");
				tapMenu();
				continue;
			}
			if(scrText.contains("APPRAISE")||scrText.contains("TRANSFER")) {
				//MENU
				tapTransfer();
				continue;
			}
			
			if(scrText.contains("Shiny")&&scrText.contains("really")) {
				//shiny mon
				tapShinyNo();
				Sleep(1000);
				tapExit();
				Sleep(2000);
				continue;
			}
			if(scrText.contains("undo")&&scrText.contains("action")) {
				//transfer screen click yes
				tapTransferYes();
				Sleep(2000);
				continue;
			}
			if(scrText.contains("EGGS")&&scrText.contains("km")) {
				//egg invetory
				hatchCount=hatchCount+1;
				if(hatchCount%2==0) {
					tapExit();
					continue;
				}
				tapFirstEgg();
				continue;
			}
			if(scrText.contains("Use an incubator")) {
				//select incubate
				tapIncubate();
				continue;
			}
			
			if(scrText.contains("INCUBATORS")) {
				//ALL INCUBATORS, SELECT FIRST
				tapFirstIncubator();
				Sleep(4000);
				continue;
			}
			if(scrText.contains("Oh?")||scrText.contains("0h?")) {
				//egg hatch screen
				tapExit();
				Sleep(5000);
				continue;
			}
			counterFilter();
			readText();
			if(scrText.toUpperCase().contains("CP")) {
				System.out.println("ecncounter");
				//encounter
				catchMon();
				//Sleep(1000);
				continue;
			}
			if(scrText.contains("caught!")||scrText.contains("Gotcha")||scrText.contains("CAUGHT")&&scrText.contains("TOTAL")) {
				//CAUGHT SCREEN
				System.out.println("Caught");
				tapMenu();
				continue;
			}
			//tapExit();
			//add more here
			System.out.println("couldnt recognize");
			//System.out.println(scrText);
		}
	}
	
	void testDisplay() {
		System.out.println("Setting up display");
		scrHeight=loaded.getHeight();
		scrWidth=loaded.getWidth();
		if(isNotched()) {
			scrHeight=notchStartY-1;
		}
		shinyNoX=scrWidth/2;
		shinyNoY=(int)(scrHeight*0.6042);
		
		firstEggX=(int)(scrWidth*0.173);
		firstEggY=(int)(scrHeight*0.271);
		
		transferYesX=scrWidth/2;
		transferYesY=(int)(scrHeight*0.5782);
		
		startSwipeY=(int)(scrHeight*0.9063);
		
		firstIncubatorX=(int)(scrWidth*0.145);
		firstIncubatorY=(int)(scrHeight*0.7396);
		
		exitCordX=scrWidth/2;
		exitCordY=(int)(scrHeight*0.9235);
		
		menuCordX=(int)(scrWidth*0.86575);
		menuCordY=(int)(scrHeight*0.9235);
		
		incubateX=scrWidth/2;
		incubateY=(int)(scrHeight*0.729);
		
		transferX=(int)(scrWidth*0.6575);
		transferY=(int)(scrHeight*0.82);
		
		toeX=(int)(0.5*scrWidth);
		toeY=(int)(0.641*scrHeight);
		
		cropX=(int)(0.1482*scrWidth);
		cropY=(int)(0.5266*scrHeight);
		cropW=(int)(0.7019*scrWidth);
		cropH=(int)(0.2761*scrHeight);
		
		charStartX=(int)(0.43982*scrWidth)-cropX;
		charStartY=(int)(0.6*scrHeight)-cropY;
		charEndX=(int)(0.5787*scrWidth)-cropX;
		charEndY=(int)(0.6834*scrHeight)-cropY;
		
		System.out.println("done");
	}
	
	boolean isNotched() {
		int startPix=(int)(0.8*scrHeight);
		int pixCount=0;
		for(int i=startPix;i<scrHeight;i++) {
			Color clr=new Color(loaded.getRGB(50, i));
			int red;
			if(((red=clr.getRed())==clr.getBlue())&&(red<5)) {
				pixCount++;
				if(pixCount>20) {
					notchStartY=i-pixCount+2;
					return true;
				}
			}
		}
		return false;
	}
	
	void calcScrCords() {
		scrTapX=cropX+objectX+objectLen/2;
		scrTapY=cropY+objectY+objectLen/2;
	}
	
	void readText() {
		Tesseract tess=new Tesseract();
		try {
			tess.setDatapath("D:\\Applications\\eclipsPugins\\Tess4J-3.4.8-src\\Tess4J\\tessdata");
			scrText=tess.doOCR(loaded);
		}
		catch(TesseractException e) {
			e.printStackTrace();
		}
	}
	
	void screenCap()  {
			String command="cmd.exe /c \"adb shell screencap -p /sdcard/PoGoscrnCap.jpeg && adb pull /sdcard/PoGoscrnCap.jpeg D:\\PogoProcess\"";
			adbCommand(command);
	}
	
	void Sleep(int delay) {
		try {
			Thread.sleep(delay);
		}
		catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	BufferedImage openBuffImage() {
		File imgFile=new File("D:\\PogoProcess\\PoGoscrnCap.jpeg");
		BufferedImage buff = null;
		try {
		buff=ImageIO.read(imgFile);
		return buff;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return buff;
	}
	
	BufferedImage removeNoise(BufferedImage buff) {
		int first,last;
		Color cl=new Color(250,250,250);
		for(int i=0;i<buff.getHeight();i++) {
			for(int j=9;j<buff.getWidth();j++) {
				first=(new Color(buff.getRGB(j,i))).getRed();
				last=(new Color(buff.getRGB(j-9,i))).getRed();
				if(!(first==last&&first>200)) {
					continue;
				}
				for(int k=9;k>-1;k--) {
					buff.setRGB(j-k,i,cl.getRGB());
										
				}
			}
		}
		for(int i=0;i<buff.getWidth();i++) {
			for(int j=9;j<buff.getHeight();j++) {
				first=(new Color(buff.getRGB(i,j))).getRed();
				last=(new Color(buff.getRGB(i,j-9))).getRed();
				if(!(first==last&&first>200)) {
					continue;
				}
				for(int k=9;k>-1;k--) {
					buff.setRGB(i,j-k,cl.getRGB());										
				}
			}
		}
		for(int i=0;i<buff.getHeight();i++) {
			for(int j=9;j<buff.getWidth();j++) {
				first=(new Color(buff.getRGB(j,i))).getRed();
				last=(new Color(buff.getRGB(j-9,i))).getRed();
				if(!(first==last&&first>200)) {
					continue;
				}
				for(int k=9;k>-1;k--) {
					buff.setRGB(j-k,i,cl.getRGB());
										
				}
			}
		}
		return buff;
	}
	
	void catchMon() {
		double dist=getDist();
		if(dist==50) {
			System.out.println("Default distance");
			dist=5.5;
		}	
		int totalSwipe=(int)(dist*scrHeight/8.1);
		int endSwipeY=startSwipeY-totalSwipe;
		
		makeSwipe(scrWidth/2,startSwipeY,scrWidth/2,endSwipeY,190);
	}
	
	double getDist() {
		double dist=50;
		while(sc.hasNext()) {
			String line=sc.nextLine();
			int indexComma=line.indexOf(",");
			String name=line.substring(0,indexComma-1);
			if(this.scrText.contains(name)) {
				System.out.println(line.substring(0,indexComma));
				dist=Double.parseDouble(line.substring(indexComma+1));
				break;
			}
		}
		openMonDataFile();
		return dist;
	}
	
	void openMonDataFile() {
		try {
			sc=new Scanner(new File(monCamDist));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	BufferedImage cropImage(BufferedImage buff,int cordX,int cordY,int width,int height) {
		return buff.getSubimage(cordX,cordY,width,height);
	}
	
	BufferedImage mainScrFilter(BufferedImage inImage) {
		for(int i=0;i<inImage.getHeight();i++) {
			for(int j=0;j<inImage.getWidth();j++) {
				Color inColor =new Color(inImage.getRGB(j,i));
				int red=inColor.getRed();
				int green=inColor.getGreen();
				int blue=inColor.getBlue();
				if((i>=charStartY&&i<=charEndY)&&(j>=charStartX&&j<=charEndX)) {
					red=green=blue=250;
				}
				//grass,water,yellow,road,darkgrass
				else if((green>230&&red>80&&red<210&&blue>90&&blue<200)||(red>50&&red<80&&green>180&&green<200&&blue>205&&blue<225)||(red>250&&green>250&&blue>144&&blue<160)||(red>50&&red<85&&green>144&&green<170&&blue>130&&blue<165)||(red<20&&green>150&&green<180&&blue>120&&blue<145)) {
					red=green=blue=250;
				}
				//if inscence is on
				else if(inscenced&&red>210&&green>185&&green<195&&blue>205&&blue<240) {
					red=green=blue=250;
				}
				//if pokestop lured
				else if(red>170&&red<185&&green>245&&blue>252) {
					red=green=blue=250;
				}
				else {
					red=green=blue=20;
				}
				inColor=new Color(red,green,blue);
				inImage.setRGB(j, i, inColor.getRGB());
			}
		}
		return inImage;
	}
	
	boolean isMainScr() {
		//System.out.println("checking if main");
		int i=scrWidth/2;
		int dif=(int) (0.0235*scrHeight);
		int var=(int) (0.9262*scrHeight-dif);
		for(int j=var;j<var+100;) {
			//System.out.println("j= "+j);
			Color clr=new Color(loaded.getRGB(i,j));
			int red=clr.getRed();
			int green=clr.getGreen();
			if(!((red>200)||(red==green&&red>250)||(red==green&&red==185))) {
				System.out.println("exiting isMain false");
				return false;
				
			}
			j+=dif;
		}
		//cmpltCode
		//System.out.println("exiting isMain true");
		return true;
	}
	
	void counterFilter() {
		for(int i=0;i<scrHeight;i++) {
			for(int j=0;j<scrWidth;j++) {
				Color clr=new Color(loaded.getRGB(j, i));
				int red=clr.getRed();
				//int green=clr.getGreen();
				if(red<160) {
					clr=new Color(10,10,10);
					loaded.setRGB(j, i, clr.getRGB());
				}
			}
		}
	}
	
	boolean findObject() {
		for(int i=0;i<cropH-objectLen;i++) {
			for(int j=0;j<cropW-objectLen;j++) {
				Color clr=new Color(mainScrCroped.getRGB(j,i));
				if(clr.getRed()<50) {
					objectX=j;
					objectY=i;
					BufferedImage cropedObject=cropImage(mainScrCroped,objectX,objectY,objectLen,objectLen);
					boolean a=isObject(cropedObject);
					if(a) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	boolean isObject(BufferedImage buff) {
		int darkPixCount=0;
		for(int i=0;i<objectLen;i++) {
			for(int j=0;j<objectLen;j++) {
				Color c=new Color(buff.getRGB(j,i));
				if(c.getRed()<50)
					darkPixCount++;
			}
		}
		if(darkPixCount>((objectLen*objectLen)/8)) {
			return true;
		}
		return false;
	}
	
	void makeSwipe(int startX,int startY,int endX,int endY,int duration) {
		String command="cmd.exe /c \"adb shell input swipe "+startX+" "+startY+" "+endX+" "+endY+" "+duration+"\"";
		//System.out.println(command);
		adbCommand(command);
	}
	
	void adbCommand(String command) {
		try {
			Process p=this.r.exec(command);
			p.waitFor();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();		}
	}
	
	void makeTap(int cordX,int cordY) {
		String command="cmd.exe /c \"adb shell input tap "+cordX+" "+cordY+"\"";
		adbCommand(command);
	}
	
	void tapExit() {
		makeTap(exitCordX,exitCordY);
	}
	
	void tapMenu() {
		makeTap(menuCordX,menuCordY);
	}
	
	void tapIncubate() {
		makeTap(incubateX,incubateY);
	}
	
	
	void tapFirstEgg() {
		makeTap(firstEggX,firstEggY);
	}
	
	void tapTransfer() {
		makeTap(transferX,transferY);
	}
	
	void tapTransferYes() {
		makeTap(transferYesX,transferYesY);
	}
	void tapShinyNo() {
		makeTap(shinyNoX,shinyNoY);
	}
	
	void tapToe() {
		makeTap(toeX,toeY);
	}
	
	void tapFirstIncubator() {
		makeTap(firstIncubatorX,firstIncubatorY);
	}
}
