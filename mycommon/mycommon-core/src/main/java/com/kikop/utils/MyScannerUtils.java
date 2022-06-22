package com.kikop.utils;

import java.util.Scanner;


/**
 * @author kikop
 * @version 1.0
 * @project mycommon-core
 * @file MyScannerUtils
 * @desc 终端扫描器
 * @date 2022/02/26
 * @time 18:30
 * @by IDE IntelliJ IDEA
 */
public class MyScannerUtils {

    public static void loopReadTerminalMsg() {

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            System.out.println(msg);
        }

    }
}
