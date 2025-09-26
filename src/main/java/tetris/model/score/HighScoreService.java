package tetris.model.score;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Enhanced HighScoreService with proper multi-threading support
 * Uses ExecutorService for asynchronous score saving and ReentrantReadWriteLock for thread safety
 */
public class HighScoreService {
    private static final int MAX = 10;
    private final HighScoreStore store;
    private final List<ScoreEntry> scores = new ArrayList<>();
    private volatile int currentScore = 0;

    // Multi-threading components
    private final ExecutorService scoreExecutor;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final CompletionService<Boolean> completionService;

    // Thread-safe score submission queue
    private final BlockingQueue<ScoreSubmissionTask> submissionQueue = new LinkedBlockingQueue<>();
    private final Thread submissionWorker;
    private volatile boolean shutdown = false;

    public HighScoreService(HighScoreStore store) {
        this.store = store;

        // Initialize thread pool for score operations
        this.scoreExecutor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "HighScore-Worker");
            t.setDaemon(true);
            return t;
        });
        this.completionService = new ExecutorCompletionService<>(scoreExecutor);

        // Initialize submission worker thread
        this.submissionWorker = new Thread(this::processSubmissions, "Score-Submission-Worker");
        this.submissionWorker.setDaemon(true);
        this.submissionWorker.start();

        // Load initial scores
        loadScoresAsync();
    }

    /**
     * Asynchronously loads scores from store
     */
    private void loadScoresAsync() {
        scoreExecutor.submit(() -> {
            List<ScoreEntry> loadedScores = store.load();
            lock.writeLock().lock();
            try {
                this.scores.clear();
                this.scores.addAll(loadedScores);
                this.scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                trim();
            } finally {
                lock.writeLock().unlock();
            }
        });
    }

    /**
     * Thread-safe score addition
     */
    public void addLinesScore(int linesCleared, int level) {
        int scoreToAdd = ScoreCalculator.computeLineClearScore(linesCleared, level);
        synchronized (this) {
            currentScore += scoreToAdd;
        }
    }

    /**
     * Asynchronous score submission with Future for result tracking
     */
    public Future<Boolean> submitScoreAsync(String name) {
        return completionService.submit(() -> submitScoreInternal(name));
    }

    /**
     * Synchronous score submission (blocking)
     */
    public boolean submitScore(String name) {
        try {
            return submitScoreAsync(name).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Non-blocking score submission using worker thread
     */
    public void submitScoreNonBlocking(String name, ScoreSubmissionCallback callback) {
        if (shutdown) return;

        ScoreSubmissionTask task = new ScoreSubmissionTask(name, getCurrentScore(), callback);
        try {
            boolean offered = submissionQueue.offer(task, 1, TimeUnit.SECONDS);
            if (!offered && callback != null) {
                callback.onSubmissionComplete(false, "Submission queue full");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (callback != null) {
                callback.onSubmissionComplete(false, "Submission interrupted");
            }
        }
    }

    /**
     * Worker thread method to process score submissions
     */
    private void processSubmissions() {
        while (!shutdown && !Thread.currentThread().isInterrupted()) {
            try {
                ScoreSubmissionTask task = submissionQueue.take();
                boolean success = submitScoreInternal(task.playerName);

                if (task.callback != null) {
                    task.callback.onSubmissionComplete(success,
                            success ? "Score submitted successfully" : "Score submission failed");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Internal score submission logic
     */
    private boolean submitScoreInternal(String name) {
        int scoreToSubmit;
        synchronized (this) {
            scoreToSubmit = currentScore;
            if (scoreToSubmit <= 0) return false;
            currentScore = 0; // Reset immediately
        }

        // Check eligibility without holding locks for too long
        if (!isEligibleForHighScore(scoreToSubmit)) {
            return false;
        }

        String playerName = (name == null || name.isBlank()) ? "Player" : name.trim();
        ScoreEntry entry = new ScoreEntry(playerName, scoreToSubmit);

        // Perform file operations outside of locks
        List<ScoreEntry> currentScores = store.load();
        currentScores.add(entry);
        currentScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        // Keep only top 10
        if (currentScores.size() > MAX) {
            currentScores = currentScores.subList(0, MAX);
        }

        // Save and update in-memory copy
        store.save(currentScores);

        lock.writeLock().lock();
        try {
            this.scores.clear();
            this.scores.addAll(currentScores);
        } finally {
            lock.writeLock().unlock();
        }

        return true;
    }

    public synchronized int getCurrentScore() {
        return currentScore;
    }

    public synchronized void resetScore() {
        currentScore = 0;
    }

    /**
     * Thread-safe access to top scores
     */
    public List<ScoreEntry> getTopScores() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(scores);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void trim() {
        if (scores.size() > MAX) {
            scores.subList(MAX, scores.size()).clear();
        }
    }

    /**
     * Asynchronous refresh from store
     */
    public Future<Void> refreshFromStoreAsync() {
        return scoreExecutor.submit(() -> {
            List<ScoreEntry> fresh = store.load();
            lock.writeLock().lock();
            try {
                this.scores.clear();
                this.scores.addAll(fresh);
                this.scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                trim();
            } finally {
                lock.writeLock().unlock();
            }
            return null;
        });
    }

    public void refreshFromStore() {
        try {
            refreshFromStoreAsync().get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Thread-safe eligibility check
     */
    public boolean isEligibleForHighScore(int score) {
        if (score <= 0) return false;

        lock.readLock().lock();
        try {
            if (scores.size() < MAX) return true;
            int worstScore = scores.get(scores.size() - 1).getScore();
            return score > worstScore;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Asynchronous clear scores
     */
    public Future<Void> clearScoresAsync() {
        return scoreExecutor.submit(() -> {
            lock.writeLock().lock();
            try {
                scores.clear();
                store.save(scores);
            } finally {
                lock.writeLock().unlock();
            }
            return null;
        });
    }

    public void clearScores() {
        try {
            clearScoresAsync().get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Graceful shutdown of all threads
     */
    public void shutdown() {
        shutdown = true;
        submissionWorker.interrupt();
        scoreExecutor.shutdown();
        try {
            if (!scoreExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scoreExecutor.shutdownNow();
                if (!scoreExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.err.println("Score executor did not terminate cleanly");
                }
            }
        } catch (InterruptedException e) {
            scoreExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Inner record for score submission tasks
     */
    private record ScoreSubmissionTask(String playerName, int score, ScoreSubmissionCallback callback) {
    }

    /**
     * Callback interface for non-blocking score submissions
     */
    public interface ScoreSubmissionCallback {
        void onSubmissionComplete(boolean success, String message);
    }
}