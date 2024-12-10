package report.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PicTools {

    public static int bWidth = 300;
    public static int bHeight = 300;

    public static int nWidth = 800;
    public static int nHeight = 600;

    public static Color black = new Color(0,0,0);

    public static Color red = new Color(255,0,0);

    public static Color yellow = new Color(255,255,0);

    public static Color green = new Color(0,255,64);

    public static Color skyBlue = new Color(0,255,255);

    public static Color deepBlue = new Color(0,0,255);

    public static Color orange = new Color(255,128,0);

    public static Color dark = new Color(128,128,128);

    public static Color white = new Color(255,255,255);

    public static Color pink = new Color(255,64,255);

    public static Color dBlue = new Color(105, 127, 206);

    public static Color deepRed = new Color(102, 33, 43);

    public static Color lightRed = new Color(206, 16, 76);

    public static Color deepGreen = new Color(12, 92, 29);

    public static Color phuBlue = new Color(67,153,214);

    public static Color phuDark = new Color(133,136,141);

    public static Color phuBlack = new Color(50,49,54);

    public static Color phuYellow = new Color(225,234,61);

    public static Color phuDgreen = new Color(126,180,161);


    public static ArrayList<Color> colors = new ArrayList<Color>();

    public static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static Font strFont = new Font("Times New Roman", Font.BOLD,16);

    public static Font phuFont = new Font("simsun", Font.BOLD,38);

    public static Color[] colorArray = {green,deepGreen,skyBlue,deepBlue,orange,pink,dBlue,deepRed,yellow,lightRed,red};

    static {
        colors.add(green);
        colors.add(deepGreen);
        colors.add(skyBlue);
        colors.add(deepBlue);
        colors.add(orange);
        colors.add(pink);
        colors.add(dBlue);
        colors.add(deepRed);
        colors.add(yellow);
        colors.add(lightRed);
        colors.add(red);
    }



    /*
    * 从图片中截取图片并且压缩图片
    * sourceImage为原始图片
    * x为原始图片的x坐标
    * y为原始图片的y坐标
    * width为截取宽度
    * height为截取的长度
    * expand为缩放的比例
    * */
    public static BufferedImage getImage(BufferedImage sourceImage,int x,int y,int width,int height,int expand){
        try {
            return Thumbnails.of(sourceImage).sourceRegion(x, y, width, height).scale(expand).imageType(1).asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
    image 为原始的图片；
    name为需要画的图例的名称，如PDCP Throughput DL(Mbps)
    sections为区间的数组，如[-100,-90,-80,-70,-60]
    colors为对应的区间颜色
     */
    public static BufferedImage addThresholdMapBySection(BufferedImage image,String name,int[] sections,Color[] colors){
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        int width = image.getWidth() + bWidth;
        int height = sourceHeight;
        BufferedImage target = new BufferedImage(width,height, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = target.createGraphics();
        graphics.setColor(white);
        graphics.fillRect(0,0,target.getWidth(),target.getHeight());
        graphics.drawImage(image,0,0,sourceWidth,sourceHeight,null);
        //画标签头，如PDCP Throughput DL(Mbps)
        graphics.setColor(Color.black);
        graphics.setFont(new Font("SimHei",Font.PLAIN,17));
        int px = sourceWidth+25;
        int py = 20;
        graphics.drawString(name,px,py);
        //画第一个标签
        String mark = "-INF <= X < "+sections[0];
        graphics.setColor(colors[0]);
        py = py + 20;
        graphics.fillRect(px +10,py,15,15);
        graphics.setColor(Color.black);
        graphics.setFont(new Font("SimHei",Font.PLAIN,16));
        graphics.drawString(mark,px+30,py+14);

        //遍历中间标签
        for (int i = 1; i < sections.length; i++) {
            mark = sections[i-1]+" <= X < "+sections[i];
            graphics.setColor(colors[i]);
            py = py + 20;
            graphics.fillRect(px +10,py,15,15);
            graphics.setColor(Color.black);
            graphics.setFont(new Font("SimHei",Font.PLAIN,16));
            graphics.drawString(mark,px+30,py+14);
        }

        //画最后一个标签
        mark = sections[sections.length-1]+" <= X < INF";
        graphics.setColor(colors[colors.length-1]);
        py = py + 20;
        graphics.fillRect(px +10,py,15,15);
        graphics.setColor(Color.black);
        graphics.setFont(new Font("SimHei",Font.PLAIN,16));
        graphics.drawString(mark,px+30,py+14);

        return target;

    }


    public static BufferedImage addThresholdMapByType(BufferedImage image,String name,HashMap<String,Color> colorMap) throws IOException {
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        int width = image.getWidth() + bWidth;
        int height = sourceHeight;
        BufferedImage target = new BufferedImage(width,height, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = target.createGraphics();
        graphics.setColor(white);
        graphics.fillRect(0,0,target.getWidth(),target.getHeight());
        graphics.drawImage(image,0,0,sourceWidth,sourceHeight,null);
        //画标签头，如PDCP Throughput DL(Mbps)
        graphics.setColor(Color.black);
        graphics.setFont(new Font("SimHei",Font.PLAIN,17));
        int px = sourceWidth+25;
        int py = 20;
        graphics.drawString(name,px,py);

        //遍历添加下标签
        Set<String> markSet = colorMap.keySet();
        for (String mark : markSet) {
            graphics.setColor(colorMap.get(mark));
            py = py + 20;
            graphics.fillRect(px +10,py,15,15);
            graphics.setColor(Color.black);
            graphics.setFont(new Font("SimHei",Font.PLAIN,16));
            graphics.drawString(mark,px+30,py+14);

        }
        return target;
    }

    /**
     * 计算图片翻转到正常显示需旋转角度
     */
    public static int getRotateAngle(File file) {

        int angel = 0;
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
            int orientation = 0;
            Iterable<Directory> iterable = metadata.getDirectories();

            for (Iterator<Directory> iter = iterable.iterator(); iter.hasNext(); ) {
                Directory dr = iter.next();
                if (dr.getString(ExifIFD0Directory.TAG_ORIENTATION) != null) {
                    orientation = dr.getInt(ExifIFD0Directory.TAG_ORIENTATION);
                }
                Collection<Tag> tags = dr.getTags();
            }
            if (orientation == 0 || orientation == 1) {
                angel = 360;
            } else if (orientation == 3) {
                angel = 180;
            } else if (orientation == 6) {
                angel = 90;
            } else if (orientation == 8) {
                angel = 270;
            }
        } catch (Exception e) {
            System.out.println(file.getName());
            e.printStackTrace();
        }
        return angel;
    }

    /**
     * 旋转图片
     */
    public static BufferedImage rotateImage(BufferedImage bufferedImage, int angel) {
        if (bufferedImage == null) {
            return null;
        }
        if (angel < 0) {
            // 将负数角度，纠正为正数角度
            angel = angel + 360;
        }
        int imageWidth = bufferedImage.getWidth(null);
        int imageHeight = bufferedImage.getHeight(null);
        // 计算重新绘制图片的尺寸
        Rectangle rectangle = calculatorRotatedSize(new Rectangle(new Dimension(imageWidth, imageHeight)), angel);
        // 获取原始图片的透明度
        int type = bufferedImage.getColorModel().getTransparency();
        BufferedImage newImage = null;
        newImage = new BufferedImage(rectangle.width, rectangle.height, type);
        Graphics2D graphics = newImage.createGraphics();
        // 平移位置
        graphics.translate((rectangle.width - imageWidth) / 2, (rectangle.height - imageHeight) / 2);
        // 旋转角度
        graphics.rotate(Math.toRadians(angel), imageWidth / 2, imageHeight / 2);
        // 绘图
        graphics.drawImage(bufferedImage, null, null);
        return newImage;
    }

    //File是文件夹，将文件夹中的图片合并为一个照片
    public static BufferedImage combinePic(File file) throws IOException {
        if(file.exists()){
            File[] files = file.listFiles();
            if(files.length==0){
                return null;
            }
            int width = 0;
            int height= 0;
            ArrayList<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
            for (int i = 0; i < files.length; i++) {
                BufferedImage bufferedImage = readPhoto(files[i]);
                bufferedImages.add(bufferedImage);
                width = bufferedImage.getWidth() + width + 5;
                if(height<bufferedImage.getHeight()){
                    height = bufferedImage.getHeight();
                }
            }

            int position = 0;
            BufferedImage res = new BufferedImage(width, height, 1);
            for (int i = 0; i < bufferedImages.size(); i++) {
                BufferedImage bufferedImage = bufferedImages.get(i);
                int w = bufferedImage.getWidth();
                int h = bufferedImage.getHeight();
                for (int j = 0; j < w; j++) {
                    for (int k = 0; k < h; k++) {
                        int c = bufferedImage.getRGB(j,k);
                        res.setRGB(position+j,k,c);
                    }
                }
                position = position + w + 5;
            }



            return  checkReturn(res);
        }

        return null;
    }

    //第一个方框是代表列，第2个方框代表行，用于将这些图片合并为一个图片,图片要求等宽等高
    public static BufferedImage combinePicByBufferedImageArray(BufferedImage[][] bImages,int width,int height) throws IOException {
        //如果传入的宽度为0，则计算宽度
        if(width==0){
            for (int i = 0; i < bImages[0].length; i++) {
                width = width + bImages[0][i].getWidth();
            }
        }
        //如果传入的高度为0，则计算高度
        if(height==0){
            for (int i = 0; i < bImages.length; i++) {
                height = height + bImages[i][0].getHeight();
            }
        }

        BufferedImage res = new BufferedImage(width, height, 1);
        int startY = 0;
        for (int i = 0; i < bImages.length; i++) {
            int startX = 0;
            for (int j = 0; j < bImages[i].length; j++) {
                paintBufferImageOn(startX,startY,bImages[i][j],res);
                startX = startX + bImages[i][j].getWidth();
            }
            startY = startY + bImages[i][0].getHeight();
        }

        return res;
    }

    //在TargetImage的x,y位置开始画图sourceImage
    public static boolean paintBufferImageOn(int x,int y, BufferedImage sourceImage,BufferedImage targetImage){
        int tWidth = targetImage.getWidth();
        int tHeight = targetImage.getHeight();
        int sWidth = sourceImage.getWidth();
        int sHeight = sourceImage.getHeight();
        //宽度和高度超过目的图片则返回失败
        if(x+sWidth>tWidth){
            return false;
        }
        if(y+sHeight>tHeight){
            return false;
        }
        for (int i = 0; i < sHeight; i++) {
            for (int j = 0; j < sWidth; j++) {
                int rgb = sourceImage.getRGB(j, i);
                targetImage.setRGB(x+j,y+i,rgb);
            }
        }
        return true;
    }

    /**
     * 计算旋转后的尺寸
     *
     * @param src
     * @param angel
     * @return
     */
    private static Rectangle calculatorRotatedSize(Rectangle src, int angel) {
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }

    //读取照片，由于照片可能是手机拍摄，导致照片是躺着的，使用该方法读取的照片返回的是标准的未躺下的Buffer
    public static BufferedImage readPhoto(File file) throws IOException {
        int rotateAngle = getRotateAngle(file);
        BufferedImage bufferedImage = ImageIO.read(file);
        if(rotateAngle%360 ==0){
            return bufferedImage;
        }
        BufferedImage bufferedImage1 = rotateImage(bufferedImage, rotateAngle);
        String fileName = file.getName();
        int lastIndexOf=fileName.lastIndexOf(".")+1;
        String suffix = fileName.substring(lastIndexOf);
        ImageIO.write(bufferedImage1,suffix,file);
        return bufferedImage1;
    }

    //缩放照片
    public static  BufferedImage checkReturn(BufferedImage b) throws IOException {
        int height = b.getHeight();
        int width = b.getWidth();
        double size = Math.max(height/800,width/800);
        if(size>8){
            size = 8.0;
        }
        double expand = 1.0/size;
        if(expand>1){
            return b;
        }
        return Thumbnails.of(b).scale(expand).asBufferedImage();
    }


    //输入照片，返回3个元素的数组，第1个为经度，第2个是维度，第3个是高德查询的地址
    public static String[] getPhotoMSG(File file) throws ImageProcessingException, IOException, MetadataException {
        String[] res = new String[3];
        Metadata metadata = null;
        metadata = ImageMetadataReader.readMetadata(file);
        GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
        if (gpsDirectory!=null) {
            Collection<Tag> tags = gpsDirectory.getTags();
            for (Tag tag : tags) {
                if(tag.getTagType()==2){
                    String[] temp = tag.toString().split(" ");
                    double d = Double.parseDouble(temp[4].split("/")[0]);
                    double f = Double.parseDouble(temp[5].split("/")[0]);
                    double m = Double.parseDouble(temp[6].split("/")[0]);
                    res[1] = ((int)((d + (f +m/60)/60)*10000))/10000.0+"";
                }
                if(tag.getTagType()==4){
                    String[] temp = tag.toString().split(" ");
                    double d = Double.parseDouble(temp[4].split("/")[0]);
                    double f = Double.parseDouble(temp[5].split("/")[0]);
                    double m = Double.parseDouble(temp[6].split("/")[0]);
                    res[0] = ((int)((d + (f +m/60)/60)*10000))/10000.0+"";
                }
            }
            String addressByGaode = HttpTools.getAddressByGaode(res[0], res[1]);
            res[2] = addressByGaode;
            return res;
        }
        return null;
    }

    public static double calExpand(int width,int height,int nWidth,int nHeight){
        double expand1 = nWidth*1.0/width;
        double expand2 = nHeight*1.0/height;
        return Math.max(expand1,expand2);
    }

    public static BufferedImage drawPeak(File file){
        return null;

    }




}
