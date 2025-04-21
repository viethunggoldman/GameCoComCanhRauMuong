package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import view.Giaodienmanchoi;

public class BoardPanel extends JPanel {
    private GameManager gameManager;
    private Point selectedPiece = null;
    private List<Point> suggestedMoves = new ArrayList<>();
    private Runnable updateStatusCallback;
    private int spacing = 150;
    private final Point center = new Point(300, 300);
    private Giaodienmanchoi parent;
    private List<Point> validPositions;
    private int currentMoveStep = 0;
    private Point currentMovingPiece = null;
    private Point previousPosition = null;
    private Point initialPosition = null;
    private Set<Point> visitedPositions = new HashSet<>();
    private boolean interactionEnabled = true;
    private boolean isGamePaused = false;

    private boolean isInsideBoard(Point p) {
        for (Point valid : validPositions) {
            if (p.distance(valid) < 45) return true;
        }
        return false;
    }

    public void setGamePaused(boolean paused) {
        this.isGamePaused = paused;
    }

    public BoardPanel(GameManager gameManager, Runnable updateStatusCallback, Giaodienmanchoi parentFrame) {
        this.gameManager = gameManager;
        this.updateStatusCallback = updateStatusCallback;
        this.parent = parentFrame;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);

        validPositions = List.of(
                new Point(225, 75), new Point(375, 75), new Point(375, 225),
                new Point(225, 225), new Point(75, 225), new Point(525, 225),
                new Point(525, 375), new Point(375, 375), new Point(225, 375),
                new Point(75, 375), new Point(225, 525), new Point(375, 525)
        );

        gameManager.setOnBotPlayedCallback(() -> {
            if (parent != null) {
                parent.updateSoldiersCount(
                    gameManager.getRedCount(),
                    gameManager.getYellowCount()
                );
            }
            updateStatusCallback.run();
            repaint();
        });
    }

    public void enableInteraction() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (isGamePaused) {
                    return;
                }
               
                // Kiểm tra ngay đầu lượt xem người chơi có nước đi hợp lệ không
                if (!gameManager.isOnePlayer() || gameManager.isPlayerTurn()) {
                    if (!gameManager.hasValidMovesForCurrentPlayer()) {
                        System.out.println("No valid moves for current player. Switching turn...");
                        gameManager.switchTurn();
                        updateStatusCallback.run();
                        repaint();
                        return;
                    }
                }
               
                Point click = e.getPoint();
                System.out.println("Click tại: " + click);
                Point matched = findPieceAt(click);  

                GameManager.Player current = gameManager.getCurrentPlayer();
                // Nêu chọn quân cờ và quân của người chơi hiện tại
                if (matched != null && gameManager.getBoardState().get(matched) == current) {
                    if (gameManager.isOnePlayer() && gameManager.getBoardState().get(matched) == GameManager.Player.RED) {
                        System.out.println("Không thể chọn quân đỏ của AI ở chế độ một người chơi.");
                        return;
                    }
                    if (currentMoveStep == 0 || matched.equals(selectedPiece)) {
                        selectedPiece = matched;
                        initialPosition = new Point(matched);
                        suggestedMoves = getValidMoves(selectedPiece);
                        System.out.println("Đã chọn quân tại: " + matched);
                        System.out.println("Gợi ý bước đi: " + suggestedMoves);
                        repaint();
                    }
                    return;
                }

                if (selectedPiece != null) {
                    Point clickedTarget = findSuggestedMove(click); // Nhấp vào nước đi hợp lệ
                    if (clickedTarget != null) {
                        boolean eat = currentMoveStep == 2 && gameManager.canEat(clickedTarget);
                        if (eat) {
                            System.out.println("Ăn quân tại: " + clickedTarget);
                            gameManager.eat(selectedPiece, clickedTarget);
                            if (parent != null) {
                                parent.updateSoldiersCount(
                                    gameManager.getRedCount(),
                                    gameManager.getYellowCount()
                                );
                            }
                            resetMove();
                            gameManager.switchTurn();
                            updateStatusCallback.run();
                        } else {
                            System.out.println("Di chuyển đến: " + clickedTarget);
                            gameManager.move(selectedPiece, clickedTarget);
                            previousPosition = selectedPiece;
                            selectedPiece = clickedTarget;
                            currentMoveStep++;
                            suggestedMoves = getValidMoves(selectedPiece);
                            if (parent != null) {
                                parent.updateSoldiersCount(
                                    gameManager.getRedCount(),
                                    gameManager.getYellowCount()
                                );
                            }
                            repaint();
                            if (suggestedMoves.isEmpty() || currentMoveStep >= 3) {
                                resetMove();
                                gameManager.switchTurn();
                                updateStatusCallback.run();
                            }
                        }

                        if (gameManager.isOnePlayer() && !gameManager.isPlayerTurn()) {
                            EventQueue.invokeLater(() -> {
                                System.out.println("Player turn finished. Triggering bot move...");
                                gameManager.autoBotMoveIfNeeded();
                            });
                        }
                    }
                }
            }
        });
    }

    private void resetMove() {
        selectedPiece = null;
        suggestedMoves.clear();
        currentMoveStep = 0;
        previousPosition = null;
        initialPosition = null;
        repaint();
    }

    private Point findSuggestedMove(Point click) {
        for (Point move : suggestedMoves) {
            if (click.distance(move) < 45) return move;
        }
        return null;
    }

    private Point findPieceAt(Point p) {
        for (Point pos : gameManager.getBoardState().keySet()) {
            if (p.distance(pos) < 75) return pos;
        }
        return null;
    }

    private boolean isOccupied(Point p) {
        for (Point key : gameManager.getBoardState().keySet()) {
            if (key.equals(p)) return true;
        }
        return false;
    }

    private List<Point> getValidMoves(Point from) {
        List<Point> moves = new ArrayList<>();
        int step = currentMoveStep;
        System.out.println("Quân " + from + " đang ở bước thứ: " + step);
        int[][] dirs = {{0, 150}, {150, 0}, {0, -150}, {-150, 0}};

        for (int[] dir : dirs) {
            Point next = new Point(from.x + dir[0], from.y + dir[1]);

            if (!isInsideBoard(next)) continue;
            if (initialPosition != null && next.equals(previousPosition)) {
                continue;
            }
            if (step < 2 && !isOccupied(next)) { // vi tri dich trong
                moves.add(next);
            } else if (step == 2) {
                if (gameManager.canEat(next)) {
                    moves.add(next);
                    System.out.println("   -> Có thể ăn tại: " + next);
                } else if (!isOccupied(next)) {
                    moves.add(next);
                    System.out.println("   -> Không có quân để ăn, cho phép đi thường tới: " + next);
                }
            }
        }

        System.out.println("Gợi ý bước đi cho " + from + ": " + moves);
        return moves;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
    }

    private void drawBoard(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawRect(center.x - spacing / 2, (int)(center.y - spacing * 1.5), spacing, spacing);
        g.drawRect((int)(center.x - spacing * 1.5), center.y - spacing / 2, spacing, spacing);
        g.drawRect(center.x + spacing / 2, center.y - spacing / 2, spacing, spacing);
        g.drawRect(center.x - spacing / 2, center.y + spacing / 2, spacing, spacing);
    }

    private void drawPieces(Graphics g) {
        for (Map.Entry<Point, GameManager.Player> entry : gameManager.getBoardState().entrySet()) {
            Point p = entry.getKey();
            GameManager.Player player = entry.getValue();

            g.setColor(player == GameManager.Player.RED ? Color.RED : Color.YELLOW);
            g.fillOval(p.x - 23, p.y - 23, 45, 45);
        }

        g.setColor(Color.GREEN);
        for (Point k : suggestedMoves) {
            g.fillOval(k.x - 8, k.y - 8, 15, 15);
        }
    }

    public Point getCenter() {
        return center;
    }

    public int getSpacing() {
        return spacing;
    }
}