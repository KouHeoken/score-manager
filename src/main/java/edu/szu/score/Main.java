/*
V
编写Main.java，调用前面的方法，把它们接成一个能跑的程序
 */
package edu.szu.score;

import edu.szu.score.model.Student;
import edu.szu.score.service.ScoreStatistics;
import edu.szu.score.service.StudentManager;

/**
 * 程序入口：把 Student、StudentManager、ScoreStatistics 串起来。
 *
 * <p>
 * 直接运行进入命令行菜单；加参数 {@code --demo} 会先预置
 * 几条示例数据并打印一次统计结果，方便课堂演示。
 * </p>
 */
public class Main {

    public static void main(String[] args) {
        StudentManager manager = new StudentManager();

        // 带 --demo 参数时预置数据，否则从空列表开始
        if (args.length > 0 && "--demo".equalsIgnoreCase(args[0])) {
            preloadDemoData(manager);
            printStatistics(manager);
        }

        // 进入 do-while 命令行菜单（add/remove/update/find/list/help/exit）
        manager.runMenu();
    }

    /** 预置几条演示数据 */
    private static void preloadDemoData(StudentManager manager) {
        manager.add(new Student(2021150001, "张三", 90, 85, 78));
        manager.add(new Student(2021150002, "李四", 60, 70, 80));
        manager.add(new Student(2021150003, "王五", 100, 100, 100));
        System.out.println("已预置 3 条演示数据。");
    }

    /** 调用 ScoreStatistics 打印一次三门课的统计结果 */
    private static void printStatistics(StudentManager manager) {
        ScoreStatistics stats = new ScoreStatistics(manager.getAll());
        double[][] d = stats.stats();
        System.out.println("----- 成绩统计 -----");
        for (int i = 0; i < 3; i++) {
            System.out.printf("Course%d：平均=%.2f, 最高=%.0f, 最低=%.0f%n",
                    i + 1,
                    d[i][ScoreStatistics.AVG],
                    d[i][ScoreStatistics.MAX],
                    d[i][ScoreStatistics.MIN]);
        }
        System.out.println("-------------------");
    }
}