package controller;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.Timer;
import java.util.stream.Collectors;

public class BotPlayer {
    private final GameManager gameManager;
    private final BoardPanel boardPanel;
    private final Random random = new Random();
    private Timer currentBotTimer = null;
    private boolean isBotInterrupted = false;

    public BotPlayer(GameManager gameManager, BoardPanel boardPanel) {
        this.gameManager = gameManager;
        this.boardPanel = boardPanel;
    }

    private List<Point> getValidMoves(Point from, int stepCount) {
        List<Point> directions = Arrays.asList(
                new Point(150, 0), new Point(-150, 0), // Tăng từ 100 lên 150
                new Point(0, 150), new Point(0, -150)
        );

        List<Point> valid = new ArrayList<>();

        for (Point d : directions) {
            Point to = new Point(from.x + d.x, from.y + d.y);
            if (!gameManager.isValidBoardPosition(to)) continue;
            
            if (gameManager.isMoveValid(from, to, stepCount)) {
                Map<Point, GameManager.Player> board = gameManager.getBoardState();
                if (!board.containsKey(to) || (stepCount == 3 && gameManager.canEat(to))) {
                    valid.add(to);
                }
            }
        }

        return valid;
    }

    public void resumeIfInterrupted() {
        if (isBotInterrupted && currentBotTimer != null) {
            System.out.println("Resuming bot movement...");
            isBotInterrupted = false;
            currentBotTimer.start();
        }
    }

    public void play(Runnable onFinish) {
        if (gameManager.isGamePaused()) {
            System.out.println("Bot movement paused.");
            onFinish.run();
            return;
        }

        List<Point> botPieces = gameManager.getBoardState().entrySet().stream()
            .filter(entry -> entry.getValue() == GameManager.Player.RED)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        if (botPieces.isEmpty()) {
            System.out.println("No bot pieces available to move.");
            onFinish.run();
            return;
        }

        java.util.Collections.shuffle(botPieces);

        final List<Point>[] finalPathHolder = new List[]{new ArrayList<>()};
        Point selectedPiece = null;

        for (Point piece : botPieces) {
            List<Point> path = generateMovePath(piece);
            if (!path.isEmpty()) {
                selectedPiece = piece;
                finalPathHolder[0] = path;
                break;
            }
        }

        if (selectedPiece == null || finalPathHolder[0].isEmpty()) {
            System.out.println("Bot could not find a valid path to move with any piece.");
            onFinish.run();
            return;
        }

        System.out.println("Bot selected piece: " + selectedPiece + ", Path: " + finalPathHolder[0]);

        final Point[] currentPiece = {selectedPiece};
        final int[] stepIndex = {0};

        if (currentBotTimer == null) {
            currentBotTimer = new Timer(500, null);
        } else {
            currentBotTimer.stop();
            for (ActionListener al : currentBotTimer.getActionListeners()) {
                currentBotTimer.removeActionListener(al);
            }
        }

        currentBotTimer.addActionListener(e -> {
            if (gameManager.isGamePaused()) {
                System.out.println("Bot paused mid-step.");
                isBotInterrupted = true;
                currentBotTimer.stop();
                return;
            }

            if (stepIndex[0] >= finalPathHolder[0].size()) {
                System.out.println("Bot completed all steps in path: " + finalPathHolder[0]);
                currentBotTimer.stop();
                onFinish.run();
                return;
            }

            Point next = finalPathHolder[0].get(stepIndex[0]);
            Map<Point, GameManager.Player> board = gameManager.getBoardState();

            if (stepIndex[0] == 2 && gameManager.canEat(next)) {
                System.out.println("Bot eats at: " + next);
                gameManager.eat(currentPiece[0], next);
            } else if (!board.containsKey(next)) {
                System.out.println("Bot moves to: " + next);
                gameManager.move(currentPiece[0], next);
            } else {
                System.out.println("Bot stopped at: " + next + " due to invalid move.");
                currentBotTimer.stop();
                onFinish.run();
                return;
            }

            currentPiece[0] = next;
            stepIndex[0]++;
            boardPanel.repaint();
        });

        currentBotTimer.start();
    }

    private List<Point> generateMovePath(Point from) {
        List<Point> path = new ArrayList<>();
        List<Point> visited = new ArrayList<>();
        visited.add(from);
        Point current = from;

        for (int i = 1; i <= 3; i++) {
            List<Point> moves = getValidMoves(current, i);
            if (moves.isEmpty()) {
                System.out.println("No valid moves at step " + i + " from " + current);
                break;
            }

            moves.removeIf(visited::contains);

            if (moves.isEmpty()) {
                System.out.println("No valid moves after removing visited positions at step " + i);
                break;
            }

            if (i == 3) {
                for (Point move : moves) {
                    if (gameManager.canEat(move)) {
                        path.add(move);
                        System.out.println("Bot chose to eat at: " + move);
                        return path;
                    }
                }
                Point next = moves.get(random.nextInt(moves.size()));
                path.add(next);
                visited.add(next);
                System.out.println("Bot moved to: " + next + " at step " + i);
            } else {
                Point next = moves.get(random.nextInt(moves.size()));
                path.add(next);
                visited.add(next);
                System.out.println("Bot moved to: " + next + " at step " + i);
                current = next;
            }
        }

        return path;
    }
}