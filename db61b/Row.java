package db61b;

import java.util.Arrays;
import java.util.List;

/** A single row of a database.
 *  @author Ajai K. Sharma
 */
class Row {
    /** A Row whose column values are DATA.  The array DATA must not be altered
     *  subsequently. */
    Row(String[] data) {
        _data = data;
    }

    /** Given M COLUMNS that were created from a sequence of Tables
     *  [t0,...,tn] as well as ROWS [r0,...,rn] that were drawn from those
     *  same tables [t0,...,tn], constructs a new Row containing M values,
     *  where the ith value of this new Row is taken from the location given
     *  by the ith COLUMN (for each i from 0 to M-1).
     *
     *  More specifically, if _table is the Table number corresponding to
     *  COLUMN i, then the ith value of the newly created Row should come from
     *  ROWS[_table].
     *
     *  Even more specifically, the ith value of the newly created Row should
     *  be equal to ROWS[_table].get(_column), where _column is the column
     *  number in ROWS[_table] corresponding to COLUMN i.
     *
     *  There is a method in the Column class that you'll need to use, see
     *  {@link db61b.Column#getFrom}).
     */
    Row(List<Column> columns, Row... rows) {
        _data = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            _data[i] = columns.get(i).getFrom(rows);
        }
    }

    /** Return my number of columns. */
    int size() {
        return _data.length;
    }

    /** Return the value of my Kth column.  Requires that 0 <= K < size(). */
    String get(int k) {
        return _data[k];
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    

    @Override
    public int hashCode() {
        return Arrays.hashCode(_data);
    }

    /** Does formatting for writing a ROW (can be table._columnTitles).
     *  Nondestructive, returns a string */
    static String doCommas(String[] row) {
        return Arrays.toString(row)
                .replaceAll("(^\\[)|(\\]$)", "")
                .replaceAll(", ", ",");
    }

    /** Returns a string of format "foo bar baz". */
    public String toString() {
        return Arrays.toString(_data).
                replaceAll("^\\[|\\]$", "").replaceAll(",", "");
    }

    /** Contents of this row. */
    private String[] _data;
}
