package data;



// LIBRARIES USED
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;



/**
 * @author  Paul Johannes Aru
 * @version 0.9
 * @since   2021-03-27
 */
public class UniversalTableModel extends AbstractTableModel {
    //region TABLE VARIABLES
    private String[] columnTitles = {};
    private ArrayList<String[]> cellData = new ArrayList<>();
    private boolean editMode = false;
    //endregion



    //region TABLE INTERACTION METHODS
    /**
     * Clear and Fill Table.
     *
     * @param data Row Contents List.
     * @param titles Column Titles.
     */
    public void fillTable(List<String[]> data, String[] titles) {
        cellData.clear();
        columnTitles = titles;
        for (int row=0;row<data.size();row++) {
            cellData.add(data.get(row));
        }
    }


    /**
     * Refresh Table
     */
    public void refreshTable() {fireTableStructureChanged();}


    /**
     * Remove Row from Table.
     *
     * @param row Row Number.
     * @return Success/Failure.
     */
    public boolean removeRow(int row) {
        if (cellData.get(row) != null) {
            cellData.remove(row);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Add Row to Table.
     *
     * @return New Row Number.
     */
    public int addRow() {
        String[] emptyRow = new String[columnTitles.length];
        cellData.add(emptyRow);
        return (cellData.size()-1);
    }


    /**
     * Modify a Table Value.
     *
     * @param row Row Number.
     * @param column Column Number.
     * @param value New Value.
     * @return Success/Failure.
     */
    public boolean modifyValue(int row, int column, String value) {

        if (cellData.get(row) != null) {
            String[] rowArray = cellData.get(row);
            if (rowArray != null) {
                rowArray[column] = value;
                cellData.set(row,rowArray);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    //endregion


    //region VARIABLE ACCESS METHODS
    /**
     * Modifies the Edit Mode.
     *
     * @param value Condition Boolean.
     */
    public void setEditMode(boolean value) {editMode=value;}
    /**
     * Returns The Raw Table Contents.
     *
     * @return String ArrayList.
     */
    public ArrayList<String[]> getTableContents() {return cellData;}
    //endregion


    //region INHERITED METHODS
    @Override
    public int getRowCount() {
        return cellData.size();
    }


    @Override
    public int getColumnCount() {
        return columnTitles.length;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return cellData.get(rowIndex)[columnIndex];
    }


    @Override
    public String getColumnName(int column) {
        return columnTitles[column];
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editMode;
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        cellData.get(rowIndex)[columnIndex] = aValue.toString();
    }
    //endregion
}
