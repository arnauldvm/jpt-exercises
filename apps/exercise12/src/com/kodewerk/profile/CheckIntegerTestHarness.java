package com.kodewerk.profile;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

public class CheckIntegerTestHarness {
    public static void main(String[] args) throws IOException {
        testDataset("dataset1.dat");
        testDataset("dataset2.dat");
        testDataset("dataset3.dat");
    }

    public static void testDataset(String dataset) throws IOException {
        DataInputStream rdr = new DataInputStream(new FileInputStream(dataset));
        long starttime = System.currentTimeMillis();
        int truecount = 0;
        String s;
        try {
            while ((s = rdr.readUTF()) != null) {
                if (checkInteger(s))
                    truecount++;
            }
        } catch (EOFException e) {
        }
        rdr.close();
        System.out.println(truecount + " (count); time " + (System.currentTimeMillis() - starttime));
    }

    public static boolean checkInteger(String testInteger) {
        char[] chars = testInteger.toCharArray();
        for (char ch: chars) {
          if ((ch<'0')||(ch>'9')) return false;
        }
        int theInteger;
        try {
            theInteger = Integer.parseInt(testInteger);//fails if not  a number
        } catch (NumberFormatException err) {
            throw new RuntimeException("Should not happend", err);
        }
        return
                (testInteger != "") && //not empty
                (theInteger > 10) && //greater than ten
                ((theInteger >= 2) &&
                (theInteger <= 100000)) && //2>=X<=100000
                (testInteger.charAt(0) == '3'); //first digit is 3
    }

}
