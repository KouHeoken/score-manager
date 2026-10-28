/*
I-i
定义一个Student类：学号(int)、姓名(String)、三门课成绩(int)，属性用 private，有构造方法和 toString

I-ii
修改：路径改为 src/main/java/edu/szu/score/ ，包名 edu.szu.score ，后续全部如此。三门课程直接用 Course1, Course2, Course3命名，取消name和Id的setter

I-iii
为3个分数都添加范围限制：分数在0到100之间，接收到超出范围的参数时抛出异常

*/
package edu.szu.score.model;

/**
 * 学生实体类
 * 属性全部 private；学号、姓名一经创建不可修改（不提供 setter）
 * 三门课成绩限制在 0~100 之间，越界抛出 IllegalArgumentException
 */
public class Student {

    /** 成绩合法区间下界 */
    public static final int MIN_SCORE = 0;
    /** 成绩合法区间上界 */
    public static final int MAX_SCORE = 100;

    private int id; // 学号
    private String name; // 姓名
    private int course1; // 课程1成绩
    private int course2; // 课程2成绩
    private int course3; // 课程3成绩

    /** 全参构造：学号、姓名在创建时确定；成绩越界直接拒绝创建 */
    public Student(int id, String name, int course1, int course2, int course3) {
        this.id = id;
        this.name = name;
        this.course1 = checkScore(course1, "Course1");
        this.course2 = checkScore(course2, "Course2");
        this.course3 = checkScore(course3, "Course3");
    }

    // ==================== getter（id、name 只读） ====================

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // ==================== 三门课成绩 getter / setter ====================

    public int getCourse1() {
        return course1;
    }

    public void setCourse1(int course1) {
        this.course1 = checkScore(course1, "Course1");
    }

    public int getCourse2() {
        return course2;
    }

    public void setCourse2(int course2) {
        this.course2 = checkScore(course2, "Course2");
    }

    public int getCourse3() {
        return course3;
    }

    public void setCourse3(int course3) {
        this.course3 = checkScore(course3, "Course3");
    }

    // ==================== 校验 ====================

    /**
     * 校验成绩是否在 [0, 100] 内，合法则原样返回，否则抛 IllegalArgumentException
     *
     * @param score      待校验成绩
     * @param courseName 课程名，仅用于拼异常信息
     */
    private static int checkScore(int score, String courseName) {
        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(
                    courseName + " 成绩必须在 " + MIN_SCORE + "~" + MAX_SCORE
                            + " 之间，当前值：" + score);
        }
        return score;
    }

    // ==================== 便于统计的辅助方法 ====================

    /** 总分 */
    public int getTotal() {
        return course1 + course2 + course3;
    }

    /** 平均分 */
    public double getAverage() {
        return getTotal() / 3.0;
    }

    // ==================== Object 方法 ====================

    @Override
    public String toString() {
        return String.format(
                "Student{学号=%d, 姓名='%s', Course1=%d, Course2=%d, Course3=%d, 总分=%d, 平均分=%.2f}",
                id, name, course1, course2, course3, getTotal(), getAverage());
    }

    /** 学号相同即认为是同一个学生，方便 List/Set 去重 */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}