package pogoBot1;

import java.io.IOException;

public class Taining {
	Runtime r= Runtime.getRuntime();
	
	void beginProcess() {
		while(true) {
		clickBlanche();
		System.out.println("training member");
		Sleep(2000);
		clickTrain();
		System.out.println("clicked train");
		Sleep(2000);
		clickBlanche();
		System.out.println("select league");
		Sleep(2000);
		clicknext();
		System.out.println("select team");
		Sleep(12000);
		clicknext();
		System.out.println("select next");
		Sleep(2000);
		}
	}
	
	void Sleep(int delay) {
		try {
			Thread.sleep(delay);
		}
		catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	void clickBlanche() {
		makeTap(540, 1900);
	}
	
	void clickTrain() {
		makeTap(540, 1700);
	}
	
	void clicknext() {
		makeTap(540, 2100);
	}
	
	void makeTap(int cordX,int cordY) {
		String command="cmd.exe /c \"adb shell input tap "+cordX+" "+cordY+"\"";
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
}
