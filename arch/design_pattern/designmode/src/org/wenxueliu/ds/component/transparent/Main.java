package org.wenxueliu.ds.component.transparent;

import sun.misc.CompoundEnumeration;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class Main {
    public static void main(String[] args) {
        Component root = new Composite();
        Component branch = new Composite();
        Leaf leaf = new Leaf();
        root.add(branch);
        branch.add(leaf);
        display(root);
    }

    public static void display(Component root) {
        for (Component com : root.getChildren()) {
            if (com instanceof Composite) {
                display(com);
            } else  if (com instanceof Leaf) {
                com.doSth();
            }
        }

    }
}
