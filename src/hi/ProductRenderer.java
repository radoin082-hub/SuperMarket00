import javax.swing.*;
import java.awt.*;

class ProductRenderer extends JLabel implements ListCellRenderer<Product> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Product> list, Product value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getName());
        setIcon(new ImageIcon(value.getImageUrl()));
        return this;
    }
}
