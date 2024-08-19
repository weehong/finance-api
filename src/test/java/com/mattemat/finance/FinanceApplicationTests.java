package com.mattemat.finance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinanceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        // This test calls the main method to ensure it runs without exceptions.
        FinanceApplication.main(new String[]{});
    }
}
