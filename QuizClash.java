// QuizClash - Live Tournament Leaderboard using Rank-Augmented AVL Tree
// DSA-2 Case Study | KL University

public class QuizClash {

    // ─── Node ────────────────────────────────────────────────────────────────
    static class Node {
        int score;
        int height;
        int size;        // subtree size for O(log n) rank queries
        Node left, right;

        Node(int score) {
            this.score  = score;
            this.height = 1;
            this.size   = 1;
        }
    }

    // ─── AVL Tree ─────────────────────────────────────────────────────────────
    static class AVLTree {

        private Node root;

        // Utility helpers
        private int height(Node n) { return n == null ? 0 : n.height; }
        private int size(Node n)   { return n == null ? 0 : n.size;   }

        private void update(Node n) {
            if (n != null) {
                n.height = 1 + Math.max(height(n.left), height(n.right));
                n.size   = 1 + size(n.left) + size(n.right);
            }
        }

        private int bf(Node n) {
            return n == null ? 0 : height(n.left) - height(n.right);
        }

        // ── Rotations ────────────────────────────────────────────────────────
        private Node rotateRight(Node y) {
            Node x  = y.left;
            Node T2 = x.right;
            x.right = y;
            y.left  = T2;
            update(y); update(x);
            System.out.println("  [Rotation] RR (right-rotate) at pivot " + y.score);
            return x;
        }

        private Node rotateLeft(Node x) {
            Node y  = x.right;
            Node T2 = y.left;
            y.left  = x;
            x.right = T2;
            update(x); update(y);
            System.out.println("  [Rotation] LL (left-rotate) at pivot " + x.score);
            return y;
        }

        // ── Balance ───────────────────────────────────────────────────────────
        private Node balance(Node n) {
            update(n);
            int b = bf(n);

            // Left-Left
            if (b > 1 && bf(n.left) >= 0)
                return rotateRight(n);

            // Left-Right
            if (b > 1 && bf(n.left) < 0) {
                System.out.println("  [Rotation] LR at pivot " + n.score);
                n.left = rotateLeft(n.left);
                return rotateRight(n);
            }

            // Right-Right
            if (b < -1 && bf(n.right) <= 0)
                return rotateLeft(n);

            // Right-Left
            if (b < -1 && bf(n.right) > 0) {
                System.out.println("  [Rotation] RL at pivot " + n.score);
                n.right = rotateRight(n.right);
                return rotateLeft(n);
            }

            return n;
        }

        // ── Insert ────────────────────────────────────────────────────────────
        private Node insert(Node n, int score) {
            if (n == null) return new Node(score);
            if (score > n.score)      n.left  = insert(n.left,  score);
            else if (score < n.score) n.right = insert(n.right, score);
            else                      return n;   // duplicate score ignored
            return balance(n);
        }

        public void insert(int score) { root = insert(root, score); }

        // ── Delete ────────────────────────────────────────────────────────────
        private Node minNode(Node n) {
            while (n.left != null) n = n.left;
            return n;
        }

        private Node delete(Node n, int score) {
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

        public void delete(int score) { root = delete(root, score); }

        // ── Score Update (delete old + insert new) ────────────────────────────
        public void updateScore(int oldScore, int newScore) {
            System.out.println("\n→ Updating score: " + oldScore + " → " + newScore);
            delete(oldScore);
            insert(newScore);
            System.out.println("  Done. New score " + newScore + " inserted.");
        }

        // ── Rank of Player X  (scores stored descending) ─────────────────────
        // rank = number of nodes with score > X  +  1
        public int rankOf(int score) {
            int rank = 1;
            Node cur = root;
            while (cur != null) {
                if (score == cur.score) {
                    rank += size(cur.left);   // nodes with higher score (left subtree)
                    break;
                } else if (score < cur.score) {
                    rank += 1 + size(cur.left);
                    cur = cur.right;
                } else {
                    cur = cur.left;
                }
            }
            return rank;
        }

        // ── Top-K Players ─────────────────────────────────────────────────────
        private void topK(Node n, int k, int[] result, int[] idx) {
            if (n == null || idx[0] >= k) return;
            topK(n.left, k, result, idx);           // left = higher scores
            if (idx[0] < k) result[idx[0]++] = n.score;
            topK(n.right, k, result, idx);
        }

        public int[] topK(int k) {
            int[] result = new int[k];
            int[] idx    = {0};
            topK(root, k, result, idx);
            return result;
        }

        // ── In-order print (descending scores) ───────────────────────────────
        private void inorder(Node n) {
            if (n == null) return;
            inorder(n.left);
            System.out.print(n.score + "(s=" + n.size + ") ");
            inorder(n.right);
        }

        public void printTree() { inorder(root); System.out.println(); }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        AVLTree tree = new AVLTree();

        // ── Step 1: Insert first 11 score updates ────────────────────────────
        int[] scores = {820, 540, 910, 770, 880, 460, 990, 600, 730, 950, 510};
        System.out.println("=== QuizClash: Live Tournament Leaderboard ===");
        System.out.println("\n[Step 1] Inserting 11 scores (descending AVL Tree):");
        System.out.print("Insertion order: ");
        for (int s : scores) System.out.print(s + " ");
        System.out.println();

        for (int s : scores) {
            tree.insert(s);
        }

        System.out.println("\nFinal AVL Tree (in-order, descending by score):");
        tree.printTree();

        // ── Step 2: Rank queries before updates ──────────────────────────────
        System.out.println("\n[Step 2] Rank Queries (before updates):");
        int[] queryScores = {820, 540, 990, 460, 730};
        for (int qs : queryScores) {
            System.out.println("  Rank of player with score " + qs + " = " + tree.rankOf(qs));
        }

        // ── Step 3: Top-K query ───────────────────────────────────────────────
        int K = 5;
        System.out.println("\n[Step 3] Top-" + K + " Players (before updates):");
        int[] topK = tree.topK(K);
        System.out.print("  Scores: ");
        for (int s : topK) System.out.print(s + " ");
        System.out.println();

        // ── Step 4: Two score updates ─────────────────────────────────────────
        System.out.println("\n[Step 4] Processing Score Updates:");
        tree.updateScore(540, 815);   // player jumps from 540 → 815
        tree.updateScore(910, 685);   // player drops from 910 → 685

        System.out.println("\nAVL Tree after updates:");
        tree.printTree();

        // ── Step 5: Rank queries after updates ────────────────────────────────
        System.out.println("\n[Step 5] Rank Queries (after updates):");
        System.out.println("  Rank of player with score 815 = " + tree.rankOf(815));
        System.out.println("  Rank of player with score 685 = " + tree.rankOf(685));

        // ── Step 6: Top-K after updates ───────────────────────────────────────
        System.out.println("\n[Step 6] Top-" + K + " Players (after updates):");
        topK = tree.topK(K);
        System.out.print("  Scores: ");
        for (int s : topK) System.out.print(s + " ");
        System.out.println();

        // ── Complexity Summary ────────────────────────────────────────────────
        System.out.println("\n=== Time Complexity Summary ===");
        System.out.println("  AVL Insert / Delete   : O(log n)");
        System.out.println("  Rank of Player X      : O(log n)");
        System.out.println("  Top-K Players         : O(log n + K)");
        System.out.println("  Naive scan (60,000)   : O(n) — too slow");
        System.out.println("  Rank-Augmented AVL    : O(log n) — handles 10,000 updates/sec ✓");
    }
}
