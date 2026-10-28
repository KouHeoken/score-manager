/*
II
定义StudentManager类，用 ArrayList<Student>存储学生信息，实现：
- add：传入Student对象，将其添加到列表
- removeById：找不到该学号时返回 false，不要抛异常；只删除第一个匹配项
- updateScore：找不到该学号时返回 false，不要抛异常；设置的分数超出范围时传递抛出的异常
- findById：返回学生对象引用，找不到该学号时返回空
- listAll：打印所有学生信息

IV
为StudentManager添加2个方法：
1. 接受命令行输入 add / remove（ById） / update（Score） / find（ById） / list（All）和它们各自所需的参数，switch选择并调用相应方法。非法输入打印提示让用户重输而不抛出异常
2.命令行菜单，do-while 循环
 */
package edu.szu.score.service;

import edu.szu.score.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * 学生信息管理：基于 ArrayList 的增删改查
 */
public class StudentManager {

    /** 用 ArrayList 存储学生信息 */
    private final List<Student> students = new ArrayList<>();

    
    /** 返回只读的学生列表视图（防止外部直接 add/remove 破坏内部状态） */
    public List<Student> getAll() {
        return Collections.unmodifiableList(students);
    }

    // ==================== 增 ====================

    /**
     * 添加学生
     *
     * @param student 待添加的学生对象
     * @throws IllegalArgumentException student 为 null 时抛出
     */
    public void add(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("待添加的学生不能为 null");
        }
        students.add(student);
    }

    // ==================== 删 ====================

    /**
     * 按学号删除学生，只删除第一个匹配项
     *
     * @param id 学号
     * @return 删除成功返回 true；找不到该学号返回 false（不抛异常）
     */
    public boolean removeById(int id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == id) {
                students.remove(i);
                return true; // 立即返回，保证只删第一个
            }
        }
        return false;
    }

    // ==================== 改 ====================

    /**
     * 更新指定一门课的成绩
     *
     * @param id       学号
     * @param courseNo 课程编号，只能为 1/2/3
     * @param score    新成绩，必须在 0~100 之间
     * @return 找不到该学号返回 false；找到并更新成功返回 true
     * @throws IllegalArgumentException 分数越界时由 setter 抛出并传播；
     *                                  courseNo 非法时也抛出该异常
     */
    public boolean updateScore(int id, int courseNo, int score) {
        Student student = findById(id);
        if (student == null) {
            return false; // 找不到：返回 false，不抛异常
        }
        switch (courseNo) {
            case 1 -> student.setCourse1(score); // 越界时 setter 抛 IllegalArgumentException，不捕获，向上传播
            case 2 -> student.setCourse2(score);
            case 3 -> student.setCourse3(score);
            default -> throw new IllegalArgumentException("课程编号只能是 1/2/3，当前：" + courseNo);
        }
        return true;
    }

    /**
     * 一次性更新三门课成绩（原子更新：全部合法才赋值）
     *
     * @return 找不到该学号返回 false；找到并更新成功返回 true
     * @throws IllegalArgumentException 任一分数越界时抛出
     */
    public boolean updateScore(int id, int course1, int course2, int course3) {
        Student student = findById(id);
        if (student == null) {
            return false;
        }
        // 先全部校验，避免"第一门已改、第二门抛异常"的半更新状态
        validateScore(course1, "Course1");
        validateScore(course2, "Course2");
        validateScore(course3, "Course3");
        student.setCourse1(course1);
        student.setCourse2(course2);
        student.setCourse3(course3);
        return true;
    }

    /** 校验分数区间，越界抛 IllegalArgumentException（与 Student 内部校验语义一致） */
    private static void validateScore(int score, String courseName) {
        if (score < Student.MIN_SCORE || score > Student.MAX_SCORE) {
            throw new IllegalArgumentException(
                    courseName + " 成绩必须在 " + Student.MIN_SCORE + "~" + Student.MAX_SCORE
                            + " 之间，当前值：" + score);
        }
    }

    // ==================== 查 ====================

    /**
     * 按学号查找学生
     *
     * @param id 学号
     * @return 列表中的学生对象引用；找不到返回 null
     */
    public Student findById(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return s; // 直接返回列表中的引用，不是副本
            }
        }
        return null;
    }

    // ==================== 打印 ====================

    /** 打印所有学生信息；列表为空时给出提示 */
    public void listAll() {
        if (students.isEmpty()) {
            System.out.println("（暂无学生信息）");
            return;
        }
        for (Student s : students) {
            System.out.println(s);
        }
    }
        // ==================== 命令行交互 ====================

    /**
     * 解析一行命令文本并分发执行。
     *
     * <p>命令格式：</p>
     * <pre>
     *   add    &lt;学号&gt; &lt;姓名&gt; &lt;Course1&gt; &lt;Course2&gt; &lt;Course3&gt;
     *   remove &lt;学号&gt;
     *   update &lt;学号&gt; &lt;课程号1-3&gt; &lt;新成绩&gt;
     *   find   &lt;学号&gt;
     *   list
     * </pre>
     *
     * <p>任何非法输入（空行、未知命令、参数个数/类型错误、成绩越界等）
     * 都只打印提示，不向调用方抛出异常。</p>
     *
     * @param line 用户输入的一行命令
     */
    public void handleCommand(String line) {
        if (line == null || line.isBlank()) {
            System.out.println("输入为空，请重新输入。");
            printUsage();
            return;
        }

        String[] parts = line.trim().split("\\s+");
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "add"    -> handleAdd(parts);
            case "remove" -> handleRemove(parts);
            case "update" -> handleUpdate(parts);
            case "find"   -> handleFind(parts);
            case "list"   -> handleList(parts);
            default -> {
                System.out.println("未知命令：" + parts[0] + "，请重新输入。");
                printUsage();
            }
        }
    }

    /**
     * 命令行菜单主循环：do-while 不断读入命令，
     * 输入 exit / quit 或输入流结束时退出。
     */
    public void runMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===== 学生成绩管理系统 =====");
        printUsage();

        boolean running = true;
        do {
            System.out.print("> ");
            // 流结束（如管道输入完毕）时直接退出，避免 NoSuchElementException
            String line = scanner.hasNextLine() ? scanner.nextLine() : "exit";
            String cmd = line.trim().toLowerCase();

            if (cmd.equals("exit") || cmd.equals("quit")) {
                running = false;
            } else if (cmd.equals("help")) {
                printUsage();
            } else {
                handleCommand(line);
            }
        } while (running);

        System.out.println("已退出学生成绩管理系统。");
        // 不关闭 scanner：关闭会连带关闭 System.in，后续无法再读
    }

    // ==================== 各命令的具体处理 ====================

    private void handleAdd(String[] parts) {
        if (parts.length != 6) {
            System.out.println("用法：add <学号> <姓名> <Course1> <Course2> <Course3>");
            return;
        }
        Integer id = parseInt(parts[1]);
        Integer c1 = parseInt(parts[3]);
        Integer c2 = parseInt(parts[4]);
        Integer c3 = parseInt(parts[5]);
        if (id == null || c1 == null || c2 == null || c3 == null) {
            System.out.println("学号和成绩必须是整数，请重新输入。");
            return;
        }
        try {
            add(new Student(id, parts[2], c1, c2, c3));
            System.out.println("已添加学生：" + parts[2] + "（学号 " + id + "）");
        } catch (IllegalArgumentException e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    private void handleRemove(String[] parts) {
        if (parts.length != 2) {
            System.out.println("用法：remove <学号>");
            return;
        }
        Integer id = parseInt(parts[1]);
        if (id == null) {
            System.out.println("学号必须是整数，请重新输入。");
            return;
        }
        if (removeById(id)) {
            System.out.println("已删除学号 " + id + " 的第一个匹配项。");
        } else {
            System.out.println("未找到学号 " + id + " 的学生。");
        }
    }

    private void handleUpdate(String[] parts) {
        if (parts.length != 4) {
            System.out.println("用法：update <学号> <课程号1-3> <新成绩>");
            return;
        }
        Integer id = parseInt(parts[1]);
        Integer courseNo = parseInt(parts[2]);
        Integer score = parseInt(parts[3]);
        if (id == null || courseNo == null || score == null) {
            System.out.println("学号、课程号、成绩都必须是整数，请重新输入。");
            return;
        }
        try {
            if (updateScore(id, courseNo, score)) {
                System.out.println("已更新学号 " + id + " 的 Course" + courseNo + " 为 " + score + "。");
            } else {
                System.out.println("未找到学号 " + id + " 的学生。");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("更新失败：" + e.getMessage());
        }
    }

    private void handleFind(String[] parts) {
        if (parts.length != 2) {
            System.out.println("用法：find <学号>");
            return;
        }
        Integer id = parseInt(parts[1]);
        if (id == null) {
            System.out.println("学号必须是整数，请重新输入。");
            return;
        }
        Student s = findById(id);
        if (s == null) {
            System.out.println("未找到学号 " + id + " 的学生。");
        } else {
            System.out.println(s);
        }
    }

    private void handleList(String[] parts) {
        if (parts.length != 1) {
            System.out.println("用法：list（不带参数）");
            return;
        }
        listAll();
    }

    // ==================== 工具方法 ====================

    /** 解析整数，失败返回 null（不抛异常） */
    private static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void printUsage() {
        System.out.println("""
                可用命令：
                  add    <学号> <姓名> <Course1> <Course2> <Course3>   添加学生
                  remove <学号>                                        按学号删除
                  update <学号> <课程号1-3> <新成绩>                   修改某科成绩
                  find   <学号>                                        按学号查询
                  list                                                 列出全部学生
                  help                                                 显示本帮助
                  exit / quit                                          退出
                提示：成绩范围 0~100，多个空格会被自动忽略。""");
    }
}