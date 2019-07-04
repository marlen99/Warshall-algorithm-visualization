
public class BoolMatrix{
    private int rowCount;
    private int columnCount;
    private boolean[] matr;

    public BoolMatrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        matr = new boolean[rowCount*columnCount];
    }
    
    public BoolMatrix(BoolMatrix m) {
        rowCount = m.rowCount;
        columnCount = m.columnCount;
        matr = new boolean[rowCount*columnCount];
        for(int i=0; i<matr.length; ++i)
            matr[i] = m.matr[i];
    }

    public void set(int rowIndex, int columnIndex, boolean value) {
        matr[rowIndex*columnCount + columnIndex] = value;
    }

    public boolean get(int rowIndex, int columnIndex) {
        return matr[rowIndex*columnCount + columnIndex];
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void add(BoolMatrix m) {
        for(int i=0; i<matr.length; ++i)
            matr[i] = matr[i] || m.matr[i];
    }

    public BoolMatrix multiply(BoolMatrix m) {
        BoolMatrix res = new BoolMatrix(rowCount, m.columnCount);
        for(int i=0; i<rowCount; ++i){
            for(int j=0; j<res.columnCount; ++j){
                for(int k=0; k<columnCount; ++k){
                    if(matr[i*columnCount + k] && m.matr[k*m.columnCount + j]){
                        res.matr[i*res.columnCount + j] = true;
                        break;
                    }
                }
            }
        }
        return res;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int i=0; i<rowCount; ++i){
            for(int j=0; j<columnCount; ++j){
                str.append(matr[i*columnCount + j] ? '1' : '0');
                str.append(' ');
            }
            str.append(System.lineSeparator());
        }
        return str.toString();
    }

    public boolean equals(Object o) {
        if(!(o instanceof BoolMatrix))
             return false;
        BoolMatrix m = (BoolMatrix) o;
        if(rowCount != m.rowCount || columnCount != m.columnCount)
            return false;
        for(int i=0; i<matr.length; ++i)
            if(matr[i] != m.matr[i])
                return false;
        return true;
    }
}
