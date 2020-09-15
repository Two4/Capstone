package za.ac.mandela.WRPV301.Capstone.Util;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableCallable;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A collection of useful utilities that don't go anywhere in particular
 */
public class Utils {
    /**
     * A list of monospace fonts installed on the system
     */
    public static final ImmutableList<String> monoSpaceFonts = getMonoFontFamilyNames();

    /**
     * A class for containing and manipulating XY screenspace coordinates
     */
    public static class XY {
        /**
         * X Coordinate
         */
        public final double X;
        /**
         * Y Coordinate
         */
        public final double Y;

        /**
         * Private constructor
         * @param x X coordinate
         * @param y Y coordinate
         */
        private XY(double x, double y) {
            X = x;
            Y = y;
        }

        /**
         * Static constructor
         * @param x X coordinate
         * @param y Y coordinate
         * @return a new XY instance
         */
        public static XY of(double x, double y) {
            return new XY(x, y);
        }

        /**
         * Produces a new XY instance that is equal to this instance with the given translation applied
         * @param translation the XY value to translate the current instance by
         * @return a new XY instance that is equal to this instance with the given translation applied
         */
        public XY translate(XY translation) {
            return new XY(X + translation.X, Y + translation.Y);
        }

        /**
         * Produces a new XY instance that is equal to this instance with the given translation applied
         * @param xTranslation the X value to translate the current instance by
         * @param yTranslation the Y value to translate the current instance by
         * @return a new XY instance that is equal to this instance with the given translation applied
         */
        public XY translate(double xTranslation, double yTranslation) {
            return new XY(X + xTranslation, Y + yTranslation);
        }

    }

    /**
     * Like the XY class, but represents a space, rather than a point
     */
    public static class XYWH {
        /**
         * X Coordinate
         */
        public final double X;
        /**
         * Y Coordinate
         */
        public final double Y;
        /**
         * W Coordinate
         */
        public final double W;
        /**
         * H Coordinate
         */
        public final double H;

        /**
         * Private constructor
         * @param x X coordinate
         * @param y Y coordinate
         * @param w Width
         * @param h Height
         */
        private XYWH(double x, double y, double w, double h) {
            X = x;
            Y = y;
            W = w;
            H = h;
        }

        /**
         * Static constructor
         * @param x X coordinate
         * @param y Y coordinate
         * @param w Width
         * @param h Height
         * @return a new XYWH instance
         */
        public static XYWH of(double x, double y, double w, double h) {
            return new XYWH(x, y, w, h);
        }

        /**
         * Static contructor that take an XY point as an argument
         * @param origin XY coordinate this shape must start at
         * @param w Width
         * @param h Height
         * @return a new XYWH instance
         */
        public static XYWH of(XY origin, double w, double h) {
            return new XYWH(origin.X, origin.Y, w, h);
        }

        /**
         * Produces a new XYWH instance that is equal to this instance with the given translation applied
         * @param translation the XY value to translate the current instance by
         * @return a new XYWH instance that is equal to this instance with the given translation applied
         */
        public XYWH translate(XY translation) {
            return new XYWH(X + translation.X, Y + translation.Y, W, H);
        }

        /**
         * Produces a new XYWH instance that is equal to this instance with the given translation applied
         * @param xTranslation the X value to translate the current instance by
         * @param yTranslation the Y value to translate the current instance by
         * @return a new XYWH instance that is equal to this instance with the given translation applied
         */
        public XYWH translate(double xTranslation, double yTranslation) {
            return new XYWH(X + xTranslation, Y + yTranslation, W, H);
        }

        /**
         * Produces a new XYWH that is equal to this one, but has been translated by its width and height in the
         * specified direction
         * @param to the {@link Direction} in which to translate this XYWH instance
         * @return a new XYWH that is equal to this one, but has been translated by its width and height in the
         * specified direction
         */
        public XYWH translate(Direction to) {
            return new XYWH(to.translateX(X, W), to.translateY(Y, H), W, H);
        }

        /**
         * Draws a solid rectangle with the given {@link GraphicsContext} using the current XYWH instance as coordinates
         * @param graphicsContext the {@link GraphicsContext} to use to draw the resultant rectangle
         * @param fill the {@link javafx.scene.paint.Color}
         */
        public void rect(GraphicsContext graphicsContext, Paint fill) {
            graphicsContext.setFill(fill);
            graphicsContext.fillRect(X, Y, W, H);
        }

        /**
         * Draws the mapped image resource for the class of a given object at the coordinates of this XYWH instance
         * @param graphicsContext the {@link GraphicsContext} to use to draw the resultant image
         * @param object the object to map to an image resource
         */
        public void img(GraphicsContext graphicsContext, Object object) {
            graphicsContext.drawImage(ResourceMapping.of(object), X, Y, W, H);
        }
    }

    /**
     * A {@link Random} instance used for dice, coin and percentile rolls
     */
    private static final Random random = new Random();
    /**
     * Messages bus used to pass events to their handlers
     */
    public static final EventBus eventBus = new EventBus();

    /**
     * A man in Bangladesh rolls a dice with the specified number of sides and returns the result to this method, which
     * returns it to the caller. His name is Rajesh, be nice to Rajesh.
     * @param sides the number of sides for the dice (i.e, the upper bound of the result, exclusive)
     * @return an integer between 0 and {@code sides}
     */
    public static int d(int sides) {
        return random.nextInt(sides);
    }

    /**
     * Performs {@code d(sides)} exactly {@code numDice} times, and returns the total
     * @param sides the number of sides for the dice (i.e, the upper bound of each roll result, exclusive)
     * @param numDice the number of ({@code sides})-sided dice to roll and sum
     * @return the sum of the dice rolls
     */
    public static int d(int sides, int numDice) {
        int total = 0;
        for (int i = 0; i < numDice; i++) {
            total += d(sides);
        }
        return total;
    }

    /**
     * Like a coin flip, but the likelihood of the result can be specified.
     * Rolls a d100, and if the result is less than {@code percent}, returns true (hence 'p', for percentile)
     * @param percent the likelihood, in percentage, of a {@code true} result being returned
     * @return true or false
     */
    public static boolean p(int percent) {
        return d(100) < percent;
    }

    /**
     * Flips a coin and returns the result
     * @return true if the coin flip passes, false if not
     */
    public static boolean coin() {
        return random.nextBoolean();
    }

    /**
     * Return a list of all the mono-spaced fonts on the system.
     * <a href="https://yo-dave.com/2015/07/27/finding-mono-spaced-fonts-in-javafx/">https://yo-dave.com/2015/07/27/finding-mono-spaced-fonts-in-javafx/</a>
     * @author David D. Clark, with modifications
     *
     * @return A list of all of the mono-spaced fonts on the system.
     */
    private static ImmutableList<String> getMonoFontFamilyNames() {

        // Compare the layout widths of two strings. One string is composed
        // of "thin" characters, the other of "wide" characters. In mono-spaced
        // fonts the widths should be the same.

        final Text thinTxt = new Text("1 l"); // note the space
        final Text thickText = new Text("MWX");

        List<String> fontFamilyList = Font.getFamilies();
        ArrayList<String> monoFamilyList = new ArrayList<>();

        Font font;

        for (String fontFamilyName : fontFamilyList) {
            font = Font.font(fontFamilyName, FontWeight.NORMAL, FontPosture.REGULAR, 14.0d);
            thinTxt.setFont(font);
            thickText.setFont(font);
            if (thinTxt.getLayoutBounds().getWidth() == thickText.getLayoutBounds().getWidth()) {
                monoFamilyList.add(fontFamilyName);
            }
        }

        return ImmutableList.copyOf(monoFamilyList);
    }
}
