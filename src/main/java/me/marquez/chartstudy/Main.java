package me.marquez.chartstudy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) throws Exception{
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphic = (Graphics2D) image.getGraphics();
        graphic.setColor(Color.BLACK);
        graphic.fillRect(0, 0, image.getWidth(), image.getHeight());

        Map<Integer, Double> data = loadData();

        AtomicReference<Integer> xMax = new AtomicReference<>(0);
        AtomicReference<Double> yMax = new AtomicReference<>(0D);
        data.keySet().forEach(value -> xMax.set(Math.max(xMax.get(), value)));
        data.values().forEach(value -> yMax.set(Math.max(yMax.get(), value)));

        graphic.setColor(Color.RED);
        data.forEach((key, value) -> {
            int x = (int)(image.getWidth()/(double)xMax.get()*key);
            int y = (int)(image.getHeight()/yMax.get()*value);
            graphic.fillOval(x, image.getHeight()-y, 5, 5);
        });

        double sumX = Arrays.stream(data.keySet().toArray(new Integer[0])).mapToInt(Integer::valueOf).sum();
        double avgX = sumX/xMax.get();
        double sumY = Arrays.stream(data.values().toArray(new Double[0])).mapToDouble(Double::valueOf).sum();
        double avgY = sumY/xMax.get();

        double sumTop = 0, sumBottom = 0;
        for(Map.Entry<Integer, Double> entry : data.entrySet()) {
            sumTop += (entry.getKey() - avgX)*(entry.getValue() - avgY);
            sumBottom += Math.pow((entry.getKey() - avgX), 2);
        }
        double slope = sumTop/sumBottom;
        double intercept = 0;//avgY - slope*avgX;

        graphic.setColor(Color.BLUE);
//        graphic.drawLine(0, image.getHeight(), image.getWidth(), 0);
        graphic.drawLine(0, image.getHeight()-(int)(image.getHeight()/yMax.get()*intercept), image.getWidth(), image.getHeight()-(int)(image.getHeight()/yMax.get()*(xMax.get()*slope+intercept)));


        ImageIO.write(image, "png", new File("./img.png"));
    }

    public static Map<Integer, Double> loadData() throws Exception{
        Map<Integer, Double> map = new HashMap<>();
        File file = new File("statistics.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String s = "";
        while((s = reader.readLine()) != null) {
            String[] split = s.split(", ");
            map.put(Integer.parseInt(split[0]), Double.parseDouble(split[1]));
        }
        reader.close();
        return map;
    }
}
