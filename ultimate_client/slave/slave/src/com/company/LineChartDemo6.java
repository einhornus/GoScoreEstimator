package com.company;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Created by Einhorn on 08.04.2017.
 */


public class LineChartDemo6 extends ApplicationFrame {
    public String outFile = "";
    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public LineChartDemo6(final String title, double[] estimates, String out) {
        super(title);
        outFile = out;
        final XYDataset dataset = createDataset(estimates);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
        setContentPane(chartPanel);
    }

    /**
     * Creates a sample dataset.
     *
     * @return a sample dataset.
     */
    private XYDataset createDataset(double[] estimates) {


        final XYSeries series1 = new XYSeries("First");


        final XYSeries series2 = new XYSeries("Second");
        for(int i = 0; i<estimates.length; i++){
            if(estimates[i] != -1){
                series2.add(i, estimates[i]);
            }
        }

        final XYSeries series3 = new XYSeries("Third");

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);


        return dataset;

    }

    /**
     * Creates a chart.
     *
     * @param dataset the data for the chart.
     * @return a chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Black win probability",      // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                false,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        try {
            writeAsPNG(chart, new FileOutputStream(outFile), 1200, 800);
        }
        catch (Exception e){

        }
        return chart;

    }

    public void writeAsPNG(JFreeChart chart, OutputStream out, int width, int height )
    {
        try
        {
            BufferedImage chartImage = chart.createBufferedImage( width, height, null);
            ImageIO.write( chartImage, "png", out );
        }
        catch (Exception e)
        {

        }
    }

    public static void draw(double[] estimates) {

        /*
        final LineChartDemo6 demo = new LineChartDemo6("Winrate", estimates, LeelaReviewMain.out);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        */



    }
}