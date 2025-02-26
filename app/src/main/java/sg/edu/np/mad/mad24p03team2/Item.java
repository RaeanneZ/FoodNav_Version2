package sg.edu.np.mad.mad24p03team2;

/**
 * Item
 * Object class to store information of item_view.xml
 * Used to display Search Food Results
 */
public class Item {

    String name;
    Double calNum;
    Double servingSize;

    public Item(String name, Double calNum, Double servingSize) {
        this.name = name;
        this.calNum = calNum;
        this.servingSize = servingSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCalNum() {
        return calNum;
    }

    public void setCalNum(Double calNum) {
        this.calNum = calNum;
    }

    public Double getServingSize() {
        return servingSize;
    }

    public void setServingSize(Double servingSize) {
        this.servingSize = servingSize;
    }
}
