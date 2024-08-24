package program;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private final List<Entry> stack;

    public Stack() {
        this.stack = new ArrayList<>();
    }

    public void add(int function, int line) {
        this.stack.add(new Entry(function, line));
    }

    public Entry retrieve() {
        return this.stack.removeLast();
    }

    public void clear() {
        this.stack.clear();
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public int size() {
        return this.stack.size();
    }

    public record Entry(int function, int line) {
    }
}
