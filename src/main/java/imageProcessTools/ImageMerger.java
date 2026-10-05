package imageProcessTools;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageMerger {

    //This is a replacement of snapshot method containing problems of image compression causing blurr on the image
    public static WritableImage merge(Image im1 , Image im2){
        //putting im2 over ima1
        PixelReader pix1 = im1.getPixelReader();
        PixelReader pix2 = im2.getPixelReader();
        WritableImage result = new WritableImage((int) im1.getWidth(), (int) im1.getHeight());
        for (int y = 0 ; y < im1.getHeight() ; ++y){
            for (int x = 0 ; x < im1.getWidth(); ++x){

                if(pix2.getColor(x,y).equals(Color.TRANSPARENT) || pix2.getColor(x,y).getOpacity() < 0.9 ){//Be aware that it can ignore layers that are drawn in none totally opaque colors

                    result.getPixelWriter().setColor(x,y,pix1.getColor(x,y));

                }else{
                    result.getPixelWriter().setColor(x,y,pix2.getColor(x,y));

                }
            }
        }

        return result;
    }

    public static void mergeBack(StackPane stack, ImageView imView, int stackMaxSize ){

        if(stack.getChildren().size() >= stackMaxSize - 1) {

            ImageView root =  new ImageView( imView.getImage() ) ;
            ImageView backLayer = (ImageView) stack.getChildren().get(1) ;


            ImageView view = root;
            BufferedImage bImage = SwingFXUtils.fromFXImage(view.getImage(), null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream ;

            ImageView backImageView ;


            try {
                ImageIO.write(bImage, "png", outputStream);
                byte[] res  = outputStream.toByteArray();
                inputStream = new ByteArrayInputStream(res);
                Image im = new Image(inputStream , backLayer.getImage().getWidth(), backLayer.getImage().getHeight(), true, true);
                backImageView = new ImageView( ImageMerger.merge(im, backLayer.getImage()) );
            } catch (IOException e) {
                backImageView = new ImageView( ImageMerger.merge(root.getImage(), backLayer.getImage()) );
                e.printStackTrace();
            }


            stack.getChildren().remove(1);

            StackPane tempStack = new StackPane();
            ImageSizerPositionner.setDimensions(tempStack,stack.getWidth(),stack.getHeight());
            root.setFitWidth(imView.getFitWidth());
            root.setPreserveRatio(true);

            tempStack.getChildren().add(root);
            tempStack.getChildren().add(new ImageView(backLayer.getImage()));

            SnapshotParameters spa = new SnapshotParameters();
            spa.setFill(Color.TRANSPARENT);


            backImageView.fitWidthProperty().bind(stack.widthProperty());
            backImageView.setPreserveRatio(true);

            //ctrPane.getChildren().add(new ImageView());

            imView.setImage(backImageView.getImage());


        }
    }


}
