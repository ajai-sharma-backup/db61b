package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */

class Table implements Iterable<Row> {

    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain dupliace names. */
    Table(String[] columnTitles) {
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _columnTitles = new String[columnTitles.length];
        System.arraycopy(columnTitles, 0, _columnTitles,
                                       0, columnTitles.length);
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _columnTitles.length;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _columnTitles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = this.columns() - 1; i >= 0; i -= 1) {
            if (this.getTitle(i).equals(title)) { return i; }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    public int size() {
        return _rows.size();
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    public boolean add(Row row) {
        for (Row foo : this) {
            if (foo.equals(row)) { return false; }
        }
        return _rows.add(row);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            for (String line = input.readLine();
                 line != null;
                 line = input.readLine()) {
                table.add(new Row(line.split(",")));
            }

        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            output.println(Row.doCommas(_columnTitles));
            for (Row row : this) {
                output.println(Row.doCommas(row.toString().split(" ")));
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output. */
    void print() {
        for (Row row : _rows) {
            System.out.println("  " + row.toString());
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<Column> columns = new ArrayList<Column>();
        for (String name : columnNames) {
            columns.add(new Column(name, this));
        }
        for (Row oldRow : this) {
            if (Condition.test(conditions, oldRow)) {
                result.add(new Row(columns, oldRow));
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<Column> columns = new ArrayList<Column>();
        for (String name : columnNames) {
            columns.add(new Column(name, this, table2));
        }
        for (Row row1 : this) {
            for (Row row2 : table2) {
                if (equijoin(
                        common(this, table2),
                        common(table2, this),
                        row1, row2)
                    && Condition.test(conditions, row1, row2)) {
                    result.add(new Row(columns, row1, row2));
                }
            }
        }
        return result;
    }

    /** Returns a list of columns in T1 that have names shared with
     *     columns in T2. */
    static List<Column> common(Table t1, Table t2) {
        HashSet<String> names =
            new HashSet<String>(Arrays.asList(t1.columnTitles()));
        names.retainAll(Arrays.asList(t2.columnTitles()));
        ArrayList<Column> result = new ArrayList<Column>();
        for (String name : names) {
            result.add(new Column(name, t1));
        }
        return result;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 come, respectively,
     *  from those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {
        for (int i = 0; i < common1.size(); i++) {
            String value1 = common1.get(i).getFrom(row1);
            String value2 = common2.get(i).getFrom(row2);
            if (!value1.equals(value2)) { return false; }
        }
        return true;
    }

    /** Returns an array containing the column titles. */
    String[] columnTitles() {
        String[] temp = new String[this.columns()];
        System.arraycopy(_columnTitles, 0, temp, 0, this.columns());
        return temp;
    }

    /** Returns a copy of the row data. */
    HashSet<Row> rows() {
        HashSet<Row> acc = new HashSet<Row>();
        for (Row row : _rows) {
            acc.add(row);
        }
        return acc;
    }


    /** My rows. */
    private HashSet<Row> _rows = new HashSet<>();
    /** The titles of my columns. */
    private String[] _columnTitles;
}

