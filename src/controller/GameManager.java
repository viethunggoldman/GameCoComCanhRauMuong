package controller;

import java.awt.EventQueue;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GameManager {
    public enum Player { RED, YELLOW }
    private boolean isOnePlayer;
    private boolean playerGoesFirst;
    private Runnable onBotPlayedCallback;
    private Player currentPlayer;
    private Map<Point, Player> board = new HashMap<>();
    private Map<Point, Integer> pieceSteps = new HashMap<>();
    private BotPlayer botPlayer;
    private BoardPanel boardPanel;
    private boolean isGamePaused = false;
    private Runnable onGameEndCallback;

    public void setOnGameEndCallback(Runnable callback) {
        this.onGameEndCallback = callback;
    }

    public void setGamePaused(boolean paused) {
        this.isGamePaused = paused;
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }

    public void setOnBotPlayedCallback(Runnable callback) {
        this.onBotPlayedCallback = callback;
    }

    public void setBotPlayer(BotPlayer botPlayer) {
        this.botPlayer = botPlayer;
    }

    public BotPlayer getBotPlayer() {
        return this.botPlayer;
    }

    public boolean isPlayerTurn() {
        return (playerGoesFirst && getCurrentPlayer() == Player.YELLOW) ||
               (!playerGoesFirst && getCurrentPlayer() == Player.RED);
    }

    public void autoBotMoveIfNeeded() {
        if (isGamePaused) {
            return;
        }
        if (isOnePlayer && currentPlayer == Player.RED && botPlayer != null) {
            System.out.println("Bot turn triggered! Current player: " + currentPlayer);
            // update
            if (!hasValidMovesForCurrentPlayer()) {
                System.out.println("Bot has no valid moves. Switching turn...");
                switchTurn();
                return;
            }
            // update
            javax.swing.Timer botTimer = new javax.swing.Timer(500, null);
            botTimer.setRepeats(false);
            botTimer.addActionListener(e -> {
                botPlayer.play(() -> {
                    System.out.println("Bot finished its turn. Switching turn...");
                    switchTurn();
                    if (onBotPlayedCallback != null) {
                        onBotPlayedCallback.run();
                    }
                });
            });
            botTimer.start();
        } else {
            System.out.println("Bot turn not triggered. isOnePlayer: " + isOnePlayer + 
                    ", currentPlayer: " + currentPlayer);
        }
    }

    public GameManager(boolean isOnePlayer, boolean playerGoesFirst, BoardPanel boardPanel) {
        this.isOnePlayer = isOnePlayer;
        this.setPlayerGoesFirst(playerGoesFirst);
        this.boardPanel = boardPanel;
        currentPlayer = playerGoesFirst ? Player.YELLOW : Player.RED;
        
        if (isOnePlayer) {
            this.botPlayer = new BotPlayer(this, boardPanel);
        }
    }

    public int getPieceStep(Point p) {
        return pieceSteps.getOrDefault(p, 0);
    }

    public void incrementStep(Point p) {
        int current = pieceSteps.getOrDefault(p, 0);
        pieceSteps.put(p, current + 1);
    }

    public boolean isOnePlayer() {
        return isOnePlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer == Player.RED) ? Player.YELLOW : Player.RED;
        System.out.println("Switched turn. Current player: " + currentPlayer);
        
        // Kiểm tra xem người chơi hiện tại có nước đi hợp lệ không
        //update
        if (!hasValidMovesForCurrentPlayer()) {
            System.out.println("No valid moves for current player (" + currentPlayer + "). Checking opponent...");
            // Lưu người chơi hiện tại để kiểm tra sau
            Player originalPlayer = currentPlayer;
            // Thử chuyển lượt cho đối phương
            currentPlayer = (currentPlayer == Player.RED) ? Player.YELLOW : Player.RED;
            System.out.println("Switched to opponent. Current player: " + currentPlayer);
            
            // Nếu đối phương cũng không có nước đi hợp lệ, kết thúc trò chơi
            if (!hasValidMovesForCurrentPlayer()) {
                System.out.println("No valid moves for opponent either. Ending game...");
                if (onGameEndCallback != null) {
                    onGameEndCallback.run();
                }
                return;
            }
            
            // Nếu đối phương có nước đi, tiếp tục với lượt của họ
            System.out.println("Opponent has valid moves. Continuing with their turn.");
        }
        //update
        // update bỏ && !isPlayerGoesFirst()
        if (isOnePlayer && currentPlayer == Player.RED) {
            EventQueue.invokeLater(() -> {
                System.out.println("Bot still has turn after switch (bot goes first). Triggering next bot move...");
                autoBotMoveIfNeeded();
            });
        }
    }

    public boolean isMoveValid(Point from, Point to, int stepCount) {
        int dx = Math.abs(to.x - from.x);
        int dy = Math.abs(to.y - from.y);
        return (dx == 150 && dy == 0) || (dx == 0 && dy == 150); // Tăng từ 100 lên 150
    }

    public boolean canEat(Point target) {
        return board.containsKey(target) && board.get(target) != currentPlayer;
    }

    public void move(Point from, Point to) {
        Player p = board.get(from);
        board.remove(from);
        board.put(to, p);
        
        int step = pieceSteps.getOrDefault(from, 0);
        pieceSteps.remove(from);
        pieceSteps.put(to, step + 1);
    }

    public void eat(Point from, Point to) {

    	board.remove(to); // Xóa quân địch
        Player p = board.get(from);
        board.remove(from);
        board.put(to, p);
        pieceSteps.put(to, 0); // Reset bước sau khi ăn
        
        // Kiểm tra kết thúc trò chơi
        if (getRedCount() == 0 || getYellowCount() == 0) {
            System.out.println("Game ended. Red count: " + getRedCount() + ", Yellow count: " + getYellowCount());
            if (onGameEndCallback != null) {
                onGameEndCallback.run();
            }
        }
    }

    public void placePiece(Point position, Player player) {
        board.put(position, player);
        pieceSteps.put(position, 0);
    }

    public Map<Point, Player> getBoardState() {
        return board;
    }

    public void setupInitialPieces() {
        placePiece(new Point(225, 75), Player.RED); // Tăng tọa độ (150*1.5, 50*1.5)
        placePiece(new Point(225, 225), Player.RED);
        placePiece(new Point(375, 75), Player.RED);
        placePiece(new Point(375, 225), Player.RED);

        placePiece(new Point(375, 375), Player.YELLOW);
        placePiece(new Point(375, 525), Player.YELLOW);
        placePiece(new Point(225, 525), Player.YELLOW);
        placePiece(new Point(225, 375), Player.YELLOW);
    }

    public int getRedCount() {
        return (int) board.values().stream().filter(p -> p == Player.RED).count();
    }

    public int getYellowCount() {
        return (int) board.values().stream().filter(p -> p == Player.YELLOW).count();
    }

    public boolean isPlayerGoesFirst() {
        return playerGoesFirst;
    }

    public void setPlayerGoesFirst(boolean playerGoesFirst) {
        this.playerGoesFirst = playerGoesFirst;
    }

    public Point getNextPosition(Point from, int stepIndex) {
        Point[] directions = {
            new Point(150, 0), 
            new Point(-150, 0),
            new Point(0, 150),
            new Point(0, -150)
        };

        if (stepIndex < 0 || stepIndex >= directions.length) return null;

        Point dir = directions[stepIndex];
        Point to = new Point(from.x + dir.x, from.y + dir.y);
        return isValidBoardPosition(to) ? to : null;
    }

    public boolean isValidBoardPosition(Point p) {
        List<Point> valid = List.of(
            new Point(225, 75), new Point(375, 75), new Point(375, 225),
            new Point(225, 225), new Point(75, 225), new Point(525, 225),
            new Point(525, 375), new Point(375, 375), new Point(225, 375),
            new Point(75, 375), new Point(225, 525), new Point(375, 525)
        );

        for (Point point : valid) {
            if (point.equals(p)) return true;
        }
        return false;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        if (botPlayer != null) {
            this.botPlayer = new BotPlayer(this, boardPanel);
        }
    }

    public boolean hasValidMovesForCurrentPlayer() {
        List<Point> currentPlayerPieces = board.entrySet().stream()
                .filter(entry -> entry.getValue() == currentPlayer)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (Point piece : currentPlayerPieces) {
            List<Point> validMoves = getValidMovesForPiece(piece, 0);
            if (!validMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<Point> getValidMovesForPiece(Point from, int stepCount) {
        List<Point> moves = new ArrayList<>();
        int[][] dirs = {{0, 150}, {150, 0}, {0, -150}, {-150, 0}}; // Tăng từ 100 lên 150

        for (int[] dir : dirs) {
            Point next = new Point(from.x + dir[0], from.y + dir[1]);
            if (!isValidBoardPosition(next)) continue;
            if (stepCount < 2 && !board.containsKey(next)) {
                moves.add(next);
            } else if (stepCount == 2 && canEat(next)) {
                moves.add(next);
            }
        }
        return moves;
    }
}