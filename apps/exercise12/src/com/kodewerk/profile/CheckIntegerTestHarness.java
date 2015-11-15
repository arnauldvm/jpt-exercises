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
        if (   (chars.length < 2) || //greater than ten & 2>=X & not empty
               (chars.length > 5) //X<=100000
           ) return false;
        if (chars[0] != '3') return false; //first digit is 3
        for (char ch: chars) {
          if ((ch<'0')||(ch>'9')) return false;
        }
        return true;
    }

}
