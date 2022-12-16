package polygonapprox;

public class Error {
	double maxErr = 0.0;
	double ISErr = 0.0;
	double sumErr = 0.0;

	Error(double maxErr, double ISErr, double sumErr){
		this.maxErr = maxErr;
		this.ISErr = ISErr;
		this.sumErr = sumErr;
	}
	public void printVal(){
		System.out.println("maxErr = "+maxErr+" ISErr = "+ISErr+" sumErr = "+sumErr);
	}
}
