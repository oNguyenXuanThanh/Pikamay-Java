/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pikamay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Administrator
 */
public class Pikamay extends JFrame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Pikamay pikamay;
        do {
            pikamay = new Pikamay(1, 480, 0, 2, 8);
        } while (pikamay.countAvailableCouple() > 15);
        pikamay.startTimer();
        pikamay.setVisible(true);
    }

    private CustomPanel pnlGameplay;
    private JLabel lblScore;
    private JLabel lblLevel;
    private JProgressBar pcbTime;
    private JButton btnSearch;
    private JButton btnReset;

    private Cell[][] cells;

    private final int SIZE = 38;
    private final int X = 12;
    private final int Y = 16;

    private Cell firstButton;
    private Cell secondButton;

    private String[] pictures;
    private int[] times;
    private Point[] p;

    private boolean visited[][];
    private boolean draw;
    private boolean suggested;
    private int level;
    private int time;
    private int score;
    private int resetTime;
    private int suggestTime;
    private Timer timer;

    public Pikamay(int lvl, int totalTime, int score, int resetTime, int suggestTime) {
        this.level = lvl;
        this.time = totalTime;
        this.score = score;
        this.resetTime = resetTime;
        this.suggestTime = suggestTime;
        setCellValue();
        initComponents();

    }

    public void startTimer() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int temp = time;

            @Override
            public void run() {
                temp--;
                pcbTime.setValue(temp);
                if (temp < 0) {
                    timer.cancel();
                    dispose();
                    if (JOptionPane.showConfirmDialog(null, "Bạn đã thua cuộc ở màn " + level + ". Bạn có muốn chơi lại không?", "Game over", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        startGame(level, time, score, resetTime, suggestTime);
                    } else {
                        System.exit(0);
                    }
                }
            }
        }, 0, 1000);
    }

    private void startGame(int nextLevel, int nextTime, int currentScore, int resetTimeLeft, int suggestTimeLeft) {
        Pikamay pikamay;
        do {

            pikamay = new Pikamay(nextLevel, nextTime, currentScore, resetTimeLeft, suggestTimeLeft);
        } while (pikamay.countAvailableCouple() > 15);
        pikamay.startTimer();
        pikamay.setVisible(true);
    }

    private void initComponents() {

        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
                break;
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Pikamay");
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);

        pnlGameplay = new CustomPanel();
        pnlGameplay.hasBackground = true;
        pnlGameplay.setLayout(new GroupLayout(pnlGameplay));
        pnlGameplay.setSize(getWidth(), getHeight());
        pnlGameplay.setLocation(0, (getHeight() - pnlGameplay.getHeight()) / 2);

        pcbTime = new JProgressBar();
        pcbTime.setSize(400, 25);
        pcbTime.setLocation((SIZE * Y - pcbTime.getWidth()) / 2, 20);
        pcbTime.setMaximum(time);
        pcbTime.setValue(time);
        pnlGameplay.add(pcbTime);

        pnlGameplay.setBackground(Color.WHITE);

        lblLevel = new JLabel("Màn : " + level + " / 15");
        lblLevel.setSize(180, 30);
        lblLevel.setHorizontalAlignment(SwingConstants.CENTER);
        lblLevel.setForeground(Color.WHITE);
        lblLevel.setFont(new Font("Consolas", Font.BOLD, 14));
        lblLevel.setLocation(620, (getHeight() - X * SIZE) / 2 - 40);

        lblScore = new JLabel("Điểm : " + score);
        lblScore.setSize(180, 30);
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblScore.setForeground(Color.WHITE);
        lblScore.setFont(new Font("Consolas", Font.BOLD, 14));
        lblScore.setLocation(620, lblLevel.getY() + 50);

        btnSearch = new JButton("Tìm 1 ô [" + suggestTime + "]");
        btnSearch.setSize(150, 60);
        btnSearch.setLocation(620, 200);
        btnSearch.setFont(new Font("Consolas", Font.BOLD, 12));
        btnSearch.setBackground(Color.WHITE);
        btnSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!suggested) {
                    if (suggestTime > 0) {
                        suggest();
                        suggestTime--;
                    } else {
                        btnSearch.setFocusable(false);
                    }
                    btnSearch.setText("Tìm 1 ô [" + suggestTime + "]");
                }
            }
        });
        pnlGameplay.add(btnSearch);

        btnReset = new JButton("Đảo hình [" + resetTime + "]");
        btnReset.setSize(150, 60);
        btnReset.setLocation(620, 300);
        btnReset.setFont(new Font("Consolas", Font.BOLD, 12));
        btnReset.setBackground(Color.WHITE);
        btnReset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (resetTime > 0) {
                    reset();
                    resetTime--;
                } else {
                    btnReset.setFocusable(false);
                }
                btnReset.setText("Đảo hình [" + resetTime + "]");
            }
        });
        pnlGameplay.add(btnReset);

        pnlGameplay.add(lblScore);
        pnlGameplay.add(lblLevel);
        cells = new Cell[X][Y];

        int start = 0;
        int end = (pnlGameplay.getHeight() - SIZE * X) / 2;
        Random random = new Random();

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (i == 0 || j == 0 || i == X - 1 || j == Y - 1) {
                    cells[i][j] = new Cell("");
                    cells[i][j].setLocation(start, end);
                    cells[i][j].setVisible(false);
                } else {
                    int t;
                    do {
                        t = random.nextInt(30);
                    } while (times[t] == 0);
                    times[t]--;
                    cells[i][j] = new Cell(pictures[t]);

                    cells[i][j].setBorder(null);
                    cells[i][j].setLocation(start, end);
                    if (!pictures[t].equals("vatcan")) {
                        cells[i][j].addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Cell clickedButton = (Cell) e.getSource();
                                if (firstButton == null) {
                                    firstButton = clickedButton;
                                    firstButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                                } else if (secondButton == null) {
                                    secondButton = clickedButton;
                                    if (firstButton != secondButton) {
                                        secondButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                                        if (!firstButton.getImg().equals(secondButton.getImg())) {
                                            firstButton.setBorder(null);
                                            firstButton = secondButton;
                                            secondButton = null;
                                        } else {
                                            draw = true;
                                            if (check(firstButton, secondButton)) {
                                                Hiding h = new Hiding();
                                                h.first = firstButton;
                                                h.second = secondButton;
                                                Thread drawing = new Thread(new Drawing());
                                                drawing.setPriority(5);
                                                Thread hiding = new Thread(h);
                                                hiding.setPriority(5);
                                                drawing.start();
                                                hiding.start();
                                                score();
                                                checkEndLevel();
                                                suggested = false;
                                                while (countAvailableCouple() == 0) {
                                                    reset();
                                                }
                                            } else {
                                                firstButton.setBorder(null);
                                                secondButton.setBorder(null);
                                            }
                                            firstButton = secondButton = null;
                                        }
                                    } else {
                                        secondButton = null;
                                    }
                                }

                            }

                        });
                    }
                }
                cells[i][j].i = i;
                cells[i][j].j = j;
                pnlGameplay.add(cells[i][j]);
                start += (SIZE);
            }
            end += SIZE;
            start = 0;
        }

        add(pnlGameplay);
    }

    private void checkEndLevel() {
        boolean check = true;
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                if (cells[i][j].isVisible()) {
                    check = false;
                }
            }
        }
        if (check) {
            if (level < 15) {
                JOptionPane.showMessageDialog(null, "Bạn đã xuất sắc vượt qua màn" + level + " với số điểm " + score + ". Nhấn OK để sẵn sàng chơi màn kế tiếp !");
                dispose();
                startGame(level + 1, time - 30, score, resetTime, suggestTime);
            } else {
                JOptionPane.showMessageDialog(null, "Chúc mừng bạn đã vượt qua tất cả các màn của game Pikamay !");
            }
        }
    }

    private void score() {
        score += 10;
        lblScore.setText("Điểm : " + score);
    }

    private void reset() {
        if (firstButton != null && secondButton != null) {
            firstButton.setVisible(false);
            secondButton.setVisible(false);
        }
        Random r = new Random();
        for (int i = 1; i < X - 1; i++) {
            for (int j = 1; j < Y - 1; j++) {
                if (cells[i][j].isVisible()) {
                    int m, n;
                    do {
                        m = r.nextInt(10) + 1;
                        n = r.nextInt(12) + 1;
                    } while (!cells[m][n].isVisible());
                    Point p1, p2;
                    int i1, i2, j1, j2;
                    i1 = cells[i][j].i;
                    j1 = cells[i][j].j;
                    i2 = cells[m][n].i;
                    j2 = cells[m][n].j;
                    p1 = cells[i][j].getLocation();
                    p2 = cells[m][n].getLocation();
                    Cell temp = cells[i][j];
                    cells[i][j] = cells[m][n];
                    cells[m][n] = temp;
                    cells[i][j].setLocation(p1);
                    cells[m][n].setLocation(p2);
                    cells[i][j].i = i1;
                    cells[i][j].j = j1;
                    cells[m][n].i = i2;
                    cells[m][n].j = j2;
                }
            }
        }

    }

    private int countAvailableCouple() {
        int result = 0;
        draw = false;
        avaiableCells = new Vector();
        visited = new boolean[X][Y];
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                visited[i][j] = false;
            }
        }
        for (int i = 1; i < X - 1; i++) {
            for (int j = 1; j < Y - 1; j++) {
                if (!visited[i][j] && cells[i][j].isVisible() && !cells[i][j].getImg().equals("vatcan")) {
                    visited[i][j] = true;
                    for (int m = 1; m < X - 1; m++) {
                        for (int n = 1; n < Y - 1; n++) {
                            if (!visited[m][n] && cells[i][j] != cells[m][n] && cells[i][j].getImg().equals(cells[m][n].getImg()) && check(cells[i][j], cells[m][n])) {
                                Cell c[] = new Cell[2];
                                c[0] = cells[i][j];
                                c[1] = cells[m][n];
                                avaiableCells.add(c);
                                result++;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private Vector<Cell[]> avaiableCells;

    private void suggest() {
        draw = false;
        suggested = true;
        Random r = new Random();
        int t = r.nextInt(avaiableCells.size());
        avaiableCells.get(t)[0].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        avaiableCells.get(t)[1].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
    }

    private boolean checkClearLine(Cell first, Cell second) {
        if (first.i == second.i) {
            Cell left = first.j < second.j ? first : second;
            Cell right = first.j < second.j ? second : first;
            if (left.isVisible()) {
                for (int i = left.j + 1; i <= right.j; i++) {
                    if (cells[left.i][i].isVisible()) {
                        return false;
                    }
                }
            }
            if (right.isVisible()) {
                for (int i = left.j; i < right.j; i++) {
                    if (cells[left.i][i].isVisible()) {
                        return false;
                    }
                }
            }
            if (!left.isVisible() && !right.isVisible()) {
                for (int i = left.j + 1; i < right.j; i++) {
                    if (cells[left.i][i].isVisible()) {
                        return false;
                    }
                }
            }
            return true;
        }
        if (first.j == second.j) {
            Cell high = first.i < second.i ? first : second;
            Cell low = first.i < second.i ? second : first;
            if (high.isVisible()) {
                for (int i = high.i + 1; i <= low.i; i++) {
                    if (cells[i][high.j].isVisible()) {
                        return false;
                    }
                }
            }
            if (low.isVisible()) {
                for (int i = high.i; i < low.i; i++) {
                    if (cells[i][high.j].isVisible()) {
                        return false;
                    }
                }
            }
            if (!high.isVisible() && !low.isVisible()) {
                for (int i = high.i + 1; i < low.i; i++) {
                    if (cells[i][high.j].isVisible()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkSingleLine(Cell first, Cell second) {
        if (first.i == second.i) {
            Cell left = first.j < second.j ? first : second;
            Cell right = first.j < second.j ? second : first;
            for (int i = left.j + 1; i < right.j; i++) {
                if (cells[left.i][i].isVisible()) {
                    return false;
                }
            }
            Cell[] points = new Cell[2];
            points[0] = left;
            points[1] = right;
            drawLine(points);
            return true;
        }
        if (first.j == second.j) {
            Cell high = first.i < second.i ? first : second;
            Cell low = first.i < second.i ? second : first;
            for (int i = high.i + 1; i < low.i; i++) {
                if (cells[i][high.j].isVisible()) {
                    return false;
                }
            }
            Cell[] points = new Cell[2];
            points[0] = high;
            points[1] = low;
            drawLine(points);
            return true;
        }
        return false;
    }

    private boolean checkDoubleLine(Cell first, Cell second) {
        if (first.i != second.i && first.j != second.j) {
            Cell high = first.i < second.i ? first : second;
            Cell low = first.i < second.i ? second : first;
            boolean condition;
            condition = checkClearLine(high, cells[high.i][low.j]) && checkClearLine(cells[high.i][low.j], low);
            if (condition) {
                Cell[] points = new Cell[3];
                points[0] = high;
                points[1] = cells[high.i][low.j];
                points[2] = low;
                drawLine(points);
                return true;

            }
            condition = checkClearLine(high, cells[low.i][high.j]) && checkClearLine(cells[low.i][high.j], low);
            if (condition) {
                Cell[] points = new Cell[3];
                points[0] = high;
                points[1] = cells[low.i][high.j];
                points[2] = low;
                drawLine(points);
                return true;
            }
        }
        return false;
    }

    private boolean checkTripleLine(Cell first, Cell second) {
        if (first.i == second.i) {
            for (int i = 0; i < X; i++) {
                if (i != first.i && checkClearLine(first, cells[i][first.j]) && checkClearLine(cells[i][first.j], cells[i][second.j]) && checkClearLine(cells[i][second.j], second)) {
                    Cell[] points = new Cell[4];
                    points[0] = first;
                    points[1] = cells[i][first.j];
                    points[2] = cells[i][second.j];
                    points[3] = second;
                    drawLine(points);
                    return true;
                }
            }
        }
        if (first.j == second.j) {
            for (int i = 0; i < Y; i++) {
                if (i != first.j && checkClearLine(first, cells[first.i][i]) && checkClearLine(cells[first.i][i], cells[second.i][i]) && checkClearLine(cells[second.i][i], second)) {
                    Cell[] points = new Cell[4];
                    points[0] = first;
                    points[1] = cells[first.i][i];
                    points[2] = cells[second.i][i];
                    points[3] = second;
                    drawLine(points);
                    return true;
                }
            }
        }
        if (first.i != second.i && first.j != second.j) {
            for (int i = 0; i < X; i++) {
                if (i != first.i && checkClearLine(first, cells[i][first.j]) && checkDoubleLine(cells[i][first.j], second)) {
                    Cell[] points = new Cell[4];
                    points[0] = first;
                    points[1] = cells[i][first.j];
                    points[2] = cells[i][second.j];
                    points[3] = second;
                    drawLine(points);
                    return true;
                }
            }
            for (int i = 0; i < Y; i++) {
                if (i != first.j && checkClearLine(first, cells[first.i][i]) && checkDoubleLine(cells[first.i][i], second)) {
                    Cell[] points = new Cell[4];
                    points[0] = first;
                    points[1] = cells[first.i][i];
                    points[2] = cells[second.i][i];
                    points[3] = second;
                    drawLine(points);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkNextTo(Cell first, Cell second) {
        if (first.getImg().equals(second.getImg())) {
            if ((first.i == second.i && Math.abs(first.j - second.j) == 1) || (first.j == second.j && Math.abs(first.i - second.i) == 1)) {
                Cell[] points = new Cell[2];
                points[0] = first;
                points[1] = second;
                drawLine(points);
                return true;
            }
        }
        return false;
    }

    private boolean check(Cell first, Cell second) {
        if (checkNextTo(first, second)) {
            return true;
        }
        if (checkSingleLine(first, second)) {
            return true;
        } else if (checkDoubleLine(first, second)) {
            return true;
        } else if (checkTripleLine(first, second)) {
            return true;
        }

        return false;
    }

    private void setCellValue() {
        pictures = new String[]{
            "saotim", "saohong", "saovang", "saoxanh", "saoxanhla",
            "basaotim", "basaohong", "basaovang", "basaoxanh", "basaoxanhla",
            "phanbonthuong", "phanboncaocap", "phanbonsieucap", "vatcan",
            "dongxu", "coctien", "nenvang", "thoivang",
            "chauchau", "kienduong", "canhcam", "saubuom", "ocsen", "bocanhtrang",
            "cay1", "cay2", "cay3", "cay4", "cay5", "cay6"
        };
        times = new int[]{
            4, 4, 6, 6, 6,
            2, 2, 2, 2, 2,
            8, 8, 6, 6,
            8, 8, 8, 8,
            4, 4, 2, 4, 4, 4,
            4, 4, 4, 4, 4, 4
        };
    }

    private void drawLine(Cell[] points) {
        if (draw) {
            p = new Point[points.length];
            for (int i = 0; i < p.length; i++) {
                p[i] = new Point(points[i].getX() + 18, points[i].getY() + 18);
            }
        }
    }

    class Drawing implements Runnable {

        @Override
        public void run() {
            pnlGameplay.points = p;
            pnlGameplay.repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            pnlGameplay.points = null;
            pnlGameplay.repaint();
        }

    }

    class Hiding implements Runnable {

        Cell first, second;

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {

            }
            first.setVisible(false);
            second.setVisible(false);
        }

    }
}
