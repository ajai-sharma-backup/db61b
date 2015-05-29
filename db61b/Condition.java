package db61b;

import java.util.List;

/** Represents a single 'where' condition in a 'select' command.
 *  @author Ajai K. Sharma */

class Condition {
    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        _col1 = col1;
        _col2 = col2;
        _rel = relation;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /** Assuming that ROWS are rows from the respective tables from which
     *  my columns are selected, returns the result of performing the test I
     *  denote. */
    boolean test(Row... rows) {
        String value1 = _col1.getFrom(rows);
        String value2;
        if (_col2 != null) {
            value2 = _col2.getFrom(rows);
        } else {
            value2 = _val2;
        }
        int val = value1.compareTo(value2);
        switch (_rel) {
        case "<":
            return val < 0;
        case ">":
            return val > 0;
        case "<=":
            return val <= 0;
        case ">=":
            return val >= 0;
        case "=":
            return val == 0;
        case "!=":
            return val != 0;
        default:
            throw new IllegalArgumentException("Unsupported relation");
        }
    }

    /** Return true iff ROWS satisfies all CONDITIONS. */
    static boolean test(List<Condition> conditions, Row... rows) {
        for (Condition cond : conditions) {
            if (!cond.test(rows)) {
                return false;
            }
        }
        return true;
    }

    /** The operands of this condition.  _col2 is null if the second operand
     *  is a literal. */
    private Column _col1, _col2;
    /** Second operand, if literal (otherwise null). */
    private String _val2;
    /** String describing the relation implemented by the condition. */
    private String _rel;

    /** Dummy condition that always tests TRUE. */
    static class NullCondition extends Condition {
        /** Constructs a dummy condition given a COL1, COL2, RELATION
         *  (none of which do anything). */
        NullCondition(Column col1, String relation, Column col2) {
            super(col1, relation, col2);
        }

        /** Constructs a dummy condition with no arguments
         *  (use this one). */
        NullCondition() {
            this(null, null, null);
        }

        /** Dummy condition always returns true regardless of ROWS. */
        boolean test(Row... rows) {
            return true;
        }
    }
}
