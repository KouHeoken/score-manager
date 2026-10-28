/*
III-i
生成 ScoreStatistics类：按科目计算StudentManager中学生的 平均分、最高分、最低分，返回 double[]

III-ii
修改：只保留一个public方法，返回值是二维数组double[][]

*/
package edu.szu.score.service;

import edu.szu.score.model.Student;

import java.util.List;

/**
 * 按科目统计学生成绩，对外只暴露一个方法 {@link #stats()}。
 *
 * <p>
 * 返回的二维数组语义：
 * </p>
 * 
 * <pre>
 *          AVG(0)        MAX(1)        MIN(2)
 * COURSE1(0)  Course1平均  Course1最高  Course1最低
 * COURSE2(1)  Course2平均  Course2最高  Course2最低
 * COURSE3(2)  Course3平均  Course3最高  Course3最低
 * </pre>
 */
public class ScoreStatistics {

    /** 第一维：科目下标 */
    public static final int COURSE1 = 0;
    public static final int COURSE2 = 1;
    public static final int COURSE3 = 2;

    /** 第二维：统计量下标 */
    public static final int AVG = 0;
    public static final int MAX = 1;
    public static final int MIN = 2;

    private final List<Student> students;

    /**
     * @param students 学生列表，通常传入 {@code studentManager.getAll()}
     * @throws IllegalArgumentException students 为 null 时抛出
     */
    public ScoreStatistics(List<Student> students) {
        if (students == null) {
            throw new IllegalArgumentException("学生列表不能为 null");
        }
        this.students = students;
    }

    /**
     * 按科目统计平均分、最高分、最低分。
     *
     * @return double[3][3]，第一维为科目（COURSE1/2/3），第二维为 {平均分, 最高分, 最低分}
     * @throws IllegalStateException 学生列表为空时抛出（无数据无法统计）
     */
    public double[][] stats() {
        int n = students.size();
        if (n == 0) {
            throw new IllegalStateException("学生列表为空，无法统计成绩");
        }

        double[] sum = new double[3];
        double[] max = { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
        double[] min = { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };

        for (Student s : students) {
            int[] scores = { s.getCourse1(), s.getCourse2(), s.getCourse3() };
            for (int i = 0; i < 3; i++) {
                int v = scores[i];
                sum[i] += v;
                if (v > max[i])
                    max[i] = v;
                if (v < min[i])
                    min[i] = v;
            }
        }

        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            result[i][AVG] = sum[i] / n;
            result[i][MAX] = max[i];
            result[i][MIN] = min[i];
        }
        return result;
    }
}
