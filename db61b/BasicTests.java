package db61b;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;


    /** Tests basic stuff.
     *  -- Rows
     *  -- Tables
     *  -- Selection
     *  So much spaghetti it's like I'm at Gypsy's
     *  Not worth the trouble to fix but I need to turn in tests
     */

public class BasicTests {
    @Test
    public void testRow() {
        Row r = new Row(new String[]{"foo", "bar", "baz"});
        assertEquals(3, r.size());
        assertEquals("bar", r.get(1));
        assert (r.equals(new Row(new String[] {"foo", "bar", "baz"})));
        assert (!r.equals(new Row(new String[] {"baz", "bar", "foo"})));
        assert (!r.equals(5));
        assert (!r.equals(null));
        assertEquals("foo bar baz", r.toString());
    }

    @Test
    public void testTable() {
        Table table = Table.readTable("enrolled");
        assertEquals(3, table.columns());
        String[] titles = new String[] {"SID", "CCN", "Grade"};
        for (int i = 0; i < 3; i++) {
            assertEquals(titles[i], table.getTitle(i));
            assertEquals(i, table.findColumn(titles[i]));
        }
        assertEquals(-1, table.findColumn("not_a_title"));
        assertEquals(19, table.size());
        Table empty = new Table(titles);
        assertEquals(0, empty.size());
    }

    @Test
    public void selectTest() {
        Table students = Table.readTable("students");
        List<String> columnTitles = new ArrayList<String>();
        columnTitles.add("SID");
        columnTitles.add("Firstname");
        columnTitles.add("Lastname");
        List<Column> columns = new ArrayList<Column>();
        for (String name : columnTitles) {
            columns.add(new Column(name, students));
        }
        Row oldRow = students.iterator().next();
        Row row = new Row(columns, oldRow);
        assertEquals("105 Shana Brown", row.toString());
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition.NullCondition());
        Row testRow =
            students.select(columnTitles, conditions).iterator().next();
        assertEquals("102 Valerie Chan", testRow.toString());
        conditions.add(new Condition(columns.get(0), ">=", "103"));
        testRow = students.select(columnTitles, conditions).iterator().next();
        assertEquals("105 Shana Brown", testRow.toString());
        conditions.add(new Condition(columns.get(1), "<", columns.get(2)));
        testRow = students.select(columnTitles, conditions).iterator().next();
        assertEquals("103 Jonathan Xavier", testRow.toString());
        Table enrolled = Table.readTable("enrolled");
        columns = new ArrayList<Column>();
        columnTitles = new ArrayList<String>();
        columnTitles.add("Firstname");
        columnTitles.add("Grade");
        for (String name : columnTitles) {
            columns.add(new Column(name, students, enrolled));
        }
        conditions = new ArrayList<Condition>();
        conditions.add(new Condition.NullCondition());
        Table table = students.select(enrolled, columnTitles, conditions);
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BasicTests.class));
    }
}
