public class QuizClashAVL {

    static class Node {
        int score;
        int height;
        int size;
        Node left, right;

        Node(int score) {
            this.score = score;
            this.height = 1;
            this.size = 1;
        }
    }

    // ── Utility ──────────────────────────────────────────────
    int height(Node n) { return n == null ? 0 : n.height; }
    int size(Node n)   { return n == null ? 0 : n.size; }

    void update(Node n) {
        if (n != null) {
            n.height = 1 + Math.max(height(n.left), height(n.right));
            n.size   = 1 + size(n.left) + size(n.right);
        }
    }

    int balanceFactor(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    // ── Rotations ─────────────────────────────────────────────
    Node rotateRight(Node y) {
        Node x  = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left  = T2;
        update(y);
        update(x);
        System.out.println("  RR Rotation at pivot " + y.score);
        return x;
    }

    Node rotateLeft(Node x) {
        Node y  = x.right;
        Node T2 = y.left;
        y.left  = x;
        x.right = T2;
        update(x);
        update(y);
        System.out.println("  LL Rotation at pivot " + x.score);
        return y;
    }

    // ── Balance ───────────────────────────────────────────────
    Node balance(Node n) {
        update(n);
        int bf = balanceFactor(n);

        // Left-Left
        if (bf > 1 && balanceFactor(n.left) >= 0)
            return rotateRight(n);

        // Left-Right
        if (bf > 1 && balanceFactor(n.left) < 0) {
            System.out.println("  LR Rotation at pivot " + n.score);
            n.left = rotateLeft(n.left);
            return rotateRight(n);
        }

        // Right-Right
        if (bf < -1 && balanceFactor(n.right) <= 0)
            return rotateLeft(n);

        // Right-Left
        if (bf < -1 && balanceFactor(n.right) > 0) {
            System.out.println("  RL Rotation at pivot " + n.score);
            n.right = rotateRight(n.right);
            return rotateLeft(n);
        }

        return n;
    }

    // ── Insert ────────────────────────────────────────────────
    Node insert(Node n, int score) {
        if (n == null) return new Node(score);
        if (score > n.score)      n.left  = insert(n.left,  score);
        else if (score < n.score) n.right = insert(n.right, score);
        else return n; // duplicate
        return balance(n);
    }

    // ── Find Min (for delete) ─────────────────────────────────
    Node minNode(Node n) {
        return n.left == null ? n : minNode(n.left);
    }

    // ── Delete ────────────────────────────────────────────────
    Node delete(Node n, int score) {
        if (n == null) return null;
        if (score > n.score)      n.left  = delete(n.left,  score);
        else if (score < n.score) n.right = delete(n.right, score);
        else {
            if (n.left == null)  return n.right;
            if (n.right == null) return n.left;
            Node successor = minNode(n.right);
            n.score = successor.score;
            n.right = delete(n.right, successor.score);
        }
        return balance(n);
    }

    // ── Score Update (delete old + insert new) ────────────────
    Node updateScore(Node root, int oldScore, int newScore) {
        System.out.println("\n  Deleting " + oldScore + "...");
        root = delete(root, oldScore);
        System.out.println("  Inserting " + newScore + "...");
        root = insert(root, newScore);
        return root;
    }

    // ── Rank Query (descending: rank 1 = highest score) ───────
    int getRank(Node n, int score) {
        if (n == null) return -1;
        if (score == n.score)     return size(n.right) + 1;
        else if (score > n.score) return getRank(n.left, score);
        else                      return size(n.right) + 1 + getRank(n.right, score);
    }

    // ── Top-K Query ───────────────────────────────────────────
    void topK(Node n, int k, int[] count, int[] result) {
        if (n == null || count[0] >= k) return;
        topK(n.right, k, count, result);          // right first (descending)
        if (count[0] < k) {
            result[count[0]++] = n.score;
        }
        topK(n.left, k, count, result);
    }

    // ── Print Tree (visual) ───────────────────────────────────
    void printTree(Node n, String prefix, boolean isLeft) {
        if (n == null) return;
        System.out.println(prefix + (isLeft ? "├── " : "└── ")
                + n.score + " [size=" + n.size + ", h=" + n.height + "]");
        printTree(n.left,  prefix + (isLeft ? "│   " : "    "), true);
        printTree(n.right, prefix + (isLeft ? "│   " : "    "), false);
    }

    // ── Main ──────────────────────────────────────────────────
    public static void main(String[] args) {
        QuizClashAVL avl = new QuizClashAVL();
        Node root = null;

        int[] scores = {820, 540, 910, 770, 880, 460, 990, 600, 730, 950, 510};

        // ── Step 1: Build Tree ────────────────────────────────
        System.out.println("===========================================");
        System.out.println("   QUIZCLASH AVL TREE - LEADERBOARD");
        System.out.println("===========================================");
        System.out.println("\n--- Inserting Scores ---");
        for (int s : scores) {
            System.out.println("\nInserting: " + s);
            root = avl.insert(root, s);
        }

        // ── Step 2: Print Final Tree ──────────────────────────
        System.out.println("\n--- Final AVL Tree (Descending by Score) ---");
        avl.printTree(root, "", false);

        // ── Step 3: Rank Queries (before updates) ─────────────
        System.out.println("\n--- Rank Queries (Before Updates) ---");
        int[] queryScores = {990, 820, 540, 510};
        for (int s : queryScores) {
            System.out.println("Rank of player with score " + s
                    + " = " + avl.getRank(root, s));
        }

        // ── Step 4: Top-K Query ───────────────────────────────
        System.out.println("\n--- Top 5 Players (Before Updates) ---");
        int[] top5 = new int[5];
        int[] count = {0};
        avl.topK(root, 5, count, top5);
        System.out.print("Top 5 scores: ");
        for (int i = 0; i < 5; i++)
            System.out.print(top5[i] + (i < 4 ? ", " : "\n"));

        // ── Step 5: Score Updates ─────────────────────────────
        System.out.println("\n--- Score Updates ---");
        System.out.println("Update 1: Player 540 -> 815");
        root = avl.updateScore(root, 540, 815);

        System.out.println("\nUpdate 2: Player 910 -> 685");
        root = avl.updateScore(root, 910, 685);

        // ── Step 6: Print Updated Tree ────────────────────────
        System.out.println("\n--- Updated AVL Tree ---");
        avl.printTree(root, "", false);

        // ── Step 7: Rank Queries (after updates) ──────────────
        System.out.println("\n--- Rank Queries (After Updates) ---");
        int[] updatedScores = {990, 820, 815, 685};
        for (int s : updatedScores) {
            System.out.println("Rank of player with score " + s
                    + " = " + avl.getRank(root, s));
        }

        // ── Step 8: Top-K after updates ───────────────────────
        System.out.println("\n--- Top 5 Players (After Updates) ---");
        int[] top5new = new int[5];
        int[] count2  = {0};
        avl.topK(root, 5, count2, top5new);
        System.out.print("Top 5 scores: ");
        for (int i = 0; i < 5; i++)
            System.out.print(top5new[i] + (i < 4 ? ", " : "\n"));

        // ── Step 9: Complexity Summary ────────────────────────
        System.out.println("\n--- Time Complexity Summary ---");
        System.out.println("Insert/Delete  : O(log n)");
        System.out.println("Score Update   : O(log n)");
        System.out.println("Rank Query     : O(log n)");
        System.out.println("Top-K Query    : O(log n + k)");
        System.out.println("Naive scan     : O(60,000) — too slow");
        System.out.println("AVL log2(60000): ~16 operations — real-time OK");
    }
}
