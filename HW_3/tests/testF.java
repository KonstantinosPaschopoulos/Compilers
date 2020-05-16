class testF {
    public static void main(String[] a) {
        System.out.println(new testFac().ComputeFac());
    }
}

class testFac {
    public int ComputeFac() {
        boolean[] testing;
        boolean x;
        boolean y;
        testing = new boolean[4];
        testing[0] = false;
        testing[1] = true;
        testing[2] = true;
        testing[3] = true;
        x = testing[3];
        y = testing[1];

        if (x && y) {
            System.out.println(10);
        } else {
            System.out.println(5);
        }

        return testing.length;
    }
}
