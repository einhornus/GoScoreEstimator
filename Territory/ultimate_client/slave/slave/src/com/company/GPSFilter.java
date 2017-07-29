package com.company;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class GPSFilter {
    public static class Item {
        public String datetime;
        public double latitude;
        public double longitude;
        public double altitude;
        public double speed;
    }

    public static void writeToFile(String content, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String file) {
        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new FileReader(file);
            int c = 0;
            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static ArrayList<Item> parse(String content) {
        ArrayList<Item> res = new ArrayList<>();
        String[] lines = content.split("\r\n");
        for (int i = 1; i < lines.length; i++) {
            Item item = new Item();
            String[] values = lines[i].split("\t");
            String date = values[0];
            String time = values[1];
            if (values.length == 6) {
                double latitude = Double.parseDouble(values[2]);
                double longitude = Double.parseDouble(values[3]);
                double speed = Double.parseDouble(values[4]);
                double aptitude = Double.parseDouble(values[5]);
                item.latitude = latitude;
                item.longitude = longitude;
                item.speed = speed;
                item.altitude = aptitude;
                item.datetime = values[0] + "\t" + values[1];
            } else {
                String datetime = values[0];
                double latitude = Double.parseDouble(values[1]);
                double longitude = Double.parseDouble(values[2]);
                double speed = Double.parseDouble(values[3]);
                double aptitude = Double.parseDouble(values[4]);
                item.latitude = latitude;
                item.longitude = longitude;
                item.speed = speed;
                item.altitude = aptitude;
                item.datetime = datetime;
            }
            res.add(item);
        }
        return res;
    }

    public static String export(ArrayList<Item> items) {
        StringBuilder res = new StringBuilder();
        res.append("Date and time\tLatitude\tLongitude\tSpeed (km/h)\tAltitude (m)\r\n");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String s = item.datetime + "\t" + String.format("%1$.6f\n", item.latitude) + "\t" + String.format("%1$.6f\n", item.longitude) + "\t" + String.format("%1$.2f\n", item.speed) + "\t" + String.format("%1$.1f\n", item.altitude);
            res.append(s + "\r\n");
        }
        return res.toString();
    }

    public static double distance(Item item1, Item item2) {
        double latitudeDiff = item1.latitude - item2.latitude;
        double longitudeDiff = item1.longitude - item2.longitude;
        double latitude = ((item1.latitude + item2.latitude) / 2.0) * Math.PI / 180.0;
        double latitudeDegree = 111000;//lalitude degree is always 111 kilometers
        double longitudeDegree = latitudeDegree * Math.cos(latitude);
        double xDiff = latitudeDegree * latitudeDiff;
        double yDiff = longitudeDiff * longitudeDegree;
        double zDiff = item1.altitude - item2.altitude;
        double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        return dist;
    }

    public static ArrayList<Item> filter(ArrayList<Item> items) {
        ArrayList<Item> res = new ArrayList<>();
        for (int i = 0; i < items.size() - 1; i++) {
            Item current = items.get(i);
            Item next = items.get(i + 1);
            double dist = distance(current, next);
            if (dist < 5 || current.speed <= 0.1) {
                //System.out.println("True");
                res.add(current);
            } else {
                //System.out.println("False");
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String inFile = args[0];
        String outFile = "filtered_" + inFile;
        String inFileContent = readFile(inFile);
        ArrayList<Item> items = parse(inFileContent);
        ArrayList<Item> filtered = filter(items);
        String outFileContent = export(filtered);
        writeToFile(outFileContent, outFile);
    }
}
