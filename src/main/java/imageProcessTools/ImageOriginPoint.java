package imageProcessTools;

public class ImageOriginPoint{
    private double x;
    private  double y;

    public ImageOriginPoint(double x0, double y0){
        x = x0;
        y = y0;
    }

    public void setOrigin(double x0 ,double y0){
        x = x0;
        y = y0;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }
}
