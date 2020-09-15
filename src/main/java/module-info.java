/**
 * The over-arching package for this Capstone project
 */
module za.ac.mandela.WRPV301.Capstone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires com.google.common;
    requires org.apache.commons.lang3;

    opens za.ac.mandela.WRPV301.Capstone.UI to javafx.fxml, javafx.controls, javafx.graphics, javafx.base, com.google.common;
    opens za.ac.mandela.WRPV301.Capstone to javafx.fxml, javafx.controls, javafx.graphics, javafx.base, com.google.common;
    exports za.ac.mandela.WRPV301.Capstone;
}