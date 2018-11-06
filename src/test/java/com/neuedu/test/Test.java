package com.neuedu.test;

import com.Utils.BigDecimalUtils;

import java.math.BigDecimal;

public class Test {
    public static void main(String[] args){
       /* BigDecimal bigDecimal = new BigDecimal("0.05");
        BigDecimal bigDecimal2 = new BigDecimal("0.01");
        System.out.println(bigDecimal.add(bigDecimal2));*/

       System.out.println(BigDecimalUtils.add(0.05,0.01));
       System.out.println(BigDecimalUtils.sub(1.00,0.45));
       System.out.println(BigDecimalUtils.div(1.00,5.0));
    }

}
