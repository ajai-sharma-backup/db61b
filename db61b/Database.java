package db61b;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

/** A collection of Tables, indexed by name.
 *  @author Ajai K. Sharma */
class Database {
    /** An empty database. */
    public Database() {
        _data = new HashMap<String, Table>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        return _data.get(name);
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        _data.put(name, table);
    }

    /** Returns a set containing the names of tables contained
     *  in the database. */
    HashSet<String> names() {
        HashSet<String> acc = new HashSet<String>();
        for (String name : _data.keySet()) {
            acc.add(name);
        }
        return acc;
    }

    /** The tables in the database are stored in a map. */
    private Map<String, Table> _data;
}
