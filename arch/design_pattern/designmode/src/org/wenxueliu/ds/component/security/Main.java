package org.wenxueliu.ds.component.security;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {
    public static void main(String[] args) {
        Composite root = new Composite();
        Composite branch = new Composite();
        Leaf leaf = new Leaf();
        root.add(branch);
        branch.add(leaf);
        display(root);
    }

    public static void display(Composite root) {
        for (Component com : root.getChildren()) {
            if (com instanceof Composite) {
                display((Composite) com);
            } else  if (com instanceof Leaf) {
                com.doSth();
            }
        }

    }
}
